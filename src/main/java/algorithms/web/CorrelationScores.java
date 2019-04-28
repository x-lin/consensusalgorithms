package algorithms.web;

import algorithms.statistic.ArtifactWithConfusionMatrix;
import com.google.common.collect.ImmutableSet;

/**
 * @author LinX
 */
public class CorrelationScores {
    private final ImmutableSet<ArtifactWithConfusionMatrix> scores;

    private final PearsonScores pearsonScores;

    public CorrelationScores(
            final ImmutableSet<ArtifactWithConfusionMatrix> scores, final PearsonScores pearsonScores ) {
        this.scores = scores;
        this.pearsonScores = pearsonScores;
    }

    public ImmutableSet<ArtifactWithConfusionMatrix> getScores() {
        return this.scores;
    }

    public PearsonScores getPearsonScores() {
        return this.pearsonScores;
    }
}
