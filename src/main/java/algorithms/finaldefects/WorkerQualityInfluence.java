package algorithms.finaldefects;

/**
 * @author LinX
 */
public enum WorkerQualityInfluence {
    LINEAR {
        @Override
        public WorkerQuality calculateWorkerQuality( final double nrResults, final double falseResults,
                final double alpha ) {
            return new WorkerQuality( (nrResults - falseResults) / nrResults );
        }

        @Override
        public WorkerQuality calculateWorkerQualityFromScore( final double score,
                final double alpha ) {
            return new WorkerQuality( score );
        }
    },
    EXPONENTIAL {
        @Override
        public WorkerQuality calculateWorkerQuality( final double nrResults, final double falseResults,
                final double alpha ) {
            return new WorkerQuality( Math.exp( (-1) * alpha *
                    falseResults ) ); //formula from "An Incremental Truth Inference to Aggregate Contributions in GWAPs
        }

        @Override
        public WorkerQuality calculateWorkerQualityFromScore( final double score,
                final double alpha ) {
            return new WorkerQuality( Math.exp( (-1) * alpha *
                    (1 - score) ) ); //formula from "An Incremental Truth Inference to Aggregate Contributions in GWAPs
        }
    };

    public abstract WorkerQuality calculateWorkerQuality( double nrResults, double falseResults, double alpha );

    public abstract WorkerQuality calculateWorkerQualityFromScore( double score, double alpha );
}
