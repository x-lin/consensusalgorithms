package algorithms.crowdtruth;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author LinX
 */
public class Worker implements Comparable<Worker> {
    private final WorkerId id;

    //vector space of worker: annotations chosen by worker for each media unit
    private final Map<MediaUnit, WorkerVector> annotations = Maps.newHashMap();

    public Worker( WorkerId id ) {
        this.id = id;
    }

    public void annotate( MediaUnit mediaUnit, MediaUnitAnnotation mediaUnitAnnotation, AnnotationResult result ) {
        WorkerVector workerVector = this.annotations.computeIfAbsent( mediaUnit, m -> new WorkerVector() );
        workerVector.annotationResults.put( mediaUnitAnnotation, result );
    }

    public WorkerVector getWorkerVector( MediaUnit mediaUnit ) {
        return this.annotations.get( mediaUnit );
    }

    public ImmutableSet<MediaUnit> getAnnotatedMediaUnits() {
        return ImmutableSet.copyOf( this.annotations.keySet() );
    }

    @Override
    public String toString() {
        return "Worker{" +
                "id=" + this.id +
                ", annotations=" + this.annotations.entrySet().stream().collect( ImmutableMap.toImmutableMap( e -> e
                .getKey().getId(), Map.Entry::getValue ) ) +
                '}';
    }

    public WorkerId getId() {
        return this.id;
    }

    @Override
    public int compareTo( Worker o ) {
        return this.id.toString().compareTo( o.getId().toString() );
    }

    public static class WorkerVector {
        private final Map<MediaUnitAnnotation, Result> annotationResults = Maps.newHashMap();

        public ResultVector getAnnotationResults() {
            return new ResultVector( this.annotationResults );
        }

        @Override
        public String toString() {
            return "WorkerVector{" +
                    "annotationResults=" + this.annotationResults.entrySet().stream().collect( ImmutableMap
                    .toImmutableMap( e -> e.getKey().getId(), Map.Entry::getValue ) ) + '}';
        }
    }
}
