package algorithms.dawidskene;

import com.google.common.collect.ImmutableMap;

/**
 * @author LinX
 */
public final class Observer {
    private final ImmutableMap<PatientId, Label> observedValues;

    public Observer( final ImmutableMap<PatientId, Label> observedValues ) {
        this.observedValues = observedValues;
    }

    public ImmutableMap<PatientId, Label> getObservedValues() {
        return this.observedValues;
    }
}
