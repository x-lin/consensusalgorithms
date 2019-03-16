package statistic;

/**
 * @author LinX
 */
public class NamedEvaluationResultMetrics {
    private final String id;

    private final double quality;

    private final EvaluationResultMetrics metrics;

    public NamedEvaluationResultMetrics( final EvaluationResultMetrics
                                                 metrics, final String id, final double quality ) {
        this.id = id;
        this.quality = quality;
        this.metrics = metrics;
    }

    public String getId() {
        return this.id;
    }

    public double getQuality() {
        return this.quality;
    }

    public EvaluationResultMetrics getMetrics() {
        return this.metrics;
    }
}
