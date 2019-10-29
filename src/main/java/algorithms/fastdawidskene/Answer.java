package algorithms.fastdawidskene;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

/**
 * @author LinX
 */
public final class Answer {
    private final ParticipantId participantId;

    private final QuestionId questionId;

    private final ImmutableList<ChoiceId> choices;

    public Answer( final ParticipantId participantId, final QuestionId questionId,
            final ImmutableList<ChoiceId> choices ) {
        this.participantId = participantId;
        this.questionId = questionId;
        this.choices = choices;
    }

    public ParticipantId getParticipantId() {
        return this.participantId;
    }

    public QuestionId getQuestionId() {
        return this.questionId;
    }

    public ImmutableList<ChoiceId> getChoices() {
        return this.choices;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Answer answer = (Answer) o;
        return Objects.equals( this.participantId, answer.participantId ) &&
                Objects.equals( this.questionId, answer.questionId ) &&
                Objects.equals( this.choices, answer.choices );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.participantId, this.questionId, this.choices );
    }

    @Override
    public String toString() {
        return "Answer{" +
                "participantId=" + this.participantId +
                ", questionId=" + this.questionId +
                ", choices=" + this.choices +
                '}';
    }

    public static Answer create( final ParticipantId participantId, final QuestionId questionId,
            final ImmutableList<ChoiceId> choices ) {
        return new Answer( participantId, questionId, choices );
    }
}
