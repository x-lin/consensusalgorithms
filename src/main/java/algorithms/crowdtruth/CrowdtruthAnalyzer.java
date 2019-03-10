package algorithms.crowdtruth;

import algorithms.crowdtruth.CrowdtruthRunner.SamplingType;
import com.google.common.collect.ImmutableSet;
import statistic.FinalDefectAnalyzer;
import statistic.WorkerQualityAnalyzer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LinX
 */
public class CrowdtruthAnalyzer {
    private static final String ANALYSIS_OUT_CSV = "output/crowdtruth/analysis.csv";

    private static final String ANAYLSIS_ALL_WORKERS_OUT_CSV = "output/crowdtruth/analysis_all_workers.csv";

    public static void main( final String[] args ) {
        final CrowdtruthRunner crowdtruthRunner = CrowdtruthRunner.create();
        runFullAnalysis( crowdtruthRunner );
        runSamplingWorkers( crowdtruthRunner );
        runWorkerAnalysis( crowdtruthRunner );
    }


    private static void runFullAnalysis( final CrowdtruthRunner crowdtruthRunner ) {
        FinalDefectAnalyzer.analyze( crowdtruthRunner.getFinalDefects(), ANALYSIS_OUT_CSV );
    }

    private static void runWorkerAnalysis( final CrowdtruthRunner crowdtruthRunner ) {
        WorkerQualityAnalyzer.analyze( crowdtruthRunner.getAllWorkerScores(), ANAYLSIS_ALL_WORKERS_OUT_CSV );
    }

    private static void runSamplingWorkers( final CrowdtruthRunner crowdtruthRunner ) {
        final int nrWorkers = 5;
        final ImmutableSet<Integer> highestQualityWorkerIds = sampleHighestQualityWorkers(
                crowdtruthRunner, nrWorkers );
        FinalDefectAnalyzer.analyze( crowdtruthRunner.getFinalDefectsFromWorkers( highestQualityWorkerIds ),
                getCsvFilenameAnalysisHighest( nrWorkers ) );

        final ImmutableSet<Integer> lowestQualityWorkers = sampleLowestQualityWorkers(
                crowdtruthRunner, nrWorkers );
        FinalDefectAnalyzer.analyze( crowdtruthRunner.getFinalDefectsFromWorkers( lowestQualityWorkers ),
                getCsvFilenameAnalysisLowest( nrWorkers ) );
    }

    private static ImmutableSet<Integer> sampleHighestQualityWorkers( final CrowdtruthRunner
                                                                              crowdtruthRunner, final int
                                                                              nrWorkers ) {
        final ImmutableSet<CrowdtruthRunner.SampledWorker> sampleHighestWorkers = crowdtruthRunner.sampleWorkers(
                SamplingType.HIGHEST, nrWorkers );
        final AtomicInteger counter = new AtomicInteger( 1 );
        sampleHighestWorkers.stream().sorted( ( w1, w2 ) -> Double.valueOf( w2.getWorkerQuality() ).compareTo(
                w1.getWorkerQuality() ) ).forEach( w -> FinalDefectAnalyzer.analyze( w.getFinalDefects(),
                getCsvFilenameAnalysisSingleHighest( w.getWorkerId(), counter.getAndIncrement() ) ) );
        return sampleHighestWorkers.stream().map( CrowdtruthRunner.SampledWorker::getWorkerId ).collect( ImmutableSet
                .toImmutableSet() );
    }

    private static ImmutableSet<Integer> sampleLowestQualityWorkers( final CrowdtruthRunner
                                                                             crowdtruthRunner,
                                                                     final int nrWorkers ) {
        final ImmutableSet<CrowdtruthRunner.SampledWorker> sampleLowestWorkers = crowdtruthRunner.sampleWorkers(
                SamplingType.LOWEST, nrWorkers );
        final AtomicInteger counter = new AtomicInteger( 1 );
        sampleLowestWorkers.stream().sorted( ( w1, w2 ) -> Double.valueOf( w1.getWorkerQuality() ).compareTo(
                w2.getWorkerQuality() ) ).forEach( w -> FinalDefectAnalyzer.analyze( w.getFinalDefects(),
                getCsvFilenameAnalysisSingleLowest( w.getWorkerId(), counter.getAndIncrement() ) ) );
        return sampleLowestWorkers.stream().map( CrowdtruthRunner.SampledWorker::getWorkerId ).collect( ImmutableSet
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
