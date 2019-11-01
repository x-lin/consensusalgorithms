package algorithms.truthinference;

import algorithms.Id;

/**
 * @author LinX
 */
public final class ChoiceId extends Id<String> {
    private ChoiceId( final String id ) {
        super( id );
    }

    public static ChoiceId create( final int id ) {
        return new ChoiceId( String.valueOf( id ) );
    }

    public static ChoiceId create( final String id ) {
        return new ChoiceId( id );
    }
}
