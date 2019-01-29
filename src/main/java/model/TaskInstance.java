package model;

import org.jooq.Record;

import java.util.Objects;

/**
 * @author LinX
 */
public class TaskInstance {
    public static String TASK_INSTANCE_TABLE = "task_instance";

    public static String ID_COLUMN = "id";

    public static String TASK_INSTANCE_ID_COLUMN = "task_instance_id";

    public static String TASK_ID_COLUMN = "task_id";

    public static String JHI_START_COLUMN = "jhi_start";

    public static String JHI_END_COLUMN = "jhi_end";

    private final int id;

    private final int taskInstanceId;

    private final int taskId;

    private final String jhiStart; //TODO use Instant

    private final String jhiEnd; //TODO use Instance

    public TaskInstance(Record record) {
        this.id = record.getValue(ID_COLUMN, Integer.class);
        this.taskInstanceId = record.getValue(TASK_INSTANCE_ID_COLUMN, Integer.class);
        this.taskId = record.getValue(TASK_ID_COLUMN, Integer.class);
        this.jhiStart = record.getValue(JHI_START_COLUMN, String.class);
        this.jhiEnd = record.getValue(JHI_END_COLUMN, String.class);
    }

    public int getId() {
        return this.id;
    }

    public int getTaskInstanceId() {
        return this.taskInstanceId;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public String getJhiStart() {
        return this.jhiStart;
    }

    public String getJhiEnd() {
        return this.jhiEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskInstance that = (TaskInstance) o;
        return this.id == that.id &&
                this.taskInstanceId == that.taskInstanceId &&
                this.taskId == that.taskId &&
                Objects.equals(this.jhiStart, that.jhiStart) &&
                Objects.equals(this.jhiEnd, that.jhiEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.taskInstanceId, this.taskId, this.jhiStart, this.jhiEnd);
    }

    @Override
    public String toString() {
        return "TaskInstance{" +
                "id=" + this.id +
                ", taskInstanceId=" + this.taskInstanceId +
                ", taskId=" + this.taskId +
                ", jhiStart='" + this.jhiStart + '\'' +
                ", jhiEnd='" + this.jhiEnd + '\'' +
                '}';
    }
}
