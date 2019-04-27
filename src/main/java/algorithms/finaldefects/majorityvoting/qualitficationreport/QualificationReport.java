package algorithms.finaldefects.majorityvoting.qualitficationreport;

import com.google.common.collect.ImmutableMap;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import model.TaskWorkerId;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author LinX
 */
public class QualificationReport {
    private static final String CSV_FILE_PATH = "src/main/resources/additions/qualificationReports.csv";

    private final TaskWorkerId workerId;

    private final ImmutableMap<Integer, Boolean> results;

    public QualificationReport( final TaskWorkerId workerId, final ImmutableMap<Integer, Boolean> results ) {
        this.workerId = workerId;
        this.results = results;
    }

    public TaskWorkerId getWorkerId() {
        return this.workerId;
    }

    public ImmutableMap<Integer, Boolean> getResults() {
        return this.results;
    }

    public int getNrTrueResults() {
        return (int) this.results.values().stream().filter( r -> r ).count();
    }

    public int getNrFalseResults() {
        return (int) this.results.values().stream().filter( r -> !r ).count();
    }

    public int getNrResults() {
        return this.results.size();
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final QualificationReport that = (QualificationReport) o;
        return Objects.equals( this.workerId, that.workerId ) &&
                Objects.equals( this.results, that.results );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.workerId, this.results );
    }

    @Override
    public String toString() {
        return "QualificationReport{" +
                "workerId=" + this.workerId +
                ", results=" + this.results +
                '}';
    }

    public static ImmutableMap<TaskWorkerId, QualificationReport> fetchQualificationReports() {
        try (Reader reader = Files.newBufferedReader( Paths.get( CSV_FILE_PATH ) );
             CSVReader csvReader = new CSVReaderBuilder( reader ).withSkipLines( 1 ).build()) {
            return csvReader.readAll().stream().collect(
                    ImmutableMap.toImmutableMap( l -> new TaskWorkerId( l[7] ),
                            l -> new QualificationReport( new TaskWorkerId( l[7] ), getResults( l ) ) ) );
        } catch (final IOException e) {
            throw new UncheckedIOException( e );
        }
    }

    private static ImmutableMap<Integer, Boolean> getResults( final String[] csvLine ) {
        return ImmutableMap.<Integer, Boolean>builder()
                .put( 1, Boolean.valueOf( csvLine[14] ) )
                .put( 2, Boolean.valueOf( csvLine[18] ) )
                .put( 3, Boolean.valueOf( csvLine[22] ) )
                .put( 4, Boolean.valueOf( csvLine[26] ) )
                .put( 5, Boolean.valueOf( csvLine[30] ) )
                .build();
    }
}
