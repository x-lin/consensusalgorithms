package algorithms.crowdtruth;

import com.google.common.collect.ImmutableSet;
import statistic.FinalDefectAnalyzer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LinX
 */
public class CrowdtruthAnalyzer {
    private static final String ANALYSIS_OUT_CSV = "output/crowdtruth/analysis.csv";

    public static void main( final String[] args ) {
        final CrowdtruthRunner crowdtruthRunner = CrowdtruthRunner.create();
        FinalDefectAnalyzer.analyze( crowdtruthRunner.getFinalDefects(), ANALYSIS_OUT_CSV );

        final int nrWorkers = 5;
        final ImmutableSet<Integer> highestQualityWorkerIds = sampleHighestQualityWorkers(
                crowdtruthRunner, nrWorkers );
        FinalDefectAnalyzer.analyze( crowdtruthRunner.getFinalDefectsFromWorkers( highestQualityWorkerIds ),
                getAnalysisHighestOutCsv( nrWorkers ) );

        final ImmutableSet<Integer> lowestQualityWorkers = sampleLowestQualityWorkers(
                crowdtruthRunner, nrWorkers );
        FinalDefectAnalyzer.analyze( crowdtruthRunner.getFinalDefectsFromWorkers( lowestQualityWorkers ),
                getAnalysisLowestOutCsv( nrWorkers ) );
    }

    private static ImmutableSet<Integer> sampleHighestQualityWorkers( final CrowdtruthRunner
                                                                              crowdtruthRunner, final int
                                                                              nrWorkers ) {
        final ImmutableSet<CrowdtruthRunner.SampledWorker> sampleHighestWorkers = crowdtruthRunner.sampleWorkers(
                CrowdtruthRunner.SamplingType.HIGHEST, nrWorkers );
        final AtomicInteger counter = new AtomicInteger( 1 );
        sampleHighestWorkers.stream().sorted( ( w1, w2 ) -> Double.valueOf( w2.getWorkerQuality() ).compareTo(
                w1.getWorkerQuality() ) ).forEach( w -> FinalDefectAnalyzer.analyze( w.getFinalDefects(),
                getAnalysisSingleHighestOutCsv( w.getWorkerId(), counter.getAndIncrement() ) ) );
        return sampleHighestWorkers.stream().map( CrowdtruthRunner.SampledWorker::getWorkerId ).collect( ImmutableSet
                .toImmutableSet() );
    }

    private static ImmutableSet<Integer> sampleLowestQualityWorkers( final CrowdtruthRunner
                                                                             crowdtruthRunner,
                                                                     final int nrWorkers ) {
        final ImmutableSet<CrowdtruthRunner.SampledWorker> sampleLowestWorkers = crowdtruthRunner.sampleWorkers(
                CrowdtruthRunner.SamplingType.LOWEST, nrWorkers );
        final AtomicInteger counter = new AtomicInteger( 1 );
        sampleLowestWorkers.stream().sorted( ( w1, w2 ) -> Double.valueOf( w1.getWorkerQuality() ).compareTo(
                w2.getWorkerQuality() ) ).forEach( w -> FinalDefectAnalyzer.analyze( w.getFinalDefects(),
                getAnalysisSingleLowestOutCsv( w.getWorkerId(), counter.getAndIncrement() ) ) );
        return sampleLowestWorkers.stream().map( CrowdtruthRunner.SampledWorker::getWorkerId ).collect( ImmutableSet
                .toImmutableSet() );
    }

    private static String getAnalysisHighestOutCsv( final int nr ) {
        return "output/crowdtruth/analysis_worker_aggregated_highest" + nr + ".csv";
    }

    private static String getAnalysisLowestOutCsv( final int nr ) {
        return "output/crowdtruth/analysis_worker_aggregated_lowest" + nr + ".csv";
    }

    private static String getAnalysisSingleHighestOutCsv( final int workerId, final int nr ) {
        return "output/crowdtruth/analysis_worker_highest" + nr + "-" + workerId + ".csv";
    }

    private static String getAnalysisSingleLowestOutCsv( final int workerId, final int nr ) {
        return "output/crowdtruth/analysis_worker_lowest" + nr + "-" + workerId + ".csv";
    }
}
