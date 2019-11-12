package algorithms.truthinference;

import algorithms.vericom.model.DefectReport;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author LinX
 */
public final class Answers {
    private static final Logger LOG = LoggerFactory.getLogger( Answers.class );

    private final ImmutableMap<QuestionId, ImmutableMultiset<Answer>> byQuestions;

    private final ImmutableMap<ParticipantId, ImmutableMultiset<Answer>> byParticipants;

    private final ImmutableMap<ChoiceId, ImmutableMultiset<Answer>> byChoices;

    private final ImmutableMap<ParticipantId, ImmutableMap<QuestionId, ImmutableMultiset<Answer>>>
            byParticipantsForQuestion;

    public Answers( final List<Answer> answers ) {
        final Map<QuestionId, List<Answer>> byQuestions = Maps.newLinkedHashMap();
        final Map<ParticipantId, List<Answer>> byParticipants = Maps.newLinkedHashMap();
        final Map<ChoiceId, List<Answer>> byChoices = Maps.newLinkedHashMap();
        final Map<ParticipantId, Map<QuestionId, List<Answer>>> byParticipantForQuestion = Maps.newLinkedHashMap();

        answers.forEach( answer -> {
            byQuestions.computeIfAbsent( answer.getQuestionId(), i -> Lists.newArrayList() ).add( answer );
            byParticipants.computeIfAbsent( answer.getParticipantId(), i -> Lists.newArrayList() ).add( answer );
            byChoices.computeIfAbsent( answer.getChoice(), id -> Lists.newArrayList() ).add( answer );
            byParticipantForQuestion.computeIfAbsent( answer.getParticipantId(), i -> Maps.newLinkedHashMap() )
                    .computeIfAbsent( answer.getQuestionId(), k -> Lists.newArrayList() ).add(
                    answer );
        } );

        this.byQuestions = byQuestions.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> ImmutableMultiset.copyOf( e.getValue() ) ) );
        this.byParticipants = byParticipants.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> ImmutableMultiset.copyOf( e.getValue() ) ) );
        this.byChoices = byChoices.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> ImmutableMultiset.copyOf( e.getValue() ) ) );
        this.byParticipantsForQuestion = byParticipantForQuestion.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey,
                        e -> e.getValue().entrySet().stream().collect( ImmutableMap
                                .toImmutableMap( Map.Entry::getKey,
                                        e2 -> ImmutableMultiset.copyOf( e2.getValue() ) ) ) ) );
        LOG.info( "Questions #{}, participants #{}, choices #{}", this.byQuestions.size(), this.byParticipants.size(),
                this.byChoices.size() );
    }

    public ImmutableMultiset<Answer> getAnswers( final QuestionId questionId ) {
        return this.byQuestions.getOrDefault( questionId, ImmutableMultiset.of() );
    }

    public ImmutableMultiset<Answer> getAnswers( final ParticipantId participantId ) {
        return this.byParticipants.getOrDefault( participantId, ImmutableMultiset.of() );
    }

    public ImmutableMultiset<Answer> getAnswers( final ChoiceId choiceId ) {
        return this.byChoices.getOrDefault( choiceId, ImmutableMultiset.of() );
    }

    public ImmutableMultiset<Answer> getAnswers( final ParticipantId participant, final QuestionId question ) {
        return this.byParticipantsForQuestion.getOrDefault( participant, ImmutableMap.of() ).getOrDefault( question,
                ImmutableMultiset.of() );
    }

    public ImmutableSet<QuestionId> getQuestions() {
        return this.byQuestions.keySet();
    }

    public ImmutableSet<ParticipantId> getParticipants() {
        return this.byParticipants.keySet();
    }

    public ImmutableSet<ChoiceId> getChoices() {
        return this.byChoices.keySet();
    }

    public static Answers fromDefectReports( final ImmutableSet<DefectReport> defectReports ) {
        return new Answers( ImmutableList.copyOf( defectReports.stream().map( report -> Answer
                .create( ParticipantId.create( report.getWorkerId().toInt() ),
                        QuestionId.create( report.getEmeAndScenarioId().toString() ),
                        ChoiceId.create( report.getDefectType().toString() ) ) )
                .collect( ImmutableSet.toImmutableSet() ) ) );
    }
}
