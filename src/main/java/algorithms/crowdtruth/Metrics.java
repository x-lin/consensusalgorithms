package algorithms.crowdtruth;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;
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
public class Metrics {
    //compute metrics until delta falls below threshold (=stable)
    private static final double DELTA = 0.001;

    private Map<Annotation, Double> annotationQualityScores;

    private Map<Worker, Double> workerQualityScores;

    private Map<MediaUnit, Double> mediaUnitQualityScores;

    private final Map<MediaUnitId, MediaUnit> mediaUnits;

    private Metrics( final Set<MediaUnit> mediaUnits ) {
        this.mediaUnitQualityScores = new TreeMap<>( mediaUnits.stream().collect( Collectors.toMap( Function.identity
                (), u -> 1.0 ) ) );
        this.mediaUnits = this.mediaUnitQualityScores.entrySet().stream().collect( ImmutableMap.toImmutableMap( e ->
                e.getKey().getId(), Entry::getKey ) );
        final Map<AnnotationName, Set<MediaUnitAnnotation>> annotations = Maps.newHashMap();
        mediaUnits.stream().flatMap( u -> u.getMediaVector().getResult().keySet().stream() ).forEach( u ->
                annotations.computeIfAbsent( AnnotationName.create( u.getId().getName() ), k -> Sets.newHashSet() )
                        .add( u ) );
        this.annotationQualityScores = new TreeMap<>( annotations.entrySet().stream().collect( Collectors.toMap( e ->
                new Annotation( e.getKey(), e.getValue() ), e -> 1.0 ) ) );
        this.workerQualityScores = new TreeMap<>( mediaUnits.stream().flatMap( u -> u.getWorkers().stream() )
                .distinct().collect
                        ( Collectors
                                .toMap( Function.identity(), w -> 1.0 ) ) );
    }

    private MetricsScores calculate( final boolean closedTask ) {
        while (true) {
            final AtomicBoolean thresholdReached = new AtomicBoolean( true );
            System.err.println( "=============Computed values===================" );

            final Map<Annotation, Double> newAnnotationQualityScores = new TreeMap<>( this.annotationQualityScores );
            if (closedTask) {
                newAnnotationQualityScores.replaceAll( ( annotation, score ) -> {
                    final double newScore = annotationQualityScore( annotation );
                    thresholdReached.compareAndSet( true, Math.abs( newScore - score ) < DELTA );
                    return newScore;
                } );
                System.err.println( "AQS: " + newAnnotationQualityScores.entrySet().stream().collect( ImmutableMap
                        .toImmutableMap( a -> a.getKey().getName(), Entry::getValue ) ) );
            }

            final Map<MediaUnit, Double> newMediaUnitQualityScores = new TreeMap<>( this.mediaUnitQualityScores );
            newMediaUnitQualityScores.replaceAll( ( mediaUnit, score ) -> {
                final double newScore = mediaUnitQualityScore( mediaUnit );
                thresholdReached.compareAndSet( true, Math.abs( newScore - score ) < DELTA );
                return newScore;
            } );
            System.err.println( "UQS: " + newMediaUnitQualityScores.entrySet().stream().collect( ImmutableMap
                    .toImmutableMap( a -> a.getKey().getId(), Entry::getValue ) ) );

            final Map<Worker, Double> newWorkerQualityScores = new TreeMap<>( this.workerQualityScores );
            newWorkerQualityScores.replaceAll( ( worker, score ) -> {
                final double newScore = workerQualityScore( worker );
                thresholdReached.compareAndSet( true, Math.abs( newScore - score ) < DELTA );
                return newScore;
            } );
            System.err.println( "WQS: " + newWorkerQualityScores.entrySet().stream().collect( ImmutableMap
                    .toImmutableMap( a -> a.getKey().getId(), Entry::getValue ) ) );
            this.annotationQualityScores = newAnnotationQualityScores;
            this.mediaUnitQualityScores = newMediaUnitQualityScores;
            this.workerQualityScores = newWorkerQualityScores;

            if (thresholdReached.get()) {
                break;
            }
        }

        final ImmutableMap.Builder<MediaUnitAnnotation, Double> mediaUnitAnnotationScores = ImmutableMap.builder();
        this.mediaUnitQualityScores.keySet()
                .forEach( m ->
                        m.getMediaVector().getResult().keySet().forEach( a -> mediaUnitAnnotationScores.put( a,
                                getMediaUnitAnnotationScore( m, a ) )
                        ) );

        System.err.println( "Finished calculation." );
        return new MetricsScores( closedTask ? this.annotationQualityScores : ImmutableMap.of(), this
                .workerQualityScores, this.mediaUnitQualityScores, mediaUnitAnnotationScores.build() );
    }

