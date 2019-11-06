package algorithms.truthinference;

import java.util.Objects;

/**
 * @author LinX
 */
public final class Answer {
    private final ParticipantId participantId;

    private final QuestionId questionId;

    private final ChoiceId choice;

    public Answer( final ParticipantId participantId, final QuestionId questionId,
            final ChoiceId choice ) {
        this.participantId = participantId;
        this.questionId = questionId;
        this.choice = choice;
    }

    public ParticipantId getParticipantId() {
        return this.participantId;
    }

    public QuestionId getQuestionId() {
        return this.questionId;
    }

    public ChoiceId getChoice() {
        return this.choice;
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
                Objects.equals( this.choice, answer.choice );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.participantId, this.questionId, this.choice );
    }

    @Override
    public String toString() {
        return "Answer{" +
                "participantId=" + this.participantId +
                ", questionId=" + this.questionId +
                ", choice=" + this.choice +
                '}';
    }

    public static Answer create( final ParticipantId participantId, final QuestionId questionId,
            final ChoiceId choice ) {
        return new Answer( participantId, questionId, choice );
    }
}
