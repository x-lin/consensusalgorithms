package algorithms.dawidskene;

import algorithms.catd.DawidSkeneAlgorithm;
import algorithms.catd.DawidSkeneAlgorithm.ErrorRateId;
import algorithms.catd.DawidSkeneAlgorithm.IndicatorEstimation;
import algorithms.catd.Answer;
import algorithms.catd.ChoiceId;
import algorithms.catd.ParticipantId;
import algorithms.catd.QuestionId;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author LinX
 */
public class DawidSkeneAlgorithmTest {
    //data from Table 1
    private static final int[][][] RAW_DATA = {
            //obs 1      2    3    4    5   ||| patient
            {{1, 1, 1}, {1}, {1}, {1}, {1}},    //1
            {{3, 3, 3}, {4}, {3}, {3}, {4}},    //2
            {{1, 1, 2}, {2}, {1}, {2}, {2}},    //3
            {{2, 2, 2}, {3}, {1}, {2}, {1}},    //4
            {{2, 2, 2}, {3}, {2}, {2}, {2}},    //5
            {{2, 2, 2}, {3}, {3}, {2}, {2}},    //6
            {{1, 2, 2}, {2}, {1}, {1}, {1}},    //7
            {{3, 3, 3}, {3}, {4}, {3}, {3}},    //8
            {{2, 2, 2}, {2}, {2}, {2}, {3}},    //9
            {{2, 3, 2}, {2}, {2}, {2}, {3}},    //10
            {{4, 4, 4}, {4}, {4}, {4}, {4}},    //11
            {{2, 2, 2}, {3}, {3}, {4}, {3}},    //12
            {{1, 1, 1}, {1}, {1}, {1}, {1}},    //13
            {{2, 2, 2}, {3}, {2}, {1}, {2}},    //14
            {{1, 2, 1}, {1}, {1}, {1}, {1}},    //15
            {{1, 1, 1}, {2}, {1}, {1}, {1}},    //16
            {{1, 1, 1}, {1}, {1}, {1}, {1}},    //17
            {{1, 1, 1}, {1}, {1}, {1}, {1}},    //18
            {{2, 2, 2}, {2}, {2}, {2}, {1}},    //19
            {{2, 2, 2}, {1}, {3}, {2}, {2}},    //20
            {{2, 2, 2}, {2}, {2}, {2}, {2}},    //21
            {{2, 2, 2}, {2}, {2}, {2}, {1}},    //22
            {{2, 2, 2}, {3}, {2}, {2}, {2}},    //23
            {{2, 2, 1}, {2}, {2}, {2}, {2}},    //24
            {{1, 1, 1}, {1}, {1}, {1}, {1}},    //25
            {{1, 1, 1}, {1}, {1}, {1}, {1}},    //26
            {{2, 3, 2}, {2}, {2}, {2}, {2}},    //27
            {{1, 1, 1}, {1}, {1}, {1}, {1}},    //28
            {{1, 1, 1}, {1}, {1}, {1}, {1}},    //29
            {{1, 1, 2}, {1}, {1}, {2}, {1}},    //30
            {{1, 1, 1}, {1}, {1}, {1}, {1}},    //31
            {{3, 3, 3}, {3}, {2}, {3}, {3}},    //32
            {{1, 1, 1}, {1}, {1}, {1}, {1}},    //33
            {{2, 2, 2}, {2}, {2}, {2}, {2}},    //34
            {{2, 2, 2}, {3}, {2}, {3}, {2}},    //35
            {{4, 3, 3}, {4}, {3}, {4}, {3}},    //36
            {{2, 2, 1}, {2}, {2}, {3}, {2}},    //37
            {{2, 3, 2}, {3}, {2}, {3}, {3}},    //38
            {{3, 3, 3}, {3}, {4}, {3}, {2}},    //39
            {{1, 1, 1}, {1}, {1}, {1}, {1}},    //40
            {{1, 1, 1}, {1}, {1}, {1}, {1}},    //41
            {{1, 2, 1}, {2}, {1}, {1}, {1}},    //42
            {{2, 3, 2}, {2}, {2}, {2}, {2}},    //43
            {{1, 2, 1}, {1}, {1}, {1}, {1}},    //44
            {{2, 2, 2}, {2}, {2}, {2}, {2}}     //45
    };

    private static final ImmutableSet<Answer> OBSERVATIONS = createObservationsFromRawData();

    private static ImmutableSet<Answer> createObservationsFromRawData() {
        final ImmutableSet.Builder<Answer> observations = ImmutableSet.builder();
        for (int patientIdx = 0; patientIdx < RAW_DATA.length; patientIdx++) {
            for (int observerIdx = 0; observerIdx < RAW_DATA[patientIdx].length; observerIdx++) {
                observations.add(
                        Answer.create( ParticipantId.create( observerIdx + 1 ), QuestionId.create( patientIdx + 1 ),
                                Arrays.stream( RAW_DATA[patientIdx][observerIdx] ).mapToObj( ChoiceId::create )
                                      .collect( ImmutableList.toImmutableList() ) ) );
            }
        }
        return observations.build();
    }

