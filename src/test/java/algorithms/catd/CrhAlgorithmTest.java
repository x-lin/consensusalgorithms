package algorithms.catd;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author LinX
 */
public class CrhAlgorithmTest {
    @Test
    public void when_algorithmRunWithInputParameters_then_returnsExpectedClassProbabilities() throws IOException {
        final int maxDataset = 9;

        for (int dataset = 0; dataset <= maxDataset; dataset++) {
            //GIVEN
            final ImmutableSet<Answer> answers = parseData( dataset );

            //WHEN
            final CrhAlgorithm algorithm = new CrhAlgorithm( answers );
            final CrhAlgorithm.Output output = algorithm.run();

            //THEN
            final ImmutableMap<QuestionId, ChoiceId> expected = getExpectedTruths( dataset );
            assertThat( Maps.difference( output.getTruths(), expected ).toString(), output.getTruths(),
                    equalTo( expected ) );
        }
    }

    private static ImmutableSet<Answer> parseData( final int dataset ) {
        try {
            final CSVReader csvReader = new CSVReaderBuilder( new FileReader(
                    "src/test/resources/algorithms/catd/s4_Dog data/0/answer_" + dataset + ".csv" ) )
                    .withSkipLines( 1 )
                    .build();

            return csvReader.readAll().stream().map(
                    line -> Answer.create( ParticipantId.create( line[1] ), QuestionId.create( line[0] ),
                            ImmutableList.of( ChoiceId.create( line[2] ) ) ) ).collect(
                    ImmutableSet.toImmutableSet() );
        } catch (final IOException e) {
            throw new AssertionError( "Cannot parse data.", e );
        }
    }

    private static ImmutableMap<QuestionId, ChoiceId> getExpectedTruths( final int dataset ) {
        try {
            final CSVReader csvReader = new CSVReader( new FileReader(
                    "src/test/resources/algorithms/catd/s4_Dog data/0/c_PM-CRH_truth_" + dataset + ".csv" ) );

            return csvReader.readAll().stream().collect(
                    ImmutableMap.toImmutableMap( r -> QuestionId.create( r[0] ), r -> ChoiceId.create( r[1] ) ) );
        } catch (final IOException e) {
            throw new AssertionError( "Cannot parse data.", e );
        }
    }
}
