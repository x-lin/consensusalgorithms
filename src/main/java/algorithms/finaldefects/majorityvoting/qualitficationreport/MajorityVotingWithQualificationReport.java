package algorithms.finaldefects.majorityvoting.qualitficationreport;

import algorithms.finaldefects.*;
import algorithms.finaldefects.aggregation.MajorityVotingAlgorithm;
import algorithms.vericom.model.EmeAndScenarioId;
import algorithms.vericom.model.FinalDefect;
import algorithms.vericom.model.TaskWorkerId;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author LinX
 */
public class MajorityVotingWithQualificationReport implements FinalDefectAggregationAlgorithm {
    private final MajorityVotingAlgorithm majorityVoting;

    private final WorkerQualityInfluence influence;

    private final double alpha;

    private MajorityVotingWithQualificationReport( final SemesterSettings settings,
            final WorkerQualityInfluence influence, final double alpha ) {
        this.influence = influence;
        this.alpha = alpha;
        this.majorityVoting = MajorityVotingAlgorithm.create( settings, wid -> {
            final ImmutableMap<TaskWorkerId, QualificationReport> qualificationReports =
                    QualificationReport.QUALIFICATION_REPORTS;
            //TODO remove comment
//            final WorkerQuality averageWorkerQuality = getAverageWorkerQuality( influence, alpha,
//                    qualificationReports );
            return Optional.ofNullable( qualificationReports.get( wid ) ).map(
                    r -> influence.calculateWorkerQuality( r.getNrResults(), r.getNrFalseResults(), alpha ) )
                    .orElseThrow(
                            () -> new NoSuchElementException( "Unknown qualifaction test worker " + wid ) );
        } );
    }

    @Override
    public ImmutableMap<EmeAndScenarioId, FinalDefect> getFinalDefects() {
        return this.majorityVoting.getFinalDefects();
    }

    @Override
    public ImmutableMap<TaskWorkerId, WorkerDefectReports> getWorkerDefectReports() {
        return this.majorityVoting.getWorkerDefectReports();
    }

    @Override
    public SemesterSettings getSettings() {
        return this.majorityVoting.getSettings();
    }

    @Override
    public ImmutableMap<String, String> getParameters() {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder().put(
                "workerQualityInfluence", this.influence.name() );
        if (this.influence == WorkerQualityInfluence.EXPONENTIAL) {
            builder.put( "alpha", String.valueOf( this.alpha ) );
        }
        return builder.build();
    }

    private static WorkerQuality getAverageWorkerQuality( final WorkerQualityInfluence influence, final double alpha,
            final ImmutableMap<TaskWorkerId, QualificationReport> qualificationReports ) {
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
