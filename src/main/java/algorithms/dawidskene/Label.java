package algorithms.dawidskene;

import algorithms.Id;

/**
 * @author LinX
 */
public final class Label extends Id<String> {
    private Label( final String id ) {
        super( id );
    }

    public static Label create( final int id ) {
        return new Label( String.valueOf( id ) );
    }

    public static Label create( final String id ) {
        return new Label( id );
    }
}
