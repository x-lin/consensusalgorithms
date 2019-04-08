package algorithms.majorityvoting;

/**
 * @author LinX
 */
public enum WorkerQualityInfluence {
    LINEAR {
        @Override
        WorkerQuality calculateWorkerQuality( final double nrResults, final double falseResults, final double alpha ) {
            return new WorkerQuality( (nrResults - falseResults) / nrResults );
        }

        @Override
        WorkerQuality calculateWorkerQualityFromScore( final double score,
                final double alpha ) {
            return new WorkerQuality( score );
        }
    },
    EXPONENTIAL {
        @Override
        WorkerQuality calculateWorkerQuality( final double nrResults, final double falseResults, final double alpha ) {
            return new WorkerQuality( Math.exp( (-1) * alpha *
                    falseResults ) ); //formula from "An Incremental Truth Inference to Aggregate Contributions in GWAPs
        }

        @Override
        WorkerQuality calculateWorkerQualityFromScore( final double score,
                final double alpha ) {
            return new WorkerQuality( Math.exp( (-1) * alpha *
                    (1 - score) ) ); //formula from "An Incremental Truth Inference to Aggregate Contributions in GWAPs
        }
    },
    NONE {
        @Override
        WorkerQuality calculateWorkerQuality( final double nrResults, final double falseResults, final double alpha ) {
            return new WorkerQuality( 1 );
        }

        @Override
        WorkerQuality calculateWorkerQualityFromScore( final double overallScore,
                final double alpha ) {
            return new WorkerQuality( 1 );
        }
    };

    abstract WorkerQuality calculateWorkerQuality( double nrResults, double falseResults, double alpha );

    abstract WorkerQuality calculateWorkerQualityFromScore( double score, double alpha );
}
