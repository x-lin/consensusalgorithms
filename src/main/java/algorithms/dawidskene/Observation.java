package algorithms.dawidskene;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

/**
 * @author LinX
 */
public final class Observation {
    private final ObserverId observerId;

    private final PatientId patientId;

    private final ImmutableList<Label> labels;

    private Observation( final ObserverId observerId, final PatientId patientId,
            final ImmutableList<Label> labels ) {
        this.observerId = observerId;
        this.patientId = patientId;
        this.labels = labels;
    }

    public ObserverId getObserverId() {
        return this.observerId;
    }

    public PatientId getPatientId() {
        return this.patientId;
    }

    public ImmutableList<Label> getLabels() {
        return this.labels;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Observation that = (Observation) o;
        return Objects.equals( this.observerId, that.observerId ) &&
                Objects.equals( this.patientId, that.patientId ) &&
                Objects.equals( this.labels, that.labels );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.observerId, this.patientId, this.labels );
    }

    @Override
    public String toString() {
        return "Observation{" +
                "observerId=" + this.observerId +
                ", patientId=" + this.patientId +
                ", labels=" + this.labels +
                '}';
    }

    public static Observation create( final ObserverId observerId, final PatientId patientId,
            final List<Label> labels ) {
        return new Observation( observerId, patientId, ImmutableList.copyOf( labels ) );
    }
}
