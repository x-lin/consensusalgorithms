package algorithms.majorityvoting.basic;

import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.aggregation.MajorityVotingAlgorithm;
import algorithms.vericom.model.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author LinX
 */
public class MajorityVotingAlgorithmTest {
    private Eme eme;

    private static final EmeId EME_ID = new EmeId( "EME65" );

    private static final ScenarioId SCENARIO_ID = new ScenarioId( "SC1" );

    @Before
    public void setup() {
        this.eme = Eme.builder( EME_ID )
                .withEmeText( "test" )
                .withEmeGroupId( 1 )
                .withEmeType( EmeType.RELATIONSHIP_MULTIPLICITY ).build();
    }

    @Test
    public void test01_FD_defectType_shouldBeNoDefect() {

        //GIVEN
        final DefectReports defectReports = getDefectReports( DefectType.WRONG_KEY, DefectType
                        .SUPERFLUOUS_EME, DefectType.NO_DEFECT, DefectType
                        .NO_DEFECT, DefectType.NO_DEFECT, DefectType.NO_DEFECT, DefectType.SUPERFLUOUS_EME,
                DefectType.NO_DEFECT, DefectType.SUPERFLUOUS_EME, DefectType.SUPERFLUOUS_EME );

        //WHEN
        final ImmutableMap<EmeAndScenarioId, FinalDefect> finalDefects = createSut( getEmes( this.eme ),
                defectReports ).getFinalDefects();

        //THEN
        assertThat( finalDefects.size(), equalTo( 1 ) );
        final FinalDefect finalDefect = finalDefects.values().iterator().next();
        assertEquals( 0.5, finalDefect.getAgreementCoeff().toDouble(), 0.0 );
        assertEquals( FinalDefectType.NO_DEFECT, finalDefect.getFinalDefectType() );
    }

    @Test
    public void test02_FD_defectType_shouldBeUndecideable() {
        //GIVEN
        final DefectReports defectReports = getDefectReports( DefectType.WRONG_KEY, DefectType
                .SUPERFLUOUS_EME, DefectType.NO_DEFECT );

        //WHEN
        final ImmutableMap<EmeAndScenarioId, FinalDefect> finalDefects = createSut( getEmes( this.eme ),
                defectReports ).getFinalDefects();

        assertThat( finalDefects.size(), equalTo( 1 ) );
        final FinalDefect finalDefect = finalDefects.values().iterator().next();
        assertEquals( 0.3333, finalDefect.getAgreementCoeff().toDouble(), 0.0 );
        assertEquals( FinalDefectType.UNDECIDABLE, finalDefect.getFinalDefectType() );
    }

    @Test
    public void test03_FD_defectType_shouldBeUndecidable() {
        //GIVEN
        final DefectReports defectReports = getDefectReports( DefectType.NO_DEFECT, DefectType
                .SUPERFLUOUS_EME );

        //WHEN
        final ImmutableMap<EmeAndScenarioId, FinalDefect>
                finalDefects = createSut( getEmes( this.eme ), defectReports ).getFinalDefects();

        assertThat( finalDefects.size(), equalTo( 1 ) );
        final FinalDefect finalDefect = finalDefects.values().iterator().next();
        assertEquals( 0.5, finalDefect.getAgreementCoeff().toDouble(), 0.0 );
        assertEquals( FinalDefectType.UNDECIDABLE, finalDefect.getFinalDefectType() );
    }

    @Test
    public void test04_FD_defectType_shouldBeSuperfluousEme() {
        //GIVEN
        final DefectReports defectReports = getDefectReports( DefectType.SUPERFLUOUS_EME );

        //WHEN
        final ImmutableMap<EmeAndScenarioId, FinalDefect> finalDefects = createSut( getEmes( this.eme ),
                defectReports ).getFinalDefects();

        //THEN
        assertThat( finalDefects.size(), equalTo( 1 ) );
        final FinalDefect finalDefect = finalDefects.values().iterator().next();
        assertEquals( 1.0, finalDefect.getAgreementCoeff().toDouble(), 0.0 );
        assertEquals( FinalDefectType.SUPERFLUOUS_EME, finalDefect.getFinalDefectType() );
    }

