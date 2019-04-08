package algorithms.majorityvoting;

import algorithms.AggregationAlgorithm;
import algorithms.crowdtruth.WorkerId;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import model.*;
import utils.UncheckedSQLException;
import web.SemesterSettings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author LinX
 */
public class MajorityVotingRunner implements AggregationAlgorithm {
    private final ImmutableSet<FinalDefect> finalDefects;

    private final SemesterSettings settings;

    protected MajorityVotingRunner( final SemesterSettings settings,
            final Function<WorkerId, WorkerQuality> workerQuality ) {
        this.settings = settings;
        this.finalDefects = getFinalDefects( this.settings, workerQuality );
    }

    @Override
    public ImmutableSet<FinalDefect> getFinalDefects() {
        return this.finalDefects;
    }

    @Override
    public SemesterSettings getSettings() {
        return this.settings;
    }

    private static ImmutableSet<FinalDefect> getFinalDefects( final SemesterSettings settings,
            final Function<WorkerId, WorkerQuality> workerQuality ) {
        try (Connection connection = DatabaseConnector.createConnection()) {
            //calculate based on all defect reports
            final ImmutableSet<Eme> emes = Eme.fetchEmes( connection, settings );
            final ImmutableSet<DefectReport> defectReportsFiltered = DefectReport.fetchDefectReports( connection,
                    settings.getDefectReportFilter() );

            //compare with DB table for correctness
//            verifySameResults( finalDefectsFiltered, FinalDefect.fetchFinalDefects( connection, settings ) );

            return new MajorityVotingAggregator( emes, defectReportsFiltered, workerQuality ).aggregate();
        } catch (final SQLException e) {
            throw new UncheckedSQLException( e );
        }
    }

    private static void verifySameResults( final ImmutableSet<FinalDefect> calculatedFinalDefects, final
    ImmutableSet<FinalDefect> dbFinalDefects ) {
        final Map<String, FinalDefect> calculatedUnmatched = calculatedFinalDefects.stream().collect( Collectors.toMap(
                FinalDefect::getEmeId, d -> d ) );

        dbFinalDefects.forEach( db -> {
            final FinalDefect calculated = Optional.ofNullable( calculatedUnmatched.remove( db.getEmeId() ) )
                                                   .orElseThrow( () -> new
                                                           NoSuchElementException(
                                                           "No defect with eme " + db.getEmeId() +
                                                                   " found in calculated " +
                                                                   "final " +
                                                                   "defects." ) );
            Preconditions.checkArgument( calculated.getAgreementCoeff() == db.getAgreementCoeff(), "Agreement " +
                    "coefficient for eme " + db.getEmeId() + " doesn't match. Expected '%s', but was '%s'.", db
                    .getAgreementCoeff(), calculated.getAgreementCoeff() );
            Preconditions.checkArgument( calculated.getFinalDefectType().equals( db.getFinalDefectType() ), "Final " +
                    "defect type for eme " + db.getEmeId() + " doesn't match. Expected '%s', but was '%s'.", db
                    .getFinalDefectType(), calculated.getFinalDefectType() );
            Preconditions.checkArgument( calculated.getScenarioId().equals( db.getScenarioId() ), "Scenario id for " +
                            "eme " + db.getEmeId() + " doesn't match. Expected '%s', but was '%s'.", db.getScenarioId(),
                    calculated.getScenarioId() );
        } );

        final ImmutableSet<FinalDefect> calculatedUnmatchedWithDefect = calculatedUnmatched.values().stream().filter(
                c -> c
                        .getFinalDefectType() != FinalDefectType.NO_DEFECT ).collect( ImmutableSet.toImmutableSet() );

        Preconditions.checkArgument( calculatedUnmatchedWithDefect.isEmpty(), "%s final defects calculated but not in" +
                        " DB: %s",
                calculatedUnmatchedWithDefect.size(), calculatedUnmatchedWithDefect );
    }

    public static MajorityVotingRunner create( final SemesterSettings settings ) {
        return new MajorityVotingRunner( settings, MajorityVotingAggregator.PERFECT_WORKER_QUALITY );
    }

    public static MajorityVotingRunner create(
            final SemesterSettings settings, final Function<WorkerId, WorkerQuality> workerQualityFunction ) {
        return new MajorityVotingRunner( settings, workerQualityFunction );
    }
}