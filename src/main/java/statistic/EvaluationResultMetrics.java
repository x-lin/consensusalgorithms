package statistic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

/**
 * @author LinX
 */
public class EvaluationResultMetrics {
    private static final int ROUNDING_ACCURACY = 4;

    private final long truePositives;

    private final long trueNegatives;

    private final long falseNegatives;

    private final long falsePositives;

    private final double recall;

    private final double precision;

    private final double accuracy;

    private final double fmeasure;

    public EvaluationResultMetrics( final Collection<EvaluationResult> results ) {
        this.truePositives = results.stream().filter( EvaluationResult::isTruePositive ).count();
        this.trueNegatives = results.stream().filter( EvaluationResult::isTrueNegative ).count();
        this.falseNegatives = results.stream().filter( EvaluationResult::isFalseNegative ).count();
        this.falsePositives = results.stream().filter( EvaluationResult::isFalsePositive ).count();

        final BigDecimal recall = BigDecimal.valueOf( this.truePositives )
                .divide( BigDecimal.valueOf( this.truePositives + this.falseNegatives ), ROUNDING_ACCURACY * 2,
                        RoundingMode.HALF_UP );
        final BigDecimal precision = BigDecimal.valueOf( this.truePositives ).divide( BigDecimal.valueOf(
                this.truePositives + this.falsePositives ), ROUNDING_ACCURACY * 2,
                RoundingMode.HALF_UP );
        final BigDecimal accuracy = BigDecimal.valueOf( this.truePositives + this.trueNegatives ).divide( BigDecimal
                        .valueOf( this.truePositives + this.trueNegatives + this.falseNegatives + this.falsePositives
                        ), ROUNDING_ACCURACY * 2,
                RoundingMode.HALF_UP );
        final BigDecimal fmeasure = precision.add( recall ).signum() == 0 ? BigDecimal
                .ZERO : BigDecimal
                .valueOf( 2 )
                .multiply( precision.multiply( recall ) )
                .divide( precision.add( recall ), ROUNDING_ACCURACY,
                        RoundingMode.HALF_UP );
        this.recall = recall.doubleValue();
        this.precision = precision.doubleValue();
        this.accuracy = accuracy.doubleValue();
        this.fmeasure = fmeasure.doubleValue();
    }

    public long getTruePositives() {
        return this.truePositives;
    }

    public String getTruePositivesAsString() {
        return String.valueOf( this.truePositives );
    }

    public long getTrueNegatives() {
        return this.trueNegatives;
    }

    public String getTrueNegativesAsString() {
        return String.valueOf( this.trueNegatives );
    }

    public long getFalseNegatives() {
        return this.falseNegatives;
    }

    public String getFalseNegativesAsString() {
        return String.valueOf( this.falseNegatives );
    }

    public long getFalsePositives() {
        return this.falsePositives;
    }

    public String getFalsePositivesAsString() {
        return String.valueOf( this.falsePositives );
    }

    public double getRecall() {
        return this.recall;
    }

    public String getRecallAsString() {
        return String.valueOf( this.recall );
    }

    public double getPrecision() {
        return this.precision;
    }

    public String getPrecisionAsString() {
        return String.valueOf( this.precision );
    }

    public double getFmeasure() {
        return this.fmeasure;
    }

    public String getFmeasureAsString() {
        return String.valueOf( this.fmeasure );
    }

    public double getAccuracy() {
        return this.accuracy;
    }

    public String getAccuracyAsString() {
        return String.valueOf( this.accuracy );
    }
}
