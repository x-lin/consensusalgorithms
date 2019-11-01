package algorithms.catd;

import algorithms.fastdawidskene.Answer;
import algorithms.fastdawidskene.ChoiceId;
import algorithms.fastdawidskene.ParticipantId;
import algorithms.fastdawidskene.QuestionId;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author LinX
 */
public class ZenCrowdAlgorithmTest {
    @Test
    public void when_algorithmRunWithInputParameters_then_returnsExpectedClassProbabilities() throws IOException {
        final int maxDataset = 9;

        for (int dataset = 1; dataset <= maxDataset; dataset++) {
            //GIVEN
            final ImmutableSet<Answer> answers = parseData( dataset );

            //WHEN
            final ZenCrowdAlgorithm algorithm = new ZenCrowdAlgorithm( answers );
            final ZenCrowdAlgorithm.Output output = algorithm.run();
            //THEN
            final ImmutableMap<QuestionId, ChoiceId> expected = getExpectedTruths( dataset );
            output.getClassProbabilities().forEach( ( question, estimates ) -> {
                final ChoiceId highest = estimates.stream().max(
                        Comparator.comparingDouble( ZenCrowdAlgorithm.ClassEstimation::getEstimation ) ).map(
                        ZenCrowdAlgorithm.ClassEstimation::getChoice ).get();
                assertThat( "question " + question, highest, equalTo( expected.get( question ) ) );
            } );
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
                    "src/test/resources/algorithms/catd/s4_Dog data/0/l_ZenCrowd_truth_" + dataset + ".csv" ) );

            return csvReader.readAll().stream().collect(
                    ImmutableMap.toImmutableMap( r -> QuestionId.create( r[0] ), r -> ChoiceId.create( r[1] ) ) );
        } catch (final IOException e) {
            throw new AssertionError( "Cannot parse data.", e );
        }
    }
}
