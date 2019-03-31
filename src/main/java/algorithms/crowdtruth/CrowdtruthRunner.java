package algorithms.crowdtruth;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.opencsv.CSVWriter;
import model.*;
import org.jooq.lambda.UncheckedException;
import web.SemesterSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
public class CrowdtruthRunner {
    private static final String[] KNOWN_ANNOTATION_OPTIONS = Arrays.stream( DefectType.values() ).map( Enum::name
    ).toArray( String[]::new );

    private static final String CROWDTRUTH_OUT_WORKER_QUALITY_CSV = "output/crowdtruth/worker_quality.csv";

    private static final String CROWDTRUTH_OUT_ANNOTATION_QUALITY_CSV = "output/crowdtruth/annotation_quality.csv";

    private static final String CROWDTRUTH_OUT_MEDIA_UNIT_QUALITY_CSV = "output/crowdtruth/media_unit_quality.csv";

    private static final String CROWDTRUTH_OUT_MEDIA_UNIT_ANNOTATION_SCORE_CSV =
            "output/crowdtruth/media_unit_annotation_score.csv";

    private final ImmutableSet<FinalDefect> finalDefects;

    private final Metrics.MetricsScores metricsScores;

    private final ImmutableSet<DefectReport> defectReports;

    private final ImmutableMap<String, Eme> emes;

    private final SemesterSettings semesterSettings;

    private CrowdtruthRunner( final SemesterSettings semesterSettings ) {
        this.semesterSettings = semesterSettings;
        try (Connection c = DatabaseConnector.createConnection()) {
            this.defectReports = DefectReport.fetchDefectReports( c, semesterSettings.getDefectReportFilter() );
            this.metricsScores = getMetricsScores( this.defectReports );
            this.emes = Eme.fetchEmes( c, semesterSettings ).stream().collect( ImmutableMap
                    .toImmutableMap( Eme::getEmeId, Function.identity() ) );
            this.finalDefects = getFinalDefects( this.metricsScores, this.emes );
        } catch (SQLException | IOException e) {
            throw new UncheckedException( e );
        }
    }

    public SemesterSettings getSemesterSettings() {
        return this.semesterSettings;
    }

    public ImmutableSet<FinalDefect> getFinalDefectsFromWorkers( final ImmutableSet<Integer> workerIds ) {
        final ImmutableSet<DefectReport> filteredDefectReports = this.defectReports.stream().filter( r -> workerIds
                .contains( r
                        .getWorkerId() ) ).collect( ImmutableSet.toImmutableSet() );
        final Metrics.MetricsScores adaptedMetrics = getMetricsScores( filteredDefectReports );
        return calculateFinalDefects( adaptedMetrics, this.emes );
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
        final ImmutableMap<Worker, Double> orderedByQuality = Maps.newHashMap( this.metricsScores
                .getWorkerQualityScores() )
                .entrySet().stream().sorted( ( c1, c2 ) -> samplingType == SamplingType.LOWEST ? c1.getValue()
                        .compareTo( c2.getValue() ) : c2.getValue().compareTo( c1.getValue() ) ).collect(
                        ImmutableMap.toImmutableMap( Map.Entry::getKey,
                                Map.Entry::getValue ) );

        return IntStream.range( 0, nrWorkers ).mapToObj( n -> orderedByQuality.entrySet().stream().skip( n ).findFirst()
                .get() )
                .map( w -> {
                    final int workerId = Integer.valueOf( w.getKey().getId().toString() );
                    return new Sample( w.getKey().getId().toString(), w.getValue(), getFinalDefectForWorker( workerId
                    ) );
                } ).collect( ImmutableSet.toImmutableSet() );
    }

    public ImmutableSet<FinalDefect> getFinalDefects() {
        return this.finalDefects;
    }

    public Metrics.MetricsScores getMetricsScores() {
        return this.metricsScores;
    }


    private ImmutableSet<FinalDefect> getFinalDefectForMediaUnit( final String emeId ) {
        return this.defectReports.stream().filter( d -> Objects.equals( d.getEmeId(), emeId ) ).map( d ->
                FinalDefect.builder(
                        this.emes.get( d.getEmeId() ) ).withAgreementCoeff( 1.0 ).withFinalDefectType( FinalDefectType
                        .fromDefectType( d.getDefectType() ) ).build() ).collect( ImmutableSet.toImmutableSet() );
    }

    private ImmutableSet<FinalDefect> getFinalDefectForAnnotation( final DefectType defectType ) {
        return this.defectReports.stream().filter( d -> Objects.equals( d.getDefectType(), defectType ) ).map( d ->
                FinalDefect.builder(
                        this.emes.get( d.getEmeId() ) ).withAgreementCoeff( 1.0 ).withFinalDefectType( FinalDefectType
                        .fromDefectType( d.getDefectType() ) ).build() ).collect( ImmutableSet.toImmutableSet() );
    }

