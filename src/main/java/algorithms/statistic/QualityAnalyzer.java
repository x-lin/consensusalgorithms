package algorithms.statistic;

import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.crowdtruth.CrowdtruthAggregationAlgorithm;
import algorithms.model.EmeId;
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

/**
 * @author LinX
 */
public class QualityAnalyzer {
    private QualityAnalyzer() {
        //purposely left empty
    }

    public void writeConfusionMatrix( final SemesterSettings settings,
            final ImmutableSet<CrowdtruthAggregationAlgorithm.Sample> samples, final
    String idKey, final String
            outputFilePath ) {
        try {
            try (CSVWriter finalDefectsCsv = new CSVWriter( Files.newBufferedWriter( Paths.get(
                    outputFilePath ) ) )) {
                finalDefectsCsv.writeNext( new String[]{idKey, "Quality Score (WQS)", "TP", "TN", "FP",
                        "FN", "Precision", "Recall", "F-Measure", "Accuracy"} );

                final ImmutableSet<ArtifactWithConfusionMatrix> confusionMatrix = getConfusionMatrix( settings,
                        samples );

                confusionMatrix.forEach( matrix -> {
                    final ConfusionMatrix metrics = matrix.getConfusionMatrix();
                    finalDefectsCsv.writeNext( new String[]{matrix.getId(), String.valueOf( matrix.getQuality() ),
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

    public ImmutableSet<ArtifactWithConfusionMatrix> getConfusionMatrix( final SemesterSettings settings, final
    ImmutableSet<CrowdtruthAggregationAlgorithm.Sample> samples ) {
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

    public static QualityAnalyzer create() {
        return new QualityAnalyzer();
    }
}
