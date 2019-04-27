package algorithms.model;

import java.util.Objects;

/**
 * @author LinX
 */
public final class EmeAndScenarioId {
    private final EmeId emeId;

    private final ScenarioId scenarioId;

    public EmeAndScenarioId( final EmeId emeId, final ScenarioId scenarioId ) {
        this.emeId = emeId;
        this.scenarioId = scenarioId;
    }

    public EmeId getEmeId() {
        return this.emeId;
    }

    public ScenarioId getScenarioId() {
        return this.scenarioId;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmeAndScenarioId that = (EmeAndScenarioId) o;
        return Objects.equals( this.emeId, that.emeId ) &&
                Objects.equals( this.scenarioId, that.scenarioId );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.emeId, this.scenarioId );
    }

    @Override
    public String toString() {
        return this.emeId + "//" + this.scenarioId;
    }

    public static EmeAndScenarioId fromString( final String string ) {
        final String[] split = string.split( "//" );
        return new EmeAndScenarioId( new EmeId( split[0] ), new ScenarioId( split[1] ) );
    }
}
