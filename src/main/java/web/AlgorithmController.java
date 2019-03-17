package web;

import algorithms.crowdtruth.CrowdtruthRunner;
import algorithms.majorityvoting.MajorityVotingRunner;
import algorithms.majorityvoting.adapted.AdaptiveMajorityVoting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import statistic.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author LinX
 */
@RestController
@RequestMapping("algorithms")
public class AlgorithmController {
    private final CrowdtruthRunner crowdtruthRunner = CrowdtruthRunner.create();

    @GetMapping("/finalDefects/CrowdTruth")
    public WebFinalDefects crowdtruthFinalDefects() throws IOException,
            SQLException {
        return new WebFinalDefects( FinalDefectAnalyzer.getFinalDefects( this.crowdtruthRunner.getFinalDefects() ) );
    }

    @GetMapping("/finalDefects/MajorityVoting")
    public WebFinalDefects majorityVotingFinalDefects() throws IOException,
            SQLException {
        return new WebFinalDefects( FinalDefectAnalyzer.getFinalDefects( MajorityVotingRunner.calculateFinalDefects()
        ) );
    }

    @GetMapping("/finalDefects/AdaptiveMajorityVoting")
    public WebFinalDefects crowdtruthFinalDefects(
            @RequestParam(value = "threshold") final double threshold ) throws IOException,
            SQLException {
        return new WebFinalDefects( FinalDefectAnalyzer.getFinalDefects( new AdaptiveMajorityVoting().run( threshold
        ) ) );
    }


    @GetMapping("/workers")
    public CrowdtruthEvaluation workers() throws IOException,
            SQLException {
        final ImmutableSet<NamedEvaluationResultMetrics> workerScores = QualityAnalyzer.create()
                .getEvaluationResults( this.crowdtruthRunner.getAllWorkerScores() );
        final ImmutableSet<NamedEvaluationResultMetrics> annotationScores = QualityAnalyzer.create()
                .getEvaluationResults( this.crowdtruthRunner.getAllAnnotationScores() );
        final ImmutableSet<NamedEvaluationResultMetrics> mediaUnitScores = QualityAnalyzer.create()
                .getEvaluationResultsForMediaUnits( this.crowdtruthRunner.getAllMediaUnitScores() );
        return new CrowdtruthEvaluation( workerScores, new PearsonScores( workerScores ),
                annotationScores, new PearsonScores( annotationScores ),
                mediaUnitScores, new PearsonScores( mediaUnitScores ),
                new WebMetricsScores( this.crowdtruthRunner.getMetricsScores() ) );
    }

    @GetMapping("/all/metrics")
    public ImmutableMap<String, EvaluationResultMetrics> getAllMetrics() throws IOException, SQLException {
        return ImmutableMap.of( AlgorithmType.MajorityVoting.name(), new EvaluationResultMetrics( FinalDefectAnalyzer
                        .getFinalDefects( MajorityVotingRunner.calculateFinalDefects() ) ),
                AlgorithmType.CrowdTruth.name(), new EvaluationResultMetrics( FinalDefectAnalyzer.getFinalDefects( this
                        .crowdtruthRunner.getFinalDefects
                                () ) ),
                AlgorithmType.AdaptiveMajorityVoting + "t=0.1", new EvaluationResultMetrics( FinalDefectAnalyzer
                        .getFinalDefects( new AdaptiveMajorityVoting().run( 0.1
                        ) ) ),
                AlgorithmType.AdaptiveMajorityVoting + "t=0.9", new EvaluationResultMetrics( FinalDefectAnalyzer
                        .getFinalDefects( new AdaptiveMajorityVoting().run( 0.9
                        ) ) ) );
    }
}
