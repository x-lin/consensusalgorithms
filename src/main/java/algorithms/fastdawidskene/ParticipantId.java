package algorithms.fastdawidskene;

import algorithms.Id;

/**
 * @author LinX
 */
public final class ParticipantId extends Id<String> {
    private ParticipantId( final String id ) {
        super( id );
    }

    public static ParticipantId create( final int id ) {
        return new ParticipantId( String.valueOf( id ) );
    }

    public static ParticipantId create( final String id ) {
        return new ParticipantId( id );
    }
}
