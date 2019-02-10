package algorithms.crowdtruth;

import statistic.FinalDefectAnalyzer;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author LinX
 */
public class CrowdtruthAnalyzer {
    private static final String ANALYSIS_OUT_CSV = "output/crowdtruth/analysis.csv";

    public static void main( final String[] args ) throws IOException, SQLException {
        FinalDefectAnalyzer.analyze( CrowdtruthRunner.calculateFinalDefects(), ANALYSIS_OUT_CSV );
    }
}
