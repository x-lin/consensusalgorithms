package web;

import com.google.common.collect.ImmutableSet;
import statistic.ConfusionMatrix;
import statistic.FinalDefectResult;

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
