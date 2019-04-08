package algorithms.crowdtruth;

import com.google.common.collect.ImmutableSet;
import model.DefectReport;

/**
 * @author LinX
 */
public class CrowdtruthFilteredWorkersAggregation extends AbstractCrowdtruthAggregation {
    public CrowdtruthFilteredWorkersAggregation( final CrowdtruthRunner crowdtruthRunner,
            final ImmutableSet<Integer> workerIds ) {
        super( crowdtruthRunner.getSettings(), filterByWorkerIds( crowdtruthRunner, workerIds ) );
    }

    private static ImmutableSet<DefectReport> filterByWorkerIds( final CrowdtruthRunner crowdtruthRunner,
            final ImmutableSet<Integer> workerIds ) {
        return crowdtruthRunner.getDefectReports().stream()
                               .filter( r -> workerIds.contains( r.getWorkerId() ) )
                               .collect( ImmutableSet.toImmutableSet() );
    }
}
