package algorithms.finaldefects;

import algorithms.model.EmeAndScenarioId;
import algorithms.model.FinalDefect;
import algorithms.model.TaskWorkerId;
import com.google.common.collect.ImmutableMap;

/**
 * @author LinX
 */
public interface FinalDefectAggregationAlgorithm {
    ImmutableMap<EmeAndScenarioId, FinalDefect> getFinalDefects();

    SemesterSettings getSettings();

    ImmutableMap<String, String> getParameters();

    ImmutableMap<TaskWorkerId, WorkerDefectReports> getWorkerDefectReports();
}
