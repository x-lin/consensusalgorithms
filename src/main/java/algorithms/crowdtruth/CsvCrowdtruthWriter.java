package algorithms.crowdtruth;

import algorithms.finaldefects.FinalDefectCsvWriter;
import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.crowdtruth.CrowdtruthAggregationAlgorithm;
import algorithms.finaldefects.aggregation.AbstractCrowdtruthAggregation.SamplingType;
import algorithms.finaldefects.aggregation.CrowdtruthFilteredWorkersAggregation;
import algorithms.model.TaskWorkerId;
import algorithms.statistic.QualityAnalyzer;
import algorithms.truthinference.CrowdtruthAlgorithm;
import com.google.common.collect.ImmutableSet;
import com.opencsv.CSVWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LinX
 */
public class CsvCrowdtruthWriter {
    private static final String ANALYSIS_OUT_CSV = "output/crowdtruth/analysis.csv";

    private static final String ANAYLSIS_ALL_WORKERS_OUT_CSV = "output/crowdtruth/analysis_all_workers.csv";

    private static final String CROWDTRUTH_OUT_WORKER_QUALITY_CSV = "output/crowdtruth/worker_quality.csv";

    private static final String CROWDTRUTH_OUT_ANNOTATION_QUALITY_CSV = "output/crowdtruth/annotation_quality.csv";

    private static final String CROWDTRUTH_OUT_MEDIA_UNIT_QUALITY_CSV = "output/crowdtruth/media_unit_quality.csv";

    private static final String CROWDTRUTH_OUT_MEDIA_UNIT_ANNOTATION_SCORE_CSV =
            "output/crowdtruth/media_unit_annotation_score.csv";

    public static void main( final String[] args ) {
        final CrowdtruthAggregationAlgorithm
                crowdtruthAggregation = CrowdtruthAggregationAlgorithm.create( SemesterSettings.ws2017() );
        runFullAnalysis( crowdtruthAggregationAlgorithm );
        runSamplingWorkers( crowdtruthAggregationAlgorithm );
        runWorkerAnalysis( crowdtruthAggregationAlgorithm );
    }

    private static void runFullAnalysis( final CrowdtruthAggregationAlgorithm crowdtruthAggregation ) {
        FinalDefectCsvWriter.analyzeAndWrite( crowdtruthAggregationAlgorithm, ANALYSIS_OUT_CSV );
    }

    private static void runWorkerAnalysis( final CrowdtruthAggregationAlgorithm crowdtruthAggregation ) {
        QualityAnalyzer.create().writeConfusionMatrix( crowdtruthAggregationAlgorithm.getSettings(),
                crowdtruthAggregationAlgorithm.getAllWorkerScores(),
                "workerId", ANAYLSIS_ALL_WORKERS_OUT_CSV );
    }

    private static void runSamplingWorkers( final CrowdtruthAggregationAlgorithm crowdtruthAggregation ) {
        final int nrWorkers = 5;
        final ImmutableSet<TaskWorkerId> highestQualityWorkerIds = sampleHighestQualityWorkers(
                crowdtruthAggregationAlgorithm, nrWorkers );
        FinalDefectCsvWriter.analyzeAndWrite(
                new CrowdtruthFilteredWorkersAggregation( crowdtruthAggregationAlgorithm, highestQualityWorkerIds ),
                getCsvFilenameAnalysisHighest( nrWorkers ) );

        final ImmutableSet<TaskWorkerId> lowestQualityWorkers = sampleLowestQualityWorkers(
                crowdtruthAggregationAlgorithm, nrWorkers );
        FinalDefectCsvWriter.analyzeAndWrite( new CrowdtruthFilteredWorkersAggregation( crowdtruthAggregationAlgorithm,
                lowestQualityWorkers ), getCsvFilenameAnalysisLowest( nrWorkers ) );
    }

    private static ImmutableSet<TaskWorkerId> sampleHighestQualityWorkers( final CrowdtruthAggregationAlgorithm
            crowdtruthAggregation, final int
            nrWorkers ) {
        final ImmutableSet<CrowdtruthAggregationAlgorithm.Sample> sampleHighestWorkers =
                crowdtruthAggregationAlgorithm.sampleWorkers(
                        SamplingType.HIGHEST, nrWorkers );
        final AtomicInteger counter = new AtomicInteger( 1 );
        sampleHighestWorkers.stream().sorted( ( w1, w2 ) -> Double.valueOf( w2.getQuality() ).compareTo(
                w1.getQuality() ) ).forEach( w -> FinalDefectCsvWriter.analyzeAndWrite( crowdtruthAggregationAlgorithm,
                getCsvFilenameAnalysisSingleHighest( Integer.valueOf( w.getId() ), counter.getAndIncrement() ) ) );
        return sampleHighestWorkers.stream().map( CrowdtruthAggregationAlgorithm.Sample::getId ).map(
                TaskWorkerId::new ).collect(
                ImmutableSet
                        .toImmutableSet() );
    }

