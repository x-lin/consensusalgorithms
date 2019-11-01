package algorithms.truthinference;

import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author LinX
 */
public final class Answers {
    private static final Logger LOG = LoggerFactory.getLogger( Answers.class );

    private final ImmutableMap<QuestionId, ImmutableSet<Answer>> byQuestions;

    private final ImmutableMap<ParticipantId, ImmutableSet<Answer>> byParticipants;

    private final ImmutableMap<ChoiceId, ImmutableMultiset<Answer>> byChoices;

    private final ImmutableMap<ParticipantId, ImmutableMap<QuestionId, ImmutableSet<Answer>>> byParticipantsForQuestion;

    public Answers( final Set<Answer> answers ) {
        final Map<QuestionId, Set<Answer>> byQuestions = Maps.newLinkedHashMap();
        final Map<ParticipantId, Set<Answer>> byParticipants = Maps.newLinkedHashMap();
        final Map<ChoiceId, List<Answer>> byChoices = Maps.newLinkedHashMap();
        final Map<ParticipantId, Map<QuestionId, Set<Answer>>> byParticipantForQuestion = Maps.newLinkedHashMap();

        answers.forEach( answer -> {
            byQuestions.computeIfAbsent( answer.getQuestionId(), i -> Sets.newLinkedHashSet() ).add( answer );
            byParticipants.computeIfAbsent( answer.getParticipantId(), i -> Sets.newLinkedHashSet() ).add( answer );
            answer.getChoices().forEach(
                    c -> byChoices.computeIfAbsent( c, id -> Lists.newArrayList() ).add( answer ) );
            byParticipantForQuestion.computeIfAbsent( answer.getParticipantId(), i -> Maps.newLinkedHashMap() )
                                    .computeIfAbsent( answer.getQuestionId(), k -> Sets.newLinkedHashSet() ).add(
                    answer );
        } );

        this.byQuestions = byQuestions.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> ImmutableSet.copyOf( e.getValue() ) ) );
        this.byParticipants = byParticipants.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> ImmutableSet.copyOf( e.getValue() ) ) );
        this.byChoices = byChoices.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> ImmutableMultiset.copyOf( e.getValue() ) ) );
        this.byParticipantsForQuestion = byParticipantForQuestion.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey,
                        e -> e.getValue().entrySet().stream().collect( ImmutableMap
                                .toImmutableMap( Map.Entry::getKey, e2 -> ImmutableSet.copyOf( e2.getValue() ) ) ) ) );
        LOG.info( "Questions #{}, participants #{}, choices #{}", this.byQuestions.size(), this.byParticipants.size(),
                this.byChoices.size() );
    }

    public ImmutableSet<Answer> getAnswers( final QuestionId questionId ) {
        return this.byQuestions.getOrDefault( questionId, ImmutableSet.of() );
    }

    public ImmutableSet<Answer> getAnswers( final ParticipantId participantId ) {
        return this.byParticipants.getOrDefault( participantId, ImmutableSet.of() );
    }

    public ImmutableMultiset<Answer> getAnswers( final ChoiceId choiceId ) {
        return this.byChoices.getOrDefault( choiceId, ImmutableMultiset.of() );
    }

    public ImmutableSet<Answer> getAnswers( final ParticipantId participant, final QuestionId question ) {
        return this.byParticipantsForQuestion.getOrDefault( participant, ImmutableMap.of() ).getOrDefault( question,
                ImmutableSet.of() );
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
}
