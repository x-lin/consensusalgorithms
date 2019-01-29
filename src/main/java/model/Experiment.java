package model;

import org.jooq.Record;

import java.util.Objects;

/**
 * @author LinX
 */
public class Experiment {
    public static String EXPERIMENT_TABLE = "experiment";

    public static String ID_COLUMN = "id";

    public static String CODE_COLUMN = "code";

    public static String NAME_COLUMN = "name";

    public static String EXPERIMENT_DATE = "experiment_date";

    private final int id;

    private final String code;

    private final String name;

    private final String experimentDate;

    public Experiment(Record record) {
        this.id = record.getValue(ID_COLUMN, Integer.class);
        this.code = record.getValue(CODE_COLUMN, String.class);
        this.name = record.getValue(NAME_COLUMN, String.class);
        this.experimentDate = record.getValue(EXPERIMENT_DATE, String.class);
    }

    public int getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getExperimentDate() {
        return this.experimentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Experiment that = (Experiment) o;
        return this.id == that.id &&
                Objects.equals(this.code, that.code) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.experimentDate, that.experimentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.code, this.name, this.experimentDate);
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "id=" + this.id +
                ", code='" + this.code + '\'' +
                ", name='" + this.name + '\'' +
                ", experimentDate='" + this.experimentDate + '\'' +
                '}';
    }
}
