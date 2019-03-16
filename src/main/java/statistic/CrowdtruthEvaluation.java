package statistic;

import com.google.common.collect.ImmutableSet;
import web.PearsonScores;
import web.WebMetricsScores;

/**
 * @author LinX
 */
public class CrowdtruthEvaluation {
    private final ImmutableSet<NamedEvaluationResultMetrics> workerScores;

    private final PearsonScores workerPearsonScores;

    private final ImmutableSet<NamedEvaluationResultMetrics> annotationScores;

    private final PearsonScores annotationPearsonScores;

    private final ImmutableSet<NamedEvaluationResultMetrics> mediaUnitScores;

    private final PearsonScores mediaUnitPearsonScores;

    private final WebMetricsScores metricsScores;

    public CrowdtruthEvaluation( final ImmutableSet<NamedEvaluationResultMetrics> workerScores, final
    PearsonScores workerPearsonScores, final ImmutableSet<NamedEvaluationResultMetrics> annotationScores, final
                                 PearsonScores annotationPearsonScores, final
                                 ImmutableSet<NamedEvaluationResultMetrics> mediaUnitScores, final
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

    public ImmutableSet<NamedEvaluationResultMetrics> getWorkerScores() {
        return this.workerScores;
    }

    public PearsonScores getWorkerPearsonScores() {
        return this.workerPearsonScores;
    }

    public WebMetricsScores getMetricsScores() {
        return this.metricsScores;
    }

    public ImmutableSet<NamedEvaluationResultMetrics> getAnnotationScores() {
        return this.annotationScores;
    }

    public PearsonScores getAnnotationPearsonScores() {
        return this.annotationPearsonScores;
    }

    public ImmutableSet<NamedEvaluationResultMetrics> getMediaUnitScores() {
        return this.mediaUnitScores;
    }

    public PearsonScores getMediaUnitPearsonScores() {
        return this.mediaUnitPearsonScores;
    }
}
