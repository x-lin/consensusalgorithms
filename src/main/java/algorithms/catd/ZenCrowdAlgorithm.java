package algorithms.catd;

import algorithms.fastdawidskene.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Implements algorithm from:
 *
 * ZenCrowd: Leveraging Probabilistic Reasoning and Crowdsourcing Techniques for Large-Scale Entity Linking
 * G. Demartini et al.
 * 2012
 *
 * @author LinX
 */
public class ZenCrowdAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( ZenCrowdAlgorithm.class );

    //threshold under which the algorithm can be viewed as converged
    private static final double CONVERGENCE_THRESHOLD = 0.00001;

    private static final int MAXIMUM_NR_ITERATIONS = 100;

    private final Answers answers;

    public ZenCrowdAlgorithm( final Set<Answer> answers ) {
        this.answers = new Answers( answers );
    }

    //TODO incorporate prior estimations of worker quality
    public Output run() {
        int iteration = 0;

        ImmutableMap<ParticipantId, Double> workerReliability = initWorkerReliability();

        Optional<Output> output = Optional.empty();

        while (true) {
            iteration++;
            LOG.info( "Starting iteration {}.", iteration );

            //e-step / estimation step
            final ImmutableMap<QuestionId, ImmutableSet<ClassEstimation>> classEstimations = calculateClassEstimates(
                    workerReliability );

            //m-step / maximization step
            workerReliability = calculateWorkerReliability( classEstimations );

            final Output newOutput = new Output( classEstimations, workerReliability );
            if (iteration >= MAXIMUM_NR_ITERATIONS || output.map( o -> o.hasConverged( newOutput ) ).orElse( false )) {
                break;
            }

            output = Optional.of( newOutput );
        }

        return output.get();
    }

    /**
     * Calculates the class estimations for every click. See Section 4.4 on gathering
     *
     * @param workerReliabilities worker reliabilities
     * @return class estimates
     */
    private ImmutableMap<QuestionId, ImmutableSet<ClassEstimation>> calculateClassEstimates(
            final ImmutableMap<ParticipantId, Double> workerReliabilities ) {
        return Maps.toMap( this.answers.getQuestions(), click -> {
            final ImmutableSet<Answer> answers = this.answers.getAnswers( click );

            final Map<ChoiceId, Double> estimationsForLabel = Maps.newHashMap();
            answers.forEach( answer -> {
                this.answers.getChoices().forEach( label -> {
                    double estimation = estimationsForLabel.computeIfAbsent( label, l -> 1.0 );
                    final double workerReliability = workerReliabilities.get( answer.getParticipantId() );
                    if (label.equals( answer.getChoices().iterator().next() )) {
                        estimation = estimation * workerReliability;
                    }
                    else {
                        estimation = estimation * (1 - workerReliability) / (this.answers.getChoices().size() - 1);
                    }
                    estimationsForLabel.put( label, estimation );
                } );
            } );

            final double sum = estimationsForLabel.values().stream().mapToDouble( e -> e ).sum();

            return estimationsForLabel.entrySet().stream().map( e -> new ClassEstimation( e.getKey(),
                    sum == 0 ? 1.0 / estimationsForLabel.size() : e.getValue() / sum ) ).collect(
                    ImmutableSet.toImmutableSet() );
        } );
    }

    /**
     * Update worker reliability. See paragraph about gathering posterior evidences on the reliability of the
     * workers.
     *
     * @param classEstimations class estimations
     * @return worker reliability scores
     */
    private ImmutableMap<ParticipantId, Double> calculateWorkerReliability(
            final ImmutableMap<QuestionId, ImmutableSet<ClassEstimation>> classEstimations ) {
        return Maps.toMap( this.answers.getParticipants(), worker -> {
            final ImmutableSet<Answer> answersForWorker = this.answers.getAnswers( worker );
            return answersForWorker.stream().mapToDouble( answer -> {
                final Double estimation = classEstimations.get( answer.getQuestionId() )
                                                          .stream().filter(
                                q -> q.getChoice().equals( answer.getChoices().iterator().next() ) ).map(
                                ClassEstimation::getEstimation ).findFirst().orElse( 0.0 );
                return estimation / answersForWorker.size();
            } ).sum();
        } );
    }

    /**
     * Initialize the worker reliability with 0.5, if no initial estimations available. See Section 4.4 about
     * initializing the prior probability of the workers.
     *
     * @return initial worker reliability score
     */
    private ImmutableMap<ParticipantId, Double> initWorkerReliability() {
        return Maps.toMap( this.answers.getParticipants(), participantId -> 0.5 );
    }

    public final class Output {
        private final ImmutableMap<QuestionId, ImmutableSet<ClassEstimation>> classProbabilities;

        private final ImmutableMap<ParticipantId, Double> workerReliabilities;

        public Output(
                final ImmutableMap<QuestionId, ImmutableSet<ClassEstimation>> classProbabilities,
                final ImmutableMap<ParticipantId, Double> workerReliabilities ) {
            this.classProbabilities = classProbabilities;
            this.workerReliabilities = workerReliabilities;
        }

        public ImmutableMap<QuestionId, ImmutableSet<ClassEstimation>> getClassProbabilities() {
            return this.classProbabilities;
        }

        public ImmutableMap<ParticipantId, Double> getWorkerReliabilities() {
            return this.workerReliabilities;
        }

        public boolean hasConverged( final Output other ) {
            final double deltaWorkerReliabilities = this.workerReliabilities.entrySet().stream().mapToDouble(
                    e -> Math.abs( other.workerReliabilities.get( e.getKey() ) - e.getValue() ) ).sum();

            return deltaWorkerReliabilities < CONVERGENCE_THRESHOLD;
        }
    }

    public static final class ClassEstimation {
        private final ChoiceId choice;

        private final double estimation;

        public ClassEstimation( final ChoiceId choice, final double estimation ) {
            this.choice = choice;
            this.estimation = estimation;
        }

        public ChoiceId getChoice() {
            return this.choice;
        }

        public double getEstimation() {
            return this.estimation;
        }

        @Override
        public String toString() {
            return "IndicatorEstimation{" +
                    "choice=" + this.choice +
                    ", estimation=" + this.estimation +
                    '}';
        }
    }
}
