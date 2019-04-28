package algorithms.statistic;

import algorithms.model.DefectType;
import algorithms.model.FinalDefect;
import algorithms.model.FinalDefectType;
import algorithms.model.TrueDefect;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author LinX
 */
public final class FinalDefectResult {
    private static final Logger LOG = LoggerFactory.getLogger( FinalDefectResult.class );

    private final String emeId;

    private final String scenarioId;

    private final String agreementCoefficient;

    private final FinalDefectType finalDefectType;

    private final DefectType trueDefectType;

    private final String emeText;

    private final String trueDefectId;

    private final boolean isTruePositive;

    private final boolean isFalsePositive;

    private final boolean isTrueNegative;

    private final boolean isFalseNegative;

    FinalDefectResult( final FinalDefect finalDefect, final TrueDefect trueDefect ) {
        Preconditions.checkArgument( Objects.equals( finalDefect.getEmeId(), trueDefect.getAboutEmEid() ),
                "True defect eme id %s does not match final defect eme id %s", trueDefect.getAboutEmEid()
                , finalDefect.getEmeId() );
        this.emeId = finalDefect.getEmeId().toString();
        this.scenarioId = finalDefect.getScenarioId().toString();
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

    FinalDefectResult( final FinalDefect finalDefect ) {
        this.emeId = finalDefect.getEmeId().toString();
        this.scenarioId = finalDefect.getScenarioId().toString();
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
        return this.trueDefectType.toFinalDefectType() == this.finalDefectType && this
                .finalDefectType != FinalDefectType.NO_DEFECT;
    }

    private boolean checkIsTrueNegative() {
        return this.trueDefectType == DefectType.NO_DEFECT && (this.finalDefectType == FinalDefectType.NO_DEFECT
                || this.finalDefectType == FinalDefectType.UNDECIDABLE);
    }

    private boolean checkIsFalsePositive() {
        return this.trueDefectType.toFinalDefectType() != this.finalDefectType && this
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

    public String getScenarioId() {
        return this.scenarioId;
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
