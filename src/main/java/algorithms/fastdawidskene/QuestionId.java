package algorithms.fastdawidskene;

import algorithms.Id;

/**
 * @author LinX
 */
public final class QuestionId extends Id<String> {
    private QuestionId( final String id ) {
        super( id );
    }

    public static QuestionId create( final int id ) {
        return new QuestionId( String.valueOf( id ) );
    }

    public static QuestionId create( final String id ) {
        return new QuestionId( id );
    }
}
