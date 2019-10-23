package algorithms.dawidskene;

import algorithms.Id;

/**
 * @author LinX
 */
public final class PatientId extends Id<String> {

    private PatientId( final String id ) {
        super( id );
    }

    public static PatientId create( final int id ) {
        return new PatientId( String.valueOf( id ) );
    }

    public static PatientId create( final String id ) {
        return new PatientId( id );
    }
}
