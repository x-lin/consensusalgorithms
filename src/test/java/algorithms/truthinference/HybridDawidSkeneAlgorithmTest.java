package algorithms.truthinference;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author LinX
 */
public class HybridDawidSkeneAlgorithmTest {
    private static final double SWITCH_TO_FDS_DELTA_THRESHOLD = 0.05;

    private static final Answers ANSWERS = DawidSkeneAlgorithmTest.OBSERVATIONS;

    @Test
    public void when_algorithmRunWithInputParameters_then_returnsExpectedClassProbabilities() {
        //GIVEN
        final ImmutableMap<ChoiceId, Double> expectedClassProbabilities = ImmutableMap.of( //
                ChoiceId.create( 1 ), 0.4, ChoiceId.create( 2 ), 0.42, //
                ChoiceId.create( 3 ), 0.11, ChoiceId.create( 4 ), 0.07 );

        //WHEN
        final HybridDawidSkeneAlgorithm dawidSkeneAlgorithm = new HybridDawidSkeneAlgorithm( ANSWERS,
                SWITCH_TO_FDS_DELTA_THRESHOLD );
        final HybridDawidSkeneAlgorithm.Output output = dawidSkeneAlgorithm.run();

        //THEN
        assertThat( output.getClassProbabilities().keySet(), equalTo( expectedClassProbabilities.keySet() ) );
        output.getClassProbabilities().forEach( ( label, probability ) -> assertThat( probability,
                closeTo( expectedClassProbabilities.get( label ), 0.01 ) ) );
    }

    @Test
    public void when_algorithmRunWithInputParameters_then_returnsExpectedErrorRates() {
        //GIVEN
        final ImmutableMap<HybridDawidSkeneAlgorithm.ErrorRateId, Double> expectedErrorRateEstimations =
                ImmutableMap.<HybridDawidSkeneAlgorithm.ErrorRateId, Double>builder().put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 1 ) ), 0.89 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 1 ) ), 0.11 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 2 ) ), 0.07 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 2 ) ), 0.88 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 2 ) ), 0.05 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 2 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 3 ) ), 0.33 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 3 ) ), 0.67 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 4 ) ), 0.56 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 4 ) ), 0.44 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 1 ) ), 0.83 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 1 ) ), 0.17 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 2 ) ), 0.05 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 2 ) ), 0.63 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 2 ) ), 0.32 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 2 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 3 ) ), 1.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 4 ) ), 1.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 1 ) ), 1.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 2 ) ), 0.11 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 2 ) ), 0.79 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 2 ) ), 0.11 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 2 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 3 ) ), 0.40 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 3 ) ), 0.20 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 3 ) ), 0.40 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 4 ) ), 0.67 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 4 ) ), 0.33 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 1 ) ), 0.94 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 1 ) ), 0.06 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 2 ) ), 0.05 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 2 ) ), 0.84 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 2 ) ), 0.11 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 2 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 3 ) ), 0.80 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 3 ) ), 0.20 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 4 ) ), 0.33 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 4 ) ), 0.67 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 1 ) ), 1.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 2 ) ), 0.16 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 2 ) ), 0.74 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 2 ) ), 0.11 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 2 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 3 ) ), 0.20 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 3 ) ), 0.80 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 4 ) ), 0.33 ).put(
                        new HybridDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 4 ) ), 0.67 ).build();


        //WHEN
        final HybridDawidSkeneAlgorithm dawidSkeneAlgorithm = new HybridDawidSkeneAlgorithm( ANSWERS,
                SWITCH_TO_FDS_DELTA_THRESHOLD );
        final HybridDawidSkeneAlgorithm.Output output = dawidSkeneAlgorithm.run();

        //THEN
        assertThat( output.getErrorRates().keySet(), equalTo( expectedErrorRateEstimations.keySet() ) );
        output.getErrorRates().forEach( ( id, estimation ) -> assertThat( estimation.getErrorRateEstimation(),
                closeTo( expectedErrorRateEstimations.get( id ), 0.01 ) ) );
    }

    @Test
    public void when_algorithmRunWithInputParameters_then_returnsExpectedClassEstimations() {
        //GIVEN
        final ImmutableMap<QuestionId, ImmutableSet<HybridDawidSkeneAlgorithm.IndicatorEstimation>>
                expectedClassEstimatinos =
                ImmutableMap.<QuestionId, ImmutableSet<HybridDawidSkeneAlgorithm.IndicatorEstimation>>builder().put(
                        QuestionId.create( 1 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 2 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                1.00 ) ) ).put(
                        QuestionId.create( 3 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 4 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 5 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 6 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 7 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 8 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 9 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 10 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 11 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                1.00 ) ) ).put(
                        QuestionId.create( 12 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 13 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 14 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 15 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 16 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 17 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 18 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 19 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 20 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 21 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 22 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 23 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 24 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 25 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 26 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 27 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 28 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 29 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 30 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 31 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 32 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 33 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 34 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 35 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 36 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                1.00 ) ) ).put(
                        QuestionId.create( 37 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 38 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 39 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 40 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 41 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 42 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 43 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 44 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) ).put(
                        QuestionId.create( 45 ),
                        ImmutableSet
                                .of( new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                        new HybridDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ),
                                                0.00 ) ) )
                        .build();


        //WHEN
        final HybridDawidSkeneAlgorithm dawidSkeneAlgorithm = new HybridDawidSkeneAlgorithm( ANSWERS,
                SWITCH_TO_FDS_DELTA_THRESHOLD );
        final HybridDawidSkeneAlgorithm.Output output = dawidSkeneAlgorithm.run();

        //THEN
        assertThat( output.getClassEstimations().keySet(), equalTo( expectedClassEstimatinos.keySet() ) );
        output.getClassEstimations().forEach(
                ( patient, estimations ) -> estimations.forEach( estimation -> {
                    final HybridDawidSkeneAlgorithm.IndicatorEstimation expectedEstimation =
                            expectedClassEstimatinos.get(
                                    patient ).stream().filter( p -> p.getChoice().equals( estimation.getChoice() ) )
                                    .findFirst()
                                    .get();
                    assertThat( estimation.getIndicatorEstimation(),
                            closeTo( expectedEstimation.getIndicatorEstimation(),
                                    0.01 ) );
                } ) );
    }
}
