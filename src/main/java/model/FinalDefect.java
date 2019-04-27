package model;

import algorithms.finaldefects.SemesterSettings;
import com.google.common.collect.ImmutableSet;
import org.jooq.Record;
import org.jooq.impl.DSL;
import utils.UncheckedSQLException;

import java.sql.Connection;
import java.sql.SQLException;
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

    private final EmeId emeId;

    private final String emeText;

    private final ScenarioId scenarioId;

    private final AgreementCoefficient agreementCoeff;

    private final FinalDefectType finalDefectType;

    public FinalDefect( final Record record ) {
        this.emeId = new EmeId( record.getValue( EME_ID_COLUMN, String.class ) );
        this.emeText = record.getValue( EME_TEXT_COLUMN, String.class );
        this.scenarioId = new ScenarioId( record.getValue( SCENARIO_ID, String.class ) );
        this.agreementCoeff = new AgreementCoefficient( record.getValue( AGREEMENT_COEFF_COLUMN, Double.class ) );
        this.finalDefectType = record.getValue( FINAL_DEFECT_TYPE_COLUMN, FinalDefectType.class );
    }

    private FinalDefect( final Builder builder ) {
        this.emeId = builder.emeId;
        this.emeText = builder.emeText;
        this.scenarioId = builder.scenarioId;
        this.agreementCoeff = builder.agreementCoeff;
        this.finalDefectType = builder.finalDefectType;
    }

    public EmeId getEmeId() {
        return this.emeId;
    }

    public String getEmeText() {
        return this.emeText;
    }

    public ScenarioId getScenarioId() {
        return this.scenarioId;
    }

    public AgreementCoefficient getAgreementCoeff() {
        return this.agreementCoeff;
    }

    public EmeAndScenarioId getEmeAndScenarioId() {
        return new EmeAndScenarioId( this.emeId, this.scenarioId );
    }

    public FinalDefectType getFinalDefectType() {
        return this.finalDefectType;
    }

    public static Builder builder( final Emes emes, final EmeAndScenarioId emeAndScenarioId ) {
        return new Builder( emes.get( emeAndScenarioId.getEmeId() ), emeAndScenarioId.getScenarioId() );
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FinalDefect that = (FinalDefect) o;
        return Objects.equals( this.scenarioId, that.scenarioId ) &&
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

    public static ImmutableSet<FinalDefect> fetchFinalDefects( final SemesterSettings settings ) {
        try (Connection connection = DatabaseConnector.createConnection()) {
            final String sql = "select * from " + FINAL_DEFECT_TABLE + " where filter_code='" + settings
                    .getFinalDefectFilterCode() + "'";
            return DSL.using( connection )
                      .fetch( sql )
                      .map( FinalDefect::new ).stream().collect( ImmutableSet.toImmutableSet() );
        } catch (final SQLException e) {
            throw new UncheckedSQLException( e );
        }
    }

    public static class Builder {
        private final EmeId emeId;

        private final String emeText;

        private final ScenarioId scenarioId;

        private AgreementCoefficient agreementCoeff = AgreementCoefficient.ZERO;

        private FinalDefectType finalDefectType;

        private Builder( final Eme eme, final ScenarioId scenarioId ) {
            this.emeId = eme.getEmeId();
            this.emeText = eme.getEmeText();
            this.scenarioId = scenarioId;
        }

        public Builder withAgreementCoeff( final AgreementCoefficient agreementCoeff ) {
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

        public AgreementCoefficient getAgreementCoeff() {
            return this.agreementCoeff;
        }

        public FinalDefect build() {
            return new FinalDefect( this );
        }
    }
}
