package algorithms.dawidskene;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.Arrays;

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

    private static final ImmutableSet<Observation> OBSERVATIONS = createObservationsFromRawData();

    private static ImmutableSet<Observation> createObservationsFromRawData() {
        final ImmutableSet.Builder<Observation> observations = ImmutableSet.builder();
        for (int patientIdx = 0; patientIdx < RAW_DATA.length; patientIdx++) {
            for (int observerIdx = 0; observerIdx < RAW_DATA[patientIdx].length; observerIdx++) {
                observations.add(
                        Observation.create( ObserverId.create( observerIdx + 1 ), PatientId.create( patientIdx + 1 ),
                                Arrays.stream( RAW_DATA[patientIdx][observerIdx] )
                                      .mapToObj( Label::create )
                                      .collect( ImmutableList.toImmutableList() ) ) );
            }
        }
        return observations.build();
    }

    @Test
    public void when_algorithmRunWithInputParameters_then_returnsExpectedValues() {
        final DawidSkeneAlgorithm dawidSkeneAlgorithm = new DawidSkeneAlgorithm( OBSERVATIONS );
        final DawidSkeneAlgorithm.Output output = dawidSkeneAlgorithm.run();
        System.err.println( output );
    }
}
