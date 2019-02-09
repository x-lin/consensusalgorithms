package model;

import org.jooq.Record;

import java.util.Objects;

/**
 * @author LinX
 */
public class Scenario {
    public static String SCENARIO_TABLE = "scenario";

    public static String SCENARIO_ID_COLUMN = "scenario_id";

    public static String SCENARIO_NAME_COLUMN = "scenario_name";

    public static String SCENARIO_TEXT_COLUMN = "scenario_text";

    private final String scenarioId;

    private final String scenarioName;

    private final String scenarioText;

    public Scenario( final Record record ) {
        this.scenarioId = record.getValue( SCENARIO_ID_COLUMN, String.class );
        this.scenarioName = record.getValue( SCENARIO_NAME_COLUMN, String.class );
        this.scenarioText = record.getValue( SCENARIO_TEXT_COLUMN, String.class );
    }

    private Scenario( final Builder builder ) {
        this.scenarioId = builder.scenarioId;
        this.scenarioName = builder.scenarioName;
        this.scenarioText = builder.scenarioText;
    }

    public String getScenarioId() {
        return this.scenarioId;
    }

    public String getScenarioName() {
        return this.scenarioName;
    }

    public String getScenarioText() {
        return this.scenarioText;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Scenario scenario = (Scenario) o;
        return
                Objects.equals( this.scenarioId, scenario.scenarioId ) &&
                        Objects.equals( this.scenarioName, scenario.scenarioName ) &&
                        Objects.equals( this.scenarioText, scenario.scenarioText );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.scenarioId, this.scenarioName, this.scenarioText );
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "scenarioId='" + this.scenarioId + '\'' +
                ", scenarioName='" + this.scenarioName + '\'' +
                ", scenarioText='" + this.scenarioText + '\'' +
                '}';
    }

    public static Builder builder( final String scenarioId ) {
        return new Builder( scenarioId );
    }

    public static class Builder {
        private final String scenarioId;

        private String scenarioName;

        private String scenarioText;

        private Builder( final String scenarioId ) {
            this.scenarioId = scenarioId;
        }

        public Builder withScenarioName( final String scenarioName ) {
            this.scenarioName = scenarioName;
            return this;
        }

        public Builder withScenarioText( final String scenarioText ) {
            this.scenarioText = scenarioText;
            return this;
        }

        public Scenario build() {
            return new Scenario( this );
        }
    }
}
