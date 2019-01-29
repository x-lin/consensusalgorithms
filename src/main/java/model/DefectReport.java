package model;


import org.jooq.Record;

import java.util.Objects;

/**
 * @author LinX
 */
public class DefectReport {
    public static final String DEFECT_REPORT_TABLE = "defect_report";

    public static final String ID_COLUMN = "id";

    public static String DEFECT_REPORT_CODE_COLUMN = "defect_report_code";

    public static String WORKSHOP_CODE_COLUMN = "workshop_code";

    public static String TASK_ID_COLUMN = "task_id";

    public static String WORKER_ID_COLUMN = "worker_id";

    public static String EME_ID_COLUMN = "eme_id";

    public static String SCENARIO_ID_COLUMN = "scenario_id";

    public static String DEFECT_TYPE_COLUMN = "defect_type";

    public static String DEFECT_DESCRIPTION_COLUMN = "defect_description";

    public static String SYN_DEFECT_DESCRIPTION_COLUMN = "syn_defect_description";

    public static String SYN_LABEL_COLUMN = "syn_label";

    public static String TASK_INSTANCE_ID_COLUMN = "task_instance_id";

    public static String WORKSHOP_ID_COLUMN = "workshop_id";

    private final int id;

    private final String defectReportCode;

    private final String workshopCode;

    private final int taskId;

    private final int workerId;

    private final String emeId;

    private final DefectType defectType;

    private final String defectDescription;

    private final String synDefectDescription;

    private final String synLabel;

    private final Integer taskInstanceId;

    private final Integer workshopId;

    private final Integer scenarioId;

    public DefectReport(Record record) {
        this.id = record.getValue(ID_COLUMN, Integer.class);
        this.defectReportCode = record.getValue(DEFECT_REPORT_CODE_COLUMN, String.class);
        this.workshopCode = record.getValue(WORKSHOP_CODE_COLUMN, String.class);
        this.taskId = record.getValue(TASK_ID_COLUMN, Integer.class);
        this.workerId = record.getValue(WORKER_ID_COLUMN, Integer.class);
        this.emeId = record.getValue(EME_ID_COLUMN, String.class);
        this.defectType = DefectType.fromString(record.getValue(DEFECT_TYPE_COLUMN, String.class));
        this.defectDescription = record.getValue(DEFECT_DESCRIPTION_COLUMN, String.class);
        this.synDefectDescription = record.getValue(SYN_DEFECT_DESCRIPTION_COLUMN, String.class);
        this.synLabel = record.getValue(SYN_LABEL_COLUMN, String.class);
        this.taskInstanceId = record.getValue(TASK_INSTANCE_ID_COLUMN, Integer.class);
        this.workshopId = record.getValue(WORKSHOP_ID_COLUMN, Integer.class);
        this.scenarioId = record.getValue(SCENARIO_ID_COLUMN, Integer.class);
    }

    public int getId() {
        return this.id;
    }

    public String getDefectReportCode() {
        return this.defectReportCode;
    }

    public String getWorkshopCode() {
        return this.workshopCode;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public int getWorkerId() {
        return this.workerId;
    }

    public String getEmeId() {
        return this.emeId;
    }

    public DefectType getDefectType() {
        return this.defectType;
    }

    public String getDefectDescription() {
        return this.defectDescription;
    }

    public String getSynDefectDescription() {
        return this.synDefectDescription;
    }

    public String getSynLabel() {
        return this.synLabel;
    }

    public Integer getTaskInstanceId() {
        return this.taskInstanceId;
    }

    public Integer getWorkshopId() {
        return this.workshopId;
    }

    public Integer getScenarioId() {
        return this.scenarioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefectReport that = (DefectReport) o;
        return this.id == that.id &&
                this.taskId == that.taskId &&
                this.workerId == that.workerId &&
                Objects.equals(this.taskInstanceId, that.taskInstanceId) &&
                Objects.equals(this.workshopId, that.workshopId) &&
                Objects.equals(this.defectReportCode, that.defectReportCode) &&
                Objects.equals(this.workshopCode, that.workshopCode) &&
                Objects.equals(this.emeId, that.emeId) &&
                Objects.equals(this.defectType, that.defectType) &&
                Objects.equals(this.defectDescription, that.defectDescription) &&
                Objects.equals(this.synDefectDescription, that.synDefectDescription) &&
                Objects.equals(this.synLabel, that.synLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.defectReportCode, this.workshopCode, this.taskId, this.workerId, this
                .emeId, this.defectType, this.defectDescription, this.synDefectDescription, this.synLabel, this
                .taskInstanceId, this.workshopId);
    }

    @Override
    public String toString() {
        return "DefectReport{" +
                "id=" + this.id +
                ", defectReportCode='" + this.defectReportCode + '\'' +
                ", workshopCode='" + this.workshopCode + '\'' +
                ", taskId=" + this.taskId +
                ", workerId=" + this.workerId +
                ", emeId='" + this.emeId + '\'' +
                ", defectType='" + this.defectType + '\'' +
                ", defectDescription='" + this.defectDescription + '\'' +
                ", synDefectDescription='" + this.synDefectDescription + '\'' +
                ", synLabel='" + this.synLabel + '\'' +
                ", taskInstanceId=" + this.taskInstanceId +
                ", workshopId=" + this.workshopId +
                '}';
    }
}
