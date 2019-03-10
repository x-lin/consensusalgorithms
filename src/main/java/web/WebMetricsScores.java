package web;

import algorithms.crowdtruth.*;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author LinX
 */
public class WebMetricsScores {
    private final ImmutableMap<AnnotationName, Double> annotationQualityScores;

    private final ImmutableMap<WorkerId, Double> workerQualityScores;

    private final ImmutableMap<MediaUnitId, Double> mediaUnitQualityScores;

    private final ImmutableMap<MediaUnitAnnotationId, Double> mediaUnitAnnotationScores;

    WebMetricsScores( final Metrics.MetricsScores scores ) {
        this.annotationQualityScores = scores.getAnnotationQualityScores().entrySet().stream().collect( ImmutableMap
                .toImmutableMap( e -> e.getKey().getName(), Map.Entry::getValue ) );
        this.workerQualityScores = scores.getWorkerQualityScores().entrySet().stream().collect( ImmutableMap
                .toImmutableMap( e -> e.getKey().getId(), Map.Entry::getValue ) );
        this.mediaUnitQualityScores = scores.getMediaUnitQualityScores().entrySet().stream().collect( ImmutableMap
                .toImmutableMap( e -> e.getKey().getId(), Map.Entry::getValue ) );
        this.mediaUnitAnnotationScores = scores.getMediaUnitAnnotationScores().entrySet().stream().collect( ImmutableMap
                .toImmutableMap( e -> e.getKey().getId(), Map.Entry::getValue ) );
    }

    public ImmutableMap<AnnotationName, Double> getAnnotationQualityScores() {
        return this.annotationQualityScores;
    }

    public ImmutableMap<WorkerId, Double> getWorkerQualityScores() {
        return this.workerQualityScores;
    }

    public ImmutableMap<MediaUnitId, Double> getMediaUnitQualityScores() {
        return this.mediaUnitQualityScores;
    }

    public ImmutableMap<MediaUnitAnnotationId, Double> getMediaUnitAnnotationScores() {
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
