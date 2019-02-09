package algorithms.crowdtruth;

import java.util.Objects;

/**
 * @author LinX
 */
public class AnnotationName {
    private final String name;

    private AnnotationName( String name ) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotationName that = (AnnotationName) o;
        return Objects.equals( this.name, that.name );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.name );
    }

    public static AnnotationName create( String name ) {
        return new AnnotationName( name );
    }
}
