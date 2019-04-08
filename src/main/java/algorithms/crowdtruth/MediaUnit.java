package algorithms.crowdtruth;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Equals a crowdsourcing task. Contains multiple annotation options (infinitely many
 * for open task).
 *
 * @author LinX
 */
public class MediaUnit implements Comparable<MediaUnit> {
    private final MediaUnitId id;

    private final ImmutableBiMap<MediaUnitAnnotationId, MediaUnitAnnotation> availableAnnotations;

    private final Set<Worker> workers = Sets.newHashSet();

    public MediaUnit( final MediaUnitId id, final Set<MediaUnitAnnotationId> availableAnnotations ) {
        this.id = id;
        this.availableAnnotations = ImmutableBiMap.copyOf( Maps.toMap( availableAnnotations, a -> new
                MediaUnitAnnotation( a,
                id ) ) );
    }

    public void annotate( final Worker worker, final MediaUnitAnnotationId mediaUnitAnnotationId,
            final boolean chosen ) {
        Preconditions.checkArgument( this.availableAnnotations.keySet().contains( mediaUnitAnnotationId ), "Unknown " +
                "mediaUnitAnnotation" +
                " %s" +
                " for " +
                "media unit %s.", mediaUnitAnnotationId, this.id );
        final MediaUnitAnnotation mediaUnitAnnotation = this.availableAnnotations.get( mediaUnitAnnotationId );
        final AnnotationResult result = AnnotationResult.create( chosen );
        worker.annotate( this, mediaUnitAnnotation, result );
        mediaUnitAnnotation.addWorkerAnnotationResult( worker, result );
        this.workers.add( worker );
    }

    //how often each option was annotated
    public ResultVector getMediaVector() {
        return new ResultVector( Maps.toMap( this.availableAnnotations.values(), MediaUnitAnnotation
                ::getAggregatedAnnotationResult ) );
    }

    public Set<Worker> getWorkers() {
        return this.workers;
    }

    public MediaUnitId getId() {
        return this.id;
    }

    public boolean hasAnnotation( final MediaUnitAnnotationId mediaUnitAnnotationId ) {
        return this.availableAnnotations.keySet().stream().anyMatch( a -> Objects.equals( mediaUnitAnnotationId
                .getName(), a.getName() ) );
    }

    public ResultVector getMediaUnitVectorExcludingWorker( final Worker worker ) {
        return new ResultVector( getMediaVector().getResult().entrySet().stream().collect
                ( ImmutableMap
                        .toImmutableMap( Map.Entry::getKey, e -> getAggregatedAnnotationResult( e.getKey(), e
                                        .getValue(),
                                worker ) ) ) );
    }

    private AggregatedAnnotationResult getAggregatedAnnotationResult( final MediaUnitAnnotation mediaUnitAnnotation,
            final Result result, final Worker
            filteredWorker ) {
        return new AggregatedAnnotationResult( result.getScore() - filteredWorker
                .getWorkerVector( this ).getAnnotationResults().getResult( mediaUnitAnnotation ).getScore() );
    }

    @Override
    public String toString() {
        return "MediaUnit{" +
                "id=" + this.id +
                ", availableAnnotations=" + this.availableAnnotations +
                ", workers=" + this.workers +
                '}';
    }

    @Override
    public int compareTo( final MediaUnit o ) {
        return this.id.toString().compareTo( o.getId().toString() );
    }
}
