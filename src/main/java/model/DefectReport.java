package model;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    private final String scenarioId;

    public DefectReport( final Record record ) {
        this.id = record.getValue( ID_COLUMN, Integer.class );
        this.defectReportCode = record.getValue( DEFECT_REPORT_CODE_COLUMN, String.class );
        this.workshopCode = record.getValue( WORKSHOP_CODE_COLUMN, String.class );
        this.taskId = record.getValue( TASK_ID_COLUMN, Integer.class );
        this.workerId = record.getValue( WORKER_ID_COLUMN, Integer.class );
        this.emeId = record.getValue( EME_ID_COLUMN, String.class );
        this.defectType = DefectType.fromString( record.getValue( DEFECT_TYPE_COLUMN, String.class ) );
        this.defectDescription = record.getValue( DEFECT_DESCRIPTION_COLUMN, String.class );
        this.synDefectDescription = record.getValue( SYN_DEFECT_DESCRIPTION_COLUMN, String.class );
        this.synLabel = record.getValue( SYN_LABEL_COLUMN, String.class );
        this.taskInstanceId = record.getValue( TASK_INSTANCE_ID_COLUMN, Integer.class );
        this.workshopId = record.getValue( WORKSHOP_ID_COLUMN, Integer.class );
        this.scenarioId = record.getValue( SCENARIO_ID_COLUMN, String.class );
    }

    private DefectReport( final Builder builder ) {
        this.id = builder.id;
        this.defectReportCode = builder.defectReportCode;
        this.workshopCode = builder.workshopCode;
        this.taskId = builder.taskId;
        this.workerId = builder.workerId;
        this.emeId = builder.emeId;
        this.defectType = builder.defectType;
        this.defectDescription = builder.defectDescription;
        this.synDefectDescription = builder.synDefectDescription;
        this.synLabel = builder.synLabel;
        this.taskInstanceId = builder.taskInstanceId;
        this.workshopId = builder.workshopId;
        this.scenarioId = builder.scenarioId;
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

    public String getScenarioId() {
        return this.scenarioId;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DefectReport that = (DefectReport) o;
        return this.id == that.id &&
                this.taskId == that.taskId &&
                this.workerId == that.workerId &&
                Objects.equals( this.taskInstanceId, that.taskInstanceId ) &&
                Objects.equals( this.workshopId, that.workshopId ) &&
                Objects.equals( this.defectReportCode, that.defectReportCode ) &&
                Objects.equals( this.workshopCode, that.workshopCode ) &&
                Objects.equals( this.emeId, that.emeId ) &&
                Objects.equals( this.defectType, that.defectType ) &&
                Objects.equals( this.defectDescription, that.defectDescription ) &&
                Objects.equals( this.synDefectDescription, that.synDefectDescription ) &&
                Objects.equals( this.synLabel, that.synLabel );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.id, this.defectReportCode, this.workshopCode, this.taskId, this.workerId, this
                .emeId, this.defectType, this.defectDescription, this.synDefectDescription, this.synLabel, this
                .taskInstanceId, this.workshopId );
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

    public static ImmutableSet<DefectReport> fetchDefectReports( final Connection connection, final
    Predicate<DefectReport> filter ) {
        final String sql = "select * from " + DEFECT_REPORT_TABLE;
        final ImmutableSet<DefectReport> defectReports = DSL.using( connection )
                .fetch( sql )
                .map( DefectReport::new ).stream().filter( filter ).filter
                        ( e -> !Objects.equals( e.getEmeId
                                (), "" ) )
                .collect( ImmutableSet.toImmutableSet() );
        final Map<Integer, List<DefectReport>> defectsByWorker = defectReports.stream().collect( Collectors
                .groupingBy( DefectReport::getWorkerId ) );
        return defectsByWorker.values().stream().flatMap( r -> {
            final Map<String, DefectReport> emeAndTaskIds = Maps.newHashMap();
            r.forEach( d -> emeAndTaskIds.compute( d.emeId, ( k, v ) -> {
                if (v == null) {
                    return d;
                } else {
                    return v.getTaskId() < d.getTaskId() ? d : v;
                }
            } ) );
            return emeAndTaskIds.values().stream();
        } ).collect( ImmutableSet.toImmutableSet() );
    }

    public static Predicate<DefectReport> workshopFilter( final String... workshops ) {
        final Set<String> filteredWorkshops = ImmutableSet.copyOf( workshops );
        return d -> filteredWorkshops.contains( d.getWorkshopCode() );
    }

    public static Builder builder( final int id ) {
        return new Builder( id );
    }

    public static class Builder {
        private final int id;

        private String defectReportCode;

        private String workshopCode;

        private int taskId;

        private int workerId;

        private String emeId;

        private DefectType defectType;

        private String defectDescription;

        private String synDefectDescription;

        private String synLabel;

        private Integer taskInstanceId;

        private Integer workshopId;

        private String scenarioId;

        private Builder( final int id ) {
            this.id = id;
        }

        public Builder withDefectReportCode( final String defectReportCode ) {
            this.defectReportCode = defectReportCode;
            return this;
        }

        public Builder withWorkshopCode( final String workshopCode ) {
            this.workshopCode = workshopCode;
            return this;
        }

        public Builder withTaskId( final int taskId ) {
            this.taskId = taskId;
            return this;
        }

        public Builder withWorkerId( final int workerId ) {
            this.workerId = workerId;
            return this;
        }

        public Builder withEmeId( final String emeId ) {
            this.emeId = emeId;
            return this;
        }

        public Builder withDefectType( final DefectType defectType ) {
            this.defectType = defectType;
            return this;
        }

        public Builder withDefectDescription( final String defectDescription ) {
            this.defectDescription = defectDescription;
            return this;
        }

        public Builder withSynDefectDescription( final String synDefectDescription ) {
            this.synDefectDescription = synDefectDescription;
            return this;
        }

        public Builder withSynLabel( final String synLabel ) {
            this.synLabel = synLabel;
            return this;
        }

        public Builder withTaskInstanceId( final Integer taskInstanceId ) {
            this.taskInstanceId = taskInstanceId;
            return this;
        }

        public Builder withWorkshopId( final Integer workshopId ) {
            this.workshopId = workshopId;
            return this;
        }

        public Builder withScenarioId( final String scenarioId ) {
            this.scenarioId = scenarioId;
            return this;
        }

        public DefectReport build() {
            return new DefectReport( this );
        }
    }
}
