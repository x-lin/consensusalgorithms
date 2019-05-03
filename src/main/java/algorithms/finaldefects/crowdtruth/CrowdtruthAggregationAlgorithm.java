package algorithms.finaldefects.crowdtruth;

import algorithms.finaldefects.SemesterSettings;
import algorithms.model.DefectReports;
import com.google.common.collect.ImmutableMap;

/**
 * @author LinX
 */
public class CrowdtruthAggregationAlgorithm extends AbstractCrowdtruthAggregation {
    private CrowdtruthAggregationAlgorithm( final SemesterSettings semesterSettings ) {
        super( semesterSettings, DefectReports.fetchFromDb( semesterSettings ) );
    }

    public static CrowdtruthAggregationAlgorithm create( final SemesterSettings settings ) {
        return new CrowdtruthAggregationAlgorithm( settings );
    }

    @Override
    public ImmutableMap<String, String> getParameters() {
        return ImmutableMap.of();
    }
}
