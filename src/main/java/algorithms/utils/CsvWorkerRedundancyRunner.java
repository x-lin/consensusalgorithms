package algorithms.utils;

import algorithms.finaldefects.SemesterSettings;
import algorithms.model.DefectReports;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author LinX
 */
public class CsvWorkerRedundancyRunner {
    private static final String BASE_OUT_PATH = "output/analysis/";

    public static void main( final String[] args ) throws IOException {
        SemesterSettings.SETTINGS.values().forEach( s -> {
            final DefectReports defectReports = DefectReports.fetchFromDb( s );
            final ImmutableList<ImmutableList<String>> values =
                    defectReports.groupedByWorkerId().entrySet().stream().map(
                            e -> ImmutableList.of( e.getKey().toString(), String.valueOf( e.getValue().size() ) ) )
                                 .collect( ImmutableList.toImmutableList() );

            CsvAlgorithmRunner.write( ImmutableList.of( "workerId", "nrTasks" ), values,
                    Paths.get( BASE_OUT_PATH, "workerRedundancy_" + s.getSemester() + ".csv" ) );
        } );
    }
}
