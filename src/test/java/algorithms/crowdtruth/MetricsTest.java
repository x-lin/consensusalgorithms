package algorithms.crowdtruth;

import algorithms.crowdtruth.Metrics.MetricsScores;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author LinX
 */
@RunWith(JUnitParamsRunner.class)
public class MetricsTest {
    private static final String[] TEST_DATA_ANNOTATION_OPTIONS = {"A", "B", "C", "D",
            "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y",
            "Z"};

    private static final String[] TUTORIAL_ANNOTATION_OPTIONS = {"causes", "manifestation", "treats",
            "prevents",
            "symptom", "diagnose_by_test_or_drug",
            "location", "side_effect", "contraindicates", "associated_with", "is_a", "part_of",
            "other", "none"};

    @Test
    @Parameters({"2work_agr.csv", "3work_agr.csv", "4work_agr.csv", "5work_agr.csv", "6work_agr.csv", "7work_agr" +
            ".csv", "8work_agr.csv", "9work_agr.csv", "10work_agr.csv"})
    public void allWorkersAgreeOnAForClosedTask( String filename ) {
        //WHEN
        MetricsScores metricsScores = calculateClosedMetricsScores( filename, TestData.class,
                TEST_DATA_ANNOTATION_OPTIONS );

        //THEN
        assertAnnotationQuality( metricsScores, equalTo( 1.0 ), a -> Objects.equals( a.toString(), "A" ) );
        assertAnnotationQuality( metricsScores, equalTo( 0.0 ), a -> !Objects.equals( a.toString(), "A" ) );
        assertWorkerQuality( metricsScores, equalTo( 1.0 ) );
        assertMediaUnitQuality( metricsScores, equalTo( 1.0 ) );
    }

    @Test
    @Parameters({"2work_disagr.csv", "3work_disagr.csv", "4work_disagr.csv", "5work_disagr.csv", "6work_disagr.csv",
            "7work_disagr.csv", "8work_disagr.csv", "9work_disagr.csv", "10work_disagr.csv"})
    public void allWorkersDisagreeForClosedTask( String filename ) {
        //WHEN
        MetricsScores metricsScores = calculateClosedMetricsScores( filename, TestData.class,
                TEST_DATA_ANNOTATION_OPTIONS );

        //THEN
        assertAnnotationQuality( metricsScores, equalTo( 0.0 ), a -> true );
        assertWorkerQuality( metricsScores, equalTo( 0.0 ) );
        assertMediaUnitQuality( metricsScores, equalTo( 0.0 ) );
    }

    @Test
    @Parameters({"3work_outlier.csv", "4work_outlier.csv", "5work_outlier.csv", "6work_outlier.csv", "7work_outlier" +
            ".csv", "8work_outlier.csv", "9work_outlier.csv", "10work_outlier.csv"})
    public void outlierWorkerForClosedTask( String filename ) {
        //WHEN
        MetricsScores metricsScores = calculateClosedMetricsScores( filename, TestData.class,
                TEST_DATA_ANNOTATION_OPTIONS );

        //THEN
        assertAnnotationQuality( metricsScores, equalTo( 1.0 ), a -> Objects.equals( a.toString(), "B" ) );
        assertAnnotationQuality( metricsScores, equalTo( 0.0 ), a -> !Objects.equals( a.toString(), "B" ) );
        assertWorkerQuality( metricsScores, equalTo( 1.0 ), w -> !Objects.equals( w.toString(), "W1" ) );
        assertWorkerQuality( metricsScores, equalTo( 0.0 ), w -> Objects.equals( w.toString(), "W1" ) );
        assertMediaUnitQuality( metricsScores, equalTo( 1.0 ) );
    }

    @Test
    @Parameters({"2vs3work_agr.csv", "3vs4work_agr.csv", "4vs5work_agr.csv", "5vs6work_agr.csv", "6vs7work_agr" +
            ".csv", "7vs8work_agr.csv", "8vs9work_agr.csv"})
    public void incrementalWorkerAgreementForClosedTask( String filename ) throws InterruptedException {
        //WHEN
        MetricsScores metricsScores = calculateClosedMetricsScores( filename, TestData.class,
                TEST_DATA_ANNOTATION_OPTIONS );

        //THEN
        //TODO add assertions
    }


    @Test
    @Parameters({"2work_agr.csv", "3work_agr.csv", "4work_agr.csv", "5work_agr.csv", "6work_agr.csv", "7work_agr" +
            ".csv", "8work_agr.csv", "9work_agr.csv", "10work_agr.csv"})
    public void allWorkersAgreeOnAForOpenTask( String filename ) {
        //WHEN
        MetricsScores metricsScores = calculateOpenMetricsScores( filename, TestData.class );

        //THEN
        assertAnnotationQualityEmpty( metricsScores );
        assertWorkerQuality( metricsScores, equalTo( 1.0 ) );
        assertMediaUnitQuality( metricsScores, equalTo( 1.0 ) );
    }


