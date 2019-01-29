package crowdtruth;

/**
 * Whether the worker has chosen the annotation option or not.
 *
 * @author LinX
 */
public class AnnotationResult implements Result {
    private final boolean isTrue;

    private AnnotationResult( boolean isTrue ) {
        this.isTrue = isTrue;
    }

    public boolean isChosen() {
        return this.isTrue;
    }

    @Override
    public int getScore() {
        return this.isTrue ? 1 : 0;
    }

    public static AnnotationResult create( boolean chosen ) {
        return new AnnotationResult( chosen );
    }

    @Override
    public String toString() {
        return "AnnotationResult{" + this.isTrue +
                '}';
    }
}
