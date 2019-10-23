package algorithms.dawidskene;

import com.google.common.collect.ImmutableSet;

import java.util.Objects;

/**
 * @author LinX
 */
public final class Patient {
    private final PatientId patientId;

    private final ImmutableSet<ObserverId> observers;

    public Patient( final PatientId patientId, final ImmutableSet<ObserverId> observers ) {
        this.patientId = patientId;
        this.observers = observers;
    }

    public PatientId getPatientId() {
        return this.patientId;
    }

    public ImmutableSet<ObserverId> getObservers() {
        return this.observers;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId=" + this.patientId +
                ", observers=" + this.observers +
                '}';
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Patient patient = (Patient) o;
        return Objects.equals( this.patientId, patient.patientId ) &&
                Objects.equals( this.observers, patient.observers );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.patientId, this.observers );
    }
}
