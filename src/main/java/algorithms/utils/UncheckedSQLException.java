package algorithms.utils;

import java.sql.SQLException;

/**
 * @author LinX
 */
public class UncheckedSQLException extends RuntimeException {
    public UncheckedSQLException( final SQLException underlyingException ) {
        super( underlyingException );
    }

    public static <T> T uncheck( final ThrowableGettableAction<T> action ) {
        try {
            return action.run();
        } catch (final SQLException e) {
            throw new UncheckedSQLException( e );
        }
    }

    public static void uncheck( final ThrowableRunnable action ) {
        try {
            action.run();
        } catch (final SQLException e) {
            throw new UncheckedSQLException( e );
        }
    }

    public interface ThrowableGettableAction<T> {
        T run() throws SQLException;
    }

    public interface ThrowableRunnable {
        void run() throws SQLException;
    }
}
