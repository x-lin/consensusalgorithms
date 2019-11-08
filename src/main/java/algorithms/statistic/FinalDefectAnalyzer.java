package algorithms.statistic;

import algorithms.finaldefects.aggregation.FinalDefectAggregationAlgorithm;
import algorithms.vericom.model.EmeAndScenarioId;
import algorithms.vericom.model.EmeId;
import algorithms.vericom.model.FinalDefect;
import algorithms.vericom.model.TrueDefect;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import java.util.Optional;

/**
 * @author LinX
 */
public class FinalDefectAnalyzer {
    private final FinalDefectAggregationAlgorithm algorithm;

    public FinalDefectAnalyzer( final FinalDefectAggregationAlgorithm algorithm ) {
        this.algorithm = algorithm;
    }

    public ImmutableBiMap<EmeAndScenarioId, FinalDefectResult> getFinalDefectResults() {
        final ImmutableMap<EmeId, TrueDefect> trueDefects = AllTrueDefectsMixin.findAllTrueDefects(
                this.algorithm.getSettings() );

        final ImmutableMap<EmeAndScenarioId, FinalDefect> finalDefects = this.algorithm.getFinalDefects();
        final ImmutableBiMap.Builder<EmeAndScenarioId, FinalDefectResult> results = ImmutableBiMap.builder();

        finalDefects.values().forEach( fd -> {
            final FinalDefectResult finalDefectResult = Optional.ofNullable( trueDefects.get( fd.getEmeId() ) ).map
                    ( td -> new FinalDefectResult(
                            fd, td ) ).orElseGet( () -> new FinalDefectResult( fd ) );
            results.put( fd.getEmeAndScenarioId(), finalDefectResult );
        } );
        return results.build();
    }

    public static ImmutableBiMap<EmeAndScenarioId, FinalDefectResult> getFinalDefects(
            final FinalDefectAggregationAlgorithm algorithm ) {
        return new FinalDefectAnalyzer( algorithm ).getFinalDefectResults();
    }
}
