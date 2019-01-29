package model;

import org.jooq.Record;

import java.util.Objects;

/**
 * @author LinX
 */
public class TrueDefect {
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

    private final String aboutEmEid;

    private final String scenario;

    private final String aboutMeType;

    private final String defectType;

    private final String defectSeverity;

    private final String description;

    public TrueDefect(Record record) {
        this.id = record.getValue(ID_COLUMN, Integer.class);
        this.codeTd = record.getValue(CODE_TD_COLUMN, String.class);
        this.aboutModelElement = record.getValue(ABOUT_MODEL_ELEMENT_COLUMN, String.class);
        this.aboutEmEid = record.getValue(ABOUT_EM_EID_COLUMN, String.class);
        this.scenario = record.getValue(SCENARIO_COLUMN, String.class);
        this.aboutMeType = record.getValue(ABOUT_ME_TYPE_COLUMN, String.class);
        this.defectType = record.getValue(DEFECT_TYPE_COLUMN, String.class);
        this.defectSeverity = record.getValue(DEFECT_SEVERITY_COLUMN, String.class);
        this.description = record.getValue(DESCRIPTION_COLUMN, String.class);
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

    public String getAboutEmEid() {
        return this.aboutEmEid;
    }

    public String getScenario() {
        return this.scenario;
    }

    public String getAboutMeType() {
        return this.aboutMeType;
    }

    public String getDefectType() {
        return this.defectType;
    }

    public String getDefectSeverity() {
        return this.defectSeverity;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrueDefect that = (TrueDefect) o;
        return this.id == that.id &&
                Objects.equals(this.codeTd, that.codeTd) &&
                Objects.equals(this.aboutModelElement, that.aboutModelElement) &&
                Objects.equals(this.aboutEmEid, that.aboutEmEid) &&
                Objects.equals(this.scenario, that.scenario) &&
                Objects.equals(this.aboutMeType, that.aboutMeType) &&
                Objects.equals(this.defectType, that.defectType) &&
                Objects.equals(this.defectSeverity, that.defectSeverity) &&
                Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.codeTd, this.aboutModelElement, this.aboutEmEid, this.scenario, this
                .aboutMeType, this.defectType, this.defectSeverity, this.description);
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
}
