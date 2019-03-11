package web;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import statistic.EvaluationResultMetrics;
import statistic.WorkerEvaluationResultMetrics;

import java.util.Set;
import java.util.function.Function;

/**
 * @author LinX
 */
public class WorkerScoresPearson {
    private final double workerQualityFMeasureCorrelation;

    private final double workerQualityPrecisionCorrelation;

    private final double workerQualityAccuracyCorrelation;

    private final double workerQualityRecallCorrelation;

    public WorkerScoresPearson( final Set<WorkerEvaluationResultMetrics> metrics ) {
        this.workerQualityFMeasureCorrelation = calculatePearsonCorrelation( metrics,
                EvaluationResultMetrics::getFmeasure );
        this.workerQualityPrecisionCorrelation = calculatePearsonCorrelation( metrics,
                EvaluationResultMetrics::getPrecision );
        this.workerQualityAccuracyCorrelation = calculatePearsonCorrelation( metrics,
                EvaluationResultMetrics::getAccuracy );
        this.workerQualityRecallCorrelation = calculatePearsonCorrelation( metrics,
                EvaluationResultMetrics::getRecall );
    }

    private double calculatePearsonCorrelation( final Set<WorkerEvaluationResultMetrics> metrics, final
    Function<EvaluationResultMetrics, Double> fetcher ) {
        return new PearsonsCorrelation().correlation( metrics.stream().mapToDouble(
                WorkerEvaluationResultMetrics::getWorkerQuality ).toArray(),
                metrics.stream().mapToDouble( r -> fetcher.apply( r.getMetrics() ) )
                        .toArray() );
    }

    public double getWorkerQualityFMeasureCorrelation() {
        return this.workerQualityFMeasureCorrelation;
    }

    public double getWorkerQualityPrecisionCorrelation() {
        return this.workerQualityPrecisionCorrelation;
    }

    public double getWorkerQualityAccuracyCorrelation() {
        return this.workerQualityAccuracyCorrelation;
    }

    public double getWorkerQualityRecallCorrelation() {
        return this.workerQualityRecallCorrelation;
    }
}
