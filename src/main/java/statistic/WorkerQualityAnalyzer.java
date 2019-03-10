package statistic;

import algorithms.crowdtruth.CrowdtruthRunner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.opencsv.CSVWriter;
import model.FinalDefect;
import model.TrueDefect;
import org.jooq.lambda.UncheckedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * @author LinX
 */
public class WorkerQualityAnalyzer {
    private WorkerQualityAnalyzer() {
        //purposely left empty
    }

    private void write( final ImmutableSet<CrowdtruthRunner.SampledWorker> sampledWorkers, final String
            outputFilePath ) {
        final ImmutableMap<String, TrueDefect> trueDefects;
        try {
            trueDefects = AllTrueDefectsMixin.findAllTrueDefects().stream()
                    .collect( ImmutableMap.toImmutableMap( TrueDefect::getAboutEmEid, Function.identity() ) );

            try (CSVWriter finalDefectsCsv = new CSVWriter( Files.newBufferedWriter( Paths.get(
                    outputFilePath ) ) )) {
                finalDefectsCsv.writeNext( new String[]{"workerId", "Worker Quality Score (WQS)", "TP", "TN", "FP",
                        "FN", "Precision", "Recall", "F-Measure", "Accuracy"} );

                sampledWorkers.forEach( worker -> {
                    final ImmutableMap<String, FinalDefect> workerFinalDefects = worker.getFinalDefects()
                            .stream().collect( ImmutableMap.toImmutableMap( FinalDefect::getEmeId, Function.identity
                                    () ) );
                    final Set<EvaluationResult> results = Sets.newHashSet();
                    workerFinalDefects.values().forEach( fd -> {
                        final EvaluationResult evaluationResult = Optional.ofNullable( trueDefects.get( fd.getEmeId()
                        ) ).map( td -> new EvaluationResult(
                                fd, td ) ).orElseGet( () -> new EvaluationResult( fd ) );
                        results.add( evaluationResult );
                    } );

                    final EvaluationResultMetrics metrics = new EvaluationResultMetrics( results );

                    finalDefectsCsv.writeNext( new String[]{String.valueOf( worker.getWorkerId() ), String.valueOf(
                            worker.getWorkerQuality() ),
                            metrics.getTruePositivesAsString(), metrics.getTrueNegativesAsString(), metrics
                            .getFalsePositivesAsString(), metrics.getFalseNegativesAsString(), metrics
                            .getPrecisionAsString(), metrics.getRecallAsString(), metrics.getFmeasureAsString(),
                            metrics.getAccuracyAsString()} );
                } );
            }
        } catch (IOException | SQLException e) {
            throw new UncheckedException( e );
        }
    }

    //TODO ugly code merge with write method
    public ImmutableSet<WorkerEvaluationResultMetrics> getEvaluationResults( final ImmutableSet<CrowdtruthRunner
            .SampledWorker> sampledWorkers ) {
        final ImmutableMap<String, TrueDefect> trueDefects;
        final ImmutableSet.Builder<WorkerEvaluationResultMetrics> builder = ImmutableSet.builder();
        try {
            trueDefects = AllTrueDefectsMixin.findAllTrueDefects().stream()
                    .collect( ImmutableMap.toImmutableMap( TrueDefect::getAboutEmEid, Function.identity() ) );

            sampledWorkers.forEach( worker -> {
                final ImmutableMap<String, FinalDefect> workerFinalDefects = worker.getFinalDefects()
                        .stream().collect( ImmutableMap.toImmutableMap( FinalDefect::getEmeId, Function.identity
                                () ) );
                final Set<EvaluationResult> results = Sets.newHashSet();
                workerFinalDefects.values().forEach( fd -> {
                    final EvaluationResult evaluationResult = Optional.ofNullable( trueDefects.get( fd.getEmeId()
                    ) ).map( td -> new EvaluationResult(
                            fd, td ) ).orElseGet( () -> new EvaluationResult( fd ) );
                    results.add( evaluationResult );
                } );

                final EvaluationResultMetrics metrics = new EvaluationResultMetrics( results );
                builder.add( new WorkerEvaluationResultMetrics( metrics, worker.getWorkerId(), worker
                        .getWorkerQuality() ) );
            } );
        } catch (IOException | SQLException e) {
            throw new UncheckedException( e );
        }
        return builder.build();
    }

    public static void analyze( final ImmutableSet<CrowdtruthRunner.SampledWorker> defects, final String
            outputFilePath ) {
        new WorkerQualityAnalyzer().write( defects, outputFilePath );
    }

    public static WorkerQualityAnalyzer create() {
        return new WorkerQualityAnalyzer();
    }
}
