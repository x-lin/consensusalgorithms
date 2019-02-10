package algorithms.crowdtruth;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.opencsv.CSVWriter;
import model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

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


    private CrowdtruthRunner() throws IOException, SQLException {
        this.finalDefects = getFinalDefects();
    }

    public static ImmutableSet<FinalDefect> calculateFinalDefects() throws IOException, SQLException {
        return new CrowdtruthRunner().finalDefects;
    }

    private static ImmutableSet<FinalDefect> getFinalDefects() throws IOException, SQLException {
        Files.createDirectories( Paths.get( "output/crowdtruth" ) );

        try (Connection c = DatabaseConnector.createConnection()) {
            final ImmutableSet<CrowdtruthData> data = DefectReport.fetchDefectReports( c, DefectReport.workshopFilter
                    ( "WS1", "WS2", "WS3", "WS4" ) ).stream().map( r -> new CrowdtruthData( String.valueOf( r
                    .getEmeId() ),
                    String.valueOf( r.getId() ), String.valueOf( r
                    .getWorkerId() ), r.getDefectType().name() ) ).collect( ImmutableSet.toImmutableSet() );
            final ImmutableSet<MediaUnit> annotatedData = CrowdtruthData.annotate( data, KNOWN_ANNOTATION_OPTIONS );
            final Metrics.MetricsScores metricsScores = Metrics.calculateClosed( annotatedData );

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

            final ImmutableMap<String, Eme> emes = Eme.fetchEmes( c ).stream().collect( ImmutableMap
                    .toImmutableMap( Eme::getEmeId, Function.identity() ) );
            return calculateFinalDefects( metricsScores, emes );
        }
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
}
