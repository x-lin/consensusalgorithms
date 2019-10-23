package algorithms.model;

import algorithms.finaldefects.SemesterSettings;
import algorithms.utils.UncheckedSQLException;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author LinX
 */
public class DefectReport {
    private static final Map<SemesterSettings, ImmutableSet<DefectReport>> CACHED_DEFECT_REPORTS =
            Maps.newConcurrentMap();

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

    private final TaskWorkerId workerId;

    private final EmeId emeId;

    private final DefectType defectType;

    private final String defectDescription;

    private final String synDefectDescription;

    private final String synLabel;

    private final Integer taskInstanceId;

    private final Integer workshopId;

    private final ScenarioId scenarioId;

    public DefectReport( final Record record ) {
        this.id = record.getValue( ID_COLUMN, Integer.class );
        this.defectReportCode = record.getValue( DEFECT_REPORT_CODE_COLUMN, String.class );
        this.workshopCode = record.getValue( WORKSHOP_CODE_COLUMN, String.class );
        this.taskId = record.getValue( TASK_ID_COLUMN, Integer.class );
        this.workerId = new TaskWorkerId( record.getValue( WORKER_ID_COLUMN, Integer.class ) );
        this.emeId = new EmeId( record.getValue( EME_ID_COLUMN, String.class ) );
        this.defectType = DefectType.fromString( record.getValue( DEFECT_TYPE_COLUMN, String.class ) );
        this.defectDescription = record.getValue( DEFECT_DESCRIPTION_COLUMN, String.class );
        this.synDefectDescription = record.getValue( SYN_DEFECT_DESCRIPTION_COLUMN, String.class );
        this.synLabel = record.getValue( SYN_LABEL_COLUMN, String.class );
        this.taskInstanceId = record.getValue( TASK_INSTANCE_ID_COLUMN, Integer.class );
        this.workshopId = record.getValue( WORKSHOP_ID_COLUMN, Integer.class );
        this.scenarioId = new ScenarioId( record.getValue( SCENARIO_ID_COLUMN, String.class ) );
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

    public TaskWorkerId getWorkerId() {
        return this.workerId;
    }

    public EmeId getEmeId() {
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

    public ScenarioId getScenarioId() {
        return this.scenarioId;
    }

    public EmeAndScenarioId getEmeAndScenarioId() {
        return new EmeAndScenarioId( this.emeId, this.scenarioId );
    }

    public FinalDefect toFinalDefect( final Emes emes ) {
        return FinalDefect.builder( emes, getEmeAndScenarioId() )
                          .withAgreementCoeff( new AgreementCoefficient( 1.0 ) )
                          .withFinalDefectType( getDefectType().toFinalDefectType() )
                          .build();
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
                .taskInstanceId, this.workshopId, this.scenarioId );
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
                ", scenarioId='" + this.scenarioId + '\'' +
                ", defectType='" + this.defectType + '\'' +
                ", defectDescription='" + this.defectDescription + '\'' +
                ", synDefectDescription='" + this.synDefectDescription + '\'' +
                ", synLabel='" + this.synLabel + '\'' +
                ", taskInstanceId=" + this.taskInstanceId +
                ", workshopId=" + this.workshopId +
                '}';
    }

    public DefectReport replaceScenarioId( final ScenarioId id ) {
        return DefectReport.builder( this.id ).withDefectType( this.defectType ).withEmeId( this.emeId )
                           .withDefectDescription( this.defectDescription ).withTaskId( this.taskId )
                           .withTaskInstanceId( this.taskInstanceId ).withWorkerId( this.workerId ).withWorkshopCode(
                        this.workshopCode ).withWorkshopId( this.workshopId ).withEmeId( this.emeId ).withScenarioId(
                        id )
                           .build();
    }

    public static ImmutableSet<DefectReport> fetchDefectReports( final SemesterSettings semesterSettings ) {
        return CACHED_DEFECT_REPORTS.computeIfAbsent( semesterSettings,
                s -> readDefectReports( s.getDefectReportFilter() ) );
    }

    private static ImmutableSet<DefectReport> readDefectReports( final Predicate<DefectReport> filter ) {
        try (Connection connection = DatabaseConnector.createConnection()) {
            final String sql = "select * from " + DEFECT_REPORT_TABLE;
            final ImmutableSet<DefectReport> defectReports = DSL.using( connection )
                                                                .fetch( sql )
                                                                .map( DefectReport::new ).stream().filter( filter )
                                                                .filter
                                                                        ( e -> !Objects
                                                                                .equals( e.getEmeId(), EmeId.EMPTY ) )
                                                                .collect( ImmutableSet.toImmutableSet() );
            final ImmutableSet<DefectReport> additionalDefectReportsFromCsv = getDefectReportsFromCsv();

            final Map<TaskWorkerId, List<DefectReport>> defectsByWorker = defectReports.stream().collect( Collectors
                    .groupingBy( DefectReport::getWorkerId ) );
            return defectsByWorker.values().stream().flatMap( Collection::stream ).map(
                    r -> r.getScenarioId().toString() == null ?
                            r.replaceScenarioId( getScenarioIdOrThrow( additionalDefectReportsFromCsv, r ) ) :
                            r ).collect( ImmutableSet.toImmutableSet() );
        } catch (final SQLException e) {
            throw new UncheckedSQLException( e );
        }
    }

    private static ScenarioId getScenarioIdOrThrow(
            final ImmutableSet<DefectReport> additionalDefectReportsFromCsv,
            final DefectReport report ) {
        final ImmutableSet<DefectReport> matches = additionalDefectReportsFromCsv.stream().filter(
                r -> r.getDefectReportCode().contains( String.valueOf( report.getTaskId() ) ) &&
                        Objects.equals( r.getWorkerId(), report.getWorkerId() ) ).collect(
                ImmutableSet.toImmutableSet() );
        if (matches.isEmpty()) {
            throw new NoSuchElementException( "Scenario id not known for defect " + report );
        }
        else if (matches.size() > 1) {
            throw new IllegalArgumentException(
                    "More than one CSV defect report " + matches + " matching for defect " + report );
        }
        else {
            return matches.iterator().next().getScenarioId();
        }
    }

    private static ImmutableSet<DefectReport> getDefectReportsFromCsv() {
        try (Reader reader = Files.newBufferedReader(
                Paths.get( "src/main/resources/additions/defectReportsSS18.csv" ) );
             CSVReader csvReader = new CSVReaderBuilder( reader ).withSkipLines( 1 ).build()) {
            return csvReader.readAll().stream().map( r -> DefectReport.builder( Integer.parseInt( r[0] ) ).withEmeId(
                    new EmeId( r[12] ) ).withScenarioId( new ScenarioId( r[11] ) ).withWorkerId(
                    new TaskWorkerId( r[7] ) )
                                                                      .withDefectType( DefectType.fromString( r[14] ) )
                                                                      .withDefectReportCode( r[1] )
                                                                      .build() ).collect(
                    ImmutableSet.toImmutableSet() );
        } catch (final IOException e) {
            throw new UncheckedIOException( e );
        }
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

        private TaskWorkerId workerId;

        private EmeId emeId;

        private DefectType defectType;

        private String defectDescription;

        private String synDefectDescription;

        private String synLabel;

        private Integer taskInstanceId;

        private Integer workshopId;

        private ScenarioId scenarioId;

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

        public Builder withWorkerId( final TaskWorkerId workerId ) {
            this.workerId = workerId;
            return this;
        }

        public Builder withEmeId( final EmeId emeId ) {
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

        public Builder withScenarioId( final ScenarioId scenarioId ) {
            this.scenarioId = scenarioId;
            return this;
        }

        public DefectReport build() {
            return new DefectReport( this );
        }
    }
}
