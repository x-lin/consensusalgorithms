package algorithms.vericom.model;

import com.google.common.collect.ImmutableSet;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.util.Objects;

/**
 * @author LinX
 */
public class TrueDefect {
    private static ImmutableSet<TrueDefect> CACHED_TRUE_DEFECTS = null;

    public static String TRUE_DEFECT_TABLE = "true_defect";

    public static String ID_COLUMN = "id";

    public static String CODE_TD_COLUMN = "code_td";

    public static String ABOUT_MODEL_ELEMENT_COLUMN = "about_model_element";

    public static String ABOUT_EM_EID_COLUMN = "about_em_eid";

    public static String SCENARIO_COLUMN = "scenario";

    public static String ABOUT_ME_TYPE_COLUMN = "about_me_type";

    public static String DEFECT_TYPE_COLUMN = "defect_type";

    public static String DEFECT_SEVERITY_COLUMN = "defect_severity";

    public static String DESCRIPTION_COLUMN = "description";

    private final int id;

    private final String codeTd;

    private final String aboutModelElement;

    private final EmeId aboutEmEid;

    private final ScenarioId scenario;

    private final String aboutMeType;

    private final DefectType defectType;

    private final String defectSeverity;

    private final String description;

    public TrueDefect( final Record record ) {
        this.id = record.getValue( ID_COLUMN, Integer.class );
        this.codeTd = record.getValue( CODE_TD_COLUMN, String.class );
        this.aboutModelElement = record.getValue( ABOUT_MODEL_ELEMENT_COLUMN, String.class );
        this.aboutEmEid = new EmeId( record.getValue( ABOUT_EM_EID_COLUMN, String.class ) );
        this.scenario = new ScenarioId( record.getValue( SCENARIO_COLUMN, String.class ) );
        this.aboutMeType = record.getValue( ABOUT_ME_TYPE_COLUMN, String.class );
        this.defectType = DefectType.fromString( record.getValue( DEFECT_TYPE_COLUMN, String.class ).trim() );
        this.defectSeverity = record.getValue( DEFECT_SEVERITY_COLUMN, String.class );
        this.description = record.getValue( DESCRIPTION_COLUMN, String.class );
    }

    private TrueDefect( final int id, final String codeTd, final String aboutModelElement, final EmeId aboutEmEid,
            final ScenarioId scenario, final String aboutMeType, final DefectType defectType, final String
            defectSeverity, final String description ) {
        this.id = id;
        this.codeTd = codeTd;
        this.aboutModelElement = aboutModelElement;
        this.aboutEmEid = aboutEmEid;
        this.scenario = scenario;
        this.aboutMeType = aboutMeType;
        this.defectType = defectType;
        this.defectSeverity = defectSeverity;
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public String getCodeTd() {
        return this.codeTd;
    }

    public String getAboutModelElement() {
        return this.aboutModelElement;
    }

    public EmeId getAboutEmEid() {
        return this.aboutEmEid;
    }

    public ScenarioId getScenario() {
        return this.scenario;
    }

    public String getAboutMeType() {
        return this.aboutMeType;
    }

    public DefectType getDefectType() {
        return this.defectType;
    }

    public String getDefectSeverity() {
        return this.defectSeverity;
    }

    public String getDescription() {
        return this.description;
    }

    public TrueDefect replaceEmeId( final EmeId newEmeId ) {
        return new TrueDefect( getId(), getCodeTd(), getAboutModelElement(), newEmeId, getScenario(), getAboutMeType
                (), getDefectType(), getDefectSeverity(), getDescription() );
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TrueDefect that = (TrueDefect) o;
        return this.id == that.id &&
                Objects.equals( this.codeTd, that.codeTd ) &&
                Objects.equals( this.aboutModelElement, that.aboutModelElement ) &&
                Objects.equals( this.aboutEmEid, that.aboutEmEid ) &&
                Objects.equals( this.scenario, that.scenario ) &&
                Objects.equals( this.aboutMeType, that.aboutMeType ) &&
                Objects.equals( this.defectType, that.defectType ) &&
                Objects.equals( this.defectSeverity, that.defectSeverity ) &&
                Objects.equals( this.description, that.description );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.id, this.codeTd, this.aboutModelElement, this.aboutEmEid, this.scenario, this
                .aboutMeType, this.defectType, this.defectSeverity, this.description );
    }

    @Override
    public String toString() {
        return "TrueDefect{" +
                "id=" + this.id +
                ", codeTd='" + this.codeTd + '\'' +
                ", aboutModelElement='" + this.aboutModelElement + '\'' +
                ", aboutEmEid='" + this.aboutEmEid + '\'' +
                ", scenario='" + this.scenario + '\'' +
                ", aboutMeType='" + this.aboutMeType + '\'' +
                ", defectType='" + this.defectType + '\'' +
                ", defectSeverity='" + this.defectSeverity + '\'' +
                ", description='" + this.description + '\'' +
                '}';
    }

    public static ImmutableSet<TrueDefect> fetchTrueDefects( final Connection connection ) {
        if (CACHED_TRUE_DEFECTS == null) {
            final String sql = "select * from " + TRUE_DEFECT_TABLE;
            CACHED_TRUE_DEFECTS = DSL.using( connection )
                    .fetch( sql )
                    .map( TrueDefect::new ).stream().collect( ImmutableSet.toImmutableSet() );
        }
        return CACHED_TRUE_DEFECTS;
    }
}
