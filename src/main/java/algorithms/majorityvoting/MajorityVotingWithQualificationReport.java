package algorithms.majorityvoting;

import algorithms.crowdtruth.WorkerId;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import model.QualificationReport;
import web.Semester;
import web.SemesterSettings;

import java.util.Optional;

/**
 * @author LinX
 */
public class MajorityVotingWithQualificationReport extends MajorityVotingRunner {
    private MajorityVotingWithQualificationReport( final SemesterSettings settings,
            final WorkerQualityInfluence influence, final double alpha ) {
        super( settings, wid -> {
            final ImmutableMap<WorkerId, QualificationReport> qualificationReports =
                    QualificationReport.fetchQualificationReports();
            final WorkerQuality averageWorkerQuality = getAverageWorkerQuality( influence, alpha,
                    qualificationReports );
            return Optional.ofNullable( qualificationReports.get( wid ) ).map(
                    r -> influence.calculateWorkerQuality( r.getNrResults(), r.getNrFalseResults(), alpha ) ).orElse(
                    averageWorkerQuality );
        } );
    }

    private static WorkerQuality getAverageWorkerQuality( final WorkerQualityInfluence influence, final double alpha,
            final ImmutableMap<WorkerId, QualificationReport> qualificationReports ) {
        final double averageNrResults = qualificationReports.values().stream().map( QualificationReport::getNrResults )
                                                            .count() / (double) qualificationReports.size();
        final double averageFalseResults = qualificationReports.values().stream().map(
                QualificationReport::getNrFalseResults ).count() / (double) qualificationReports.size();
        return influence.calculateWorkerQuality( averageNrResults, averageFalseResults, alpha );
    }

    public static MajorityVotingWithQualificationReport create( final SemesterSettings settings,
            final WorkerQualityInfluence influence, final double alpha ) {
        Preconditions.checkArgument( settings.getSemester() == Semester.SS2018,
                "Qualification reports only available for data from SS18." );
        return new MajorityVotingWithQualificationReport( settings, influence, alpha );
    }
}
