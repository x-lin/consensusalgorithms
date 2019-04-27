package algorithms.finaldefects;

import com.google.common.collect.ImmutableSet;
import model.DefectReport;
import model.TaskWorkerId;

/**
 * @author LinX
 */
public class CrowdtruthFilteredWorkersAggregation extends AbstractCrowdtruthAggregation {
    public CrowdtruthFilteredWorkersAggregation( final CrowdtruthRunner crowdtruthRunner,
            final ImmutableSet<TaskWorkerId> workerIds ) {
        super( crowdtruthRunner.getSettings(), filterByWorkerIds( crowdtruthRunner, workerIds ) );
    }

    private static ImmutableSet<DefectReport> filterByWorkerIds( final CrowdtruthRunner crowdtruthRunner,
            final ImmutableSet<TaskWorkerId> workerIds ) {
        return crowdtruthRunner.getDefectReports().stream()
                               .filter( r -> workerIds.contains( r.getWorkerId() ) )
                               .collect( ImmutableSet.toImmutableSet() );
    }
}