    @Test
    @Parameters({"2work_disagr.csv", "3work_disagr.csv", "4work_disagr.csv", "5work_disagr.csv", "6work_disagr.csv",
            "7work_disagr.csv", "8work_disagr.csv", "9work_disagr.csv", "10work_disagr.csv"})
    public void allWorkersDisagreeForOpenTask( String filename ) {
        //WHEN
        MetricsScores metricsScores = calculateOpenMetricsScores( filename, TestData.class );

        //THEN
        assertAnnotationQualityEmpty( metricsScores );
        assertWorkerQuality( metricsScores, equalTo( 0.0 ) );
        assertMediaUnitQuality( metricsScores, equalTo( 0.0 ) );
    }

    @Test
    @Parameters({"3work_outlier.csv", "4work_outlier.csv", "5work_outlier.csv", "6work_outlier.csv", "7work_outlier" +
            ".csv", "8work_outlier.csv", "9work_outlier.csv", "10work_outlier.csv"})
    public void outlierWorkerForOpenTask( String filename ) {
        //WHEN
        MetricsScores metricsScores = calculateOpenMetricsScores( filename, TestData.class );

        //THEN
        assertAnnotationQualityEmpty( metricsScores );
        assertWorkerQuality( metricsScores, equalTo( 1.0 ), w -> !Objects.equals( w.toString(), "W1" ) );
        assertWorkerQuality( metricsScores, equalTo( 0.0 ), w -> Objects.equals( w.toString(), "W1" ) );
        assertMediaUnitQuality( metricsScores, equalTo( 1.0 ) );
    }

    @Test
    @Parameters({"2vs3work_agr.csv", "3vs4work_agr.csv", "4vs5work_agr.csv", "5vs6work_agr.csv", "6vs7work_agr" +
            ".csv", "7vs8work_agr.csv", "8vs9work_agr.csv"})
    public void incrementalWorkerAgreementForOpenTask( String filename ) {
        //WHEN
        MetricsScores metricsScores = calculateOpenMetricsScores( filename, TestData.class );

        //THEN
        //TODO add assertions
    }

    @Test
    public void tutorialData() {
        //GIVEN
        List<TutorialData> data = parseData( "relex_example.csv", TutorialData.class );
        ImmutableList<CrowdtruthData> processedData = data.stream().flatMap( d -> Arrays.stream( d.getChosenAnnotation()
                .split( " " ) ).map( a -> a.substring( 1,
                a.length() - 1 ) ).map( String::toLowerCase ).map( a -> new
                CrowdtruthData( d.getMediaUnitId(), d.getId(),
                d.getWorkerId(), a ) ) )
                .collect( ImmutableList.toImmutableList() );

        //WHEN
        ImmutableSet<MediaUnit> annotatedUnits = CrowdtruthData.annotate( processedData,
                TUTORIAL_ANNOTATION_OPTIONS );
        MetricsScores metricsScores = Metrics.calculateClosed( annotatedUnits );

        //THEN
        //TODO add assertions
    }

    @Test
    public void tutorialDataCustom() {
        //GIVEN
        List<CustomTutorialData> data = parseData( "relex_example_custom.csv", CustomTutorialData.class );
        ImmutableList<CrowdtruthData> processedData = data.stream().flatMap( d -> Arrays.stream( d.getChosenAnnotation()
                .split( " " ) ).map( a -> a.substring( 1,
                a.length() - 1 ) ).map( String::toLowerCase ).map( a -> new
                CrowdtruthData( d.getMediaUnitId(), d.getId(), d.getWorkerId(), a ) ) )
                .collect( ImmutableList.toImmutableList() );

        //WHEN
        ImmutableSet<MediaUnit> annotatedUnits = CrowdtruthData.annotate( processedData,
                TUTORIAL_ANNOTATION_OPTIONS );
        MetricsScores metricsScores = Metrics.calculateClosed( annotatedUnits );

        //THEN
        //TODO add assertions
    }

    private void assertAnnotationQualityEmpty( MetricsScores metricsScores ) {
        assertThat( metricsScores.getAnnotationQualityScores(), equalTo( ImmutableMap.of() ) );
    }

    private static void assertAnnotationQuality( MetricsScores metricsScores, Matcher<Double>
            matcher, Predicate<AnnotationName> annotationFilter ) {
        metricsScores.getAnnotationQualityScores().entrySet().stream().filter( e -> annotationFilter.test( e
                .getKey().getName() ) ).forEach( e -> assertThat( "AQS does not match for " + e.getKey(), e.getValue(),
                matcher ) );
    }

    private static void assertWorkerQuality( MetricsScores metricsScores, Matcher<Double> matcher,
                                             Predicate<WorkerId>
                                                     workerFilter ) {
        metricsScores.getWorkerQualityScores().entrySet().stream().filter( e -> workerFilter.test( e.getKey().getId()
        ) ).forEach( e -> assertThat( "WQS does not match for worker " + e.getKey(), e.getValue(), matcher ) );
    }

    private static void assertWorkerQuality( MetricsScores metricsScores, Matcher<Double> matcher ) {
        metricsScores.getWorkerQualityScores().forEach( ( w, q ) -> assertThat( "WQS does not match for " +
                "worker " + w, q, matcher ) );
    }

