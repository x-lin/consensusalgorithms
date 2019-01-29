package runs;

import com.google.common.collect.ImmutableSet;
import com.opencsv.CSVWriter;
import crowdtruth.CrowdtruthData;
import crowdtruth.MediaUnit;
import crowdtruth.Metrics;
import model.DefectReport;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author LinX
 */
public class ModelToCrowdtruth {
    private static final String[] KNOWN_ANNOTATION_OPTIONS = {"MISSING", "WRONG_KEY", "SUPERFLUOUS_EME",
            "WRONG_RELM", "NO_DEFECT", "SUPERFLUOUS_SYN", "WRONG"};

    private static final String DB_PATH = "jdbc:mysql://localhost:3306/defect_report?serverTimezone=UTC";

    private static final String USER = "root";

    private static final String PASSWORD = "";

    private static final String CROWDTRUTH_OUT_WORKER_QUALITY_CSV = "output/crowdtruth/worker_quality.csv";

    private static final String CROWDTRUTH_OUT_ANNOTATION_QUALITY_CSV = "output/crowdtruth/annotation_quality.csv";

    private static final String CROWDTRUTH_OUT_MEDIA_UNIT_QUALITY_CSV = "output/crowdtruth/media_unit_quality.csv";

    private static final String CROWDTRUTH_OUT_MEDIA_UNIT_ANNOTATION_SCORE_CSV =
            "output/crowdtruth/media_unit_annotation_score.csv";


    public static void main( String[] args ) throws ClassNotFoundException, SQLException, IOException {
        Files.createDirectories( Paths.get( "output/crowdtruth" ) );

        try (Connection c = DriverManager.getConnection
                ( DB_PATH, USER, PASSWORD )) {
            String sql = "select * from " + DefectReport.DEFECT_REPORT_TABLE;

            ImmutableSet<CrowdtruthData> data = DSL.using( c )
                    .fetch( sql )
                    .map( DefectReport::new ).stream().map( r -> new CrowdtruthData( String.valueOf( r.getTaskId() ),
                            String.valueOf( r.getId() ), String.valueOf( r
                            .getWorkerId() ), r.getDefectType().name() ) ).collect( ImmutableSet.toImmutableSet() );
            ImmutableSet<MediaUnit> annotatedData = CrowdtruthData.annotate( data, KNOWN_ANNOTATION_OPTIONS );
            Metrics.MetricsScores metricsScores = Metrics.calculateClosed( annotatedData );

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
                workerQualityWriter.writeNext( new String[]{"taskId", "Media Unit Quality Score (UQS)"} );
                metricsScores.getMediaUnitQualityScores().forEach( ( w, q ) -> workerQualityWriter.writeNext( new
                        String[]{w.getId().toString(), q.toString()} ) );
            }

            try (CSVWriter workerQualityWriter = new CSVWriter( Files.newBufferedWriter( Paths.get(
                    CROWDTRUTH_OUT_MEDIA_UNIT_ANNOTATION_SCORE_CSV ) ) )) {
                workerQualityWriter.writeNext( new String[]{"taskId", "defectType", "Media Unit Quality Score (UQS)"} );
                metricsScores.getMediaUnitAnnotationScores().forEach( ( w, q ) -> workerQualityWriter.writeNext( new
                        String[]{w.getId().getMediaUnitId().toString(), w.getId().getName(), q.toString()}
                ) );
            }
        }
    }
}
