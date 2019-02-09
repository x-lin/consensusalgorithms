package algorithms.crowdtruth;

/**
 * Number of times annotation was chosen for a given media unit.
 *
 * @author LinX
 */
public class AggregatedAnnotationResult implements Result {
    private final int count;

    public AggregatedAnnotationResult( int count ) {
        this.count = count;
    }

    @Override
    public int getScore() {
        return this.count;
    }

    @Override
    public String toString() {
        return "AggregatedAnnotationResult{" + this.count +
                '}';
    }
}
