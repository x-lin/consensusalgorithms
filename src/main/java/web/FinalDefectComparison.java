package web;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import model.DefectType;
import model.FinalDefectType;

import java.util.Map;

/**
 * @author LinX
 */
public class FinalDefectComparison {
    private final String emeId;

    private final String scenarioId;

    private final DefectType trueDefectType;

    private final Map<String, FinalDefectType> finalDefectTypes;

    public FinalDefectComparison( final String emeId, final String scenarioId, final DefectType trueDefectType,
            final Map<String, FinalDefectType> results ) {
        Preconditions.checkArgument( !results.isEmpty() );
        this.scenarioId = scenarioId;
        this.emeId = emeId;
        this.trueDefectType = trueDefectType;
        this.finalDefectTypes = results;
    }

    public String getScenarioId() {
        return this.scenarioId;
    }

    public String getEmeId() {
        return this.emeId;
    }

    public DefectType getTrueDefectType() {
        return this.trueDefectType;
    }

    public ImmutableMap<String, FinalDefectType> getFinalDefectTypes() {
        return ImmutableMap.copyOf( this.finalDefectTypes );
    }
}
