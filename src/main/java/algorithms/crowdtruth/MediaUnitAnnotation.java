package algorithms.crowdtruth;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * MediaUnitAnnotation belonging to one task and can be chosen by worker or not.
 *
 * @author LinX
 */
public class MediaUnitAnnotation implements Comparable<MediaUnitAnnotation> {
    private final MediaUnitAnnotationId id;

    //vector space of annotation: results from workers for the annotation
    private final AnnotationVector annotationVector = new AnnotationVector();

    //media unit this annotation belongs to
    private final MediaUnitId mediaUnitId;

    public MediaUnitAnnotation( MediaUnitAnnotationId id, MediaUnitId mediaUnitId ) {
        this.id = id;
        this.mediaUnitId = mediaUnitId;
    }

    public MediaUnitAnnotationId getId() {
        return this.id;
    }

    public MediaUnitId getMediaUnitId() {
        return this.mediaUnitId;
    }

    //counts how many times annotation was chosen
    public AggregatedAnnotationResult getAggregatedAnnotationResult() {
        return new AggregatedAnnotationResult( (int) this.annotationVector.workerResults.values().stream().filter
                ( AnnotationResult::isChosen ).count() );
    }

    public AnnotationVector getAnnotationVector() {
        return this.annotationVector;
    }

    public void addWorkerAnnotationResult( Worker worker, AnnotationResult result ) {
        this.annotationVector.workerResults.put( worker, result );
    }

    @Override
    public String toString() {
        return "MediaUnitAnnotation{" +
                "id=" + this.id +
                ", annotationVector=" + this.annotationVector +
                ", mediaUnitId=" + this.mediaUnitId +
                '}';
    }

    @Override
    public int compareTo( MediaUnitAnnotation o ) {
        return this.id.toString().compareTo( o.getId().toString() );
    }

    public static class AnnotationVector {
        private final Map<Worker, AnnotationResult> workerResults = Maps.newHashMap();

        public Map<Worker, AnnotationResult> getWorkerResults() {
            return this.workerResults;
        }

        @Override
        public String toString() {
            return "AnnotationVector{" +
                    "workerResults=" + this.workerResults.entrySet().stream().collect( ImmutableMap.toImmutableMap( e ->
                    e.getKey().getId(), Map.Entry::getValue ) ) +
                    '}';
        }
    }
}
