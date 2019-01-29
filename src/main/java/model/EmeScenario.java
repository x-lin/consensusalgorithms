package model;

import org.jooq.Record;

import java.util.Objects;

/**
 * @author LinX
 */
public class EmeScenario {
    public static String EME_SCENARIO_TABLE = "eme_scenario";

    public static String SCENARIOS_ID_COLUMN = "scenarios_id";

    public static String EMES_ID_COLUMN = "emes_id";

    private final int scenariosId;

    private final int emesId;

    public EmeScenario(Record record) {
        this.scenariosId = record.getValue(SCENARIOS_ID_COLUMN, Integer.class);
        this.emesId = record.getValue(EMES_ID_COLUMN, Integer.class);
    }

    public int getScenariosId() {
        return this.scenariosId;
    }

    public int getEmesId() {
        return this.emesId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmeScenario that = (EmeScenario) o;
        return this.scenariosId == that.scenariosId &&
                this.emesId == that.emesId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.scenariosId, this.emesId);
    }

    @Override
    public String toString() {
        return "EmeScenario{" +
                "scenariosId=" + this.scenariosId +
                ", emesId=" + this.emesId +
                '}';
    }
}
