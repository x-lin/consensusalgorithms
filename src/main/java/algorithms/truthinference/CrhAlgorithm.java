package algorithms.truthinference;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements algorithm from:
 *
 * Resolving Conflicts in Heterogeneous Data by Truth Discovery and Source Reliability Estimation
 * Q. Li et al.
 * 2014
 *
 * @author LinX
 */
public class CrhAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( CrhAlgorithm.class );

    //maximum of iterations to perform
    private static final int MAXIMUM_ITERATIONS = 100;

    //threshold of source weight difference under which the algorithm can be viewed as converged
    private static final double CONVERGENCE_THRESHOLD = 0.00001;

    private final Answers answers;

    //question=object, participant=source, choice=entry, answer=observation
    public CrhAlgorithm( final Set<Answer> answers ) {
        this.answers = new Answers( answers );
    }

    public Output run() {
        ImmutableMap<QuestionId, ChoiceId> estimatedTruths = estimateInitialEntityTruths();
        Output output = null;
        int iteration = 0;

        while (true) {
            iteration++;
            LOG.info( "Starting iteration " + iteration );
            final ImmutableMap<ParticipantId, Double> sourceWeights = estimateSourceWeights( estimatedTruths );
            estimatedTruths = estimateTruths( sourceWeights );

            final Output newOutput = new Output( estimatedTruths, sourceWeights );
            if (output != null && (iteration > MAXIMUM_ITERATIONS || output.hasConverged( newOutput ))) {
                return newOutput;
            }
            output = newOutput;
        }
    }

    /**
     * Estimate truths v^(*)_im for i in object and m in property with weighted voting. See equation 13.
     *
     * @param sourceWeights
     * @return
     */
    private ImmutableMap<QuestionId, ChoiceId> estimateTruths(
            final ImmutableMap<ParticipantId, Double> sourceWeights ) {
        return Maps.toMap( this.answers.getQuestions(), entry -> {
            final ImmutableSet<Answer> answers = this.answers.getAnswers( entry );
            final Map<ChoiceId, AtomicDouble> score = Maps.newLinkedHashMap();
            answers.forEach(
                    o -> score.computeIfAbsent( o.getChoices().iterator().next(), k -> new AtomicDouble( 0 ) )
                              .addAndGet( sourceWeights.get( o.getParticipantId() ) ) );
            return score.entrySet().stream().max(
                    Comparator.comparingDouble( e -> e.getValue().get() ) ).get().getKey();
        } );
    }

    /**
     * Estimate source weights w_k where k in source. See equation 5.
     *
     * @param truths estimated truths
     * @return estimated source weights
     */
    private ImmutableMap<ParticipantId, Double> estimateSourceWeights(
            final ImmutableMap<QuestionId, ChoiceId> truths ) {
        final ImmutableMap<ParticipantId, Double> diffPerSource = Maps.toMap( //sum of loss functions for each source
                this.answers.getParticipants(),
                source -> {
                    final int sum = this.answers.getAnswers( source ).stream().mapToInt( observation -> {
                        final int i = observation.getChoices().iterator().next().equals(
                                truths.get( observation.getQuestionId() ) ) ? 0 : 1;
                        return i; //loss function dm
                    } ).sum();
                    return sum == 0 ? 0.00000001 : sum;
                } );

        //normalize by maximum, see paragraph above example 6
        final double maxDiff = diffPerSource.values().stream().max( Comparator.comparingDouble( d -> d ) ).get();

        return ImmutableMap.copyOf( Maps.transformValues( diffPerSource,
                s -> {
                    final double normalized = s / maxDiff;
                    return -Math.log( normalized + 0.0000001 ) + 0.0000001;
                } ) );
    }

    /**
     * Init the initial truth table X^(*) with all v^(*)_im for i in object and m in property with MV algorithm.
     */
    private ImmutableMap<QuestionId, ChoiceId> estimateInitialEntityTruths() {
        return Maps.toMap( this.answers.getQuestions(), entity -> {
            final ImmutableSet<Answer> answers = this.answers.getAnswers( entity );
            final Map<ChoiceId, AtomicInteger> nrAnswers = Maps.newLinkedHashMap();
            answers.forEach(
                    o -> nrAnswers.computeIfAbsent( o.getChoices().iterator().next(), k -> new AtomicInteger( 0 ) )
                                  .incrementAndGet() );
            return nrAnswers.entrySet().stream().max(
                    Comparator.comparingLong( e -> e.getValue().get() ) ).get().getKey();
        } );
    }

    public static final class Output {
        private final ImmutableMap<QuestionId, ChoiceId> truths;

        private final ImmutableMap<ParticipantId, Double> sourceWeights;

        public Output(
                final ImmutableMap<QuestionId, ChoiceId> truths,
                final ImmutableMap<ParticipantId, Double> sourceWeights ) {
            this.truths = truths;
            this.sourceWeights = sourceWeights;
        }

        public ImmutableMap<QuestionId, ChoiceId> getTruths() {
            return this.truths;
        }

        public ImmutableMap<ParticipantId, Double> getSourceWeights() {
            return this.sourceWeights;
        }

        public boolean hasConverged( final Output otherOutput ) {
            return this.sourceWeights.entrySet().stream().mapToDouble(
                    e -> Math.abs( e.getValue() - otherOutput.getSourceWeights().get( e.getKey() ) ) ).sum() <
                    CONVERGENCE_THRESHOLD;
        }
    }
}
