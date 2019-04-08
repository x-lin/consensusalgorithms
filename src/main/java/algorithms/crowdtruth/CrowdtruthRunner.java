package algorithms.crowdtruth;

import com.google.common.collect.ImmutableSet;
import model.DatabaseConnector;
import model.DefectReport;
import utils.UncheckedSQLException;
import web.SemesterSettings;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author LinX
 */
public class CrowdtruthRunner extends AbstractCrowdtruthAggregation {
    private CrowdtruthRunner( final SemesterSettings semesterSettings ) {
        super( semesterSettings, fetchDefectReports( semesterSettings ) );
    }

    private static ImmutableSet<DefectReport> fetchDefectReports( final SemesterSettings semesterSettings ) {
        try (Connection c = DatabaseConnector.createConnection()) {
            return DefectReport.fetchDefectReports( c, semesterSettings.getDefectReportFilter() );
        } catch (final SQLException e) {
            throw new UncheckedSQLException( e );
        }
    }

    public static CrowdtruthRunner create( final SemesterSettings settings ) {
        return new CrowdtruthRunner( settings );
    }
}
