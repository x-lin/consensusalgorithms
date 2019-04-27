package algorithms.web;

import algorithms.statistic.ConfusionMatrix;
import algorithms.statistic.FinalDefectResult;
import com.google.common.collect.ImmutableSet;

/**
 * @author LinX
 */
public class WebFinalDefects {
    private final ConfusionMatrix confusionMatrix;

    private final ImmutableSet<FinalDefectResult> finalDefectResults;

    public WebFinalDefects( final ImmutableSet<FinalDefectResult> finalDefectResults ) {
        this.finalDefectResults = finalDefectResults;
        this.confusionMatrix = new ConfusionMatrix( finalDefectResults );
    }

    public ConfusionMatrix getConfusionMatrix() {
        return this.confusionMatrix;
    }

    public ImmutableSet<FinalDefectResult> getFinalDefectResults() {
        return this.finalDefectResults;
    }
}
