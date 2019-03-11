package web;

import algorithms.crowdtruth.CrowdtruthRunner;
import algorithms.majorityvoting.MajorityVotingRunner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import model.FinalDefect;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import statistic.EvaluationResult;
import statistic.FinalDefectAnalyzer;
import statistic.WorkerEvaluationResultMetrics;
import statistic.WorkerQualityAnalyzer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author LinX
 */
@RestController
@RequestMapping("algorithms")
public class AlgorithmController {
    private final CrowdtruthRunner crowdtruthRunner = CrowdtruthRunner.create();

    private final Map<AlgorithmType, ImmutableSet<WorkerEvaluationResultMetrics>> workerCache = Maps.newHashMap();

    private final Map<AlgorithmType, ImmutableSet<EvaluationResult>> finalDefectsCache = Maps.newHashMap();

    @GetMapping("/finalDefects")
    public ImmutableSet<EvaluationResult> finalDefects(
            @RequestParam(value = "type", defaultValue = "CrowdTruth") final AlgorithmType type ) throws IOException,
            SQLException {
        if (!this.finalDefectsCache.containsKey( type )) {
            final ImmutableSet<FinalDefect> finalDefects;
            if (type == AlgorithmType.CrowdTruth) {
                finalDefects = this.crowdtruthRunner.getFinalDefects();
            } else {
                finalDefects = MajorityVotingRunner.calculateFinalDefects();
            }
            this.finalDefectsCache.put( type, FinalDefectAnalyzer.getFinalDefects( finalDefects ) );
        }

        return this.finalDefectsCache.get( type );
    }

    @GetMapping("/workers")
    public ImmutableSet<WorkerEvaluationResultMetrics> workers( @RequestParam(value = "type") final AlgorithmType
                                                                        type ) throws IOException,
            SQLException {
        if (!this.workerCache.containsKey( type )) {
            final ImmutableSet<WorkerEvaluationResultMetrics> evaluationResults = WorkerQualityAnalyzer.create()
                    .getEvaluationResults( this.crowdtruthRunner.getAllWorkerScores() );
            this.workerCache.put( type, evaluationResults );
        }
        return this.workerCache.get( type );
    }

    @GetMapping("/workerScoresPearson")
    public WorkerScoresPearson workerScoresPearson( @RequestParam(value = "algorithmType") final AlgorithmType
                                                            algorithmType ) {
        final ImmutableSet<WorkerEvaluationResultMetrics> evaluationResults = WorkerQualityAnalyzer.create()
                .getEvaluationResults( this.crowdtruthRunner.getAllWorkerScores() );
        return new WorkerScoresPearson( evaluationResults );
    }

    @GetMapping("/crowdTruthMetrics")
    public WebMetricsScores crowdTruthMetrics() throws IOException,
            SQLException {
        return new WebMetricsScores( this.crowdtruthRunner.getMetricsScores() );
    }
}