    //degree of clarity with which an mediaUnitAnnotation is expressed in a unit
    private double getMediaUnitAnnotationScore( final MediaUnit mediaUnit, final MediaUnitAnnotation
            mediaUnitAnnotation ) {
        final AtomicDouble numerator = new AtomicDouble( 0 );
        final AtomicDouble denominator = new AtomicDouble( 0 );
        mediaUnit.getWorkers().forEach( worker -> {
            final double score = worker.getWorkerVector( mediaUnit ).getAnnotationResults().getResult(
                    mediaUnitAnnotation )
                    .getScore();
            final double workerQualityScore = this.workerQualityScores.get( worker );
            numerator.addAndGet( score * workerQualityScore );
            denominator.addAndGet( workerQualityScore );
        } );
        return denominator.get() == 0 ? 0 : numerator.get() / denominator.get();
    }


    //agreement over an mediaUnitAnnotation in all media units that it appears
    private double annotationQualityScore( final Annotation annotation ) {
        final AtomicDouble numerator = new AtomicDouble( 0 );
        final AtomicDouble denominator = new AtomicDouble( 0 );
        annotation.getMediaUnitAnnotations().parallelStream().forEach( a -> {
            a.getAnnotationVector().getWorkerResults().keySet().forEach( worker1 -> {
                final double worker1Quality = this.workerQualityScores.get( worker1 );
                a.getAnnotationVector().getWorkerResults().keySet().stream().filter( w -> !Objects.equals( w, worker1
                ) ).forEach( worker2 -> {
                    final double worker2Quality = this.workerQualityScores.get( worker2 );
                    final Entry<Double, Double> probability = probabilityWorkerAnnotation( worker1, worker2,
                            annotation );
                    if (probability.getValue() > 0.0) {
                        numerator.addAndGet( worker1Quality * worker2Quality * (probability.getKey()
                                / probability
                                .getValue()) );
                        denominator.addAndGet( worker1Quality * worker2Quality );
                    }
                } );
            } );
        } );
        return denominator.get() == 0 ? 0 : numerator.get() / denominator.get();
    }

    //probability that if first worker annotates an mediaUnitAnnotation, that second worker will also
    // annotate it
    private Entry<Double, Double> probabilityWorkerAnnotation( final Worker worker1, final Worker worker2, final
    Annotation
            annotations ) {
        final AtomicDouble numerator = new AtomicDouble( 0 );
        final AtomicDouble denominator = new AtomicDouble( 0 );
        annotations.getMediaUnitAnnotations().forEach( annotation -> {
            final MediaUnit mediaUnit = this.mediaUnits.get( annotation.getMediaUnitId() );
            final double qualityScore = this.mediaUnitQualityScores.get( mediaUnit );
            if (mediaUnit.getWorkers().contains( worker1 )) {
                final int worker1Score = worker1.getWorkerVector( mediaUnit ).getAnnotationResults().getResult(
                        annotation ).getScore();
                final int worker2Score = Optional.ofNullable( worker2.getWorkerVector( mediaUnit ) ).map( w -> w
                        .getAnnotationResults
                                ().getResult( annotation ).getScore() ).orElse( 0 );
                numerator.addAndGet( qualityScore * worker1Score * worker2Score );
                denominator.addAndGet( qualityScore * worker2Score );
            }
        } );
        return new SimpleImmutableEntry<>( numerator.get(), denominator.get() );
    }

