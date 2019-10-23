package algorithms.dawidskene;

import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author LinX
 */
public class Observations {
    private static final Logger LOG = LoggerFactory.getLogger( Observations.class );

    private final ImmutableMap<PatientId, ImmutableSet<Observation>> byPatients;

    private final ImmutableMap<ObserverId, ImmutableSet<Observation>> byObservers;

    private final ImmutableMap<Label, ImmutableMultiset<Observation>> byLabels;

    private final ImmutableMap<ObserverId, ImmutableMap<PatientId, ImmutableSet<Observation>>> byObserversForPatients;

    private final ImmutableSet<PatientId> patients;

    private final ImmutableSet<ObserverId> observers;

    private final ImmutableSet<Label> labels;

    public Observations( final Set<Observation> observations ) {
        final Map<PatientId, Set<Observation>> byPatients = Maps.newHashMap();
        final Map<ObserverId, Set<Observation>> byObservers = Maps.newHashMap();
        final Map<Label, List<Observation>> byLabel = Maps.newHashMap();
        final Map<ObserverId, Map<PatientId, Set<Observation>>> byObserversForPatients = Maps.newHashMap();
        final Set<PatientId> patients = Sets.newHashSet();
        final Set<ObserverId> observers = Sets.newHashSet();
        final Set<Label> labels = Sets.newHashSet();

        observations.forEach( observation -> {
            byPatients.computeIfAbsent( observation.getPatientId(), i -> Sets.newHashSet() ).add( observation );
            byObservers.computeIfAbsent( observation.getObserverId(), i -> Sets.newHashSet() ).add( observation );
            observation.getLabels().forEach( l -> {
                byLabel.computeIfAbsent( l, id -> Lists.newArrayList() ).add( observation );
            } );
            byObserversForPatients.computeIfAbsent( observation.getObserverId(), i -> Maps.newHashMap() )
                                  .computeIfAbsent( observation.getPatientId(), i -> Sets.newHashSet() ).add(
                    observation );
            patients.add( observation.getPatientId() );
            observers.add( observation.getObserverId() );
            labels.addAll( observation.getLabels() );
        } );

        this.byPatients = byPatients.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> ImmutableSet.copyOf( e.getValue() ) ) );
        this.byObservers = byObservers.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> ImmutableSet.copyOf( e.getValue() ) ) );
        this.byLabels = byLabel.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> ImmutableMultiset.copyOf( e.getValue() ) ) );
        this.byObserversForPatients = byObserversForPatients.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey,
                        e -> e.getValue().entrySet().stream().collect( ImmutableMap
                                .toImmutableMap( Map.Entry::getKey, e2 -> ImmutableSet.copyOf( e2.getValue() ) ) ) ) );
        this.patients = ImmutableSet.copyOf( patients );
        this.observers = ImmutableSet.copyOf( observers );
        this.labels = ImmutableSet.copyOf( labels );

        LOG.info( "Patients={} (#{}), observers={} (#{}), labels={} (#{})", this.patients, this.patients.size(),
                this.observers, this.observers.size(), this.labels, this.labels.size() );
    }

    public ImmutableSet<Observation> getObservations( final PatientId patientId ) {
        return this.byPatients.getOrDefault( patientId, ImmutableSet.of() );
    }

    public ImmutableSet<Observation> getObservations( final ObserverId observerId ) {
        return this.byObservers.getOrDefault( observerId, ImmutableSet.of() );
    }

    public ImmutableMultiset<Observation> getObservations( final Label label ) {
        return this.byLabels.getOrDefault( label, ImmutableMultiset.of() );
    }

    public ImmutableSet<Observation> getObservations( final ObserverId observer, final PatientId patient ) {
        return this.byObserversForPatients.getOrDefault( observer, ImmutableMap.of() ).getOrDefault( patient,
                ImmutableSet.of() );
    }

    public ImmutableSet<PatientId> getPatients() {
        return this.patients;
    }

    public ImmutableSet<ObserverId> getObservers() {
        return this.observers;
    }

    public ImmutableSet<Label> getLabels() {
        return this.labels;
    }
}
