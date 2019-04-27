package algorithms.web;

import algorithms.statistic.ArtifactWithConfusionMatrix;
import algorithms.statistic.ConfusionMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

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

    public PearsonScores( final Set<ArtifactWithConfusionMatrix> metrics ) {
        this.qualityFMeasureCorrelation = calculatePearsonCorrelation( metrics,
                ConfusionMatrix::getFmeasure );
        this.qualityPrecisionCorrelation = calculatePearsonCorrelation( metrics,
                ConfusionMatrix::getPrecision );
        this.qualityAccuracyCorrelation = calculatePearsonCorrelation( metrics,
                ConfusionMatrix::getAccuracy );
        this.qualityRecallCorrelation = calculatePearsonCorrelation( metrics,
                ConfusionMatrix::getRecall );
    }

    private double calculatePearsonCorrelation( final Set<ArtifactWithConfusionMatrix> metrics, final
    Function<ConfusionMatrix, Double> fetcher ) {
        return new PearsonsCorrelation().correlation( metrics.stream().mapToDouble(
                ArtifactWithConfusionMatrix::getQuality ).toArray(),
                metrics.stream().mapToDouble( r -> fetcher.apply( r.getConfusionMatrix() ) )
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
