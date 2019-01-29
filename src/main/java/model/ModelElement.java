package model;

import org.jooq.Record;

import java.util.Objects;

/**
 * @author LinX
 */
public class ModelElement {
    public static String MODEL_ELEMENT_TABLE = "model_element";

    public static String ID_COLUMN = "id";

    public static String CODE_COLUMN = "code";

    public static String LABEL_COLUMN = "label";

    public static String EME_ID_COLUMN = "eme_id";

    private final int id;

    private final String code;

    private final String label;

    private final String emeId;

    public ModelElement(Record record) {
        this.id = record.getValue(ID_COLUMN, Integer.class);
        this.code = record.getValue(CODE_COLUMN, String.class);
        this.label = record.getValue(LABEL_COLUMN, String.class);
        this.emeId = record.getValue(EME_ID_COLUMN, String.class);
    }

    public int getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }

    public String getLabel() {
        return this.label;
    }

    public String getEmeId() {
        return this.emeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelElement that = (ModelElement) o;
        return this.id == that.id &&
                Objects.equals(this.code, that.code) &&
                Objects.equals(this.label, that.label) &&
                Objects.equals(this.emeId, that.emeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.code, this.label, this.emeId);
    }

    @Override
    public String toString() {
        return "ModelElement{" +
                "id=" + this.id +
                ", code='" + this.code + '\'' +
                ", label='" + this.label + '\'' +
                ", emeId='" + this.emeId + '\'' +
                '}';
    }
}
