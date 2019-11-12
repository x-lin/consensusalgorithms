package algorithms.truthinference;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implements Crowdtruth algorithm for calculating worker disagreement metrics from:
 *
 * CrowdTruth 2.0: Quality Metrics for Crowdsourcing with Disagreement
 * A. Dumitrache, O. Aroyo, B. Timmermans, C. Welty
 * 2018
 *
 * @author LinX
 */
public class CrowdtruthAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( CrowdtruthAlgorithm.class );

    //compute metrics until delta falls below threshold (=stable)
    private static final double DELTA = 0.001;

    private Map<ChoiceId, Double> annotationQualityScores;

    private Map<ParticipantId, Double> workerQualityScores;

    private Map<QuestionId, Double> mediaUnitQualityScores;

    private final Answers answers;

    private final ImmutableMap<ParticipantId, Map<QuestionId, Map<ChoiceId, Long>>> workers;

    private CrowdtruthAlgorithm( final Answers answers ) {
        this.answers = answers;
        this.mediaUnitQualityScores = this.answers.getQuestions().stream().collect(
                Collectors.toMap( Function.identity(), m -> 1.0 ) );
        this.annotationQualityScores = this.answers.getChoices().stream().collect(
                Collectors.toMap( Function.identity(), m -> 1.0 ) );
        this.workerQualityScores = this.answers.getParticipants().stream().collect(
                Collectors.toMap( Function.identity(), m -> 1.0 ) );
        this.workers = Maps.toMap( this.answers.getParticipants(), worker -> this.answers.getAnswers( worker ).stream()
                .collect( Collectors.groupingBy( Answer::getQuestionId, Collectors
                        .groupingBy( Answer::getChoice, Collectors.counting() ) ) ) );
    }

    private MetricsScores calculate( final boolean closedTask ) {
        while (true) {
            final AtomicBoolean thresholdReached = new AtomicBoolean( true );
            LOG.info( "=============Computed values===================" );

            final Map<ChoiceId, Double> newAnnotationQualityScores = new LinkedHashMap<>(
                    this.annotationQualityScores );
            if (closedTask) {
                newAnnotationQualityScores.replaceAll( ( annotation, score ) -> {
                    final double newScore = annotationQualityScore( annotation );
                    thresholdReached.compareAndSet( true, Math.abs( newScore - score ) < DELTA );
                    return newScore;
                } );
                LOG.info( "AQS: " + newAnnotationQualityScores );
            }

            final Map<QuestionId, Double> newMediaUnitQualityScores = new LinkedHashMap<>(
                    this.mediaUnitQualityScores );
            newMediaUnitQualityScores.replaceAll( ( mediaUnit, score ) -> {
                final double newScore = mediaUnitQualityScore( mediaUnit );
                thresholdReached.compareAndSet( true, Math.abs( newScore - score ) < DELTA );
                return newScore;
            } );
            LOG.info( "UQS: {}", newMediaUnitQualityScores );

            final Map<ParticipantId, Double> newWorkerQualityScores = new LinkedHashMap<>( this.workerQualityScores );
            newWorkerQualityScores.replaceAll( ( worker, score ) -> {
                final double newScore = workerQualityScore( worker );
                thresholdReached.compareAndSet( true, Math.abs( newScore - score ) < DELTA );
                return newScore;
            } );
            LOG.info( "WQS: {}", newWorkerQualityScores );
            this.annotationQualityScores = newAnnotationQualityScores;
            this.mediaUnitQualityScores = newMediaUnitQualityScores;
            this.workerQualityScores = newWorkerQualityScores;

            if (thresholdReached.get()) {
                break;
            }
        }

        final ImmutableMap.Builder<AbstractMap.Entry<QuestionId, ChoiceId>, Double> mediaUnitAnnotationScores =
                ImmutableMap.builder();
        this.answers.getQuestions().forEach( mediaUnit -> {
            final ImmutableSet<ChoiceId> annotations = this.answers.getAnswers( mediaUnit ).stream().map(
                    m -> m.getChoice() ).collect( ImmutableSet.toImmutableSet() );
            annotations.forEach( annotation -> {
                mediaUnitAnnotationScores.put( new SimpleImmutableEntry<>( mediaUnit, annotation ),
                        getMediaUnitAnnotationScore( mediaUnit, annotation ) );
            } );
        } );

        LOG.info( "Finished calculation." );
        return new MetricsScores( closedTask ? this.annotationQualityScores : ImmutableMap.of(), this
                .workerQualityScores,
                this.mediaUnitQualityScores, mediaUnitAnnotationScores.build() );
    }

    //degree of clarity with which an mediaUnitAnnotation is expressed in a unit
    private double getMediaUnitAnnotationScore( final QuestionId mediaUnit, final ChoiceId annotation ) {
        final AtomicDouble numerator = new AtomicDouble( 0 );
        final AtomicDouble denominator = new AtomicDouble( 0 );
        this.answers.getAnswers( mediaUnit ).stream().map( Answer::getParticipantId ).forEach( worker -> {
            final double score = this.workers.get( worker ).get( mediaUnit ).getOrDefault( annotation, 0L );
            final double workerQualityScore = this.workerQualityScores.get( worker );
            numerator.addAndGet( score * workerQualityScore );
            denominator.addAndGet( workerQualityScore );
        } );
        return denominator.get() == 0 ? 0 : numerator.get() / denominator.get();
    }

    //agreement over an mediaUnitAnnotation in all media units that it appears
    private double annotationQualityScore( final ChoiceId annotation ) {
        final AtomicDouble numerator = new AtomicDouble( 0 );
        final AtomicDouble denominator = new AtomicDouble( 0 );

        this.workers.forEach( ( worker1, w1annotations ) -> {
            final double worker1Quality = this.workerQualityScores.get( worker1 );
            this.workers.forEach( ( worker2, w2annotations ) -> {
                final Sets.SetView<QuestionId> commonMediaUnits = Sets.intersection( w1annotations.keySet(),
                        w2annotations.keySet() );
                if (!worker1.equals( worker2 ) && !commonMediaUnits.isEmpty()) {
                    final double worker2Quality = this.workerQualityScores.get( worker2 );

                    final AtomicDouble probabilityNumerator = new AtomicDouble( 0.0 );
                    final AtomicDouble probabilityDenominator = new AtomicDouble( 0.0 );
                    commonMediaUnits.forEach( mediaUnit -> {
                        final Entry<Double, Double> probability = probabilityWorkerAnnotation(
                                mediaUnit,
                                w1annotations.get( mediaUnit ).getOrDefault( annotation, 0L ),
                                w2annotations.get( mediaUnit ).getOrDefault( annotation, 0L ) );
                        probabilityNumerator.addAndGet( probability.getKey() );
                        probabilityDenominator.addAndGet( probability.getValue() );
                    } );

                    if (probabilityDenominator.get() > 0.0) {
                        numerator.addAndGet(
                                worker1Quality * worker2Quality *
                                        (probabilityNumerator.get() / probabilityDenominator.get()) );
                        denominator.addAndGet( worker1Quality * worker2Quality );
                    }
                }
            } );
        } );
        return denominator.get() == 0 ? 0 : numerator.get() / denominator.get();
    }

    //probability that if first worker annotates an mediaUnitAnnotation, that second worker will also
    // annotate it
    private Entry<Double, Double> probabilityWorkerAnnotation(
            final QuestionId mediaUnit, final long scoreAnnotation1, final Long scoreAnnotation2 ) {
        final AtomicDouble numerator = new AtomicDouble( 0 );
        final AtomicDouble denominator = new AtomicDouble( 0 );
        final double qualityScore = this.mediaUnitQualityScores.get( mediaUnit );
        numerator.addAndGet( qualityScore * scoreAnnotation1 * scoreAnnotation2 );
        denominator.addAndGet( scoreAnnotation1 * scoreAnnotation2 );
        return new SimpleImmutableEntry<>( numerator.get(), denominator.get() );
    }

    //overall agreement of one worker with other workers
    private double workerQualityScore( final ParticipantId worker ) {
        final double v = workerMediaUnitAgreement( worker );
        final double v1 = workerWorkerAgreement( worker );
        return v * v1;
    }

    //agreement of worker with all other workers
    private double workerWorkerAgreement( final ParticipantId worker ) {
        final Set<ParticipantId> otherWorkers = this.workerQualityScores.keySet().stream().filter(
                w -> !w.equals( worker ) ).collect( ImmutableSet.toImmutableSet() );
        final AtomicDouble numerator = new AtomicDouble( 0 );
        final AtomicDouble denominator = new AtomicDouble( 0 );
        this.answers.getAnswers( worker ).forEach(
                mediaUnit -> otherWorkers.stream().filter( w -> !this.answers.getAnswers( w, mediaUnit.getQuestionId() )
                        .isEmpty() ).forEach( otherWorker -> {
                    final double otherWorkerQualityScore = this.workerQualityScores
                            .get( otherWorker );
                    final double mediaUnitQualityScore = this.mediaUnitQualityScores.get( mediaUnit.getQuestionId() );
                    numerator.addAndGet( weightedCosineScore( mediaUnit.getQuestionId(), worker, otherWorker ) *
                            otherWorkerQualityScore *
                            mediaUnitQualityScore );
                    denominator.addAndGet( otherWorkerQualityScore * mediaUnitQualityScore );
                } ) );
        return denominator.get() == 0 ? 0 : numerator.get() / denominator.get();
    }

    //similarity between the annotations of a worker and the aggregated annotations of the rest of the workers
    private double workerMediaUnitAgreement( final ParticipantId worker ) {
        final AtomicDouble numerator = new AtomicDouble( 0 );
        final AtomicDouble denominator = new AtomicDouble( 0 );
        this.answers.getAnswers( worker ).stream().map( Answer::getQuestionId ).forEach( annotatedMediaUnit -> {
            final double mediaUnitQualityScore = this.mediaUnitQualityScores.get( annotatedMediaUnit );
            final Map<ChoiceId, Long> mediaUnitVectorExcludingWorker = Maps.newHashMap();
            this.workers.entrySet().stream().filter(
                    w -> !Objects.equals( worker, w.getKey() ) ).map(
                    w -> w.getValue().getOrDefault( annotatedMediaUnit, ImmutableMap.of() ) ).forEach(
                    v -> v.forEach( ( ve, c ) -> mediaUnitVectorExcludingWorker
                            .compute( ve, ( key, val ) -> val == null ? c : val + c ) ) );
            final double weightedCosineScore = weightedCosineScore(
                    this.workers.get( worker ).get( annotatedMediaUnit ),
                    mediaUnitVectorExcludingWorker );
            numerator.addAndGet( weightedCosineScore * mediaUnitQualityScore );
            denominator.addAndGet( mediaUnitQualityScore );
        } );
        return denominator.get() == 0 ? 0 : numerator.get() / denominator.get();
    }

    //worker agreement over one media unit
    private double mediaUnitQualityScore( final QuestionId mediaUnit ) {
        final AtomicDouble numerator = new AtomicDouble( 0 );
        final AtomicDouble denominator = new AtomicDouble( 0 );

        final ImmutableSet<ParticipantId> workersForMediaUnit = this.answers.getAnswers( mediaUnit ).stream().map(
                Answer::getParticipantId ).collect( ImmutableSet.toImmutableSet() );

        workersForMediaUnit.forEach( worker1 -> {
            final double w1Quality = this.workerQualityScores.get( worker1 );
            workersForMediaUnit.stream().filter( w -> !w.equals( worker1 ) ).forEach( worker2 -> {
                final double w2Quality = this.workerQualityScores.get( worker2 );
                numerator.addAndGet( weightedCosineScore( mediaUnit, worker1, worker2 ) * w1Quality * w2Quality );
                denominator.addAndGet( w1Quality * w2Quality );
            } );
        } );

        this.answers.getAnswers( mediaUnit ).stream().map( Answer::getParticipantId ).forEach( w1 -> {
            final double w1Quality = this.workerQualityScores.get( w1 );
            this.answers.getAnswers( mediaUnit ).stream().map( Answer::getParticipantId ).filter(
                    w -> !Objects.equals( w1, w ) ).forEach( w2 -> {
                final double w2Quality = this.workerQualityScores.get( w2 );
                numerator.addAndGet( weightedCosineScore( mediaUnit, w1, w2 ) * w1Quality * w2Quality );
                denominator.addAndGet( w1Quality * w2Quality );
            } );
        } );
        return denominator.get() == 0 ? 0 : numerator.get() / denominator.get();
    }

    //cosine similarity over 2 worker vectors
    private double weightedCosineScore( final QuestionId mediaUnit, final ParticipantId worker1,
            final ParticipantId worker2 ) {
        return weightedCosineScore( this.workers.get( worker1 ).getOrDefault( mediaUnit, ImmutableMap.of() ),
                this.workers.get( worker2 ).getOrDefault( mediaUnit, ImmutableMap.of() ) );
    }

    //cosine similarity over 2 vectors
    private double weightedCosineScore( final Map<ChoiceId, Long> vector1, final Map<ChoiceId, Long> vector2 ) {
        final AtomicDouble dotProduct = new AtomicDouble( 0 );
        final AtomicDouble magnitude1 = new AtomicDouble( 0 );
        final AtomicDouble magnitude2 = new AtomicDouble( 0 );
        Sets.union( vector1.keySet(), vector2.keySet() ).forEach( ann -> {
            final long w1Ar = vector1.getOrDefault( ann, 0L );
            final long w2Ar = vector2.getOrDefault( ann, 0L );
            final double aqs = this.annotationQualityScores.get( ann );
            dotProduct.addAndGet( w1Ar * w2Ar * aqs );
            magnitude1.addAndGet( w1Ar * w1Ar * aqs );
            magnitude2.addAndGet( w2Ar * w2Ar * aqs );
        } );
        final double denominator = Math.sqrt( magnitude1.get() * magnitude2.get() );
        return denominator == 0.0 ? 0 : (dotProduct.get() / denominator);
    }

    public static MetricsScores calculateClosed( final Answers mediaUnits ) {
        return new CrowdtruthAlgorithm( mediaUnits ).calculate( true );
    }

    public static MetricsScores calculateOpen( final Answers mediaUnits ) {
        return new CrowdtruthAlgorithm( mediaUnits ).calculate( false );
    }

    public static final class MetricsScores {
        private final ImmutableMap<ChoiceId, Double> annotationQualityScores;

        private final ImmutableMap<ParticipantId, Double> workerQualityScores;

        private final ImmutableMap<QuestionId, Double> mediaUnitQualityScores;

        private final ImmutableMap<AbstractMap.Entry<QuestionId, ChoiceId>, Double> mediaUnitAnnotationScores;

        private MetricsScores( final Map<ChoiceId, Double> annotationQualityScores, final Map<ParticipantId, Double>
                workerQualityScores,
                final Map<QuestionId, Double> mediaUnitQualityScores, final
        ImmutableMap<AbstractMap.Entry<QuestionId, ChoiceId>, Double> mediaUnitAnnotationScores ) {
            this.annotationQualityScores = ImmutableMap.copyOf( annotationQualityScores );
            this.workerQualityScores = ImmutableMap.copyOf( workerQualityScores );
            this.mediaUnitQualityScores = ImmutableMap.copyOf( mediaUnitQualityScores );
            this.mediaUnitAnnotationScores = mediaUnitAnnotationScores;
        }

        public ImmutableMap<ChoiceId, Double> getAnnotationQualityScores() {
            return this.annotationQualityScores;
        }

        public ImmutableMap<ParticipantId, Double> getWorkerQualityScores() {
            return this.workerQualityScores;
        }

        public ImmutableMap<QuestionId, Double> getMediaUnitQualityScores() {
            return this.mediaUnitQualityScores;
        }

        public ImmutableMap<AbstractMap.Entry<QuestionId, ChoiceId>, Double> getMediaUnitAnnotationScores() {
            return this.mediaUnitAnnotationScores;
        }

        @Override
        public String toString() {
            return "MetricsScores{" +
                    "annotationQualityScores=" + this.annotationQualityScores +
                    ", workerQualityScores=" + this.workerQualityScores +
                    ", mediaUnitQualityScores=" + this.mediaUnitQualityScores +
                    ", mediaUnitAnnotationScores=" + this.mediaUnitAnnotationScores +
                    '}';
        }
    }
}
