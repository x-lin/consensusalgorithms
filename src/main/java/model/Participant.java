package model;

import com.google.common.collect.ImmutableSet;
import org.jooq.Record;
import org.jooq.impl.DSL;
import web.Semester;
import web.SemesterSettings;

import java.sql.Connection;
import java.util.Objects;

/**
 * @author LinX
 */
public class Participant {
    public static final String PARTICIPANT_TABLE = "participant";

    public static final String NAME_COLUMN = "name";

    public static final String WORKER_ID_COLUMN = "worker_id";

    public static final String WORKSHOP_ID_COLUMN = "workshop_id";

    public static final String PARTICIPANT_ID_COLUMN = "p_id";

    public static final String CREATED_AT_COLUMN = "created_at";

    private final String name;

    private final String workerId;

    private final int workshopId;

    private final String participantId;

    private final String createdAt;

    public Participant( final Record record ) {
        this.name = record.getValue( NAME_COLUMN, String.class );
        this.workerId = record.getValue( WORKER_ID_COLUMN, String.class );
        this.workshopId = record.getValue( WORKSHOP_ID_COLUMN, Integer.class );
        this.participantId = record.getValue( PARTICIPANT_ID_COLUMN, String.class );
        this.createdAt = record.getValue( CREATED_AT_COLUMN, String.class );
    }

    public String getName() {
        return this.name;
    }

    public String getWorkerId() {
        return this.workerId;
    }

    public int getWorkshopId() {
        return this.workshopId;
    }

    public String getParticipantId() {
        return this.participantId;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Participant that = (Participant) o;
        return this.workshopId == that.workshopId &&
                Objects.equals( this.name, that.name ) &&
                Objects.equals( this.workerId, that.workerId ) &&
                Objects.equals( this.participantId, that.participantId ) &&
                Objects.equals( this.createdAt, that.createdAt );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.name, this.workerId, this.workshopId, this.participantId, this.createdAt );
    }

    @Override
    public String toString() {
        return "Participant{" +
                "name='" + this.name + '\'' +
                ", workerId='" + this.workerId + '\'' +
                ", workshopId=" + this.workshopId +
                ", participantId='" + this.participantId + '\'' +
                ", createdAt='" + this.createdAt + '\'' +
                '}';
    }

    public static ImmutableSet<Participant> fetchParticipants( final Connection connection,
            final SemesterSettings settings ) {
        final String sql = "select * from " + PARTICIPANT_TABLE;
        return DSL.using( connection )
                  .fetch( sql )
                  .map( Participant::new ).stream().filter(
                        p -> p.createdAt.startsWith( settings.getSemester() == Semester.WS2017 ? "2017" : "2018" ) )
                  .collect( ImmutableSet.toImmutableSet() );
    }
}
