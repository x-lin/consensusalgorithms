package algorithms.statistic;

import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.crowdtruth.CrowdtruthRunner;
import algorithms.model.EmeAndScenarioId;
import algorithms.model.EmeId;
import algorithms.model.FinalDefect;
import algorithms.model.TrueDefect;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.opencsv.CSVWriter;
import org.jooq.lambda.UncheckedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private void write( final SemesterSettings settings, final ImmutableSet<CrowdtruthRunner.Sample> samples, final
    String idKey, final String
            outputFilePath ) {
        final ImmutableMap<EmeId, TrueDefect> trueDefects;
        try {
            trueDefects = AllTrueDefectsMixin.findAllTrueDefects( settings );

            try (CSVWriter finalDefectsCsv = new CSVWriter( Files.newBufferedWriter( Paths.get(
                    outputFilePath ) ) )) {
                finalDefectsCsv.writeNext( new String[]{idKey, "Worker Quality Score (WQS)", "TP", "TN", "FP",
                        "FN", "Precision", "Recall", "F-Measure", "Accuracy"} );

                samples.forEach( worker -> {
                    final ImmutableMap<EmeAndScenarioId, FinalDefect> workerFinalDefects =
                            worker.getFinalDefects().stream().collect( ImmutableMap
                                    .toImmutableMap( FinalDefect::getEmeAndScenarioId, Function.identity() ) );
                    final Set<FinalDefectResult> results = Sets.newHashSet();
                    workerFinalDefects.values().forEach( fd -> {
                        final FinalDefectResult finalDefectResult = Optional.ofNullable( trueDefects.get( fd.getEmeId()
                        ) ).map( td -> new FinalDefectResult(
                                fd, td ) ).orElseGet( () -> new FinalDefectResult( fd ) );
                        results.add( finalDefectResult );
                    } );

                    final ConfusionMatrix metrics = new ConfusionMatrix( results );

                    finalDefectsCsv.writeNext( new String[]{String.valueOf( worker.getId() ), String.valueOf(
                            worker.getQuality() ),
                            metrics.getTruePositivesAsString(), metrics.getTrueNegativesAsString(), metrics
                            .getFalsePositivesAsString(), metrics.getFalseNegativesAsString(), metrics
                            .getPrecisionAsString(), metrics.getRecallAsString(), metrics.getFmeasureAsString(),
                            metrics.getAccuracyAsString()} );
                } );
            }
        } catch (final IOException e) {
            throw new UncheckedException( e );
        }
    }

    //TODO ugly code merge with write method
    public ImmutableSet<ArtifactWithConfusionMatrix> getEvaluationResults( final SemesterSettings settings, final
    ImmutableSet<CrowdtruthRunner.Sample> samples ) {
        final ImmutableMap<EmeId, TrueDefect> trueDefects;
        final ImmutableSet.Builder<ArtifactWithConfusionMatrix> builder = ImmutableSet.builder();
        trueDefects = AllTrueDefectsMixin.findAllTrueDefects( settings );

        samples.forEach( sample -> {
            final Set<FinalDefectResult> results = Sets.newHashSet();
            sample.getFinalDefects().forEach( fd -> {
                final FinalDefectResult finalDefectResult = Optional.ofNullable( trueDefects.get( fd.getEmeId()
                ) ).map( td -> new FinalDefectResult( fd, td ) ).orElseGet( () -> new FinalDefectResult( fd ) );
                results.add( finalDefectResult );
            } );

            final ConfusionMatrix metrics = new ConfusionMatrix( results );
            builder.add( new ArtifactWithConfusionMatrix( metrics, sample.getId(), sample.getQuality() ) );
        } );
        return builder.build();
    }

    //TODO ugly code merge with write method
    public ImmutableSet<ArtifactWithConfusionMatrix> getEvaluationResultsForMediaUnits( final SemesterSettings
            settings, final ImmutableSet<CrowdtruthRunner.Sample> samples ) {
        final ImmutableMap<EmeId, TrueDefect> trueDefects;
        final ImmutableSet.Builder<ArtifactWithConfusionMatrix> builder = ImmutableSet.builder();
        trueDefects = AllTrueDefectsMixin.findAllTrueDefects( settings );

        samples.forEach( sample -> {
            final ImmutableSet<FinalDefect> finalDefectsPerSample = sample.getFinalDefects();
            final Set<FinalDefectResult> results = Sets.newHashSet();
            finalDefectsPerSample.forEach( fd -> {
                final FinalDefectResult finalDefectResult = Optional.ofNullable( trueDefects.get( fd.getEmeId()
                ) ).map( td -> new FinalDefectResult(
                        fd, td ) ).orElseGet( () -> new FinalDefectResult( fd ) );
                results.add( finalDefectResult );
            } );

            final ConfusionMatrix metrics = new ConfusionMatrix( results );
            builder.add( new ArtifactWithConfusionMatrix( metrics, String.valueOf( sample.getId() ), sample
                    .getQuality() ) );
        } );
        return builder.build();
    }

    public static void analyze( final SemesterSettings settings, final ImmutableSet<CrowdtruthRunner.Sample> defects,
            final String idKey, final String
            outputFilePath ) {
        new QualityAnalyzer().write( settings, defects, idKey, outputFilePath );
    }

    public static QualityAnalyzer create() {
        return new QualityAnalyzer();
    }
}
