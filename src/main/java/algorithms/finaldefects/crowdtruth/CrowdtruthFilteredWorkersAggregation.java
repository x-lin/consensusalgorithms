package algorithms.finaldefects.crowdtruth;

import algorithms.model.DefectReport;
import algorithms.model.DefectReports;
import algorithms.model.TaskWorkerId;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author LinX
 */
public class CrowdtruthFilteredWorkersAggregation extends AbstractCrowdtruthAggregation {
    public CrowdtruthFilteredWorkersAggregation( final CrowdtruthAggregationAlgorithm crowdtruthAggregationAlgorithm,
            final ImmutableSet<TaskWorkerId> workerIds ) {
        super( crowdtruthAggregationAlgorithm.getSettings(),
                new DefectReports( filterByWorkerIds( crowdtruthAggregationAlgorithm, workerIds ) ) );
    }

    private static ImmutableSet<DefectReport> filterByWorkerIds(
            final CrowdtruthAggregationAlgorithm crowdtruthAggregationAlgorithm,
            final ImmutableSet<TaskWorkerId> workerIds ) {
        return crowdtruthAggregationAlgorithm.getDefectReports().stream()
                                             .filter( r -> workerIds.contains( r.getWorkerId() ) )
                                             .collect( ImmutableSet.toImmutableSet() );
    }

    @Override
    public ImmutableMap<String, String> getParameters() {
        return ImmutableMap.of();
    }
}
