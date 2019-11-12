package algorithms.csv;

import java.util.List;

/**
 * @author LinX
 */
class SingleLineStatistics {
    private final List<String> headers;

    private final List<String> values;

    public SingleLineStatistics( final List<String> headers, final List<String> values ) {
        this.headers = headers;
        this.values = values;
    }

    public List<String> getHeaders() {
        return this.headers;
    }

    public List<String> getValues() {
        return this.values;
    }
}
