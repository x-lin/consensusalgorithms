package algorithms.finaldefects;

import algorithms.crowdtruth.*;
import algorithms.crowdtruth.CrowdtruthMetrics.MetricsScores;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import model.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author LinX
 */
public abstract class AbstractCrowdtruthAggregation implements FinalDefectAggregationAlgorithm {
    private static final AnnotationName[] KNOWN_ANNOTATION_OPTIONS = Arrays.stream( DefectType.values() ).map(
            Enum::name
    ).map( AnnotationName::create ).toArray( AnnotationName[]::new );

    private final MetricsScores metricsScores;

    private final Emes emes;

    private final ImmutableSet<DefectReport> defectReports;

    private final SemesterSettings settings;

    protected AbstractCrowdtruthAggregation( final SemesterSettings settings,
            final ImmutableSet<DefectReport> defectReports ) {
        this.settings = settings;
        this.defectReports = defectReports;
        this.emes = Emes.fetchFromDb( settings );
        this.metricsScores = getMetricsScores( this.defectReports );
    }

    @Override
    public final ImmutableMap<EmeAndScenarioId, FinalDefect> getFinalDefects() {
        final Map<EmeAndScenarioId, FinalDefect.Builder> finalDefects = Maps.newHashMap();
        this.metricsScores.getMediaUnitAnnotationScores().forEach( ( annotation, score ) -> {
            final EmeAndScenarioId emeAndScenarioId = EmeAndScenarioId.fromString(
                    annotation.getId().getMediaUnitId().toString() );
            final DefectType defectType = DefectType.fromString( annotation.getId().getName().toString() );

            final FinalDefect.Builder builder = finalDefects.computeIfAbsent( emeAndScenarioId,
                    e -> FinalDefect.builder( this.emes, emeAndScenarioId ) );
            if (builder.getAgreementCoeff().toDouble() < score) {
                builder.withFinalDefectType( defectType.toFinalDefectType() ).withAgreementCoeff(
                        new AgreementCoefficient( score ) );
            }
            else if (builder.getAgreementCoeff().toDouble() == score) {
                builder.withFinalDefectType( FinalDefectType.UNDECIDABLE );
            }
        } );

        return ImmutableMap.copyOf( Maps.transformValues( finalDefects, FinalDefect.Builder::build ) );
    }

    public MetricsScores getMetricsScores() {
        return this.metricsScores;
    }

    public ImmutableSet<DefectReport> getDefectReports() {
        return this.defectReports;
    }

    @Override
    public SemesterSettings getSettings() {
        return this.settings;
    }

    public ImmutableSet<Sample> getAllWorkerScores() {
        final ImmutableMap<Worker, Double> workerQualityScores = this.metricsScores.getWorkerQualityScores();
        return workerQualityScores.entrySet().stream().map( w -> {
            final TaskWorkerId workerId = new TaskWorkerId( w.getKey().getId().toString() );
            return new Sample( w.getKey().getId().toString(), w.getValue(), getFinalDefectForWorker( workerId ) );
        } ).collect( ImmutableSet.toImmutableSet() );
    }

    public ImmutableSet<Sample> getAllAnnotationScores() {
        final ImmutableMap<Annotation, Double> annotationQualityScores = this.metricsScores
                .getAnnotationQualityScores();
        return annotationQualityScores.entrySet().stream().map( w -> {
            final String name = w.getKey().getName().toString();
            return new Sample( name, w.getValue(), getFinalDefectForAnnotation( DefectType.fromString( name ) ) );
        } ).collect( ImmutableSet.toImmutableSet() );
    }

    public ImmutableSet<Sample> getAllMediaUnitScores() {
        final ImmutableMap<MediaUnit, Double> mediaUnitQualityScores = this.metricsScores
                .getMediaUnitQualityScores();
        return mediaUnitQualityScores.entrySet().stream().map( w -> {
            final String name = w.getKey().getId().toString();
            return new Sample( name, w.getValue(), getFinalDefectForMediaUnit( EmeAndScenarioId.fromString( name ) ) );
        } ).collect( ImmutableSet.toImmutableSet() );
    }

