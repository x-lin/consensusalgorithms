package statistic;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.opencsv.CSVWriter;
import model.DefectType;
import model.FinalDefect;
import model.FinalDefectType;
import model.TrueDefect;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author LinX
 */
public class FinalDefectAnalyzer {
    private FinalDefectAnalyzer() {
        //purposely left empty
    }

    public void write( final ImmutableSet<FinalDefect> defects, final String outputFilePath ) throws IOException,
            SQLException {
        final ImmutableMap<String, TrueDefect> trueDefects = AllTrueDefectsMixin.findAllTrueDefects().stream()
                .collect( ImmutableMap.toImmutableMap( TrueDefect::getAboutEmEid, Function.identity() ) );
        final ImmutableMap<String, FinalDefect> finalDefects = defects
                .stream().collect( ImmutableMap.toImmutableMap( FinalDefect::getEmeId, Function.identity() ) );
        final Map<String, EvaluationResult> results = Maps.newHashMap();

        finalDefects.values().forEach( fd -> {
            final EvaluationResult evaluationResult = Optional.ofNullable( trueDefects.get( fd.getEmeId() ) ).map( td
                    -> new EvaluationResult(
                    fd, td ) ).orElseGet( () -> new EvaluationResult( fd ) );
            results.put( fd.getEmeId(), evaluationResult );
        } );

        try (CSVWriter finalDefectsCsv = new CSVWriter( Files.newBufferedWriter( Paths.get(
                outputFilePath ) ) )) {
            finalDefectsCsv.writeNext( new String[]{"emeId", "emeText", "agreementCoeff",
                    "finalDefectType", "trueDefectType", "trueDefectId", "isMatching"} );
            results.values().forEach( r -> finalDefectsCsv.writeNext( new
                    String[]{r.getEmeId(), r.getEmeText(), r
                    .getAgreementCoefficient(), r.getFinalDefectType().name(), r.getTrueDefectType().name(), r
                    .getTrueDefectId(), String.valueOf( r.isMatching() )} ) );
        }
    }

    public static void analyze( final ImmutableSet<FinalDefect> defects, final String outputFilePath ) throws
            IOException, SQLException {
        new FinalDefectAnalyzer().write( defects, outputFilePath );
    }

    private static class EvaluationResult {
        private final String emeId;

        private final String agreementCoefficient;

        private final FinalDefectType finalDefectType;

        private final DefectType trueDefectType;

        private final String emeText;

        private final String trueDefectId;

        private final boolean matching;

        private EvaluationResult( final FinalDefect finalDefect, final TrueDefect trueDefect ) {
            Preconditions.checkArgument( Objects.equals( finalDefect.getEmeId(), trueDefect.getAboutEmEid() ),
                    "True defect eme id %s does not match final defect eme id %s", trueDefect.getAboutEmEid()
                    , finalDefect.getEmeId() );
            if (!Objects.equals( finalDefect.getScenarioId(), trueDefect.getScenario() )) {
                System.err.println( String.format( "True defect scenario id %s does not match final defect scenario " +
                                "id %s for eme %s", trueDefect.getScenario()
                        , finalDefect.getScenarioId(), finalDefect.getEmeId() ) );
            }
            this.emeId = finalDefect.getEmeId();
            this.emeText = finalDefect.getEmeText();
            this.agreementCoefficient = String.valueOf( finalDefect.getAgreementCoeff() );
            this.finalDefectType = finalDefect.getFinalDefectType();
            this.trueDefectType = trueDefect.getDefectType();
            this.trueDefectId = trueDefect.getCodeTd();
            this.matching = checkIsMatching();
        }

        private EvaluationResult( final FinalDefect finalDefect ) {
            this.emeId = finalDefect.getEmeId();
            this.agreementCoefficient = String.valueOf( finalDefect.getAgreementCoeff() );
            this.finalDefectType = finalDefect.getFinalDefectType();
            this.trueDefectType = DefectType.NO_DEFECT;
            this.emeText = finalDefect.getEmeText();
            this.trueDefectId = "NA";
            this.matching = checkIsMatching();
        }

        private boolean checkIsMatching() {
            return FinalDefectType.fromDefectType( this.trueDefectType ) == this.finalDefectType;
        }

        public String getEmeId() {
            return this.emeId;
        }

        public String getAgreementCoefficient() {
            return this.agreementCoefficient;
        }

        public FinalDefectType getFinalDefectType() {
            return this.finalDefectType;
        }

        public DefectType getTrueDefectType() {
            return this.trueDefectType;
        }

        public String getEmeText() {
            return this.emeText;
        }

        public String getTrueDefectId() {
            return this.trueDefectId;
        }

        public boolean isMatching() {
            return this.matching;
        }
    }
}
