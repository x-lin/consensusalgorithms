package algorithms.truthinference;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TODO
 * Implements algorithm from:
 *
 * Learning From Crowds
 * V. C. Raykar et al.
 * 2010
 *
 * @author LinX
 */
public class LfcAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( LfcAlgorithm.class );

    //threshold under which the algorithm can be viewed as converged
    private static final double CONVERGENCE_THRESHOLD = 0.00001;

    private static final int MAXIMUM_NR_ITERATIONS = 100;

    private final Answers answers;

    public LfcAlgorithm( final Answers answers ) {
        this.answers = answers;
    }

    public Output run() {
        int iteration = 0;

        //u_i
        ImmutableMap<QuestionId, ImmutableSet<ClassEstimation>> classEstimations = initClassEstimates();

        Optional<Output> output = Optional.empty();

        while (true) {
            iteration++;
            LOG.info( "Starting iteration {}.", iteration );

            //m-step / maximization step
            final ImmutableMap<ParticipantId, Double> workerReliability = calculateWorkerReliability(
                    classEstimations );

            //e-step / estimation step
            classEstimations = calculateClassEstimates( workerReliability );


            final Output newOutput = new Output( classEstimations, workerReliability );
            if (iteration >= MAXIMUM_NR_ITERATIONS || output.map( o -> o.hasConverged( newOutput ) ).orElse( false )) {
                break;
            }

            output = Optional.of( newOutput );
        }

        return null;
    }

    private ImmutableMap<QuestionId, ImmutableSet<ClassEstimation>> calculateClassEstimates(
            final ImmutableMap<ParticipantId, Double> workerReliability ) {
        return null;
    }

    private ImmutableMap<ParticipantId, Double> calculateWorkerReliability(
            final ImmutableMap<QuestionId, ImmutableSet<ClassEstimation>> classEstimations ) {
        return null;
    }

    /**
     * Initialize u_i where i in instance.
     *
     * @return estimates
     */
    private ImmutableMap<QuestionId, ImmutableSet<ClassEstimation>> initClassEstimates() {
        return Maps.toMap( this.answers.getQuestions(), task -> {
            final ImmutableMultiset<Answer> answers = this.answers.getAnswers( task );
            final Map<ChoiceId, Long> nrAssignedLabels = answers.stream().map( Answer::getChoice )
                    .collect(
                            Collectors.groupingBy( Function.identity(),
                                    Collectors.counting() ) );
            return nrAssignedLabels.entrySet().stream().map(
                    l -> new ClassEstimation( l.getKey(), l.getValue() / answers.size() ) ).collect(
                    ImmutableSet.toImmutableSet() );
        } );
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
            return "ClassEstimation{" +
                    "choice=" + this.choice +
                    ", estimation=" + this.estimation +
                    '}';
        }
    }
}
