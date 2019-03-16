package web;

import com.google.common.collect.ImmutableSet;
import statistic.EvaluationResult;
import statistic.EvaluationResultMetrics;

/**
 * @author LinX
 */
public class WebFinalDefects {
    private final EvaluationResultMetrics metrics;

    private final ImmutableSet<EvaluationResult> evaluationResults;

    public WebFinalDefects( final ImmutableSet<EvaluationResult>
                                    evaluationResults ) {
        this.evaluationResults = evaluationResults;
        this.metrics = new EvaluationResultMetrics( evaluationResults );
    }

    public EvaluationResultMetrics getMetrics() {
        return this.metrics;
    }

    public ImmutableSet<EvaluationResult> getEvaluationResults() {
        return this.evaluationResults;
    }
}
