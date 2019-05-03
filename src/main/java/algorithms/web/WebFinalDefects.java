package algorithms.web;

import algorithms.finaldefects.FinalDefectAggregationAlgorithm;
import algorithms.statistic.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author LinX
 */
public class WebFinalDefects {
    private final ConfusionMatrix confusionMatrix;

    private final ImmutableSet<FinalDefectResult> finalDefectResults;

    private final ImmutableMap<String, String> parameters;

    private final AlgorithmType algorithmType;

    private final ImmutableSet<ArtifactWithConfusionMatrix> workerConfusionMatrix;

    private final PearsonScores workerPearsonScores;

    public WebFinalDefects( final AlgorithmType algorithmType, final FinalDefectAggregationAlgorithm algorithm ) {
        this.finalDefectResults = FinalDefectAnalyzer.getFinalDefects( algorithm ).values();
        this.confusionMatrix = new ConfusionMatrix( this.finalDefectResults );
        this.parameters = algorithm.getParameters();
        this.workerConfusionMatrix = QualityAnalyzer.create().getConfusionMatrixForWorkers( algorithm );
        this.workerPearsonScores = new PearsonScores( this.workerConfusionMatrix );
        this.algorithmType = algorithmType;
    }

    public ConfusionMatrix getConfusionMatrix() {
        return this.confusionMatrix;
    }

    public ImmutableSet<FinalDefectResult> getFinalDefectResults() {
        return this.finalDefectResults;
    }

    public ImmutableMap<String, String> getParameters() {
        return this.parameters;
    }

    public AlgorithmType getAlgorithmType() {
        return this.algorithmType;
    }

    public ImmutableSet<ArtifactWithConfusionMatrix> getWorkerConfusionMatrix() {
        return this.workerConfusionMatrix;
    }

    public PearsonScores getWorkerPearsonScores() {
        return this.workerPearsonScores;
    }
}