    private static void assertMediaUnitQuality( MetricsScores metricsScores, Matcher<Double> matcher ) {
        metricsScores.getMediaUnitQualityScores().forEach( ( a, q ) -> assertThat( "UQS does not match for " +
                "media unit " + a, q, matcher ) );
    }

    private static <T extends Data> MetricsScores calculateClosedMetricsScores( String filename, Class<T>
            deserializedType, String... allAnnotations ) {
        List<T> data = parseData( filename, deserializedType );
        ImmutableSet<CrowdtruthData> convertedData = convertData( data );
        ImmutableSet<MediaUnit> annotatedUnits = CrowdtruthData.annotate( convertedData, allAnnotations );
        return Metrics.calculateClosed( annotatedUnits );
    }

    private static <T extends Data> MetricsScores calculateOpenMetricsScores( String filename, Class<T>
            deserializedType ) {
        List<T> data = parseData( filename, deserializedType );
        ImmutableSet<MediaUnit> annotatedUnits = CrowdtruthData.annotate( convertData( data ) );
        return Metrics.calculateOpen( annotatedUnits );
    }

    public static <T extends Data> ImmutableSet<CrowdtruthData> convertData( List<T> data ) {
        return data.stream().map( d -> new CrowdtruthData(
                d.getMediaUnitId(), d.getId(), d.getWorkerId(), d.getChosenAnnotation() ) ).collect( ImmutableSet
                .toImmutableSet() );
    }


    private static <T extends Data> ImmutableList<T> parseData( String filename, Class<T>
            deserializedClass ) {
        try {
            List<T> data = new CsvToBeanBuilder<T>( new FileReader(
                    "src/test/resources/algorithms.crowdtruth/test_data/metrics/" + filename ) ).withType(
                    deserializedClass )
                    .build().parse();
            System.err.println( "=======Parsed CSV data=========" );
            System.err.println( data );
            return ImmutableList.copyOf( data );
        } catch (FileNotFoundException e) {
            throw new AssertionError( "Cannot parse data.", e );
        }
    }

    private interface Data {
        String getMediaUnitId();

        String getId();

        String getWorkerId();

        String getChosenAnnotation();
    }

    public static class CustomTutorialData implements Data {
        @CsvBindByName(column = "unit_id", required = true)
        private String mediaUnitId;

        @CsvBindByName(column = "_id", required = true)
        private String id;

        @CsvBindByName(column = "_worker_id", required = true)
        private String workerId;

        @CsvBindByName(column = "relations", required = true)
        private String chosenAnnotation;

        @Override
        public String getMediaUnitId() {
            return this.mediaUnitId;
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public String getWorkerId() {
            return this.workerId;
        }

        @Override
        public String getChosenAnnotation() {
            return this.chosenAnnotation;
        }

        @Override
        public String toString() {
            return "CustomTutorialData{" +
                    "mediaUnitId='" + this.mediaUnitId + '\'' +
                    ", id='" + this.id + '\'' +
                    ", workerId='" + this.workerId + '\'' +
                    ", chosenAnnotation='" + this.chosenAnnotation + '\'' +
                    '}';
        }
    }


    public static class TutorialData implements Data {
        @CsvBindByName(column = "_unit_id", required = true)
        private String mediaUnitId;

        @CsvBindByName(column = "_id", required = true)
        private String id;

        @CsvBindByName(column = "_worker_id", required = true)
        private String workerId;

        @CsvBindByName(column = "relations", required = true)
        private String chosenAnnotation;

        @Override
        public String getMediaUnitId() {
            return this.mediaUnitId;
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public String getWorkerId() {
            return this.workerId;
        }

        @Override
        public String getChosenAnnotation() {
            return this.chosenAnnotation;
        }

        @Override
        public String toString() {
            return "TutorialData{" +
                    "mediaUnitId='" + this.mediaUnitId + '\'' +
                    ", id='" + this.id + '\'' +
                    ", workerId='" + this.workerId + '\'' +
                    ", chosenAnnotation='" + this.chosenAnnotation + '\'' +
                    '}';
        }
    }

    public static class TestData implements Data {
        @CsvBindByName(column = "_unit_id", required = true)
        private String mediaUnitId;

        @CsvBindByName(column = "_id", required = true)
        private String id;

        @CsvBindByName(column = "_worker_id", required = true)
        private String workerId;

        @CsvBindByName(column = "out_col", required = true)
        private String chosenAnnotation;

        @Override
        public String toString() {
            return "TestData{" +
                    "mediaUnitId='" + this.mediaUnitId + '\'' +
                    ", id='" + this.id + '\'' +
                    ", workerId='" + this.workerId + '\'' +
                    ", chosenAnnotation='" + this.chosenAnnotation + '\'' +
                    '}';
        }

        @Override
        public String getMediaUnitId() {
            return this.mediaUnitId;
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public String getWorkerId() {
            return this.workerId;
        }

        @Override
        public String getChosenAnnotation() {
            return this.chosenAnnotation;
        }
    }
}