    //overall agreement of one worker with other workers

    private double workerQualityScore( final Worker worker ) {
        return workerMediaUnitAgreement( worker ) * workerWorkerAgreement( worker );
    }

    //agreement of worker with all other workers
    private double workerWorkerAgreement( final Worker worker ) {
        final Set<Worker> otherWorkers = this.workerQualityScores.keySet().stream().filter( w -> !w.equals( worker )
        ).collect
                ( ImmutableSet.toImmutableSet() );
        final AtomicDouble numerator = new AtomicDouble( 0 );
        final AtomicDouble denominator = new AtomicDouble( 0 );
        this.mediaUnitQualityScores.keySet().stream().filter( m -> m.getWorkers().contains( worker ) ).forEach(
                mediaUnit -> otherWorkers.stream().filter( w -> mediaUnit.getWorkers().contains( w ) ).forEach(
                        otherWorker -> {
                            final double otherWorkerQualityScore = this.workerQualityScores
                                    .get( otherWorker );
                            final double mediaUnitQualityScore = this.mediaUnitQualityScores.get( mediaUnit );
                            numerator.addAndGet( weightedCosineScore( mediaUnit, worker, otherWorker ) *
                                    otherWorkerQualityScore *
                                    mediaUnitQualityScore );
                            denominator.addAndGet( otherWorkerQualityScore * mediaUnitQualityScore );
                        } ) );
        return denominator.get() == 0 ? 0 : numerator.get() / denominator.get();
    }

    //similarity between the annotations of a worker and the aggregated annotations of the rest of the workers
    private double workerMediaUnitAgreement( final Worker worker ) {
        final AtomicDouble numerator = new AtomicDouble( 0 );
        final AtomicDouble denominator = new AtomicDouble( 0 );
        worker.getAnnotatedMediaUnits().forEach( annotatedMediaUnit -> {
            final double mediaUnitQualityScore = this.mediaUnitQualityScores.get( annotatedMediaUnit );
            final double weightedCosineScore = weightedCosineScore( worker.getWorkerVector( annotatedMediaUnit )
                    .getAnnotationResults(), annotatedMediaUnit.getMediaUnitVectorExcludingWorker( worker ) );
            numerator.addAndGet( weightedCosineScore * mediaUnitQualityScore );
            denominator.addAndGet( mediaUnitQualityScore );
        } );
        return denominator.get() == 0 ? 0 : numerator.get() / denominator.get();
    }

    //worker agreement over one media unit
    private double mediaUnitQualityScore( final MediaUnit mediaUnit ) {
        final AtomicDouble numerator = new AtomicDouble( 0 );
        final AtomicDouble denominator = new AtomicDouble( 0 );
        mediaUnit.getWorkers().forEach( w1 -> {
            final double w1Quality = this.workerQualityScores.get( w1 );
            mediaUnit.getWorkers().stream().filter( w -> !Objects.equals( w1, w ) ).forEach( w2 -> {
                final double w2Quality = this.workerQualityScores.get( w2 );
                numerator.addAndGet( weightedCosineScore( mediaUnit, w1, w2 ) * w1Quality * w2Quality );
                denominator.addAndGet( w1Quality * w2Quality );
            } );
        } );
        return denominator.get() == 0 ? 0 : numerator.get() / denominator.get();
    }

    //cosine similarity over 2 worker vectors
    private double weightedCosineScore( final MediaUnit mediaUnit, final Worker worker1, final Worker worker2 ) {
        return weightedCosineScore( worker1.getWorkerVector( mediaUnit ).getAnnotationResults(), worker2.getWorkerVector
                ( mediaUnit ).getAnnotationResults() );
    }

