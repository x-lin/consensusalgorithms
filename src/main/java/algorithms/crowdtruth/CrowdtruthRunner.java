package algorithms.crowdtruth;

import com.google.common.collect.ImmutableSet;
import com.opencsv.CSVWriter;
import model.DatabaseConnector;
import model.DefectReport;
import model.DefectType;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

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


    public static void main( final String[] args ) throws ClassNotFoundException, SQLException, IOException {
        Files.createDirectories( Paths.get( "output/crowdtruth" ) );

        try (Connection c = DatabaseConnector.createConnection()) {
            final String sql = "select * from " + DefectReport.DEFECT_REPORT_TABLE;

            final ImmutableSet<CrowdtruthData> data = DSL.using( c )
                    .fetch( sql )
                    .map( DefectReport::new ).stream().map( r -> new CrowdtruthData( String.valueOf( r.getEmeId() ),
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
        }
    }
}
