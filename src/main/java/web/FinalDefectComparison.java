package web;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import model.DefectType;
import model.FinalDefectType;

/**
 * @author LinX
 */
public class FinalDefectComparison {
    private String emeId;

    private DefectType trueDefectType;

    private ImmutableMap<String, FinalDefectType> finalDefectTypes;

    public FinalDefectComparison( final String emeId, DefectType trueDefectType,
                                  final ImmutableMap<String, FinalDefectType> results ) {
        Preconditions.checkArgument(!results.isEmpty());
        this.emeId = emeId;
        this.trueDefectType = trueDefectType;
        this.finalDefectTypes = results;
    }

    public String getEmeId() {
        return emeId;
    }

    public DefectType getTrueDefectType() {
        return trueDefectType;
    }

    public ImmutableMap<String, FinalDefectType> getFinalDefectTypes() {
        return finalDefectTypes;
    }
}
