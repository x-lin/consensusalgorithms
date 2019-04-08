package algorithms.crowdtruth;

import algorithms.crowdtruth.AbstractCrowdtruthAggregation.SamplingType;
import com.google.common.collect.ImmutableSet;
import statistic.FinalDefectAnalyzer;
import statistic.QualityAnalyzer;
import web.SemesterSettings;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LinX
 */
public class CrowdtruthAnalyzer {
    private static final String ANALYSIS_OUT_CSV = "output/crowdtruth/analysis.csv";

    private static final String ANAYLSIS_ALL_WORKERS_OUT_CSV = "output/crowdtruth/analysis_all_workers.csv";

    public static void main( final String[] args ) {
        final CrowdtruthRunner crowdtruthRunner = CrowdtruthRunner.create( SemesterSettings.ws2017() );
        runFullAnalysis( crowdtruthRunner );
        runSamplingWorkers( crowdtruthRunner );
        runWorkerAnalysis( crowdtruthRunner );
    }

    private static void runFullAnalysis( final CrowdtruthRunner crowdtruthRunner ) {
        FinalDefectAnalyzer.analyze( crowdtruthRunner, ANALYSIS_OUT_CSV );
    }

    private static void runWorkerAnalysis( final CrowdtruthRunner crowdtruthRunner ) {
        QualityAnalyzer.analyze( crowdtruthRunner.getSettings(), crowdtruthRunner.getAllWorkerScores(),
                "workerId", ANAYLSIS_ALL_WORKERS_OUT_CSV );
    }

    private static void runSamplingWorkers( final CrowdtruthRunner crowdtruthRunner ) {
        final int nrWorkers = 5;
        final ImmutableSet<Integer> highestQualityWorkerIds = sampleHighestQualityWorkers(
                crowdtruthRunner, nrWorkers );
        FinalDefectAnalyzer.analyze(
                new CrowdtruthFilteredWorkersAggregation( crowdtruthRunner, highestQualityWorkerIds ),
                getCsvFilenameAnalysisHighest( nrWorkers ) );

        final ImmutableSet<Integer> lowestQualityWorkers = sampleLowestQualityWorkers(
                crowdtruthRunner, nrWorkers );
        FinalDefectAnalyzer.analyze( new CrowdtruthFilteredWorkersAggregation( crowdtruthRunner,
                lowestQualityWorkers ), getCsvFilenameAnalysisLowest( nrWorkers ) );
    }

    private static ImmutableSet<Integer> sampleHighestQualityWorkers( final CrowdtruthRunner
            crowdtruthRunner, final int
            nrWorkers ) {
        final ImmutableSet<CrowdtruthRunner.Sample> sampleHighestWorkers = crowdtruthRunner.sampleWorkers(
                SamplingType.HIGHEST, nrWorkers );
        final AtomicInteger counter = new AtomicInteger( 1 );
        sampleHighestWorkers.stream().sorted( ( w1, w2 ) -> Double.valueOf( w2.getQuality() ).compareTo(
                w1.getQuality() ) ).forEach( w -> FinalDefectAnalyzer.analyze( crowdtruthRunner,
                getCsvFilenameAnalysisSingleHighest( Integer.valueOf( w.getId() ), counter.getAndIncrement() ) ) );
        return sampleHighestWorkers.stream().map( CrowdtruthRunner.Sample::getId ).map( Integer::valueOf ).collect(
                ImmutableSet
                        .toImmutableSet() );
    }

    private static ImmutableSet<Integer> sampleLowestQualityWorkers( final CrowdtruthRunner
            crowdtruthRunner,
            final int nrWorkers ) {
        final ImmutableSet<CrowdtruthRunner.Sample> sampleLowestWorkers = crowdtruthRunner.sampleWorkers(
                SamplingType.LOWEST, nrWorkers );
        final AtomicInteger counter = new AtomicInteger( 1 );
        sampleLowestWorkers.stream().sorted( ( w1, w2 ) -> Double.valueOf( w1.getQuality() ).compareTo(
                w2.getQuality() ) ).forEach( w -> FinalDefectAnalyzer.analyze( crowdtruthRunner,
                getCsvFilenameAnalysisSingleLowest( Integer.valueOf( w.getId() ), counter.getAndIncrement() ) ) );
        return sampleLowestWorkers.stream().map( CrowdtruthRunner.Sample::getId ).map( Integer::valueOf ).collect(
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
}
