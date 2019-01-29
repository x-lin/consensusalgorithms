package model;

import org.jooq.Record;

import java.util.Objects;

/**
 * @author LinX
 */
public class Scenario {
    public static String SCENARIO_TABLE = "scenario";

    public static String ID_COLUMN = "id";

    public static String SCENARIO_ID_COLUMN = "scenario_id";

    public static String SCENARIO_NAME_COLUMN = "scenario_name";

    public static String SCENARIO_TEXT_COLUMN = "scenario_text";

    private final int id;

    private final String scenarioId;

    private final String scenarioName;

    private final String scenarioText;

    public Scenario(Record record) {
        this.id = record.getValue(ID_COLUMN, Integer.class);
        this.scenarioId = record.getValue(SCENARIO_ID_COLUMN, String.class);
        this.scenarioName = record.getValue(SCENARIO_NAME_COLUMN, String.class);
        this.scenarioText = record.getValue(SCENARIO_TEXT_COLUMN, String.class);
    }

    public int getId() {
        return this.id;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scenario scenario = (Scenario) o;
        return this.id == scenario.id &&
                Objects.equals(this.scenarioId, scenario.scenarioId) &&
                Objects.equals(this.scenarioName, scenario.scenarioName) &&
                Objects.equals(this.scenarioText, scenario.scenarioText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.scenarioId, this.scenarioName, this.scenarioText);
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "id=" + this.id +
                ", scenarioId='" + this.scenarioId + '\'' +
                ", scenarioName='" + this.scenarioName + '\'' +
                ", scenarioText='" + this.scenarioText + '\'' +
                '}';
    }
}
