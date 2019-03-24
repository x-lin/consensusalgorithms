package web;

import algorithms.crowdtruth.CrowdtruthRunner;
import algorithms.majorityvoting.MajorityVotingRunner;
import algorithms.majorityvoting.adapted.AdaptiveMajorityVoting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import model.FinalDefect;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import statistic.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                AlgorithmType.AdaptiveMajorityVoting + ";t=0.1", new EvaluationResultMetrics( FinalDefectAnalyzer
                        .getFinalDefects( new AdaptiveMajorityVoting().run( 0.1
                        ) ) ),
                AlgorithmType.AdaptiveMajorityVoting + ";t=0.9", new EvaluationResultMetrics( FinalDefectAnalyzer
                        .getFinalDefects( new AdaptiveMajorityVoting().run( 0.9
                        ) ) ) );
    }

    @GetMapping("/all/finalDefects")
    public ImmutableSet<FinalDefectComparison> getFinalDefectComparison() throws IOException, SQLException {
        final ImmutableMap<String, EvaluationResult> majorityVotingResults = FinalDefectAnalyzer
                .getFinalDefects( MajorityVotingRunner.calculateFinalDefects() ).stream().collect(
                        ImmutableMap.toImmutableMap( EvaluationResult::getEmeId, Function.identity() ));

        final ImmutableMap<String, EvaluationResult> crowdtruthResults = FinalDefectAnalyzer
                .getFinalDefects( this.crowdtruthRunner.getFinalDefects() ).stream().collect(
                        ImmutableMap.toImmutableMap( EvaluationResult::getEmeId, Function.identity() ));

        final ImmutableMap<String, EvaluationResult> adaptiveMajorityVotingZeroOneResults = FinalDefectAnalyzer
                .getFinalDefects( new AdaptiveMajorityVoting().run( 0.1) ).stream().collect(
                        ImmutableMap.toImmutableMap( EvaluationResult::getEmeId, Function.identity() ));

        final ImmutableMap<String, EvaluationResult> adaptiveMajorityVotingZeroNineResults = FinalDefectAnalyzer
                .getFinalDefects( new AdaptiveMajorityVoting().run( 0.9) ).stream().collect(
                        ImmutableMap.toImmutableMap( EvaluationResult::getEmeId, Function.identity() ));

        ImmutableSet<String> allEmes = Streams.concat(majorityVotingResults.keySet().stream(), crowdtruthResults.keySet().stream(),
                adaptiveMajorityVotingZeroOneResults.keySet().stream(), adaptiveMajorityVotingZeroNineResults.keySet().stream())
                .collect( ImmutableSet.toImmutableSet() );

        return allEmes.stream().map( e -> {
            ImmutableMap.Builder<String, EvaluationResult> builder = ImmutableMap.builder();
            Optional.ofNullable( majorityVotingResults.get( e ) )
                    .ifPresent(r -> builder.put( AlgorithmType.MajorityVoting.name(), r ) );
            Optional.ofNullable( crowdtruthResults.get( e ) )
                    .ifPresent( r -> builder.put( AlgorithmType.CrowdTruth.name(), r ) );
            Optional.ofNullable( adaptiveMajorityVotingZeroOneResults.get( e ) )
                    .ifPresent( r -> builder.put( AlgorithmType.AdaptiveMajorityVoting + ";0.1", r) );
            Optional.ofNullable( adaptiveMajorityVotingZeroNineResults.get( e ) )
                    .ifPresent( r -> builder.put( AlgorithmType.AdaptiveMajorityVoting + ";0.9", r ) );
            final ImmutableMap<String, EvaluationResult> results = builder.build();
            return new FinalDefectComparison( e, results.values().iterator().next().getTrueDefectType(),
                    results.entrySet().stream().collect( ImmutableMap.toImmutableMap( Map.Entry::getKey,
                            r -> r.getValue().getFinalDefectType() ) ) );
        }  ).collect( ImmutableSet.toImmutableSet() );
    }
}
