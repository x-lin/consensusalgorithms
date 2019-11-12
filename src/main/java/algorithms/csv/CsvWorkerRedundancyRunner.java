package algorithms.csv;

import algorithms.finaldefects.SemesterSettings;
import algorithms.vericom.model.DefectReports;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author LinX
 */
public class CsvWorkerRedundancyRunner {
    public static void main( final String[] args ) throws IOException {
        writeWorkerRedundancyToCsv();
    }

    /**
     * Writes the worker and the nr of tasks done to a CSV file.
     */
    private static void writeWorkerRedundancyToCsv() {
        SemesterSettings.SETTINGS.values().forEach( s -> {
            final DefectReports defectReports = DefectReports.fetchFromDb( s );
            final ImmutableList<ImmutableList<String>> values =
                    defectReports.groupedByWorkerId().entrySet().stream().map(
                            e -> ImmutableList.of( e.getKey().toString(), String.valueOf( e.getValue().size() ) ) )
                            .collect( ImmutableList.toImmutableList() );

            CsvAlgorithmRunner.write( ImmutableList.of( "workerId", "nrTasks" ), values,
                    Paths.get( CsvAlgorithmRunner.BASE_OUT_PATH, "workerRedundancy_" + s.getSemester() + ".csv" ) );
        } );
    }
}