    private ImmutableSet<FinalDefect> getFinalDefectForWorker( final int workerId ) {
        return this.defectReports.stream().filter( d -> d.getWorkerId() == workerId ).map( d -> FinalDefect.builder(
                this.emes.get( d.getEmeId() ) ).withAgreementCoeff( 1.0 ).withFinalDefectType( FinalDefectType
                .fromDefectType( d.getDefectType() ) ).build() ).collect( ImmutableSet.toImmutableSet() );
    }

    public static CrowdtruthRunner create( final SemesterSettings settings ) {
        return new CrowdtruthRunner( settings );
    }

    private static ImmutableSet<FinalDefect> getFinalDefects( final Metrics.MetricsScores metricsScores, final
    ImmutableMap<String, Eme> emes ) throws
            IOException, SQLException {
        writeMetrics( metricsScores );
        return calculateFinalDefects( metricsScores, emes );
    }

    private static void writeMetrics( final Metrics.MetricsScores metricsScores ) throws IOException {
        Files.createDirectories( Paths.get( "output/crowdtruth" ) );

        try (CSVWriter workerQualityWriter = new CSVWriter( Files.newBufferedWriter( Paths.get(
                CROWDTRUTH_OUT_WORKER_QUALITY_CSV ) ) )) {
            workerQualityWriter.writeNext( new String[]{"workerId", "Worker Quality Score (WSQ)"} );
            metricsScores.getWorkerQualityScores().forEach( ( w, q ) -> workerQualityWriter.writeNext( new
                    String[]{w.getId().toString(), q.toString()} ) );
        }

        try (CSVWriter annotationQualityWriter = new CSVWriter( Files.newBufferedWriter( Paths.get(
                CROWDTRUTH_OUT_ANNOTATION_QUALITY_CSV ) ) )) {
            annotationQualityWriter.writeNext( new String[]{"annotationId", "Annotation Quality Score (AQS)"} );
            metricsScores.getAnnotationQualityScores().forEach( ( w, q ) -> annotationQualityWriter.writeNext( new
                    String[]{w.getName().toString(), q.toString()} ) );
        }

        try (CSVWriter workerQualityWriter = new CSVWriter( Files.newBufferedWriter( Paths.get(
                CROWDTRUTH_OUT_MEDIA_UNIT_QUALITY_CSV ) ) )) {
            workerQualityWriter.writeNext( new String[]{"emeId", "Media Unit Quality Score (UQS)"} );
            metricsScores.getMediaUnitQualityScores().forEach( ( w, q ) -> workerQualityWriter.writeNext( new
                    String[]{w.getId().toString(), q.toString()} ) );
        }

        try (CSVWriter workerQualityWriter = new CSVWriter( Files.newBufferedWriter( Paths.get(
                CROWDTRUTH_OUT_MEDIA_UNIT_ANNOTATION_SCORE_CSV ) ) )) {
            workerQualityWriter.writeNext( new String[]{"emeId", "defectType", "Media Unit Quality Score (UQS)"} );
            metricsScores.getMediaUnitAnnotationScores().forEach( ( w, q ) -> workerQualityWriter.writeNext( new
                    String[]{w.getId().getMediaUnitId().toString(), w.getId().getName(), q.toString()}
            ) );
        }
    }

    private static Metrics.MetricsScores getMetricsScores( final ImmutableSet<DefectReport> defectReports ) {
        final ImmutableSet<CrowdtruthData> data = defectReports.stream().map( r -> new CrowdtruthData( String.valueOf( r
                .getEmeId() ),
                String.valueOf( r.getId() ), String.valueOf( r
                .getWorkerId() ), r.getDefectType().name() ) ).collect( ImmutableSet.toImmutableSet() );
        final ImmutableSet<MediaUnit> annotatedData = CrowdtruthData.annotate( data, KNOWN_ANNOTATION_OPTIONS );
        return Metrics.calculateClosed( annotatedData );
    }

    private static ImmutableSet<FinalDefect> calculateFinalDefects( final Metrics.MetricsScores metricsScores, final
    ImmutableMap<String, Eme> emes ) {
        final Map<MediaUnitId, FinalDefect.Builder> finalDefects = Maps.newHashMap();
        metricsScores.getMediaUnitAnnotationScores().forEach( ( annotation, score ) -> {
            final MediaUnitId emeId = annotation.getId().getMediaUnitId();
            final DefectType defectType = DefectType.fromString( annotation.getId().getName() );

            final FinalDefect.Builder builder = finalDefects.computeIfAbsent( emeId, e -> FinalDefect.builder(
                    emes.get( emeId.toString() ) ) );
            if (builder.getAgreementCoeff() < score) {
                builder.withFinalDefectType( FinalDefectType.fromDefectType( defectType ) ).withAgreementCoeff(
                        score );
            } else if (builder.getAgreementCoeff() == score) {
                builder.withFinalDefectType( FinalDefectType.UNDECIDABLE );
            }
        } );

        return finalDefects.values().stream().map( FinalDefect.Builder::build ).collect( ImmutableSet
                .toImmutableSet() );
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
