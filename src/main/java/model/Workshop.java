package model;

import org.jooq.Record;

import java.util.Objects;

/**
 * @author LinX
 */
public class Workshop {
    public static String WORKSHOP_TABLE = "workshop";

    public static String ID_COLUMN = "id";

    public static String WORKSHOP_CODE_COLUMN = "workshop_code";

    public static String WORKSHOP_DATE_COLUMN = "workshop_date";

    private final int id;

    private final String workshopCode;

    private final String workshopDate;

    public Workshop(Record record) {
        this.id = record.getValue(ID_COLUMN, Integer.class);
        this.workshopCode = record.getValue(WORKSHOP_CODE_COLUMN, String.class);
        this.workshopDate = record.getValue(WORKSHOP_DATE_COLUMN, String.class);
    }

    public int getId() {
        return this.id;
    }

    public String getWorkshopCode() {
        return this.workshopCode;
    }

    public String getWorkshopDate() {
        return this.workshopDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workshop workshop = (Workshop) o;
        return this.id == workshop.id &&
                Objects.equals(this.workshopCode, workshop.workshopCode) &&
                Objects.equals(this.workshopDate, workshop.workshopDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.workshopCode, this.workshopDate);
    }

    @Override
    public String toString() {
        return "Workshop{" +
                "id=" + this.id +
                ", workshopCode='" + this.workshopCode + '\'' +
                ", workshopDate='" + this.workshopDate + '\'' +
                '}';
    }
}
