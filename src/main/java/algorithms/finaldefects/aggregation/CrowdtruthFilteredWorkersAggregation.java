package algorithms.finaldefects.aggregation;

import algorithms.vericom.model.DefectReport;
import algorithms.vericom.model.DefectReports;
import algorithms.vericom.model.TaskWorkerId;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author LinX
 */
public class CrowdtruthFilteredWorkersAggregation extends AbstractCrowdtruthAggregation {
    public CrowdtruthFilteredWorkersAggregation( final CrowdtruthAggregation crowdtruthAggregation,
            final ImmutableSet<TaskWorkerId> workerIds ) {
        super( crowdtruthAggregation.getSettings(),
                new DefectReports( filterByWorkerIds( crowdtruthAggregation, workerIds ) ) );
    }

    private static ImmutableSet<DefectReport> filterByWorkerIds(
            final CrowdtruthAggregation crowdtruthAggregation,
            final ImmutableSet<TaskWorkerId> workerIds ) {
        return crowdtruthAggregation.getDefectReports().stream().filter( r -> workerIds.contains( r.getWorkerId() ) )
                .collect( ImmutableSet.toImmutableSet() );
    }

    @Override
    public ImmutableMap<String, String> getParameters() {
        return ImmutableMap.of();
    }
}
