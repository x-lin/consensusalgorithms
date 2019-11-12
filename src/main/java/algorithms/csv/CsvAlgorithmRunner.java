package algorithms.csv;

import algorithms.finaldefects.Semester;
import algorithms.finaldefects.SemesterSettings;
import algorithms.statistic.ConfusionMatrix;
import algorithms.web.AlgorithmController;
import algorithms.web.WebFinalDefects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * @author LinX
 */
public class CsvAlgorithmRunner {
    private static final Logger LOG = LoggerFactory.getLogger( CsvAlgorithmRunner.class );

    static final String BASE_OUT_PATH = "output/analysis/";

    public static void main( final String[] args ) throws IOException {
        final Runner runner = new Runner();
        final ImmutableSet<CompletableFuture<WebFinalDefects>> defects = runner.run();

        LOG.info( "Number of final defect reports to be written: " + defects.size() );

        runConfusionMatrix( defects );

        CompletableFuture.allOf( defects.toArray( new CompletableFuture[]{} ) ).whenComplete(
                ( d, t ) -> {
                    LOG.info( "Shutting down." );
                    runner.shutdown();
                } );
    }

    private static void runConfusionMatrix( final ImmutableSet<CompletableFuture<WebFinalDefects>> defects ) {
        final Map<Semester, List<SingleLineStatistics>> list = Maps.toMap( ImmutableSet.copyOf( Semester.values() ),
                s -> Lists.newArrayList() );

        defects.forEach( f -> {
            final WebFinalDefects d = f.join();
            final ConfusionMatrix confusionMatrix = d.getConfusionMatrix();

            final ImmutableList.Builder<String> headers = ImmutableList.<String>builder().add( "precision", "recall",
                    "accuracy", "fmeasure", "evaluatedEmes", "nrWorkers" );
            final ImmutableList.Builder<String> line = ImmutableList.<String>builder().add(
                    confusionMatrix.getPrecisionAsString(),
                    confusionMatrix.getRecallAsString(),
                    confusionMatrix.getAccuracyAsString(), confusionMatrix.getFmeasureAsString(),
                    String.valueOf( d.getNrEvaluatedEmes() ), String.valueOf( d.getNrWorkers() ) );

            //TODO fix number of workers in adaptive MV

            d.getParameters().forEach( ( k, v ) -> {
                line.add( v );
                headers.add( k );
            } );

            list.get( d.getSemester() ).add( new SingleLineStatistics( headers.build(), line.build() ) );
        } );

        list.forEach(
                ( s, l ) -> write( l.iterator().next().getHeaders(),
                        l.stream().map( SingleLineStatistics::getValues ).collect( ImmutableList.toImmutableList() ),
                        Paths.get( BASE_OUT_PATH, "confusionMatrix", "adaptiveMV_" + s + ".csv" ) ) );
    }

    public static void write( final List<String> headers, final List<? extends List<String>> lines, final Path path ) {
        try {
            Files.createDirectories( path.getParent() );

            LOG.info( "Writing into CSV file {}.", path );
            try (CSVWriter finalDefectsCsv = new CSVWriter( Files.newBufferedWriter( path ) )) {
                finalDefectsCsv.writeNext( headers.toArray( new String[]{} ) );
                lines.forEach( l -> finalDefectsCsv.writeNext( l.toArray( new String[]{} ) ) );
            }
        } catch (final IOException e) {
            LOG.error( "Error occurred while writing to CSV file {}.", path, e );
        }
    }

    private static class Runner {
        private final ExecutorService executors = Executors.newFixedThreadPool( 10 );

        private final AlgorithmController algorithmController = new AlgorithmController();

        public ImmutableSet<CompletableFuture<WebFinalDefects>> run() {
            final ImmutableSet.Builder<CompletableFuture<WebFinalDefects>> defects = ImmutableSet.builder();
            SemesterSettings.SETTINGS.keySet().forEach( semester -> {
//                defects.add( getFinalDefects(
//                        () -> this.algorithmController.majorityVotingFinalDefects( semester ) ) )
//                       .add( getFinalDefects(
//                               () -> this.algorithmController.crowdTruthFinalDefects( semester ) ) );
//                if (semester == Semester.SS2018) {
//                    addMajorityVotingWithQualificationTest( defects, semester );
//                }
//                addMajorityVotingWithExperienceQuestionnaire( defects, semester );

                addAdaptiveMajorityVoting( defects, semester );
            } );
            return defects.build();
        }

        private void addAdaptiveMajorityVoting( final ImmutableSet.Builder<CompletableFuture<WebFinalDefects>> defects,
                final Semester semester ) {
            IntStream.rangeClosed( 0, 100 ).mapToObj( i -> ((double) i) / 100 )
                    .forEach( threshold -> {
                        defects.add( getFinalDefects(
                                () -> this.algorithmController
                                        .adaptiveMajorityVotingFinalDefects( threshold, semester ) ) );
                    } );
        }

        public void shutdown() {
            this.executors.shutdown();
        }

        private CompletableFuture<WebFinalDefects> getFinalDefects( final Supplier<WebFinalDefects> task ) {
            return CompletableFuture.supplyAsync( task, this.executors );
        }
    }
}