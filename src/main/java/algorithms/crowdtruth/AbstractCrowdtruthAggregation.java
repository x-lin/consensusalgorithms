package algorithms.crowdtruth;

import algorithms.AggregationAlgorithm;
import algorithms.crowdtruth.Metrics.MetricsScores;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import model.*;
import utils.UncheckedSQLException;
import web.SemesterSettings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * @author LinX
 */
public abstract class AbstractCrowdtruthAggregation implements AggregationAlgorithm {
    private static final String[] KNOWN_ANNOTATION_OPTIONS = Arrays.stream( DefectType.values() ).map( Enum::name
    ).toArray( String[]::new );

    private final MetricsScores metricsScores;

    private final ImmutableMap<String, Eme> emes;

    private final ImmutableSet<DefectReport> defectReports;

    private final SemesterSettings settings;

    protected AbstractCrowdtruthAggregation( final SemesterSettings settings,
            final ImmutableSet<DefectReport> defectReports ) {
        try (Connection c = DatabaseConnector.createConnection()) {
            this.settings = settings;
            this.defectReports = defectReports;
            this.metricsScores = getMetricsScores( this.defectReports );
            this.emes = Eme.fetchEmes( c, settings ).stream().collect(
                    ImmutableMap.toImmutableMap( Eme::getEmeId, Function.identity() ) );
        } catch (final SQLException e) {
            throw new UncheckedSQLException( e );
        }
    }

    @Override
    public final ImmutableSet<FinalDefect> getFinalDefects() {
        final Map<MediaUnitId, FinalDefect.Builder> finalDefects = Maps.newHashMap();
        this.metricsScores.getMediaUnitAnnotationScores().forEach( ( annotation, score ) -> {
            final MediaUnitId emeId = annotation.getId().getMediaUnitId();
            final DefectType defectType = DefectType.fromString( annotation.getId().getName() );

            final FinalDefect.Builder builder = finalDefects.computeIfAbsent( emeId, e -> FinalDefect.builder(
                    this.emes.get( emeId.toString() ) ) );
            if (builder.getAgreementCoeff() < score) {
                builder.withFinalDefectType( FinalDefectType.fromDefectType( defectType ) ).withAgreementCoeff(
                        score );
            }
            else if (builder.getAgreementCoeff() == score) {
                builder.withFinalDefectType( FinalDefectType.UNDECIDABLE );
            }
        } );

        return finalDefects.values().stream().map( FinalDefect.Builder::build ).collect(
                ImmutableSet.toImmutableSet() );
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
            final int workerId = Integer.valueOf( w.getKey().getId().toString() );
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
            return new Sample( name, w.getValue(), getFinalDefectForMediaUnit( name ) );
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
                            final int workerId = Integer.valueOf( w.getKey().getId().toString() );
                            return new Sample( w.getKey().getId().toString(), w.getValue(),
                                    getFinalDefectForWorker( workerId
                                    ) );
                        } ).collect( ImmutableSet.toImmutableSet() );
    }

    private ImmutableSet<FinalDefect> getFinalDefectForMediaUnit( final String emeId ) {
        return this.defectReports.stream().filter( d -> Objects.equals( d.getEmeId(), emeId ) ).map(
                this::getFinalDefect )
                                 .collect( ImmutableSet.toImmutableSet() );
    }

    private ImmutableSet<FinalDefect> getFinalDefectForAnnotation( final DefectType defectType ) {
        return this.defectReports.stream().filter( d -> Objects.equals( d.getDefectType(), defectType ) ).map(
                this::getFinalDefect ).collect( ImmutableSet.toImmutableSet() );
    }

    private ImmutableSet<FinalDefect> getFinalDefectForWorker( final int workerId ) {
        return this.defectReports.stream().filter( d -> d.getWorkerId() == workerId ).map( this::getFinalDefect )
                                 .collect( ImmutableSet.toImmutableSet() );
    }

    private FinalDefect getFinalDefect( final DefectReport defectReport ) {
        return FinalDefect.builder( this.emes.get( defectReport.getEmeId() ) ).withAgreementCoeff( 1.0 )
                          .withFinalDefectType( FinalDefectType.fromDefectType( defectReport.getDefectType() ) )
                          .build();
    }

    private static Metrics.MetricsScores getMetricsScores( final ImmutableSet<DefectReport> defectReports ) {
        final ImmutableSet<CrowdtruthData> data = defectReports.stream().map( r -> new CrowdtruthData(
                String.valueOf( r.getEmeId() ), String.valueOf( r.getId() ), String.valueOf( r.getWorkerId() ),
                r.getDefectType().name() ) ).collect( ImmutableSet.toImmutableSet() );
        final ImmutableSet<MediaUnit> annotatedData = CrowdtruthData.annotate( data, KNOWN_ANNOTATION_OPTIONS );
        return Metrics.calculateClosed( annotatedData );
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
            return "SampledWorker{" +
                    "id=" + this.id +
                    ", quality=" + this.quality +
                    ", finalDefects=" + this.finalDefects +
                    '}';
        }
    }
}
