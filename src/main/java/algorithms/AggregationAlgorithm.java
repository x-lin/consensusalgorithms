package algorithms;

import com.google.common.collect.ImmutableSet;
import model.FinalDefect;
import web.SemesterSettings;

/**
 * @author LinX
 */
public interface AggregationAlgorithm {
    ImmutableSet<FinalDefect> getFinalDefects();

    SemesterSettings getSettings();
}
