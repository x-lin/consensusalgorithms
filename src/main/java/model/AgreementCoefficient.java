package model;

import com.google.common.base.Preconditions;

import java.util.Objects;

/**
 * @author LinX
 */
public final class AgreementCoefficient {
    public static final AgreementCoefficient ZERO = new AgreementCoefficient( 0.0 );

    private final double coefficient;

    public AgreementCoefficient( final double coefficient ) {
        Preconditions.checkArgument( coefficient >= 0.0 && coefficient <= 1.0,
                "Agreement coefficient must be in range [0,1], but was %s.", coefficient );
        this.coefficient = coefficient;
    }

    public double toDouble() {
        return this.coefficient;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AgreementCoefficient that = (AgreementCoefficient) o;
        return Double.compare( that.coefficient, this.coefficient ) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.coefficient );
    }

    @Override
    public String toString() {
        return String.valueOf( this.coefficient );
    }
}
