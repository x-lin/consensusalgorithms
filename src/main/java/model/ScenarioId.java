package model;

import java.util.Objects;

/**
 * @author LinX
 */
public final class ScenarioId {
    private final String id;

    public ScenarioId( final String id ) {
        this.id = id;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ScenarioId that = (ScenarioId) o;
        return Objects.equals( this.id, that.id );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.id );
    }

    @Override
    public String toString() {
        return this.id;
    }
}
