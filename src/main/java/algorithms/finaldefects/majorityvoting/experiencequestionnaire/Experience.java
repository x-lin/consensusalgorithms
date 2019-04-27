package algorithms.finaldefects.majorityvoting.experiencequestionnaire;

import java.util.Objects;

/**
 * @author LinX
 */
public class Experience {
    private final ExperienceQuestionType questionType;

    private final int questionNr;

    private final int minRange;

    private final int maxRange;

    private final int score;

    public Experience( final ExperienceQuestionType questionType, final int questionNr, final int minRange,
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

    public ExperienceQuestionType getQuestionType() {
        return this.questionType;
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
