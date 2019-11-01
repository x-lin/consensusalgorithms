package algorithms.truthinference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Implements algorithm from:
 *
 * Learning From Crowds
 * V. C. Raykar et al.
 * 2010
 *
 * @author LinX
 */
public class LfcAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( LfcAlgorithm.class );

    //threshold under which the algorithm can be viewed as converged
    private static final double CONVERGENCE_THRESHOLD = 0.00001;

    private static final int MAXIMUM_NR_ITERATIONS = 100;

    private final Answers answers;

    public LfcAlgorithm( final Set<Answer> answers ) {
        this.answers = new Answers( answers );
    }

    public ZenCrowdAlgorithm.Output run() {
        final int iteration = 0;

//        ImmutableMap<ParticipantId, Double> workerReliability = initWorkerReliability();
//
//        Optional<ZenCrowdAlgorithm.Output> output = Optional.empty();
//
//        while (true) {
//            iteration++;
//            LOG.info( "Starting iteration {}.", iteration );
//
//            //e-step / estimation step
//            final ImmutableMap<QuestionId, ImmutableSet<ClassEstimation>> classEstimations = calculateClassEstimates(
//                    workerReliability );
//
//            //m-step / maximization step
//            workerReliability = calculateWorkerReliability( classEstimations );
//
//            final ZenCrowdAlgorithm.Output newOutput = new ZenCrowdAlgorithm.Output( classEstimations,
//                    workerReliability );
//            if (iteration >= MAXIMUM_NR_ITERATIONS || output.map( o -> o.hasConverged( newOutput ) ).orElse( false )) {
//                break;
//            }
//
//            output = Optional.of( newOutput );
//        }

        return null;
    }
}
