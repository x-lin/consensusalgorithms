package web;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import statistic.EvaluationResultMetrics;
import statistic.NamedEvaluationResultMetrics;

import java.util.Set;
import java.util.function.Function;

/**
 * @author LinX
 */
public class PearsonScores {
    private final double qualityFMeasureCorrelation;

    private final double qualityPrecisionCorrelation;

    private final double qualityAccuracyCorrelation;

    private final double qualityRecallCorrelation;

    public PearsonScores( final Set<NamedEvaluationResultMetrics> metrics ) {
        this.qualityFMeasureCorrelation = calculatePearsonCorrelation( metrics,
                EvaluationResultMetrics::getFmeasure );
        this.qualityPrecisionCorrelation = calculatePearsonCorrelation( metrics,
                EvaluationResultMetrics::getPrecision );
        this.qualityAccuracyCorrelation = calculatePearsonCorrelation( metrics,
                EvaluationResultMetrics::getAccuracy );
        this.qualityRecallCorrelation = calculatePearsonCorrelation( metrics,
                EvaluationResultMetrics::getRecall );
    }

    private double calculatePearsonCorrelation( final Set<NamedEvaluationResultMetrics> metrics, final
    Function<EvaluationResultMetrics, Double> fetcher ) {
        return new PearsonsCorrelation().correlation( metrics.stream().mapToDouble(
                NamedEvaluationResultMetrics::getQuality ).toArray(),
                metrics.stream().mapToDouble( r -> fetcher.apply( r.getMetrics() ) )
                       .toArray() );
    }

    public double getQualityFMeasureCorrelation() {
        return this.qualityFMeasureCorrelation;
    }

    public double getQualityPrecisionCorrelation() {
        return this.qualityPrecisionCorrelation;
    }

    public double getQualityAccuracyCorrelation() {
        return this.qualityAccuracyCorrelation;
    }

    public double getQualityRecallCorrelation() {
        return this.qualityRecallCorrelation;
    }
}
