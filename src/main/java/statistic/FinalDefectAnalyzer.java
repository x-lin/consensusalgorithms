package statistic;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.opencsv.CSVWriter;
import model.FinalDefect;
import model.TrueDefect;
import org.jooq.lambda.UncheckedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author LinX
 */
public class FinalDefectAnalyzer {
    private FinalDefectAnalyzer() {
        //purposely left empty
    }

    private void write( final ImmutableSet<FinalDefect> defects, final String outputFilePath ) {
        try {
            final Map<String, EvaluationResult> results = getFinalDefectResults( defects );

            try (CSVWriter finalDefectsCsv = new CSVWriter( Files.newBufferedWriter( Paths.get(
                    outputFilePath ) ) )) {
                finalDefectsCsv.writeNext( new String[]{"emeId", "emeText", "agreementCoeff",
                        "finalDefectType", "trueDefectType", "trueDefectId", "isTruePositive",
                        "isTrueNegative", "isFalsePositive", "isFalseNegative"} );
                results.values().forEach( r -> finalDefectsCsv.writeNext( new
                        String[]{r.getEmeId(), r.getEmeText(), r
                        .getAgreementCoefficient(), r.getFinalDefectType().name(), r.getTrueDefectType().name(), r
                        .getTrueDefectId(), writeAsStringIfTrue( r.isTruePositive()
                ), writeAsStringIfTrue( r.isTrueNegative() ), writeAsStringIfTrue( r.isFalsePositive() ),
                        writeAsStringIfTrue( r.isFalseNegative() )} ) );
            }
        } catch (IOException | SQLException e) {
            throw new UncheckedException( e );
        }
    }

    private Map<String, EvaluationResult> getFinalDefectResults( final ImmutableSet<FinalDefect> defects ) throws
            IOException, SQLException {
        final ImmutableMap<String, TrueDefect> trueDefects = AllTrueDefectsMixin.findAllTrueDefects().stream()
                .collect( ImmutableMap.toImmutableMap( TrueDefect::getAboutEmEid, Function.identity() ) );

        final ImmutableMap<String, FinalDefect> finalDefects = defects
                .stream().collect( ImmutableMap.toImmutableMap( FinalDefect::getEmeId, Function.identity() ) );
        final Map<String, EvaluationResult> results = Maps.newHashMap();

        finalDefects.values().forEach( fd -> {
            final EvaluationResult evaluationResult = Optional.ofNullable( trueDefects.get( fd.getEmeId() ) ).map
                    ( td -> new EvaluationResult(
                            fd, td ) ).orElseGet( () -> new EvaluationResult( fd ) );
            results.put( fd.getEmeId(), evaluationResult );
        } );
        return results;
    }

    private static String writeAsStringIfTrue( final boolean value ) {
        return value ? String.valueOf( true ) : "";
    }

    public static void analyze( final ImmutableSet<FinalDefect> defects, final String outputFilePath ) {
        new FinalDefectAnalyzer().write( defects, outputFilePath );
    }

    public static ImmutableSet<EvaluationResult> getFinalDefects( final ImmutableSet<FinalDefect> defects ) throws
            IOException, SQLException {
        return ImmutableSet.copyOf( new FinalDefectAnalyzer().getFinalDefectResults( defects ).values() );
    }
}
