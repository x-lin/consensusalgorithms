package algorithms.statistic;

/**
 * @author LinX
 */
public class ArtifactWithConfusionMatrix {
    private final String id;

    private final double quality;

    private final ConfusionMatrix confusionMatrix;

    public ArtifactWithConfusionMatrix( final ConfusionMatrix
            confusionMatrix, final String id, final double quality ) {
        this.id = id;
        this.quality = quality;
        this.confusionMatrix = confusionMatrix;
    }

    public String getId() {
        return this.id;
    }

    public double getQuality() {
        return this.quality;
    }

    public ConfusionMatrix getConfusionMatrix() {
        return this.confusionMatrix;
    }
}
