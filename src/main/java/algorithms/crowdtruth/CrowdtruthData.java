package algorithms.crowdtruth;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author LinX
 */
public class CrowdtruthData {
    private final MediaUnitId mediaUnitId;

    private final WorkerId workerId;

    private final AnnotationName chosenAnnotation;

    public CrowdtruthData( final MediaUnitId mediaUnitId, final WorkerId workerId,
            final AnnotationName chosenAnnotation ) {
        this.mediaUnitId = mediaUnitId;
        this.workerId = workerId;
        this.chosenAnnotation = chosenAnnotation;
    }

    public MediaUnitId getMediaUnitId() {
        return this.mediaUnitId;
    }

    public WorkerId getWorkerId() {
        return this.workerId;
    }

    public AnnotationName getChosenAnnotation() {
        return this.chosenAnnotation;
    }

    @Override
    public String toString() {
        return "CrowdtruthData{" +
                "mediaUnitId='" + this.mediaUnitId + '\'' +
                ", workerId='" + this.workerId + '\'' +
                ", chosenAnnotation='" + this.chosenAnnotation + '\'' +
                '}';
    }

    public static ImmutableSet<MediaUnit> annotate( final Collection<CrowdtruthData> data, final AnnotationName...
            allAnnotationNames ) {
        final Map<MediaUnitId, Map<WorkerId, Set<MediaUnitAnnotationId>>> knownAnnotations = Maps.newHashMap();
        final Set<MediaUnit> mediaUnits = Sets.newHashSet();
        data.forEach( d -> {
            final Map<WorkerId, Set<MediaUnitAnnotationId>> workers = knownAnnotations.computeIfAbsent(
                    d.getMediaUnitId(),
                    u ->
                            Maps.newHashMap() );
            final Set<MediaUnitAnnotationId> annotations = workers.computeIfAbsent( d.getWorkerId(),
                    a -> Sets.newHashSet() );
            annotations.add( new MediaUnitAnnotationId( d.getChosenAnnotation(), d.getMediaUnitId() ) );
        } );

        final Map<WorkerId, Worker> workers = knownAnnotations.values().stream().flatMap( a -> a.keySet().stream() )
                                                              .distinct().collect(
                        ImmutableMap.toImmutableMap( Function.identity(), Worker::new ) );

        knownAnnotations.forEach( ( mediaUnitId, workerAnnotations ) -> {
            final ImmutableSet<MediaUnitAnnotationId> allAnnotations = Arrays.stream(
                    allAnnotationNames ).map( a -> new MediaUnitAnnotationId( a, mediaUnitId ) )
                                                                             .collect( ImmutableSet.toImmutableSet() );
            final MediaUnit mediaUnit = new MediaUnit( mediaUnitId, allAnnotationNames.length > 0 ? allAnnotations :
                    workerAnnotations.values()
                                     .stream().flatMap( Collection::stream ).collect( ImmutableSet.toImmutableSet() ) );
            mediaUnits.add( mediaUnit );
            workerAnnotations.forEach( (( workerId, annotationIds ) -> {
                annotationIds.forEach( mediaUnitAnnotationId -> mediaUnit.annotate( workers.get( workerId ),
                        mediaUnitAnnotationId,
                        true ) );
                if (allAnnotationNames.length > 0) {
                    allAnnotations.stream().filter( a -> !annotationIds.contains( a ) ).forEach( a -> {
                        mediaUnit.annotate( workers.get( workerId ), a, false );
                    } );
                }
            }) );
        } );

        return ImmutableSet.copyOf( mediaUnits );
    }
}
