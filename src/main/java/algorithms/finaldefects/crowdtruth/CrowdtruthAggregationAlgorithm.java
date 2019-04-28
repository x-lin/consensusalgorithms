package algorithms.finaldefects.crowdtruth;

import algorithms.finaldefects.SemesterSettings;
import algorithms.model.DefectReport;

/**
 * @author LinX
 */
public class CrowdtruthAggregationAlgorithm extends AbstractCrowdtruthAggregation {
    private CrowdtruthAggregationAlgorithm( final SemesterSettings semesterSettings ) {
        super( semesterSettings, DefectReport.fetchDefectReports( semesterSettings ) );
    }

    public static CrowdtruthAggregationAlgorithm create( final SemesterSettings settings ) {
        return new CrowdtruthAggregationAlgorithm( settings );
    }
}
