package algorithms.majorityvoting;

import statistic.FinalDefectAnalyzer;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author LinX
 */
public class MajorityVotingAnalyzer {
    private static final String ANALYSIS_OUT_CSV = "output/majorityvoting/analysis.csv";

    public static void main( final String[] args ) throws IOException, SQLException {
        FinalDefectAnalyzer.analyze( MajorityVotingRunner.calculateFinalDefects(), ANALYSIS_OUT_CSV );
    }
}
