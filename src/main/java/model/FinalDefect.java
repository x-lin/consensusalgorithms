package model;

import java.util.Objects;

/**
 * @author LinX
 */
public class FinalDefect {
    private final String emeId;

    private final String emeText;

    private final String scenarioId;


    private final Double agreementCoeff;

    private final FinalDefectType finalDefectType;

    private FinalDefect( final Builder builder ) {
        this.emeId = builder.emeId;
        this.emeText = builder.emeText;
        this.scenarioId = builder.scenarioId;
        this.agreementCoeff = builder.agreementCoeff;
        this.finalDefectType = builder.finalDefectType;
    }

    public String getEmeId() {
        return this.emeId;
    }

    public String getEmeText() {
        return this.emeText;
    }

    public String getScenarioId() {
        return this.scenarioId;
    }

    public double getAgreementCoeff() {
        return this.agreementCoeff;
    }

    public FinalDefectType getFinalDefectType() {
        return this.finalDefectType;
    }

    public static Builder builder( final Eme eme ) {
        return new Builder( eme );
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final FinalDefect that = (FinalDefect) o;
        return this.scenarioId == that.scenarioId &&
                Objects.equals( this.emeId, that.emeId ) &&
                Objects.equals( this.emeText, that.emeText ) &&
                Objects.equals( this.agreementCoeff, that.agreementCoeff ) &&
                this.finalDefectType == that.finalDefectType;
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.emeId, this.emeText, this.scenarioId, this.agreementCoeff, this.finalDefectType );
    }

    @Override
    public String toString() {
        return "FinalDefect{" +
                "emeId='" + this.emeId + '\'' +
                ", emeText='" + this.emeText + '\'' +
                ", scenarioId=" + this.scenarioId +
                ", agreementCoeff=" + this.agreementCoeff +
                ", finalDefectType=" + this.finalDefectType +
                '}';
    }

    public static class Builder {
        private final String emeId;

        private final String emeText;

        private String scenarioId;

        private double agreementCoeff;

        private FinalDefectType finalDefectType;

        private Builder( final Eme eme ) {
            this.emeId = eme.getEmeId();
            this.emeText = eme.getEmeText();
        }


        public Builder withScenarioId( final String scenarioId ) {
            this.scenarioId = scenarioId;
            return this;
        }


        public Builder withAgreementCoeff( final double agreementCoeff ) {
            this.agreementCoeff = agreementCoeff;
            return this;
        }

        public Builder withFinalDefectType( final FinalDefectType finalDefectType ) {
            this.finalDefectType = finalDefectType;
            return this;
        }

        public FinalDefect build() {
            return new FinalDefect( this );
        }
    }
}