    //cosine similarity over 2 vectors
    private double weightedCosineScore( final ResultVector vector1, final ResultVector vector2 ) {
        final AtomicDouble dotProduct = new AtomicDouble( 0 );
        final AtomicDouble magnitude1 = new AtomicDouble( 0 );
        final AtomicDouble magnitude2 = new AtomicDouble( 0 );
        vector1.getResult().forEach( ( ma, w1Ar ) -> {
            final Result w2Ar = vector2.getResult( ma );
            final Double aqs = this.annotationQualityScores.entrySet().stream().filter( a -> a.getKey().getName()
                    .toString
                            ().equals( ma.getId().getName() ) ).findFirst().get().getValue();
            dotProduct.addAndGet( w1Ar.getScore() * w2Ar.getScore() * aqs );
            magnitude1.addAndGet( w1Ar.getScore() * w1Ar.getScore() * aqs );
            magnitude2.addAndGet( w2Ar.getScore() * w2Ar.getScore() * aqs );
        } );
        final double denominator = Math.sqrt( magnitude1.get() * magnitude2.get() );
        return denominator == 0.0 ? 0 : (dotProduct.get() / denominator);
    }

    public static MetricsScores calculateClosed( final Set<MediaUnit> mediaUnits ) {
        return new Metrics( mediaUnits ).calculate( true );
    }

    public static MetricsScores calculateOpen( final Set<MediaUnit> mediaUnits ) {
        return new Metrics( mediaUnits ).calculate( false );
    }

    public static final class MetricsScores {
        private final ImmutableMap<Annotation, Double> annotationQualityScores;

        private final ImmutableMap<Worker, Double> workerQualityScores;

        private final ImmutableMap<MediaUnit, Double> mediaUnitQualityScores;

        private final ImmutableMap<MediaUnitAnnotation, Double> mediaUnitAnnotationScores;

        private MetricsScores( final Map<Annotation, Double> annotationQualityScores, final Map<Worker, Double>
                workerQualityScores, final Map<MediaUnit, Double> mediaUnitQualityScores, final
                               ImmutableMap<MediaUnitAnnotation,
                                       Double> mediaUnitAnnotationScores ) {
            this.annotationQualityScores = ImmutableMap.copyOf( annotationQualityScores );
            this.workerQualityScores = ImmutableMap.copyOf( workerQualityScores );
            this.mediaUnitQualityScores = ImmutableMap.copyOf( mediaUnitQualityScores );
            this.mediaUnitAnnotationScores = mediaUnitAnnotationScores;
        }

        public ImmutableMap<Annotation, Double> getAnnotationQualityScores() {
            return this.annotationQualityScores;
        }

        public ImmutableMap<Worker, Double> getWorkerQualityScores() {
            return this.workerQualityScores;
        }

        public ImmutableMap<MediaUnit, Double> getMediaUnitQualityScores() {
            return this.mediaUnitQualityScores;
        }

        public ImmutableMap<MediaUnitAnnotation, Double> getMediaUnitAnnotationScores() {
            return this.mediaUnitAnnotationScores;
        }

        @Override
        public String toString() {
            return "MetricsScores{" +
                    "annotationQualityScores=" + this.annotationQualityScores.entrySet().stream().collect( ImmutableMap
                    .toImmutableMap( a -> a.getKey().getName(), Entry::getValue ) ) +
                    ", workerQualityScores=" + this.workerQualityScores.entrySet().stream().collect( ImmutableMap
                    .toImmutableMap( a -> a.getKey().getId(), Entry::getValue ) ) +
                    ", mediaUnitQualityScores=" + this.mediaUnitQualityScores.entrySet().stream().collect( ImmutableMap
                    .toImmutableMap( a -> a.getKey().getId(), Entry::getValue ) ) +
                    ", mediaUnitAnnotationScores=" + this.mediaUnitAnnotationScores.entrySet().stream().collect(
                    ImmutableMap
                            .toImmutableMap( a -> a.getKey().getId(), Entry::getValue ) ) +
                    '}';
        }
    }
}
