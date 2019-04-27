package algorithms.finaldefects.crowdtruth;

import algorithms.finaldefects.SemesterSettings;
import algorithms.model.DefectReport;

/**
 * @author LinX
 */
public class CrowdtruthRunner extends AbstractCrowdtruthAggregation {
    private CrowdtruthRunner( final SemesterSettings semesterSettings ) {
        super( semesterSettings, DefectReport.fetchDefectReports( semesterSettings.getDefectReportFilter() ) );
    }

    public static CrowdtruthRunner create( final SemesterSettings settings ) {
        return new CrowdtruthRunner( settings );
    }
}