    @Test
    public void when_algorithmRunWithInputParameters_then_returnsExpectedClassProbabilities() {
        //GIVEN
        final ImmutableMap<ChoiceId, Double> expectedClassProbabilities = ImmutableMap.of( //
                ChoiceId.create( 1 ), 0.4, ChoiceId.create( 2 ), 0.42, //
                ChoiceId.create( 3 ), 0.11, ChoiceId.create( 4 ), 0.07 );

        //WHEN
        final DawidSkeneAlgorithm dawidSkeneAlgorithm = new DawidSkeneAlgorithm( OBSERVATIONS );
        final DawidSkeneAlgorithm.Output output = dawidSkeneAlgorithm.run();

        //THEN
        assertThat( output.getPatientClassProbabilities().keySet(), equalTo( expectedClassProbabilities.keySet() ) );
        output.getPatientClassProbabilities().forEach( ( label, probability ) -> assertThat( probability,
                closeTo( expectedClassProbabilities.get( label ), 0.01 ) ) );
    }

    @Test
    public void when_algorithmRunWithInputParameters_then_returnsExpectedErrorRates() {
        //GIVEN
        final ImmutableMap<ErrorRateId, Double> expectedErrorRateEstimations = getExpectedErrorRates();


        //WHEN
        final DawidSkeneAlgorithm dawidSkeneAlgorithm = new DawidSkeneAlgorithm( OBSERVATIONS );
        final DawidSkeneAlgorithm.Output output = dawidSkeneAlgorithm.run();

        //THEN
        assertThat( output.getErrorRates().keySet(), equalTo( expectedErrorRateEstimations.keySet() ) );
        output.getErrorRates().forEach( ( id, estimation ) -> assertThat( estimation.getErrorRateEstimation(),
                closeTo( expectedErrorRateEstimations.get( id ), 0.01 ) ) );
    }

