package algorithms.majorityvoting;

import com.google.common.collect.ImmutableSet;
import model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author LinX
 */
public class MajorityVotingAggregatorTest {
    private Eme eme;

    private Scenario scenario;

    private static final String EME_ID = "EME65";

    private static final String SCENARIO_ID = "SC1";


    @Before
    public void setup() {
        this.eme = Eme.builder( EME_ID )
                .withEmeText( "test" )
                .withEmeGroupId( 1 )
                .withEmeType( EmeType.RELATIONSHIP_MULTIPLICITY ).build();

        this.scenario = Scenario.builder( SCENARIO_ID )
                .withScenarioName( "scenario" )
                .withScenarioText( "test" ).build();
    }

    @Test
    public void test01_FD_defectType_shouldBeNoDefect() {

        //GIVEN
        final ImmutableSet<DefectReport> defectReports = getDefectReports( DefectType.WRONG_KEY, DefectType
                        .SUPERFLUOUS_EME, DefectType.NO_DEFECT, DefectType
                        .NO_DEFECT, DefectType.NO_DEFECT, DefectType.NO_DEFECT, DefectType.SUPERFLUOUS_EME,
                DefectType.NO_DEFECT, DefectType.SUPERFLUOUS_EME, DefectType.SUPERFLUOUS_EME );

        //WHEN
        final ImmutableSet<FinalDefect> finalDefects = new MajorityVotingAggregator( ImmutableSet.of( this.eme ),
                defectReports ).aggregate();

        //THEN
        assertThat( finalDefects.size(), equalTo( 1 ) );
        final FinalDefect finalDefect = finalDefects.iterator().next();
        assertEquals( 0.5, finalDefect.getAgreementCoeff(), 0.0 );
        assertEquals( FinalDefectType.NO_DEFECT, finalDefect.getFinalDefectType() );
    }

    @Test
    public void test02_FD_defectType_shouldBeUndecideable() {
        //GIVEN
        final ImmutableSet<DefectReport> defectReports = getDefectReports( DefectType.WRONG_KEY, DefectType
                .SUPERFLUOUS_EME, DefectType.NO_DEFECT );

        //WHEN
        final ImmutableSet<FinalDefect> finalDefects = new MajorityVotingAggregator( ImmutableSet.of( this.eme ),
                defectReports ).aggregate();

        assertThat( finalDefects.size(), equalTo( 1 ) );
        final FinalDefect finalDefect = finalDefects.iterator().next();
        assertEquals( 0.3333, finalDefect.getAgreementCoeff(), 0.0 );
        assertEquals( FinalDefectType.UNDECIDABLE, finalDefect.getFinalDefectType() );
    }

    @Test
    public void test03_FD_defectType_shouldBeUndecidable() {
        //GIVEN
        final ImmutableSet<DefectReport> defectReports = getDefectReports( DefectType.NO_DEFECT, DefectType
                .SUPERFLUOUS_EME );

        //WHEN
        final ImmutableSet<FinalDefect> finalDefects = new MajorityVotingAggregator( ImmutableSet.of( this.eme ),
                defectReports ).aggregate();

        assertThat( finalDefects.size(), equalTo( 1 ) );
        final FinalDefect finalDefect = finalDefects.iterator().next();
        assertEquals( 0.5, finalDefect.getAgreementCoeff(), 0.0 );
        assertEquals( FinalDefectType.UNDECIDABLE, finalDefect.getFinalDefectType() );
    }

    @Test
    public void test04_FD_defectType_shouldBeSuperfluousEme() {
        //GIVEN
        final ImmutableSet<DefectReport> defectReports = getDefectReports( DefectType.SUPERFLUOUS_EME );

        //WHEN
        final ImmutableSet<FinalDefect> finalDefects = new MajorityVotingAggregator( ImmutableSet.of( this.eme ),
                defectReports ).aggregate();

        //THEN
        assertThat( finalDefects.size(), equalTo( 1 ) );
        final FinalDefect finalDefect = finalDefects.iterator().next();
        assertEquals( 1.0, finalDefect.getAgreementCoeff(), 0.0 );
        assertEquals( FinalDefectType.SUPERFLUOUS_EME, finalDefect.getFinalDefectType() );
    }

    @Test
    public void test05_FD_defectType_shouldBeUndecidable() {
        //GIVEN
        final ImmutableSet<DefectReport> defectReports = getDefectReports( DefectType.WRONG_KEY, DefectType
                .SUPERFLUOUS_EME, DefectType.NO_DEFECT, DefectType.NO_DEFECT, DefectType.SUPERFLUOUS_EME );

        //WHEN
        final ImmutableSet<FinalDefect> finalDefects = new MajorityVotingAggregator( ImmutableSet.of( this.eme ),
                defectReports ).aggregate();

        assertThat( finalDefects.size(), equalTo( 1 ) );
        final FinalDefect finalDefect = finalDefects.iterator().next();
        assertEquals( 0.4, finalDefect.getAgreementCoeff(), 0.0 );
        assertEquals( FinalDefectType.UNDECIDABLE, finalDefect.getFinalDefectType() );
    }

    private static ImmutableSet<DefectReport> getDefectReports( final DefectType... types ) {
        final AtomicInteger defectReportIdCounter = new AtomicInteger( 0 );
        return Arrays.stream( types ).map( t -> getDefectReport( t, defectReportIdCounter.incrementAndGet() ) )
                .collect( ImmutableSet.toImmutableSet() );
    }

    private static DefectReport getDefectReport( final DefectType type, final int id ) {
        return DefectReport.builder( id )
                .withEmeId( EME_ID )
                .withScenarioId( SCENARIO_ID )
                .withDefectType( type ).build();
    }
}