    private static ImmutableSet<TaskWorkerId> sampleLowestQualityWorkers( final CrowdtruthAggregationAlgorithm
            crowdtruthAggregation,
            final int nrWorkers ) {
        final ImmutableSet<CrowdtruthAggregationAlgorithm.Sample> sampleLowestWorkers =
                crowdtruthAggregationAlgorithm.sampleWorkers(
                        SamplingType.LOWEST, nrWorkers );
        final AtomicInteger counter = new AtomicInteger( 1 );
        sampleLowestWorkers.stream().sorted( ( w1, w2 ) -> Double.valueOf( w1.getQuality() ).compareTo(
                w2.getQuality() ) ).forEach( w -> FinalDefectCsvWriter.analyzeAndWrite( crowdtruthAggregationAlgorithm,
                getCsvFilenameAnalysisSingleLowest( Integer.valueOf( w.getId() ), counter.getAndIncrement() ) ) );
        return sampleLowestWorkers.stream().map( CrowdtruthAggregationAlgorithm.Sample::getId ).map( TaskWorkerId::new )
                .collect(
                        ImmutableSet
                                .toImmutableSet() );
    }

    private static String getCsvFilenameAnalysisHighest( final int nr ) {
        return "output/crowdtruth/analysis_worker_aggregated_highest" + nr + ".csv";
    }

    private static String getCsvFilenameAnalysisLowest( final int nr ) {
        return "output/crowdtruth/analysis_worker_aggregated_lowest" + nr + ".csv";
    }

    private static String getCsvFilenameAnalysisSingleHighest( final int workerId, final int nr ) {
        return "output/crowdtruth/analysis_worker_highest" + nr + "-" + workerId + ".csv";
    }

    private static String getCsvFilenameAnalysisSingleLowest( final int workerId, final int nr ) {
        return "output/crowdtruth/analysis_worker_lowest" + nr + "-" + workerId + ".csv";
    }

    public static void writeMetrics( final CrowdtruthAlgorithm.MetricsScores metricsScores )
            throws IOException {
        Files.createDirectories( Paths.get( "output/crowdtruth" ) );

        try (CSVWriter workerQualityWriter = new CSVWriter( Files.newBufferedWriter( Paths.get(
                CROWDTRUTH_OUT_WORKER_QUALITY_CSV ) ) )) {
            workerQualityWriter.writeNext( new String[]{"workerId", "Worker Quality Score (WSQ)"} );
            metricsScores.getWorkerQualityScores().forEach( ( w, q ) -> workerQualityWriter.writeNext( new
                    String[]{w.getId(), q.toString()} ) );
        }

        try (CSVWriter annotationQualityWriter = new CSVWriter( Files.newBufferedWriter( Paths.get(
                CROWDTRUTH_OUT_ANNOTATION_QUALITY_CSV ) ) )) {
            annotationQualityWriter.writeNext( new String[]{"annotationId", "Annotation Quality Score (AQS)"} );
            metricsScores.getAnnotationQualityScores().forEach( ( w, q ) -> annotationQualityWriter.writeNext( new
                    String[]{w.getId(), q.toString()} ) );
        }

        try (CSVWriter workerQualityWriter = new CSVWriter( Files.newBufferedWriter( Paths.get(
                CROWDTRUTH_OUT_MEDIA_UNIT_QUALITY_CSV ) ) )) {
            workerQualityWriter.writeNext( new String[]{"emeId", "Media Unit Quality Score (UQS)"} );
            metricsScores.getMediaUnitQualityScores().forEach( ( w, q ) -> workerQualityWriter.writeNext( new
                    String[]{w.getId(), q.toString()} ) );
        }

        try (CSVWriter workerQualityWriter = new CSVWriter( Files.newBufferedWriter( Paths.get(
                CROWDTRUTH_OUT_MEDIA_UNIT_ANNOTATION_SCORE_CSV ) ) )) {
            workerQualityWriter.writeNext( new String[]{"emeId", "defectType", "Media Unit Quality Score (UQS)"} );
            metricsScores.getMediaUnitAnnotationScores().forEach( ( w, q ) -> workerQualityWriter.writeNext( new
                    String[]{w.getKey().getId(), w.getValue().getId(), q.toString()}
            ) );
        }
    }
}
