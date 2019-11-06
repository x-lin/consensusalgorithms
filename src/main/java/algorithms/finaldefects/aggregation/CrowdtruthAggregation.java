package algorithms.finaldefects.aggregation;

import algorithms.finaldefects.SemesterSettings;
import algorithms.model.DefectReports;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LinX
 */
public class CrowdtruthAggregation extends AbstractCrowdtruthAggregation {
    private static final Logger LOG = LoggerFactory.getLogger( CrowdtruthAggregation.class );

    private CrowdtruthAggregation( final SemesterSettings semesterSettings ) {
        super( semesterSettings, DefectReports.fetchFromDb( semesterSettings ) );
    }

    public static void main( final String[] args ) {
        final CrowdtruthAggregation aggregation = new CrowdtruthAggregation(
                SemesterSettings.ws2017() );
        LOG.info( "Final defects: " + aggregation.getFinalDefects() );
    }

    public static CrowdtruthAggregation create( final SemesterSettings settings ) {
        return new CrowdtruthAggregation( settings );
    }

    @Override
    public ImmutableMap<String, String> getParameters() {
        return ImmutableMap.of();
    }
}
