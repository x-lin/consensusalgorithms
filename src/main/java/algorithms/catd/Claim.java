package algorithms.catd;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

/**
 * @author LinX
 */
public final class Claim {
    private final SourceId sourceId;

    private final EntityId entityId;

    private final ImmutableList<InformationId> informationIds;

    private Claim( final SourceId sourceId, final EntityId entityId,
            final ImmutableList<InformationId> informationIds ) {
        this.sourceId = sourceId;
        this.entityId = entityId;
        this.informationIds = informationIds;
    }

    public SourceId getSourceId() {
        return this.sourceId;
    }

    public EntityId getEntityId() {
        return this.entityId;
    }

    public ImmutableList<InformationId> getInformationIds() {
        return this.informationIds;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Claim that = (Claim) o;
        return Objects.equals( this.sourceId, that.sourceId ) &&
                Objects.equals( this.entityId, that.entityId ) &&
                Objects.equals( this.informationIds, that.informationIds );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.sourceId, this.entityId, this.informationIds );
    }

    @Override
    public String toString() {
        return "Claim{" +
                "sourceId=" + this.sourceId +
                ", entityId=" + this.entityId +
                ", informationIds=" + this.informationIds +
                '}';
    }

    public static Claim create( final SourceId sourceId, final EntityId entityId,
            final List<InformationId> informationIds ) {
        return new Claim( sourceId, entityId, ImmutableList.copyOf( informationIds ) );
    }
}