    @Test
    public void test05_FD_defectType_shouldBeUndecidable() {
        //GIVEN
        final DefectReports defectReports = getDefectReports( DefectType.WRONG_KEY, DefectType
                .SUPERFLUOUS_EME, DefectType.NO_DEFECT, DefectType.NO_DEFECT, DefectType.SUPERFLUOUS_EME );

        //WHEN
        final ImmutableMap<EmeAndScenarioId, FinalDefect> finalDefects = createSut( getEmes( this.eme ),
                defectReports ).getFinalDefects();

        assertThat( finalDefects.size(), equalTo( 1 ) );
        final FinalDefect finalDefect = finalDefects.values().iterator().next();
        assertEquals( 0.4, finalDefect.getAgreementCoeff().toDouble(), 0.0 );
        assertEquals( FinalDefectType.UNDECIDABLE, finalDefect.getFinalDefectType() );
    }

    @Test
    public void testWsResultsSameAsDb() {
        final ImmutableMap<EmeAndScenarioId, FinalDefect> calculatedFinalDefects = MajorityVotingAlgorithm.create(
                SemesterSettings.ws2017() ).getFinalDefects();
        final ImmutableSet<FinalDefect> dbFinalDefects = FinalDefect.fetchFinalDefects( SemesterSettings.ws2017() );

        final Map<EmeAndScenarioId, FinalDefect> calculatedUnmatched = Maps.newHashMap( calculatedFinalDefects );

        dbFinalDefects.forEach( db -> {
            final FinalDefect calculated = Optional.ofNullable( calculatedUnmatched.remove( db.getEmeAndScenarioId() ) )
                    .orElseThrow( () -> new
                            NoSuchElementException(
                            "No defect with eme/scenariod id " +
                                    db.getEmeAndScenarioId() +
                                    " found in calculated " +
                                    "final " +
                                    "defects." ) );
            Preconditions.checkArgument( Objects.equals( calculated.getAgreementCoeff(), db.getAgreementCoeff() ),
                    "Agreement " +
                            "coefficient for eme " + db.getEmeId() + " doesn't match. Expected '%s', but was '%s'.", db
                            .getAgreementCoeff(), calculated.getAgreementCoeff() );
            Preconditions.checkArgument( calculated.getFinalDefectType().equals( db.getFinalDefectType() ),
                    "Final " +
                            "defect type for eme " + db.getEmeId() + " doesn't match. Expected '%s', but was '%s'.",
                    db
                            .getFinalDefectType(), calculated.getFinalDefectType() );
            Preconditions.checkArgument( calculated.getScenarioId().equals( db.getScenarioId() ),
                    "Scenario id for " +
                            "eme " + db.getEmeId() + " doesn't match. Expected '%s', but was '%s'.",
                    db.getScenarioId(),
                    calculated.getScenarioId() );
        } );

        final ImmutableSet<FinalDefect> calculatedUnmatchedWithDefect =
                calculatedUnmatched.values().stream().filter(
                        c -> c
                                .getFinalDefectType() != FinalDefectType.NO_DEFECT ).collect(
                        ImmutableSet.toImmutableSet() );

        Preconditions.checkArgument( calculatedUnmatchedWithDefect.isEmpty(),
                "%s final defects calculated but not in" +
                        " DB: %s",
                calculatedUnmatchedWithDefect.size(), calculatedUnmatchedWithDefect );
    }

    private MajorityVotingAlgorithm createSut( final Emes emes, final DefectReports defectReports ) {
        return MajorityVotingAlgorithm.create(
                SemesterSettings.ws2017(), emes, defectReports,
                MajorityVotingAlgorithm.PERFECT_WORKER_QUALITY );
    }

    private static Emes getEmes( final Eme eme ) {
        return new Emes( ImmutableBiMap.of( eme.getEmeId(), eme ) );
    }

    private static DefectReports getDefectReports( final DefectType... types ) {
        final AtomicInteger defectReportIdCounter = new AtomicInteger( 0 );
        return new DefectReports( Arrays.stream( types )
                .map( t -> getDefectReport( t, defectReportIdCounter.incrementAndGet() ) )
                .collect( ImmutableSet.toImmutableSet() ) );
    }

    private static DefectReport getDefectReport( final DefectType type, final int id ) {
        return DefectReport.builder( id )
                .withEmeId( EME_ID )
                .withScenarioId( SCENARIO_ID )
                .withDefectType( type ).build();
    }
}
