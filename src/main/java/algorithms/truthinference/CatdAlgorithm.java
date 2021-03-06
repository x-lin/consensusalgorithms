package algorithms.truthinference;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Maps;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implements algorithm for estimating truths from:
 *
 * A Confidence-Aware Approach for Truth Discovery on Long-Tail Data
 * Li et al.
 * 2014
 *
 * @author LinX
 */
public class CatdAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( CatdAlgorithm.class );

    private final Answers answers;

    //maximum of iterations to perform
    private static final int MAXIMUM_ITERATIONS = 100;

    //threshold of source weight difference under which the algorithm can be viewed as converged
    private static final double CONVERGENCE_THRESHOLD = 0.00001;

    //question=entity, participant=source, choice=information
    public CatdAlgorithm( final Answers answers ) {
        this.answers = answers;
    }

    public Output run( final double alpha ) {
        ImmutableMap<QuestionId, ChoiceId> estimatedTruths = estimateInitialEntityTruths();
        Output output = null;
        int iteration = 0;

        while (true) {
            iteration++;
            LOG.info( "Starting iteration " + iteration );
            final ImmutableMap<ParticipantId, Double> sourceWeights = estimateSourceWeights( estimatedTruths, alpha );
            estimatedTruths = estimateEntityTruths( sourceWeights );

            final Output newOutput = new Output( estimatedTruths, sourceWeights );
            if (output != null && (MAXIMUM_ITERATIONS < iteration || output.hasConverged( newOutput ))) {
                return newOutput;
            }
            output = newOutput;
        }
    }

    /**
     * Init the initial truth x^*(0)_n with MV algorithm.
     */
    private ImmutableMap<QuestionId, ChoiceId> estimateInitialEntityTruths() {
        return Maps.toMap( this.answers.getQuestions(), entity -> {
            final ImmutableMultiset<Answer> answers = this.answers.getAnswers( entity );
            final Map<ChoiceId, Long> nrAnswersPerChoice = answers.stream().collect(
                    Collectors.groupingBy( Answer::getChoice, Collectors.counting() ) );
            return nrAnswersPerChoice.entrySet().stream().max(
                    Comparator.comparingLong( Map.Entry::getValue ) ).get().getKey();
        } );
    }

    /**
     * Estimate source weights (source reliability degree) w_s.
     * See equation 7
     */
    private ImmutableMap<ParticipantId, Double> estimateSourceWeights(
            final ImmutableMap<QuestionId, ChoiceId> currentTruths, final double alpha ) {
        final ImmutableMap<ParticipantId, Double> initialSourceWeightEstimation = Maps.toMap(
                this.answers.getParticipants(), source -> {
                    final ImmutableMultiset<Answer> claimsFromSource = this.answers.getAnswers( source );
                    final double numerator = new ChiSquaredDistribution( claimsFromSource.size() )
                            .inverseCumulativeProbability( alpha / 2 );

                    final long diffs = claimsFromSource.stream().filter( claim -> !currentTruths.get(
                            claim.getQuestionId() ).equals( claim.getChoice() ) ).count();

                    return numerator / ((double) diffs + 0.000000001);
                } );

        final double weightSum = initialSourceWeightEstimation.values().stream().mapToDouble( e -> e ).sum();

        return ImmutableMap.copyOf( Maps.transformValues( initialSourceWeightEstimation, v -> v / weightSum ) );
    }

    /**
     * Estimate entity truths x^*_n.
     * See equation 1
     */
    private ImmutableMap<QuestionId, ChoiceId> estimateEntityTruths(
            final ImmutableMap<ParticipantId, Double> sourceWeights ) {
        return Maps.toMap( this.answers.getQuestions(), entity -> {
            final ImmutableMultiset<Answer> answers = this.answers.getAnswers( entity );
            final Map<ChoiceId, Double> weightedAnswersPerChoice = Maps.newHashMap();
            answers.forEach( claim -> {
                weightedAnswersPerChoice.compute( claim.getChoice(),
                        ( k, v ) -> Optional.ofNullable( v ).orElse( 0.0 ) +
                                sourceWeights.get( claim.getParticipantId() ) );
            } );

            return weightedAnswersPerChoice.entrySet().stream().max(
                    Comparator.comparingDouble( Map.Entry::getValue ) ).get().getKey();
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
