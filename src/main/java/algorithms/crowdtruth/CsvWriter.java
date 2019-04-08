package algorithms.crowdtruth;

import com.opencsv.CSVWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author LinX
 */
public class CsvWriter {

    private static final String CROWDTRUTH_OUT_WORKER_QUALITY_CSV = "output/crowdtruth/worker_quality.csv";

    private static final String CROWDTRUTH_OUT_ANNOTATION_QUALITY_CSV = "output/crowdtruth/annotation_quality.csv";

    private static final String CROWDTRUTH_OUT_MEDIA_UNIT_QUALITY_CSV = "output/crowdtruth/media_unit_quality.csv";

    private static final String CROWDTRUTH_OUT_MEDIA_UNIT_ANNOTATION_SCORE_CSV =
            "output/crowdtruth/media_unit_annotation_score.csv";

    public static void writeMetrics( final Metrics.MetricsScores metricsScores ) throws IOException {
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

}
