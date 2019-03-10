package statistic;

/**
 * @author LinX
 */
public class WorkerEvaluationResultMetrics {
    private final int workerId;

    private final double workerQuality;

    private final EvaluationResultMetrics metrics;

    public WorkerEvaluationResultMetrics( final EvaluationResultMetrics
                                                  metrics, final int workerId, final double workerQuality ) {
        this.workerId = workerId;
        this.workerQuality = workerQuality;
        this.metrics = metrics;
    }

    public int getWorkerId() {
        return this.workerId;
    }

    public double getWorkerQuality() {
        return this.workerQuality;
    }

    public EvaluationResultMetrics getMetrics() {
        return this.metrics;
    }
}
