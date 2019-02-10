package algorithms.majorityvoting;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.opencsv.CSVWriter;
import model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LinX
 */
public class MajorityVotingRunner {
    private static final String FINAL_DEFECT_ALL_OUT_CSV = "output/majorityvoting/finalDefects.csv";

    private static final String FINAL_DEFECT_REMOVE_NULL_TASK_INSTANCES_OUT_CSV =
            "output/majorityvoting/finalDefects_noNullTaskInstances.csv";

    private final ImmutableSet<FinalDefect> finalDefects;

    private MajorityVotingRunner() throws IOException, SQLException {
        this.finalDefects = getFinalDefects();
    }

    public static ImmutableSet<FinalDefect> calculateFinalDefects() throws IOException, SQLException {
        return new MajorityVotingRunner().finalDefects;
    }

    private static ImmutableSet<FinalDefect> getFinalDefects() throws IOException, SQLException {
        Files.createDirectories( Paths.get( "output/majorityvoting" ) );

        try (Connection connection = DatabaseConnector.createConnection()) {
            //calculate based on all defect reports
            final ImmutableSet<DefectReport> defectReports = DefectReport.fetchDefectReports( connection );
            final ImmutableSet<Eme> emes = Eme.fetchEmes( connection );
            final ImmutableSet<FinalDefect> finalDefects = new MajorityVotingAggregator( emes, defectReports )
                    .aggregate();
            try (CSVWriter finalDefectsCsv = new CSVWriter( Files.newBufferedWriter( Paths.get(
                    FINAL_DEFECT_ALL_OUT_CSV ) ) )) {
                finalDefectsCsv.writeNext( new String[]{"emeId", "emeText", "scenarioId", "agreementCoeff",
                        "finalDefectType"} );
                finalDefects.forEach( d -> finalDefectsCsv.writeNext( new
                        String[]{d.getEmeId(), d.getEmeText(), d.getScenarioId(), String.valueOf( d.getAgreementCoeff
                        () ), d.getFinalDefectType().name()} ) );
            }

            //calculate based on all defect reports that have a task instance
            final Set<String> filteredWorkshops = ImmutableSet.of( "WS1", "WS2", "WS3", "WS4" );
            final ImmutableSet<DefectReport> defectReportsFiltered = DefectReport.fetchDefectReports( connection )
                    .stream().filter
                            ( d -> filteredWorkshops.contains( d.getWorkshopCode() ) ).collect( ImmutableSet
                            .toImmutableSet() );
            final ImmutableSet<FinalDefect> finalDefectsFiltered = new MajorityVotingAggregator( emes,
                    defectReportsFiltered )
                    .aggregate();
            try (CSVWriter finalDefectsCsv = new CSVWriter( Files.newBufferedWriter( Paths.get(
                    FINAL_DEFECT_REMOVE_NULL_TASK_INSTANCES_OUT_CSV ) ) )) {
                finalDefectsCsv.writeNext( new String[]{"emeId", "emeText", "scenarioId", "agreementCoeff",
                        "finalDefectType"} );
                finalDefectsFiltered.forEach( d -> finalDefectsCsv.writeNext( new
                        String[]{d.getEmeId(), d.getEmeText(), d.getScenarioId(), String.valueOf( d.getAgreementCoeff
                        () ), d.getFinalDefectType().name()} ) );
            }

            //compare with DB table for correctness
            verifySameResults( finalDefectsFiltered, FinalDefect.fetchFinalDefects( connection ) );

            return finalDefectsFiltered;
        }
    }

    private static void verifySameResults( final ImmutableSet<FinalDefect> calculatedFinalDefects, final
    ImmutableSet<FinalDefect> dbFinalDefects ) {
        final Map<String, FinalDefect> calculatedUnmatched = calculatedFinalDefects.stream().collect( Collectors.toMap(
                FinalDefect::getEmeId, d -> d ) );

        dbFinalDefects.forEach( db -> {
            final FinalDefect calculated = Optional.ofNullable( calculatedUnmatched.remove( db.getEmeId() ) )
                    .orElseThrow( () -> new
                            NoSuchElementException( "No defect with eme " + db.getEmeId() + " found in calculated " +
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

}