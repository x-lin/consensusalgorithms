package model;

import com.google.common.collect.ImmutableSet;
import org.jooq.Record;
import org.jooq.impl.DSL;
import web.SemesterSettings;

import java.sql.Connection;
import java.util.Objects;

/**
 * @author LinX
 */
public class FinalDefect {
    public static final String FINAL_DEFECT_TABLE = "final_defect";

    public static final String EME_ID_COLUMN = "eme_id";

    public static final String EME_TEXT_COLUMN = "eme_text";

    public static final String SCENARIO_ID = "scenario_id";

    public static final String AGREEMENT_COEFF_COLUMN = "agreement_coeff";

    public static final String FINAL_DEFECT_TYPE_COLUMN = "final_defect_type";

    private final String emeId;

    private final String emeText;

    private final String scenarioId;

    private final Double agreementCoeff;

    private final FinalDefectType finalDefectType;

    public FinalDefect( final Record record ) {
        this.emeId = record.getValue( EME_ID_COLUMN, String.class );
        this.emeText = record.getValue( EME_TEXT_COLUMN, String.class );
        this.scenarioId = record.getValue( SCENARIO_ID, String.class );
        this.agreementCoeff = record.getValue( AGREEMENT_COEFF_COLUMN, Double.class );
        this.finalDefectType = record.getValue( FINAL_DEFECT_TYPE_COLUMN, FinalDefectType.class );
    }

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


    public static ImmutableSet<FinalDefect> fetchFinalDefects( final Connection connection, final SemesterSettings
            settings ) {
        final String sql = "select * from " + FINAL_DEFECT_TABLE + " where filter_code='" + settings
                .getFinalDefectFilterCode() + "'";
        return DSL.using( connection )
                .fetch( sql )
                .map( FinalDefect::new ).stream().collect( ImmutableSet.toImmutableSet() );
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

        public FinalDefectType getFinalDefectType() {
            return this.finalDefectType;
        }

        public double getAgreementCoeff() {
            return this.agreementCoeff;
        }

        public FinalDefect build() {
            return new FinalDefect( this );
        }
    }
}
