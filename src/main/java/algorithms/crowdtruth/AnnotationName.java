package algorithms.crowdtruth;

import java.util.Objects;

/**
 * @author LinX
 */
public class AnnotationName {
    private final String name;

    private AnnotationName( final String name ) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AnnotationName that = (AnnotationName) o;
        return Objects.equals( this.name, that.name );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.name );
    }

    public static AnnotationName create( final String name ) {
        return new AnnotationName( name );
    }
}
