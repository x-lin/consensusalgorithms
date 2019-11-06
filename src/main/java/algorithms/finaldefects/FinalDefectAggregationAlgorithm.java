package algorithms.finaldefects;

import algorithms.vericom.model.EmeAndScenarioId;
import algorithms.vericom.model.FinalDefect;
import algorithms.vericom.model.TaskWorkerId;
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
