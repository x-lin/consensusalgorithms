package model;

import org.jooq.Record;

import java.util.Objects;

/**
 * @author LinX
 */
public class TrueDefectNeighborhood {
    public static String TRUE_DEFECT_NEIGHBORHOOD_TABLE = "true_defect_neighbourhood";

    public static String ID_COLUMN = "id";

    public static String EME_ID_COLUMN = "eme_id";

    public static String RELEVANCE_COLUMN = "relevance";

    public static String TRUE_DEFECT_ID_COLUMN = "true_defect_id";

    private final int id;

    private final String emeId;

    private final int relevance;

    private final int trueDefectId;

    public TrueDefectNeighborhood(Record record) {
        this.id = record.getValue(ID_COLUMN, Integer.class);
        this.emeId = record.getValue(EME_ID_COLUMN, String.class);
        this.relevance = record.getValue(RELEVANCE_COLUMN, Integer.class);
        this.trueDefectId = record.getValue(TRUE_DEFECT_ID_COLUMN, Integer.class);
    }

    public int getId() {
        return this.id;
    }

    public String getEmeId() {
        return this.emeId;
    }

    public int getRelevance() {
        return this.relevance;
    }

    public int getTrueDefectId() {
        return this.trueDefectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrueDefectNeighborhood that = (TrueDefectNeighborhood) o;
        return this.id == that.id &&
                this.relevance == that.relevance &&
                this.trueDefectId == that.trueDefectId &&
                Objects.equals(this.emeId, that.emeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.emeId, this.relevance, this.trueDefectId);
    }

    @Override
    public String toString() {
        return "TrueDefectNeighborhood{" +
                "id=" + this.id +
                ", emeId='" + this.emeId + '\'' +
                ", relevance=" + this.relevance +
                ", trueDefectId=" + this.trueDefectId +
                '}';
    }
}
