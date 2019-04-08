package algorithms.majorityvoting;

import com.google.common.base.Preconditions;

import java.util.Objects;

/**
 * @author LinX
 */
public class WorkerQuality {
    private final double quality;

    public WorkerQuality( final double quality ) {
        Preconditions.checkArgument( quality >= 0 && quality <= 1,
                "Worker quality can only be between 0-1, but was %s.", quality );
        this.quality = quality;
    }

    public double toDouble() {
        return this.quality;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WorkerQuality that = (WorkerQuality) o;
        return this.quality == that.quality;
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.quality );
    }

    @Override
    public String toString() {
        return String.valueOf( this.quality );
    }
}
