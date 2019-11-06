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
public class FastDawidSkeneAlgorithmTest {
    private static final Answers ANSWERS = DawidSkeneAlgorithmTest.OBSERVATIONS;

    @Test
    public void when_algorithmRunWithInputParameters_then_returnsExpectedClassProbabilities() {
        //GIVEN
        final ImmutableMap<ChoiceId, Double> expectedClassProbabilities = ImmutableMap.of( //
                ChoiceId.create( 1 ), 0.4, ChoiceId.create( 2 ), 0.44444444, //
                ChoiceId.create( 3 ), 0.13333333, ChoiceId.create( 4 ), 0.02222222 );

        //WHEN
        final FastDawidSkeneAlgorithm dawidSkeneAlgorithm = new FastDawidSkeneAlgorithm( ANSWERS );
        final FastDawidSkeneAlgorithm.Output output = dawidSkeneAlgorithm.run();

        //THEN
        assertThat( output.getClassProbabilities().keySet(), equalTo( expectedClassProbabilities.keySet() ) );
        output.getClassProbabilities().forEach( ( label, probability ) -> assertThat( probability,
                closeTo( expectedClassProbabilities.get( label ), 0.01 ) ) );
    }

    @Test
    public void when_algorithmRunWithInputParameters_then_returnsExpectedErrorRates() {
        //GIVEN
        final ImmutableMap<FastDawidSkeneAlgorithm.ErrorRateId, Double> expectedErrorRateEstimations =
                ImmutableMap.<FastDawidSkeneAlgorithm.ErrorRateId, Double>builder().put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 1 ) ), 0.89 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 1 ) ), 0.11 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 2 ) ), 0.07 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 2 ) ), 0.88 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 2 ) ), 0.05 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 2 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 3 ) ), 0.11 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 3 ) ), 0.83 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 3 ) ), 0.06 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 4 ) ), 1.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 1 ) ), 0.83 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 1 ) ), 0.17 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 2 ) ), 0.05 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 2 ) ), 0.60 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 2 ) ), 0.35 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 2 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 3 ) ), 0.67 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 3 ) ), 0.33 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 4 ) ), 1.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 1 ) ), 1.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 2 ) ), 0.10 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 2 ) ), 0.75 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 2 ) ), 0.15 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 2 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 3 ) ), 0.33 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 3 ) ), 0.33 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 3 ) ), 0.33 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 4 ) ), 1.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 1 ) ), 0.94 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 1 ) ), 0.06 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 2 ) ), 0.05 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 2 ) ), 0.80 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 2 ) ), 0.10 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 2 ) ), 0.05 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 3 ) ), 0.83 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 3 ) ), 0.17 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 4 ) ), 1.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 1 ) ), 1.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 1 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 2 ) ), 0.15 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 2 ) ), 0.70 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 2 ) ), 0.15 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 2 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 3 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 3 ) ), 0.17 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 3 ) ), 0.67 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 3 ) ), 0.17 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 1 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 2 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 3 ),
                                ChoiceId.create( 4 ) ), 0.00 ).put(
                        new FastDawidSkeneAlgorithm.ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 4 ),
                                ChoiceId.create( 4 ) ), 1.00 ).build();


        //WHEN
        final FastDawidSkeneAlgorithm dawidSkeneAlgorithm = new FastDawidSkeneAlgorithm( ANSWERS );
        final FastDawidSkeneAlgorithm.Output output = dawidSkeneAlgorithm.run();

        //THEN
        assertThat( output.getErrorRates().keySet(), equalTo( expectedErrorRateEstimations.keySet() ) );
        output.getErrorRates().forEach( ( id, estimation ) -> assertThat( estimation.getErrorRateEstimation(),
                closeTo( expectedErrorRateEstimations.get( id ), 0.01 ) ) );
    }

    @Test
    public void when_algorithmRunWithInputParameters_then_returnsExpectedClassEstimations() {
        //GIVEN
        final ImmutableMap<QuestionId, ImmutableSet<FastDawidSkeneAlgorithm.IndicatorEstimation>>
                expectedClassEstimatinos =
                ImmutableMap.<QuestionId, ImmutableSet<FastDawidSkeneAlgorithm.IndicatorEstimation>>builder().put(
                        QuestionId.create( 1 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 2 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 3 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 4 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 5 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 6 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 7 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 8 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 9 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 10 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 11 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 1.00 ) ) ).put(
                        QuestionId.create( 12 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 13 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 14 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 15 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 16 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 17 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 18 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 19 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 20 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 21 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 22 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 23 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 24 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 25 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 26 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 27 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 28 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 29 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 30 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 31 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 32 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 33 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 34 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 35 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 36 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 37 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 38 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 39 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 40 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 41 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 42 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 43 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 44 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put(
                        QuestionId.create( 45 ),
                        ImmutableSet.of( new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new FastDawidSkeneAlgorithm.IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) )
                        .build();


        //WHEN
        final FastDawidSkeneAlgorithm dawidSkeneAlgorithm = new FastDawidSkeneAlgorithm( ANSWERS );
        final FastDawidSkeneAlgorithm.Output output = dawidSkeneAlgorithm.run();

        //THEN
        assertThat( output.getClassEstimations().keySet(), equalTo( expectedClassEstimatinos.keySet() ) );
        output.getClassEstimations().forEach(
                ( patient, estimations ) -> estimations.forEach( estimation -> {
                    final FastDawidSkeneAlgorithm.IndicatorEstimation expectedEstimation = expectedClassEstimatinos.get(
                            patient ).stream().filter( p -> p.getChoice().equals( estimation.getChoice() ) )
                            .findFirst()
                            .get();
                    assertThat( estimation.getIndicatorEstimation(),
                            closeTo( expectedEstimation.getIndicatorEstimation(),
                                    0.01 ) );
                } ) );
    }
}
