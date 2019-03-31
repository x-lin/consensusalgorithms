package web;

import algorithms.crowdtruth.CrowdtruthRunner;
import algorithms.majorityvoting.MajorityVotingRunner;
import algorithms.majorityvoting.adapted.AdaptiveMajorityVoting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
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

/**
 * @author LinX
 */
@RestController
@RequestMapping("algorithms")
public class AlgorithmController {
    private final Map<Semester, CrowdtruthRunner> crowdtruthRunners = ImmutableMap.of( Semester.WS2017,
            CrowdtruthRunner.create( SemesterSettings.get( Semester.WS2017 ) ), Semester.SS2018, CrowdtruthRunner
                    .create( SemesterSettings.get( Semester.SS2018 ) ) );

    @GetMapping("/finalDefects/CrowdTruth")
    public WebFinalDefects crowdtruthFinalDefects(
            @RequestParam(value = "semester", defaultValue = "WS2017") final Semester semester ) throws
            IOException,
            SQLException {
        return new WebFinalDefects( FinalDefectAnalyzer.getFinalDefects( this.crowdtruthRunners.get( semester )
                .getFinalDefects(), SemesterSettings.get( semester ) ) );
    }

    @GetMapping("/finalDefects/MajorityVoting")
    public WebFinalDefects majorityVotingFinalDefects(
            @RequestParam(value = "semester", defaultValue = "WS2017") final Semester semester ) throws IOException,
            SQLException {
        return new WebFinalDefects( FinalDefectAnalyzer.getFinalDefects( MajorityVotingRunner.calculateFinalDefects(
                SemesterSettings.get( semester ) ), SemesterSettings.get( semester )
        ) );
    }

    @GetMapping("/finalDefects/AdaptiveMajorityVoting")
    public WebFinalDefects crowdtruthFinalDefects(
            @RequestParam(value = "threshold") final double threshold,
            @RequestParam(value = "semester", defaultValue = "WS2017") final Semester semester ) throws IOException,
            SQLException {
        return new WebFinalDefects( FinalDefectAnalyzer.getFinalDefects( new AdaptiveMajorityVoting().run( threshold,
                SemesterSettings.get( semester )
        ), SemesterSettings.get( semester ) ) );
    }


    @GetMapping("/workers")
    public CrowdtruthEvaluation workers(
            @RequestParam(value = "semester", defaultValue = "WS2017") final Semester semester ) throws IOException,
            SQLException {
        final CrowdtruthRunner crowdtruthRunner = this.crowdtruthRunners.get( semester );
        final ImmutableSet<NamedEvaluationResultMetrics> workerScores = QualityAnalyzer.create()
                .getEvaluationResults( crowdtruthRunner.getSemesterSettings(), crowdtruthRunner.getAllWorkerScores() );
        final ImmutableSet<NamedEvaluationResultMetrics> annotationScores = QualityAnalyzer.create()
                .getEvaluationResults( crowdtruthRunner.getSemesterSettings(), crowdtruthRunner
                        .getAllAnnotationScores() );
        final ImmutableSet<NamedEvaluationResultMetrics> mediaUnitScores = QualityAnalyzer.create()
                .getEvaluationResultsForMediaUnits( crowdtruthRunner.getSemesterSettings(), crowdtruthRunner
                        .getAllMediaUnitScores() );
        return new CrowdtruthEvaluation( workerScores, new PearsonScores( workerScores ),
                annotationScores, new PearsonScores( annotationScores ),
                mediaUnitScores, new PearsonScores( mediaUnitScores ),
                new WebMetricsScores( crowdtruthRunner.getMetricsScores() ) );
    }