    private static ImmutableMap<ErrorRateId, Double> getExpectedErrorRates() {
        return ImmutableMap.<ErrorRateId, Double>builder()
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 1 ), ChoiceId.create( 1 ) ),
                        0.89 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 2 ), ChoiceId.create( 1 ) ),
                        0.11 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 3 ), ChoiceId.create( 1 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 4 ), ChoiceId.create( 1 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 1 ), ChoiceId.create( 2 ) ),
                        0.07 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 2 ), ChoiceId.create( 2 ) ),
                        0.88 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 3 ), ChoiceId.create( 2 ) ),
                        0.05 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 4 ), ChoiceId.create( 2 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 1 ), ChoiceId.create( 3 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 2 ), ChoiceId.create( 3 ) ),
                        0.34 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 3 ), ChoiceId.create( 3 ) ),
                        0.66 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 4 ), ChoiceId.create( 3 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 1 ), ChoiceId.create( 4 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 2 ), ChoiceId.create( 4 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 3 ), ChoiceId.create( 4 ) ),
                        0.56 ) //
                .put( new ErrorRateId( ParticipantId.create( 1 ), ChoiceId.create( 4 ), ChoiceId.create( 4 ) ),
                        0.44 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 1 ), ChoiceId.create( 1 ) ),
                        0.83 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 2 ), ChoiceId.create( 1 ) ),
                        0.17 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 3 ), ChoiceId.create( 1 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 4 ), ChoiceId.create( 1 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 1 ), ChoiceId.create( 2 ) ),
                        0.05 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 2 ), ChoiceId.create( 2 ) ),
                        0.63 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 3 ), ChoiceId.create( 2 ) ),
                        0.31 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 4 ), ChoiceId.create( 2 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 1 ), ChoiceId.create( 3 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 2 ), ChoiceId.create( 3 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 3 ), ChoiceId.create( 3 ) ),
                        1.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 4 ), ChoiceId.create( 3 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 1 ), ChoiceId.create( 4 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 2 ), ChoiceId.create( 4 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 3 ), ChoiceId.create( 4 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 2 ), ChoiceId.create( 4 ), ChoiceId.create( 4 ) ),
                        1.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 1 ), ChoiceId.create( 1 ) ),
                        1.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 2 ), ChoiceId.create( 1 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 3 ), ChoiceId.create( 1 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 4 ), ChoiceId.create( 1 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 1 ), ChoiceId.create( 2 ) ),
                        0.11 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 2 ), ChoiceId.create( 2 ) ),
                        0.79 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 3 ), ChoiceId.create( 2 ) ),
                        0.11 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 4 ), ChoiceId.create( 2 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 1 ), ChoiceId.create( 3 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 2 ), ChoiceId.create( 3 ) ),
                        0.40 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 3 ), ChoiceId.create( 3 ) ),
                        0.20 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 4 ), ChoiceId.create( 3 ) ),
                        0.40 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 1 ), ChoiceId.create( 4 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 2 ), ChoiceId.create( 4 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 3 ), ChoiceId.create( 4 ) ),
                        0.67 ) //
                .put( new ErrorRateId( ParticipantId.create( 3 ), ChoiceId.create( 4 ), ChoiceId.create( 4 ) ),
                        0.33 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 1 ), ChoiceId.create( 1 ) ),
                        0.94 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 2 ), ChoiceId.create( 1 ) ),
                        0.06 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 3 ), ChoiceId.create( 1 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 4 ), ChoiceId.create( 1 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 1 ), ChoiceId.create( 2 ) ),
                        0.05 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 2 ), ChoiceId.create( 2 ) ),
                        0.84 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 3 ), ChoiceId.create( 2 ) ),
                        0.10 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 4 ), ChoiceId.create( 2 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 1 ), ChoiceId.create( 3 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 2 ), ChoiceId.create( 3 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 3 ), ChoiceId.create( 3 ) ),
                        0.80 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 4 ), ChoiceId.create( 3 ) ),
                        0.20 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 1 ), ChoiceId.create( 4 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 2 ), ChoiceId.create( 4 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 3 ), ChoiceId.create( 4 ) ),
                        0.33 ) //
                .put( new ErrorRateId( ParticipantId.create( 4 ), ChoiceId.create( 4 ), ChoiceId.create( 4 ) ),
                        0.67 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 1 ), ChoiceId.create( 1 ) ),
                        1.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 2 ), ChoiceId.create( 1 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 3 ), ChoiceId.create( 1 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 4 ), ChoiceId.create( 1 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 1 ), ChoiceId.create( 2 ) ),
                        0.16 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 2 ), ChoiceId.create( 2 ) ),
                        0.73 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 3 ), ChoiceId.create( 2 ) ),
                        0.11 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 4 ), ChoiceId.create( 2 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 1 ), ChoiceId.create( 3 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 2 ), ChoiceId.create( 3 ) ),
                        0.21 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 3 ), ChoiceId.create( 3 ) ),
                        0.79 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 4 ), ChoiceId.create( 3 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 1 ), ChoiceId.create( 4 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 2 ), ChoiceId.create( 4 ) ),
                        0.00 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 3 ), ChoiceId.create( 4 ) ),
                        0.33 ) //
                .put( new ErrorRateId( ParticipantId.create( 5 ), ChoiceId.create( 4 ), ChoiceId.create( 4 ) ),
                        0.67 ) //
                .build();
    }

    @Test
    public void when_algorithmRunWithInputParameters_then_returnsExpectedClassEstimations() {
        //GIVEN
        final ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> expectedClassEstimatinos =
                ImmutableMap.<QuestionId, ImmutableSet<IndicatorEstimation>>builder().put( QuestionId.create( 1 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 2 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 1.00 ) ) ).put( QuestionId.create( 3 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 4 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 5 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 6 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 7 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.98 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.02 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 8 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 9 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 10 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 11 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 1.00 ) ) ).put( QuestionId.create( 12 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 13 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 14 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 15 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 16 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 17 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 18 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 19 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 20 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 21 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 22 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 23 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 24 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 25 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 26 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 27 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 28 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 29 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 30 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 31 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 32 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 33 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 34 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 35 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.95 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.05 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 36 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 1.00 ) ) ).put( QuestionId.create( 37 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 38 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.02 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.98 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 39 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 40 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 41 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 42 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 43 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 44 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).put( QuestionId.create( 45 ),
                        ImmutableSet.of( new IndicatorEstimation( ChoiceId.create( 1 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 2 ), 1.00 ),
                                new IndicatorEstimation( ChoiceId.create( 3 ), 0.00 ),
                                new IndicatorEstimation( ChoiceId.create( 4 ), 0.00 ) ) ).build();


        //WHEN
        final DawidSkeneAlgorithm dawidSkeneAlgorithm = new DawidSkeneAlgorithm( OBSERVATIONS );
        final DawidSkeneAlgorithm.Output output = dawidSkeneAlgorithm.run();

        //THEN
        assertThat( output.getPatientClassEstimations().keySet(), equalTo( expectedClassEstimatinos.keySet() ) );
        output.getPatientClassEstimations().forEach(
                ( patient, estimations ) -> estimations.forEach( estimation -> {
                    final IndicatorEstimation expectedEstimation = //
                            expectedClassEstimatinos.get( patient ).stream().filter(
                                    p -> p.getLabel().equals( estimation.getLabel() ) ).findFirst().get();
                    assertThat( estimation.getIndicatorEstimation(),
                            closeTo( expectedEstimation.getIndicatorEstimation(), 0.01 ) );
                } ) );
    }
}
