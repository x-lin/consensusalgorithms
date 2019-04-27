package algorithms.crowdtruth;

import java.util.Objects;

/**
 * @author LinX
 */
public final class MediaUnitAnnotationId {
    private final AnnotationName name;

    private final MediaUnitId mediaUnitId;

    public MediaUnitAnnotationId( final AnnotationName name, final MediaUnitId mediaUnitId ) {
        this.name = name;
        this.mediaUnitId = mediaUnitId;
    }

    public AnnotationName getName() {
        return this.name;
    }

    public MediaUnitId getMediaUnitId() {
        return this.mediaUnitId;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MediaUnitAnnotationId that = (MediaUnitAnnotationId) o;
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
