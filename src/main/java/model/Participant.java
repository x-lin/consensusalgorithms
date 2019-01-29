package model;

import org.jooq.Record;

import java.util.Objects;

/**
 * @author LinX
 */
public class Participant {
    public static final String PARTICIPANT_TABLE = "participant";

    public static final String NAME_COLUMN = "name";

    public static final String WORKER_ID_COLUMN = "worker_id";

    public static final String WORKSHOP_ID_COLUMN = "workshop_id";

    private final String name;

    private final String workerId;

    private final int workshopId;

    public Participant(Record record) {
        this.name = record.getValue(NAME_COLUMN, String.class);
        this.workerId = record.getValue(WORKER_ID_COLUMN, String.class);
        this.workshopId = record.getValue(WORKSHOP_ID_COLUMN, Integer.class);
    }

    public String getName() {
        return this.name;
    }

    public String getWorkerId() {
        return this.workerId;
    }

    public int getWorkshopId() {
        return this.workshopId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(this.workerId, that.workerId) &&
                this.workshopId == that.workshopId &&
                Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.workerId, this.workshopId);
    }

    @Override
    public String toString() {
        return "Participant{" +
                "name='" + this.name + '\'' +
                ", workerId=" + this.workerId +
                ", workshopId=" + this.workshopId +
                '}';
    }
}