    public ImmutableSet<Sample> sampleWorkers( final SamplingType samplingType, final int
            nrWorkers ) {
        final ImmutableMap<Worker, Double> orderedByQuality = Maps.newHashMap(
                this.metricsScores.getWorkerQualityScores() )
                                                                  .entrySet().stream().sorted(
                        ( c1, c2 ) -> samplingType == SamplingType.LOWEST ?
                                c1.getValue().compareTo( c2.getValue() ) :
                                c2.getValue().compareTo( c1.getValue() ) ).collect(
                        ImmutableMap.toImmutableMap( Map.Entry::getKey, Map.Entry::getValue ) );

        return IntStream.range( 0, nrWorkers ).mapToObj( n -> orderedByQuality.entrySet().stream().skip( n ).findFirst()
                                                                              .get() )
                        .map( w -> {
                            final TaskWorkerId workerId = new TaskWorkerId( w.getKey().getId().toString() );
                            return new Sample( w.getKey().getId().toString(), w.getValue(),
                                    getFinalDefectForWorker( workerId ) );
                        } ).collect( ImmutableSet.toImmutableSet() );
    }

    private ImmutableSet<FinalDefect> getFinalDefectForMediaUnit( final EmeAndScenarioId emeAndScenarioId ) {
        return this.defectReports.stream().filter( d -> Objects.equals( d.getEmeAndScenarioId(), emeAndScenarioId ) )
                                 .map( this::getFinalDefect )
                                 .collect( ImmutableSet.toImmutableSet() );
    }

    private ImmutableSet<FinalDefect> getFinalDefectForAnnotation( final DefectType defectType ) {
        return this.defectReports.stream().filter( d -> Objects.equals( d.getDefectType(), defectType ) ).map(
                this::getFinalDefect ).collect( ImmutableSet.toImmutableSet() );
    }

    private ImmutableSet<FinalDefect> getFinalDefectForWorker( final TaskWorkerId workerId ) {
        return this.defectReports.stream().filter( d -> Objects.equals( d.getWorkerId(), workerId ) ).map(
                this::getFinalDefect ).collect( ImmutableSet.toImmutableSet() );
    }

    private FinalDefect getFinalDefect( final DefectReport defectReport ) {
        return FinalDefect.builder( this.emes, defectReport.getEmeAndScenarioId() )
                          .withAgreementCoeff( new AgreementCoefficient( 1.0 ) )
                          .withFinalDefectType( defectReport.getDefectType().toFinalDefectType() )
                          .build();
    }

    private static MetricsScores getMetricsScores( final ImmutableSet<DefectReport> defectReports ) {
        final ImmutableSet<CrowdtruthData> data = defectReports.stream().map( r -> new CrowdtruthData(
                new MediaUnitId( r.getEmeAndScenarioId().toString() ), new WorkerId( r.getWorkerId().toString() ),
                AnnotationName.create( r.getDefectType().name() ) ) ).collect( ImmutableSet.toImmutableSet() );
        final ImmutableSet<MediaUnit> annotatedData = CrowdtruthData.annotate( data, KNOWN_ANNOTATION_OPTIONS );
        return CrowdtruthMetrics.calculateClosed( annotatedData );
    }

    public enum SamplingType {
        HIGHEST,
        LOWEST
    }

    public static class Sample {
        private final String id;

        private final double quality;

        private final ImmutableSet<FinalDefect> finalDefects;

        private Sample( final String id, final double quality, final ImmutableSet<FinalDefect>
                finalDefects ) {
            this.id = id;
            this.quality = quality;
            this.finalDefects = finalDefects;
        }

        public String getId() {
            return this.id;
        }

        public double getQuality() {
            return this.quality;
        }

        public ImmutableSet<FinalDefect> getFinalDefects() {
            return this.finalDefects;
        }

        @Override
        public String toString() {
            return "Sample{" +
                    "id=" + this.id +
                    ", quality=" + this.quality +
                    ", finalDefects=" + this.finalDefects +
                    '}';
        }
    }
}
