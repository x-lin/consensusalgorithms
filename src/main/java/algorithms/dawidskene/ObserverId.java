package algorithms.dawidskene;

import algorithms.Id;

/**
 * @author LinX
 */
public final class ObserverId extends Id<String> {
    private ObserverId( final String id ) {
        super( id );
    }

    public static ObserverId create( final int id ) {
        return new ObserverId( String.valueOf( id ) );
    }

    public static ObserverId create( final String id ) {
        return new ObserverId( id );
    }
}
