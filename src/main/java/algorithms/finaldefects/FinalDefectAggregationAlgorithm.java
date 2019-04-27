package algorithms.finaldefects;

import com.google.common.collect.ImmutableMap;
import model.EmeAndScenarioId;
import model.FinalDefect;

/**
 * @author LinX
 */
public interface FinalDefectAggregationAlgorithm {
    ImmutableMap<EmeAndScenarioId, FinalDefect> getFinalDefects();

    SemesterSettings getSettings();
}
