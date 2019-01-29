package crowdtruth;

import java.util.Map;

/**
 * @author LinX
 */
public class ResultVector {
    private final Map<MediaUnitAnnotation, Result> result;

    public ResultVector( Map<MediaUnitAnnotation, Result> result ) {
        this.result = result;
    }

    public Result getResult( MediaUnitAnnotation mediaUnitAnnotation ) {
        return this.result.getOrDefault( mediaUnitAnnotation, AnnotationResult.create( false ) );
    }


    public Map<MediaUnitAnnotation, Result> getResult() {
        return this.result;
    }
}
