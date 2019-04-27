package algorithms.finaldefects;

import algorithms.model.EmeAndScenarioId;
import algorithms.statistic.FinalDefectAnalyzer;
import algorithms.statistic.FinalDefectResult;
import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author LinX
 */
public class FinalDefectCsvWriter {
    public void write( final Map<EmeAndScenarioId, FinalDefectResult> finalDefectsResults,
            final String outputFilePath ) {
        try {
            try (CSVWriter finalDefectsCsv = new CSVWriter( Files.newBufferedWriter( Paths.get(
                    outputFilePath ) ) )) {
                finalDefectsCsv.writeNext( new String[]{"emeId", "emeText", "scenarioId", "agreementCoeff",
                        "finalDefectType", "trueDefectType", "trueDefectId", "isTruePositive",
                        "isTrueNegative", "isFalsePositive", "isFalseNegative"} );
                finalDefectsResults.values().forEach( r -> finalDefectsCsv.writeNext(
                        new String[]{r.getEmeId(), r.getEmeText(), r.getScenarioId(), r.getAgreementCoefficient(),
                                r.getFinalDefectType().name(), r.getTrueDefectType().name(), r.getTrueDefectId(),
                                writeAsStringIfTrue( r.isTruePositive() ), writeAsStringIfTrue( r.isTrueNegative() ),
                                writeAsStringIfTrue( r.isFalsePositive() ),
                                writeAsStringIfTrue( r.isFalseNegative() )} ) );
            }
        } catch (final IOException e) {
            throw new UncheckedIOException( e );
        }
    }

    private static String writeAsStringIfTrue( final boolean value ) {
        return value ? String.valueOf( true ) : "";
    }

    public static void analyzeAndWrite( final FinalDefectAggregationAlgorithm algorithm, final String outputFilePath ) {
        new FinalDefectCsvWriter().write( new FinalDefectAnalyzer( algorithm ).getFinalDefectResults(),
                outputFilePath );
    }
}
