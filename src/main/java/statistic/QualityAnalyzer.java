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
public class QualityAnalyzer {
    private QualityAnalyzer() {
        //purposely left empty
    }

    private void write( final ImmutableSet<CrowdtruthRunner.Sample> samples, final String idKey, final String
            outputFilePath ) {
        final ImmutableMap<String, TrueDefect> trueDefects;
        try {
            trueDefects = AllTrueDefectsMixin.findAllTrueDefects().stream()
                    .collect( ImmutableMap.toImmutableMap( TrueDefect::getAboutEmEid, Function.identity() ) );

            try (CSVWriter finalDefectsCsv = new CSVWriter( Files.newBufferedWriter( Paths.get(
                    outputFilePath ) ) )) {
                finalDefectsCsv.writeNext( new String[]{idKey, "Worker Quality Score (WQS)", "TP", "TN", "FP",
                        "FN", "Precision", "Recall", "F-Measure", "Accuracy"} );

                samples.forEach( worker -> {
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

                    finalDefectsCsv.writeNext( new String[]{String.valueOf( worker.getId() ), String.valueOf(
                            worker.getQuality() ),
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
    public ImmutableSet<NamedEvaluationResultMetrics> getEvaluationResults( final ImmutableSet<CrowdtruthRunner
            .Sample> samples ) {
        final ImmutableMap<String, TrueDefect> trueDefects;
        final ImmutableSet.Builder<NamedEvaluationResultMetrics> builder = ImmutableSet.builder();
        try {
            trueDefects = AllTrueDefectsMixin.findAllTrueDefects().stream()
                    .collect( ImmutableMap.toImmutableMap( TrueDefect::getAboutEmEid, Function.identity() ) );

            samples.forEach( sample -> {
                final ImmutableMap<String, FinalDefect> finalDefectsPerSample = sample.getFinalDefects()
                        .stream().collect( ImmutableMap.toImmutableMap( FinalDefect::getEmeId, Function.identity
                                () ) );
                final Set<EvaluationResult> results = Sets.newHashSet();
                finalDefectsPerSample.values().forEach( fd -> {
                    final EvaluationResult evaluationResult = Optional.ofNullable( trueDefects.get( fd.getEmeId()
                    ) ).map( td -> new EvaluationResult(
                            fd, td ) ).orElseGet( () -> new EvaluationResult( fd ) );
                    results.add( evaluationResult );
                } );

                final EvaluationResultMetrics metrics = new EvaluationResultMetrics( results );
                builder.add( new NamedEvaluationResultMetrics( metrics, String.valueOf( sample.getId() ), sample
                        .getQuality() ) );
            } );
        } catch (IOException | SQLException e) {
            throw new UncheckedException( e );
        }
        return builder.build();
    }

    //TODO ugly code merge with write method
    public ImmutableSet<NamedEvaluationResultMetrics> getEvaluationResultsForMediaUnits( final
                                                                                         ImmutableSet<CrowdtruthRunner
                                                                                                 .Sample> samples ) {
        final ImmutableMap<String, TrueDefect> trueDefects;
        final ImmutableSet.Builder<NamedEvaluationResultMetrics> builder = ImmutableSet.builder();
        try {
            trueDefects = AllTrueDefectsMixin.findAllTrueDefects().stream()
                    .collect( ImmutableMap.toImmutableMap( TrueDefect::getAboutEmEid, Function.identity() ) );

            samples.forEach( sample -> {
                final ImmutableSet<FinalDefect> finalDefectsPerSample = sample.getFinalDefects()
                        .stream().collect( ImmutableSet.toImmutableSet() );
                final Set<EvaluationResult> results = Sets.newHashSet();
                finalDefectsPerSample.forEach( fd -> {
                    final EvaluationResult evaluationResult = Optional.ofNullable( trueDefects.get( fd.getEmeId()
                    ) ).map( td -> new EvaluationResult(
                            fd, td ) ).orElseGet( () -> new EvaluationResult( fd ) );
                    results.add( evaluationResult );
                } );

                final EvaluationResultMetrics metrics = new EvaluationResultMetrics( results );
                builder.add( new NamedEvaluationResultMetrics( metrics, String.valueOf( sample.getId() ), sample
                        .getQuality() ) );
            } );
        } catch (IOException | SQLException e) {
            throw new UncheckedException( e );
        }
        return builder.build();
    }

    public static void analyze( final ImmutableSet<CrowdtruthRunner.Sample> defects, final String idKey, final String
            outputFilePath ) {
        new QualityAnalyzer().write( defects, idKey, outputFilePath );
    }

    public static QualityAnalyzer create() {
        return new QualityAnalyzer();
    }
}
