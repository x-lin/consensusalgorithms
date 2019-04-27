package web;

import com.google.common.collect.ImmutableSet;
import statistic.ArtifactWithConfusionMatrix;

/**
 * @author LinX
 */
public class CrowdtruthScores {
    private final ImmutableSet<ArtifactWithConfusionMatrix> workerScores;

    private final PearsonScores workerPearsonScores;

    private final ImmutableSet<ArtifactWithConfusionMatrix> annotationScores;

    private final PearsonScores annotationPearsonScores;

    private final ImmutableSet<ArtifactWithConfusionMatrix> mediaUnitScores;

    private final PearsonScores mediaUnitPearsonScores;

    private final WebMetricsScores metricsScores;

    public CrowdtruthScores( final ImmutableSet<ArtifactWithConfusionMatrix> workerScores, final
    PearsonScores workerPearsonScores, final ImmutableSet<ArtifactWithConfusionMatrix> annotationScores, final
    PearsonScores annotationPearsonScores, final
    ImmutableSet<ArtifactWithConfusionMatrix> mediaUnitScores, final
    PearsonScores mediaUnitPearsonScores,
            final WebMetricsScores metricsScores ) {
        this.workerScores = workerScores;
        this.workerPearsonScores = workerPearsonScores;
        this.annotationScores = annotationScores;
        this.annotationPearsonScores = annotationPearsonScores;
        this.mediaUnitScores = mediaUnitScores;
        this.mediaUnitPearsonScores = mediaUnitPearsonScores;
        this.metricsScores = metricsScores;
    }

    public ImmutableSet<ArtifactWithConfusionMatrix> getWorkerScores() {
        return this.workerScores;
    }

    public PearsonScores getWorkerPearsonScores() {
        return this.workerPearsonScores;
    }

    public WebMetricsScores getMetricsScores() {
        return this.metricsScores;
    }

    public ImmutableSet<ArtifactWithConfusionMatrix> getAnnotationScores() {
        return this.annotationScores;
    }

    public PearsonScores getAnnotationPearsonScores() {
        return this.annotationPearsonScores;
    }

    public ImmutableSet<ArtifactWithConfusionMatrix> getMediaUnitScores() {
        return this.mediaUnitScores;
    }

    public PearsonScores getMediaUnitPearsonScores() {
        return this.mediaUnitPearsonScores;
    }
}
