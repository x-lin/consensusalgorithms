package algorithms.majorityvoting;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author LinX
 */
public class MajorityVotingAnalyzer {
    public static void main( final String[] args ) throws IOException, SQLException {
        MajorityVotingRunner.calculateFinalDefects();
    }
}
