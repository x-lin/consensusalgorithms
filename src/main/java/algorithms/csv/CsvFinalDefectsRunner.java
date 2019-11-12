package algorithms.csv;

import algorithms.finaldefects.Semester;
import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.WorkerQualityInfluence;
import algorithms.web.AlgorithmController;
import algorithms.web.WebFinalDefects;
import com.google.common.collect.ImmutableSet;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * @author LinX
 */
public class CsvFinalDefectsRunner {
    private static final Logger LOG = LoggerFactory.getLogger( CsvAlgorithmRunner.class );

    public static void main( final String[] args ) throws IOException {
        final Runner runner = new Runner();
        final ImmutableSet<CompletableFuture<WebFinalDefects>> defects = runner.run();

        LOG.info( "Number of final defect reports to be written: " + defects.size() );

        CompletableFuture.allOf( defects.toArray( new CompletableFuture[]{} ) ).whenComplete(
                ( d, t ) -> {
                    LOG.info( "Shutting down." );
                    runner.shutdown();
                } );
    }

    private static String getFilename( final WebFinalDefects d ) {
        final StringBuilder builder = new StringBuilder();
        builder.append( d.getSemester() ).append( "_" ).append( d.getAlgorithmType() );
        d.getParameters().forEach( ( k, v ) -> builder.append( "_" ).append( k ).append( "_" ).append( v ) );
        return builder.toString();
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

    private void runFinalDefects() {

    }

    private static class Runner {
        private final ExecutorService executors = Executors.newFixedThreadPool( 10 );

        private final AlgorithmController algorithmController = new AlgorithmController();

        public ImmutableSet<CompletableFuture<WebFinalDefects>> run() {
            final ImmutableSet.Builder<CompletableFuture<WebFinalDefects>> defects = ImmutableSet.builder();
            SemesterSettings.SETTINGS.keySet().forEach( semester -> {
                defects.add( getFinalDefects(
                        () -> this.algorithmController.majorityVotingFinalDefects( semester ) ) )
                        .add( getFinalDefects(
                                () -> this.algorithmController.crowdTruthFinalDefects( semester ) ) );
                if (semester == Semester.SS2018) {
                    addMajorityVotingWithQualificationTest( defects, semester );
                }
                addMajorityVotingWithExperienceQuestionnaire( defects, semester );

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

        private void addMajorityVotingWithQualificationTest(
                final ImmutableSet.Builder<CompletableFuture<WebFinalDefects>> defects, final Semester semester ) {
            defects.add( getFinalDefects( () -> this.algorithmController
                    .majorityVotingWithQualificationReportFinalDefects( WorkerQualityInfluence.LINEAR, 0,
                            semester ) ) );
            IntStream.rangeClosed( 0, 20 ).mapToObj( i -> ((double) i) / 20 ).forEach(
                    alpha -> defects.add( getFinalDefects( () -> this.algorithmController
                            .majorityVotingWithQualificationReportFinalDefects(
                                    WorkerQualityInfluence.EXPONENTIAL,
                                    alpha, semester ) ) ) );
        }

        private void addMajorityVotingWithExperienceQuestionnaire(
                final ImmutableSet.Builder<CompletableFuture<WebFinalDefects>> defects, final Semester semester ) {
            IntStream.rangeClosed( 0, 1 ).mapToObj( i -> ((double) i) / 2 ).forEach( wLanguageSkills -> {
                IntStream.rangeClosed( 0, 1 ).mapToObj( i -> ((double) i) / 2 ).forEach( wProjectSkills -> {
                    IntStream.rangeClosed( 0, 1 ).mapToObj( i -> ((double) i) / 2 ).forEach( wQASkills -> {
                        IntStream.rangeClosed( 0, 1 ).mapToObj( i -> ((double) i) / 2 ).forEach( wWorkingEnv -> {
                            IntStream.rangeClosed( 0, 1 ).mapToObj( i -> ((double) i) / 2 ).forEach( wDomainExp -> {
                                IntStream.rangeClosed( 0, 1 ).mapToObj( i -> ((double) i) / 2 ).forEach(
                                        wCrowdsourcing -> {
                                            IntStream.rangeClosed( 0, 5 ).mapToObj( i -> ((double) i) / 5 )
                                                    .forEach(
                                                            alpha -> defects.add( getFinalDefects(
                                                                    () -> this.algorithmController
                                                                            .majorityVotingWithExperienceQuestionnaireFinalDefects(
                                                                                    WorkerQualityInfluence.EXPONENTIAL,
                                                                                    alpha,
                                                                                    semester,
                                                                                    wLanguageSkills,
                                                                                    wProjectSkills,
                                                                                    wQASkills,
                                                                                    wWorkingEnv, wDomainExp,
                                                                                    wCrowdsourcing ) ) ) );
                                            defects.add( getFinalDefects( () -> this.algorithmController
                                                    .majorityVotingWithExperienceQuestionnaireFinalDefects(
                                                            WorkerQualityInfluence.LINEAR, 0,
                                                            semester,
                                                            wLanguageSkills, wProjectSkills, wQASkills,
                                                            wWorkingEnv, wDomainExp, wCrowdsourcing ) ) );
                                        } );
                            } );
                        } );
                    } );
                } );
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
