package runs;

/**
 * @author LinX
 */
public class MajorityVotingRunner {
//    public static void main( final String[] args ) throws SQLException, IOException {
//        Files.createDirectories( Paths.get( "output/algorithms.crowdtruth" ) );
//        try (Connection connection = DatabaseConnector.createConnection()) {
//            final String sql = "select * from " + DefectReport.DEFECT_REPORT_TABLE;
//
//            final ImmutableSet<CrowdtruthData> data = DSL.using( c )
//                    .fetch( sql )
//                    .map( DefectReport::new ).stream().map( r -> new CrowdtruthData( String.valueOf( r.getTaskId() ),
//                            String.valueOf( r.getId() ), String.valueOf( r
//                            .getWorkerId() ), r.getDefectType().name() ) ).collect( ImmutableSet.toImmutableSet() );
//            final ImmutableSet<MediaUnit> annotatedData = CrowdtruthData.annotate( data, KNOWN_ANNOTATION_OPTIONS );
//            final Metrics.MetricsScores metricsScores = Metrics.calculateClosed( annotatedData );
//
//            try (CSVWriter workerQualityWriter = new CSVWriter( Files.newBufferedWriter( Paths.get(
//                    CROWDTRUTH_OUT_WORKER_QUALITY_CSV ) ) )) {
//                workerQualityWriter.writeNext( new String[]{"workerId", "Worker Quality Score (WSQ)"} );
//                metricsScores.getWorkerQualityScores().forEach( ( w, q ) -> workerQualityWriter.writeNext( new
//                        String[]{w.getId().toString(), q.toString()} ) );
//            }
//
//            try (CSVWriter annotationQualityWriter = new CSVWriter( Files.newBufferedWriter( Paths.get(
//                    CROWDTRUTH_OUT_ANNOTATION_QUALITY_CSV ) ) )) {
//                annotationQualityWriter.writeNext( new String[]{"annotationId", "Annotation Quality Score (AQS)"} );
//                metricsScores.getAnnotationQualityScores().forEach( ( w, q ) -> annotationQualityWriter.writeNext( new
//                        String[]{w.getName().toString(), q.toString()} ) );
//            }
//
//            try (CSVWriter workerQualityWriter = new CSVWriter( Files.newBufferedWriter( Paths.get(
//                    CROWDTRUTH_OUT_MEDIA_UNIT_QUALITY_CSV ) ) )) {
//                workerQualityWriter.writeNext( new String[]{"taskId", "Media Unit Quality Score (UQS)"} );
//                metricsScores.getMediaUnitQualityScores().forEach( ( w, q ) -> workerQualityWriter.writeNext( new
//                        String[]{w.getId().toString(), q.toString()} ) );
//            }
//
//            try (CSVWriter workerQualityWriter = new CSVWriter( Files.newBufferedWriter( Paths.get(
//                    CROWDTRUTH_OUT_MEDIA_UNIT_ANNOTATION_SCORE_CSV ) ) )) {
//                workerQualityWriter.writeNext( new String[]{"taskId", "defectType", "Media Unit Quality Score (UQS)
// "} );
//                metricsScores.getMediaUnitAnnotationScores().forEach( ( w, q ) -> workerQualityWriter.writeNext( new
//                        String[]{w.getId().getMediaUnitId().toString(), w.getId().getName(), q.toString()}
//                ) );
//            }
//        }
//    }
}