    @GetMapping("/all/metrics")
    public ImmutableMap<String, EvaluationResultMetrics> getAllMetrics(
            @RequestParam(value = "semester", defaultValue = "WS2017") final Semester semester ) throws IOException,
            SQLException {
        return ImmutableMap.of( AlgorithmType.MajorityVoting.name(), new EvaluationResultMetrics( FinalDefectAnalyzer
                        .getFinalDefects( MajorityVotingRunner.calculateFinalDefects( SemesterSettings.get( semester
                        ) ), SemesterSettings.get( semester ) ) ),
                AlgorithmType.CrowdTruth.name(), new EvaluationResultMetrics( FinalDefectAnalyzer.getFinalDefects(
                        this.crowdtruthRunners.get( semester ).getFinalDefects(), SemesterSettings.get( semester ) ) ),
                AlgorithmType.AdaptiveMajorityVoting + ";t=0.1", new EvaluationResultMetrics( FinalDefectAnalyzer
                        .getFinalDefects( new AdaptiveMajorityVoting().run( 0.1, SemesterSettings.get( semester ) ),
                                SemesterSettings.get( semester ) ) ),
                AlgorithmType.AdaptiveMajorityVoting + ";t=0.9", new EvaluationResultMetrics( FinalDefectAnalyzer
                        .getFinalDefects( new AdaptiveMajorityVoting().run( 0.9, SemesterSettings.get( semester )
                        ), SemesterSettings.get( semester ) ) ) );
    }

    @GetMapping("/all/finalDefects")
    public ImmutableSet<FinalDefectComparison> getFinalDefectComparison(
            @RequestParam(value = "semester", defaultValue = "WS2017") final Semester semester ) throws IOException,
            SQLException {
        final SemesterSettings settings = SemesterSettings.get( semester );
        final ImmutableMap<String, EvaluationResult> majorityVotingResults = FinalDefectAnalyzer
                .getFinalDefects( MajorityVotingRunner.calculateFinalDefects( settings ),
                        settings )
                .stream().collect(
                        ImmutableMap.toImmutableMap( EvaluationResult::getEmeId, Function.identity() ) );

        final ImmutableMap<String, EvaluationResult> crowdtruthResults = FinalDefectAnalyzer
                .getFinalDefects( this.crowdtruthRunners.get( semester ).getFinalDefects(), settings ).stream().collect(
                        ImmutableMap.toImmutableMap( EvaluationResult::getEmeId, Function.identity() ) );

        final ImmutableMap<String, EvaluationResult> adaptiveMajorityVotingZeroOneResults = FinalDefectAnalyzer
                .getFinalDefects( new AdaptiveMajorityVoting().run( 0.1, settings ),
                        settings
                ).stream().collect(
                        ImmutableMap.toImmutableMap( EvaluationResult::getEmeId, Function.identity() ) );

        final ImmutableMap<String, EvaluationResult> adaptiveMajorityVotingZeroNineResults = FinalDefectAnalyzer
                .getFinalDefects( new AdaptiveMajorityVoting().run( 0.9, settings ),
                        settings
                ).stream().collect(
                        ImmutableMap.toImmutableMap( EvaluationResult::getEmeId, Function.identity() ) );

        final ImmutableSet<String> allEmes = Streams.concat( majorityVotingResults.keySet().stream(),
                crowdtruthResults.keySet().stream(),
                adaptiveMajorityVotingZeroOneResults.keySet().stream(), adaptiveMajorityVotingZeroNineResults.keySet
                        ().stream() )
                .collect( ImmutableSet.toImmutableSet() );

        return allEmes.stream().map( e -> {
            ImmutableMap.Builder<String, EvaluationResult> builder = ImmutableMap.builder();
            Optional.ofNullable( majorityVotingResults.get( e ) )
                    .ifPresent( r -> builder.put( AlgorithmType.MajorityVoting.name(), r ) );
            Optional.ofNullable( crowdtruthResults.get( e ) )
                    .ifPresent( r -> builder.put( AlgorithmType.CrowdTruth.name(), r ) );
            Optional.ofNullable( adaptiveMajorityVotingZeroOneResults.get( e ) )
                    .ifPresent( r -> builder.put( AlgorithmType.AdaptiveMajorityVoting + ";0.1", r ) );
            Optional.ofNullable( adaptiveMajorityVotingZeroNineResults.get( e ) )
                    .ifPresent( r -> builder.put( AlgorithmType.AdaptiveMajorityVoting + ";0.9", r ) );
            final ImmutableMap<String, EvaluationResult> results = builder.build();
            return new FinalDefectComparison( e, results.values().iterator().next().getTrueDefectType(),
                    results.entrySet().stream().collect( ImmutableMap.toImmutableMap( Map.Entry::getKey,
                            r -> r.getValue().getFinalDefectType() ) ) );
        } ).collect( ImmutableSet.toImmutableSet() );
    }
}
