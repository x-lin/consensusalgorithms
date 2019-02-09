package algorithms.crowdtruth;

import java.util.Objects;

/**
 * @author LinX
 */
public final class MediaUnitAnnotationId {
    private final String name;

    private final MediaUnitId mediaUnitId;

    public MediaUnitAnnotationId( String name, MediaUnitId mediaUnitId ) {
        this.name = name;
        this.mediaUnitId = mediaUnitId;
    }

    public String getName() {
        return this.name;
    }

    public MediaUnitId getMediaUnitId() {
        return this.mediaUnitId;
    }

    @Override
    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaUnitAnnotationId that = (MediaUnitAnnotationId) o;
        return Objects.equals( this.name, that.name ) &&
                Objects.equals( this.mediaUnitId, that.mediaUnitId );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.name, this.mediaUnitId );
    }

    @Override
    public String toString() {
        return this.name + "/" + this.mediaUnitId;
    }
}
