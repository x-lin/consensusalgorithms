package model;

import algorithms.crowdtruth.WorkerId;
import algorithms.finaldefects.Semester;
import algorithms.finaldefects.SemesterSettings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.UncheckedSQLException;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @author LinX
 */
public class ExperienceQuestionnaireResult {
    private static final Logger LOG = LoggerFactory.getLogger( ExperienceQuestionnaireResult.class );

    private static final ImmutableMap<Semester, String> CSV_FILE_PATHS = ImmutableMap.of(
            Semester.WS2017, "src/main/resources/additions/experienceQuestionnaireWS17.csv",
            Semester.SS2018, "src/main/resources/additions/experienceQuestionnaireSS18.csv" );

    private final WorkerId workerId;

    private final ImmutableSet<Experience> results;

    public ExperienceQuestionnaireResult( final WorkerId workerId, final ImmutableSet<Experience> results ) {
        this.workerId = workerId;
        this.results = results;
    }

    public WorkerId getWorkerId() {
        return this.workerId;
    }

    public ImmutableSet<Experience> getResults() {
        return this.results;
    }

    @Override
    public String toString() {
        return "ExperienceQuestionnaireResult{" +
                "workerId=" + this.workerId +
                ", results=" + this.results +
                '}';
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ExperienceQuestionnaireResult that = (ExperienceQuestionnaireResult) o;
        return Objects.equals( this.workerId, that.workerId ) &&
                Objects.equals( this.results, that.results );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.workerId, this.results );
    }

    public static ImmutableMap<WorkerId, ExperienceQuestionnaireResult> fetch( final SemesterSettings settings ) {
        final ImmutableSet<Participant> participants = getParticipants( settings );
        try (Reader reader = Files.newBufferedReader( Paths.get( CSV_FILE_PATHS.get( settings.getSemester() ) ) );
             CSVReader csvReader = new CSVReaderBuilder( reader ).withSkipLines( 1 ).build()) {
            final List<String[]> lines = csvReader.readAll();
            return lines.stream().map( l -> {
                int startIndex = settings.getSemester() == Semester.WS2017 ? 1 : 0;
                final String name = l[1];
                final ImmutableSet.Builder<String> participantIds = ImmutableSet.<String>builder().add(
                        l[startIndex + 2] );
                if (settings.getSemester() == Semester.WS2017) {
                    participantIds.add( l[startIndex + 1] );
                }
                return matchParticipant( participants, participantIds.build(), name ).map( p -> {
                    WorkerId workerId = new WorkerId( p.getWorkerId() );
                    ImmutableSet<Experience> results = calculateExperienceResults( l, startIndex );
                    return new AbstractMap.SimpleImmutableEntry<>( workerId,
                            new ExperienceQuestionnaireResult( workerId, results ) );
                } ).orElse( null );
            } ).filter( Objects::nonNull ).collect(
                    ImmutableMap.toImmutableMap( Map.Entry::getKey, Map.Entry::getValue ) );
        } catch (final IOException e) {
            throw new UncheckedIOException( e );
        }
    }

    private static ImmutableSet<Experience> calculateExperienceResults( final String[] l,
            final int startIndex ) {
        return ImmutableSet.<Experience>builder()
                .add( new Experience( QuestionType.LANGUAGE, 1, 0, 5, Integer.valueOf( l[startIndex + 3] ) ) )
                .add( new Experience( QuestionType.SOFTWARE_PROJECT, 1, 0, 3,
                        assignNrSoftwareProjectsToRange( l[startIndex + 4] ) ) )
                .add( new Experience( QuestionType.SOFTWARE_PROJECT, 3, 0, 5,
                        Integer.valueOf( l[startIndex + 6] ) ) )
                .add( new Experience( QuestionType.SOFTWARE_PROJECT, 4, 0, 5,
                        Integer.valueOf( l[startIndex + 7] ) ) )
                .add( new Experience( QuestionType.SOFTWARE_PROJECT, 5, 0, 5,
                        Integer.valueOf( l[startIndex + 8] ) ) )
                .add( new Experience( QuestionType.SOFTWARE_PROJECT, 6, 0, 5,
                        Integer.valueOf( l[startIndex + 9] ) ) )
                .add( new Experience( QuestionType.SOFTWARE_PROJECT, 7, 0, 5,
                        Integer.valueOf( l[startIndex + 10] ) ) )
                .add( new Experience( QuestionType.SOFTWARE_PROJECT, 8, 0, 5,
                        Integer.valueOf( l[startIndex + 11] ) ) )
                .add( new Experience( QuestionType.SOFTWARE_PROJECT, 9, 0, 5,
                        Integer.valueOf( l[startIndex + 12] ) ) )
                .add( new Experience( QuestionType.QUALITY_ASSURANCE, 1, 0, 5,
                        Integer.valueOf( l[startIndex + 13] ) ) )
                .add( new Experience( QuestionType.QUALITY_ASSURANCE, 2, 0, 5,
                        Integer.valueOf( l[startIndex + 14] ) ) )
                .add( new Experience( QuestionType.QUALITY_ASSURANCE, 3, 0, 5,
                        Integer.valueOf( l[startIndex + 15] ) ) )
                .add( new Experience( QuestionType.WORKING_ENVIRONMENT, 1, 0, 5,
                        Integer.valueOf( l[startIndex + 16] ) ) )
                .add( new Experience( QuestionType.WORKING_ENVIRONMENT, 2, 0, 5,
                        Integer.valueOf( l[startIndex + 17] ) ) )
                .add( new Experience( QuestionType.DOMAIN_EXPERIENCE, 1, 0, 5,
                        Integer.valueOf( l[startIndex + 18] ) ) )
                .add( new Experience( QuestionType.CROWDSOURCING_APPLICATIONS, 1, 0, 5,
                        Integer.valueOf( l[startIndex + 19] ) ) )
                .add( new Experience( QuestionType.CROWDSOURCING_APPLICATIONS, 2, 0, 5,
                        Integer.valueOf( l[startIndex + 20] ) ) )
                .add( new Experience( QuestionType.CROWDSOURCING_APPLICATIONS, 3, 0, 5,
                        Integer.valueOf( l[startIndex + 21] ) ) )
                .build();
    }

    private static int assignNrSoftwareProjectsToRange( final String value ) {
        if (Objects.equals( "0", value )) {
            return 0;
        }
        else if (Objects.equals( "1-5", value )) {
            return 1;
        }
        else if (Objects.equals( "5-10", value )) {
            return 2;
        }
        else if (Objects.equals( "> 10", value )) {
            return 3;
        }
        else {
            throw new NoSuchElementException( "Unknown value" + value );
        }
    }

    private static Optional<Participant> matchParticipant( final ImmutableSet<Participant> participants,
            final ImmutableSet<String> participantIdCandidates, final String name ) {
        final Participant participant = participants.stream().filter(
                p -> (participantIdCandidates.contains( p.getParticipantId() ) ||
                        participantIdCandidates.stream().anyMatch( c -> p.getParticipantId().contains( c ) )) &&
                        Objects.equals( ImmutableSet.copyOf( p.getName().toLowerCase().split( " " ) ),
                                ImmutableSet.copyOf( name.toLowerCase().split( " " ) ) ) ).findFirst().orElseGet(
                () -> participants.stream().filter(
                        p -> Objects.equals( ImmutableSet.copyOf( p.getName().toLowerCase().split( " " ) ),
                                ImmutableSet.copyOf( name.toLowerCase().split( " " ) ) ) ).findFirst()
                                  .orElseGet( () -> participants.stream().filter( p -> participantIdCandidates.stream()
                                                                                                              .anyMatch(
                                                                                                                      c -> p
                                                                                                                              .getParticipantId()
                                                                                                                              .contains(
                                                                                                                                      c ) ) )
                                                                .findFirst().orElse( null ) ) );
        if (participant == null) {
            LOG.warn( "Participant from questionnaire {} could not be matched to workerId!", name );
        }

        return Optional.ofNullable( participant );
    }

    private static ImmutableSet<Participant> getParticipants( final SemesterSettings settings ) {
        try (Connection c = DatabaseConnector.createConnection()) {
            return Participant.fetchParticipants( c, settings );
        } catch (final SQLException e) {
            throw new UncheckedSQLException( e );
        }
    }

    public static void main( final String[] args ) {
        ExperienceQuestionnaireResult.fetch( SemesterSettings.ss2018() );
    }

    public static class Experience {
        private final QuestionType questionType;

        private final int questionNr;

        private final int minRange;

        private final int maxRange;

        private final int score;

        public Experience( final QuestionType questionType, final int questionNr, final int minRange,
                final int maxRange, final int score ) {
            this.questionType = questionType;
            this.questionNr = questionNr;
            this.minRange = minRange;
            this.maxRange = maxRange;
            this.score = score;
        }

        public double getScoreRatio() {
            return ((double) this.minRange + this.score) / ((double) this.maxRange - this.minRange);
        }

        @Override
        public boolean equals( final Object o ) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Experience that = (Experience) o;
            return this.questionNr == that.questionNr &&
                    this.minRange == that.minRange &&
                    this.maxRange == that.maxRange &&
                    this.score == that.score &&
                    this.questionType == that.questionType;
        }

        @Override
        public int hashCode() {
            return Objects.hash( this.questionType, this.questionNr, this.minRange, this.maxRange, this.score );
        }

        @Override
        public String toString() {
            return "Experience{" +
                    "questionType=" + this.questionType +
                    ", questionNr=" + this.questionNr +
                    ", minRange=" + this.minRange +
                    ", maxRange=" + this.maxRange +
                    ", score=" + this.score +
                    '}';
        }
    }

    public enum QuestionType {
        LANGUAGE( "Language Skills" ),
        SOFTWARE_PROJECT( "Software Project Skills" ),
        QUALITY_ASSURANCE( "Quality Assurance Skills" ),
        WORKING_ENVIRONMENT( "Working Environment" ),
        DOMAIN_EXPERIENCE( "Domain Experience" ),
        CROWDSOURCING_APPLICATIONS( "Crowdsourcing Applications Experience" );

        private final String name;

        QuestionType( final String name ) {
            this.name = name;
        }
    }
}
