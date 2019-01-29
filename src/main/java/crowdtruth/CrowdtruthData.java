package crowdtruth;

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
    private final String mediaUnitId;

    private final String id;

    private final String workerId;

    private final String chosenAnnotation;

    public CrowdtruthData( String mediaUnitId, String id, String workerId, String
            chosenAnnotation ) {
        this.mediaUnitId = mediaUnitId;
        this.id = id;
        this.workerId = workerId;
        this.chosenAnnotation = chosenAnnotation;
    }

    public String getMediaUnitId() {
        return this.mediaUnitId;
    }

    public String getId() {
        return this.id;
    }

    public String getWorkerId() {
        return this.workerId;
    }

    public String getChosenAnnotation() {
        return this.chosenAnnotation;
    }

    @Override
    public String toString() {
        return "CrowdtruthData{" +
                "mediaUnitId='" + this.mediaUnitId + '\'' +
                ", id='" + this.id + '\'' +
                ", workerId='" + this.workerId + '\'' +
                ", chosenAnnotation='" + this.chosenAnnotation + '\'' +
                '}';
    }

    public static ImmutableSet<MediaUnit> annotate( Collection<CrowdtruthData> data, String...
            allAnnotationNames ) {
        Map<MediaUnitId, Map<WorkerId, Set<MediaUnitAnnotationId>>> knownAnnotations = Maps.newHashMap();
        Set<MediaUnit> mediaUnits = Sets.newHashSet();
        data.forEach( d -> {
            MediaUnitId mediaUnitId = new MediaUnitId( d.getMediaUnitId() );
            Map<WorkerId, Set<MediaUnitAnnotationId>> workers = knownAnnotations.computeIfAbsent( mediaUnitId, u ->
                    Maps.newHashMap() );
            Set<MediaUnitAnnotationId> annotations = workers.computeIfAbsent( new WorkerId( d.getWorkerId() ), a -> Sets
                    .newHashSet() );
            annotations.add( new MediaUnitAnnotationId( d.getChosenAnnotation(), mediaUnitId ) );
        } );

        Map<WorkerId, Worker> workers = knownAnnotations.values().stream().flatMap( a -> a.keySet().stream() )
                .distinct().collect( ImmutableMap.toImmutableMap( Function.identity(), Worker::new ) );

        knownAnnotations.forEach( ( mediaUnitId, workerAnnotations ) -> {
            ImmutableSet<MediaUnitAnnotationId> allAnnotations = Arrays.stream(
                    allAnnotationNames )
                    .map( a -> new MediaUnitAnnotationId( String.valueOf( a ), mediaUnitId ) )
                    .collect( ImmutableSet.toImmutableSet() );
            MediaUnit mediaUnit = new MediaUnit( mediaUnitId, allAnnotationNames.length > 0 ? allAnnotations :
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
