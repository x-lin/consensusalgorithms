package statistic;

import com.google.common.base.Preconditions;
import model.DefectType;
import model.FinalDefect;
import model.FinalDefectType;
import model.TrueDefect;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author LinX
 */
public final class EvaluationResult {
    private final String emeId;

    private final String agreementCoefficient;

    private final FinalDefectType finalDefectType;

    private final DefectType trueDefectType;

    private final String emeText;

    private final String trueDefectId;

    private final boolean isTruePositive;

    private final boolean isFalsePositive;

    private final boolean isTrueNegative;

    private final boolean isFalseNegative;

    EvaluationResult( final FinalDefect finalDefect, final TrueDefect trueDefect ) {
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
        this.isTruePositive = checkIsTruePositive();
        this.isTrueNegative = checkIsTrueNegative();
        this.isFalseNegative = checkIsFalseNegative();
        this.isFalsePositive = checkIsFalsePositive();
        checkAssignedToOneCategory();
    }

    EvaluationResult( final FinalDefect finalDefect ) {
        this.emeId = finalDefect.getEmeId();
        this.agreementCoefficient = String.valueOf( finalDefect.getAgreementCoeff() );
        this.finalDefectType = finalDefect.getFinalDefectType();
        this.trueDefectType = DefectType.NO_DEFECT;
        this.emeText = finalDefect.getEmeText();
        this.trueDefectId = "NA";
        this.isTruePositive = checkIsTruePositive();
        this.isTrueNegative = checkIsTrueNegative();
        this.isFalseNegative = checkIsFalseNegative();
        this.isFalsePositive = checkIsFalsePositive();
        checkAssignedToOneCategory();
    }

    private void checkAssignedToOneCategory() {
        Preconditions.checkArgument( Stream.of( this.isTrueNegative, this.isTruePositive, this.isFalseNegative, this
                .isFalsePositive ).filter( v -> v ).count() == 1, "Data must be exactly one of TP, TN, FP, FN" );
    }

    private boolean checkIsTruePositive() {
        return FinalDefectType.fromDefectType( this.trueDefectType ) == this.finalDefectType && this
                .finalDefectType != FinalDefectType.NO_DEFECT;
    }

    private boolean checkIsTrueNegative() {
        return this.trueDefectType == DefectType.NO_DEFECT && (this.finalDefectType == FinalDefectType.NO_DEFECT
                || this.finalDefectType == FinalDefectType.UNDECIDABLE);
    }

    private boolean checkIsFalsePositive() {
        return FinalDefectType.fromDefectType( this.trueDefectType ) != this.finalDefectType && this
                .finalDefectType != FinalDefectType.NO_DEFECT && this.finalDefectType != FinalDefectType.UNDECIDABLE;
    }

    private boolean checkIsFalseNegative() {
        return this.trueDefectType != DefectType
                .NO_DEFECT && (this.finalDefectType == FinalDefectType.NO_DEFECT
                || this.finalDefectType == FinalDefectType.UNDECIDABLE);
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

    public boolean isTruePositive() {
        return this.isTruePositive;
    }

    public boolean isFalsePositive() {
        return this.isFalsePositive;
    }

    public boolean isTrueNegative() {
        return this.isTrueNegative;
    }

    public boolean isFalseNegative() {
        return this.isFalseNegative;
    }
}
