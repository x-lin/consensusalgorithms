package algorithms.web;

import algorithms.truthinference.ChoiceId;
import algorithms.truthinference.CrowdtruthAlgorithm;
import algorithms.truthinference.ParticipantId;
import algorithms.truthinference.QuestionId;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author LinX
 */
public class WebMetricsScores {
    private final ImmutableMap<ChoiceId, Double> annotationQualityScores;

    private final ImmutableMap<ParticipantId, Double> workerQualityScores;

    private final ImmutableMap<QuestionId, Double> mediaUnitQualityScores;

    private final ImmutableMap<Map.Entry<QuestionId, ChoiceId>, Double> mediaUnitAnnotationScores;

    WebMetricsScores( final CrowdtruthAlgorithm.MetricsScores scores ) {
        this.annotationQualityScores = scores.getAnnotationQualityScores();
        this.workerQualityScores = scores.getWorkerQualityScores();
        this.mediaUnitQualityScores = scores.getMediaUnitQualityScores();
        this.mediaUnitAnnotationScores = scores.getMediaUnitAnnotationScores();
    }

    public ImmutableMap<ChoiceId, Double> getAnnotationQualityScores() {
        return this.annotationQualityScores;
    }

    public ImmutableMap<ParticipantId, Double> getWorkerQualityScores() {
        return this.workerQualityScores;
    }

    public ImmutableMap<QuestionId, Double> getMediaUnitQualityScores() {
        return this.mediaUnitQualityScores;
    }

    public ImmutableMap<Map.Entry<QuestionId, ChoiceId>, Double> getMediaUnitAnnotationScores() {
        return this.mediaUnitAnnotationScores;
    }

    @Override
    public String toString() {
        return "WebMetricsScores{" +
                "annotationQualityScores=" + this.annotationQualityScores +
                ", workerQualityScores=" + this.workerQualityScores +
                ", mediaUnitQualityScores=" + this.mediaUnitQualityScores +
                ", mediaUnitAnnotationScores=" + this.mediaUnitAnnotationScores +
                '}';
    }
}
