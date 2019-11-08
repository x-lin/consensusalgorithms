package algorithms.vericom.model;

import com.google.common.base.Preconditions;

import java.util.Objects;

/**
 * @author LinX
 */
public final class Weight {
    public static final Weight ONE = new Weight( 1.0 );

    private final double weight;

    public Weight( final double weight ) {
        Preconditions.checkArgument( weight >= 0 && weight <= 1,
                "Experience weight can only be in range [0-1], but was %s.", weight );
        this.weight = weight;
    }

    public double toDouble() {
        return this.weight;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Weight that = (Weight) o;
        return this.weight == that.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.weight );
    }

    @Override
    public String toString() {
        return String.valueOf( this.weight );
    }
}
