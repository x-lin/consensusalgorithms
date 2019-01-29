package crowdtruth;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * @author LinX
 */
public class Annotation implements Comparable<Annotation> {
    private final AnnotationName name;

    private final ImmutableSet<MediaUnitAnnotation> annotations;

    public Annotation( AnnotationName name, Set<MediaUnitAnnotation> annotations ) {
        this.name = name;
        this.annotations = ImmutableSet.copyOf( annotations );
    }

    public ImmutableSet<MediaUnitAnnotation> getMediaUnitAnnotations() {
        return this.annotations;
    }


    public AnnotationName getName() {
        return this.name;
    }

    @Override
    public int compareTo( Annotation o ) {
        return this.name.toString().compareTo( o.name.toString() );
    }
}
