package algorithms.crowdtruth;

import algorithms.finaldefects.FinalDefectCsvWriter;
import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.crowdtruth.AbstractCrowdtruthAggregation.SamplingType;
import algorithms.finaldefects.crowdtruth.CrowdtruthFilteredWorkersAggregation;
import algorithms.finaldefects.crowdtruth.CrowdtruthRunner;
import algorithms.model.TaskWorkerId;
import algorithms.statistic.QualityAnalyzer;
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
        final CrowdtruthRunner crowdtruthRunner = CrowdtruthRunner.create( SemesterSettings.ws2017() );
        runFullAnalysis( crowdtruthRunner );
        runSamplingWorkers( crowdtruthRunner );
        runWorkerAnalysis( crowdtruthRunner );
    }

    private static void runFullAnalysis( final CrowdtruthRunner crowdtruthRunner ) {
        FinalDefectCsvWriter.analyzeAndWrite( crowdtruthRunner, ANALYSIS_OUT_CSV );
    }

    private static void runWorkerAnalysis( final CrowdtruthRunner crowdtruthRunner ) {
        QualityAnalyzer.create().writeConfusionMatrix( crowdtruthRunner.getSettings(),
                crowdtruthRunner.getAllWorkerScores(),
                "workerId", ANAYLSIS_ALL_WORKERS_OUT_CSV );
    }

    private static void runSamplingWorkers( final CrowdtruthRunner crowdtruthRunner ) {
        final int nrWorkers = 5;
        final ImmutableSet<TaskWorkerId> highestQualityWorkerIds = sampleHighestQualityWorkers(
                crowdtruthRunner, nrWorkers );
        FinalDefectCsvWriter.analyzeAndWrite(
                new CrowdtruthFilteredWorkersAggregation( crowdtruthRunner, highestQualityWorkerIds ),
                getCsvFilenameAnalysisHighest( nrWorkers ) );

        final ImmutableSet<TaskWorkerId> lowestQualityWorkers = sampleLowestQualityWorkers(
                crowdtruthRunner, nrWorkers );
        FinalDefectCsvWriter.analyzeAndWrite( new CrowdtruthFilteredWorkersAggregation( crowdtruthRunner,
                lowestQualityWorkers ), getCsvFilenameAnalysisLowest( nrWorkers ) );
    }

    private static ImmutableSet<TaskWorkerId> sampleHighestQualityWorkers( final CrowdtruthRunner
            crowdtruthRunner, final int
            nrWorkers ) {
        final ImmutableSet<CrowdtruthRunner.Sample> sampleHighestWorkers = crowdtruthRunner.sampleWorkers(
                SamplingType.HIGHEST, nrWorkers );
        final AtomicInteger counter = new AtomicInteger( 1 );
        sampleHighestWorkers.stream().sorted( ( w1, w2 ) -> Double.valueOf( w2.getQuality() ).compareTo(
                w1.getQuality() ) ).forEach( w -> FinalDefectCsvWriter.analyzeAndWrite( crowdtruthRunner,
                getCsvFilenameAnalysisSingleHighest( Integer.valueOf( w.getId() ), counter.getAndIncrement() ) ) );
        return sampleHighestWorkers.stream().map( CrowdtruthRunner.Sample::getId ).map( TaskWorkerId::new ).collect(
                ImmutableSet
                        .toImmutableSet() );
    }

    private static ImmutableSet<TaskWorkerId> sampleLowestQualityWorkers( final CrowdtruthRunner
            crowdtruthRunner,
            final int nrWorkers ) {
        final ImmutableSet<CrowdtruthRunner.Sample> sampleLowestWorkers = crowdtruthRunner.sampleWorkers(
                SamplingType.LOWEST, nrWorkers );
        final AtomicInteger counter = new AtomicInteger( 1 );
        sampleLowestWorkers.stream().sorted( ( w1, w2 ) -> Double.valueOf( w1.getQuality() ).compareTo(
                w2.getQuality() ) ).forEach( w -> FinalDefectCsvWriter.analyzeAndWrite( crowdtruthRunner,
                getCsvFilenameAnalysisSingleLowest( Integer.valueOf( w.getId() ), counter.getAndIncrement() ) ) );
        return sampleLowestWorkers.stream().map( CrowdtruthRunner.Sample::getId ).map( TaskWorkerId::new ).collect(
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

    public static void writeMetrics( final CrowdtruthMetrics.MetricsScores metricsScores ) throws IOException {
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
                    String[]{w.getId().getMediaUnitId().toString(), w.getId().getName().toString(), q.toString()}
            ) );
        }
    }
}
