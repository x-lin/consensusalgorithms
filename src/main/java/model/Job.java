package model;

import org.jooq.Record;

import java.util.Objects;

/**
 * @author LinX
 */
public class Job {
    public static String JOB_TABLE = "job";

    public static String ID_COLUMN = "id";

    public static String JOB_ID_COLUMN = "job_id";

    public static String JOB_CODE_COLUMN = "job_code";

    public static String JOB_TITLE_COLUMN = "job_title";

    public static String JOB_TYPE_COLUMN = "job_type";

    public static String EXPERIMENT_ID = "experiment_id";

    private final int id;

    private final int jobId;

    private final String jobCode;

    private final String jobType;

    private final int experimentId;

    private final String jobTitle;

    public Job(Record record) {
        this.id = record.getValue(ID_COLUMN, Integer.class);
        this.jobId = record.getValue(JOB_ID_COLUMN, Integer.class);
        this.jobCode = record.getValue(JOB_CODE_COLUMN, String.class);
        this.jobType = record.getValue(JOB_TYPE_COLUMN, String.class);
        this.experimentId = record.getValue(EXPERIMENT_ID, Integer.class);
        this.jobTitle = record.getValue(JOB_TITLE_COLUMN, String.class);
    }

    public int getId() {
        return this.id;
    }

    public int getJobId() {
        return this.jobId;
    }

    public String getJobCode() {
        return this.jobCode;
    }

    public String getJobTitle() {
        return this.jobTitle;
    }

    public String getJobType() {
        return this.jobType;
    }

    public int getExperimentId() {
        return this.experimentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return this.id == job.id &&
                this.jobId == job.jobId &&
                this.experimentId == job.experimentId &&
                Objects.equals(this.jobCode, job.jobCode) &&
                Objects.equals(this.jobTitle, job.jobTitle) &&
                Objects.equals(this.jobType, job.jobType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.jobId, this.jobCode, this.jobTitle, this.jobType, this.experimentId);
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + this.id +
                ", jobId=" + this.jobId +
                ", jobCode='" + this.jobCode + '\'' +
                ", jobTitle='" + this.jobTitle + '\'' +
                ", jobType='" + this.jobType + '\'' +
                ", experimentId=" + this.experimentId +
                '}';
    }
}
