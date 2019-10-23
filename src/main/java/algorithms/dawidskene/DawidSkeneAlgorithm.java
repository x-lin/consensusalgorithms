package algorithms.dawidskene;

import algorithms.Id;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implements Crowdtruth algorithm for calculating worker disagreement metrics from:
 *
 * Maximum Likelihood IndicatorEstimation of Observer Error-Rates Using the EM Algorithm
 * A. P. Dawid and A. M. Skene
 * 1979
 *
 * @author LinX
 */
public class DawidSkeneAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( DawidSkeneAlgorithm.class );

    //threshold under which the algorithm can be viewed as converged
    private static final double CONVERGENCE_THRESHOLD = 0.00001;

    private static final int MAXIMUM_NR_ITERATIONS = 100;

    private final Observations observations;

    public DawidSkeneAlgorithm( final Set<Observation> observations ) {
        this.observations = new Observations( observations );
    }

    public Output run() {
        int iteration = 0;

        ImmutableMap<PatientId, ImmutableSet<IndicatorEstimation>> patientClassEstimations = Maps.toMap(
                this.observations.getPatients(), this::getInitialEstimatesForTruePatientClasses );

        Optional<Output> output = Optional.empty();

        while (true) {
            iteration++;

            //m-step / maximization step
            //estimate for p_j
            final ImmutableMap<Label, Double> patientClassProbabilities = calculatePatientClassProbabilities(
                    patientClassEstimations );
            LOG.info( "estimation for pj done." );
            //estimate for pi^k_jl
            final ImmutableMap<ErrorRateId, ErrorRateEstimation> errorRates = calculateErrorRates(
                    patientClassEstimations );
            LOG.info( "Maximization step done." );

            //e-step / estimation step
            //estimate for T_ij
            patientClassEstimations = reCalculatePatientClassEstimatesForTruePatientClasses( errorRates,
                    patientClassProbabilities );
            LOG.info( "Estimation step done." );

            //calculate log likelihood -> should go up as algorithm proceeds
            final double logLikelihood = calculateLogLikelihood( patientClassProbabilities, errorRates );
            LOG.info( "Log-Likelihood on iteration {}: {}", iteration, logLikelihood );

            final Output newOutput = new Output( patientClassProbabilities, errorRates, patientClassEstimations );
            if (iteration > MAXIMUM_NR_ITERATIONS || output.map( o -> o.hasConverged( newOutput ) ).orElse( false )) {
                break;
            }

            output = Optional.of( newOutput );
            LOG.info( "=======================" );
        }

        return output.get();
    }

    /**
     * Calculates the log likelihood given the current parameter estimates -> should monotically go up as EM proceeds.
     * See Equation 2.7
     *
     * @return likelihood
     */
    private double calculateLogLikelihood( final ImmutableMap<Label, Double> patientClassProbabilities,  //pj
            final ImmutableMap<ErrorRateId, ErrorRateEstimation> errorRates ) {                          //piKjl
        final AtomicDouble likelihood = new AtomicDouble( 0.0 );
        this.observations.getPatients().forEach( patient -> {
            final AtomicDouble sumPjTimesPkjlPowerNkil = new AtomicDouble( 0.0 );
            this.observations.getLabels().forEach( trueLabel -> {
                final AtomicDouble productPkjlPowerNkil = new AtomicDouble( 1.0 );
                final Double pj = patientClassProbabilities.get( trueLabel );

                this.observations.getObservers().forEach( observer -> {
                    this.observations.getObservations( observer, patient ).forEach( o -> {
                        final Map<Label, Long> countForEachLabel = o.getLabels().stream().collect(
                                Collectors.groupingBy( Function.identity(), Collectors.counting() ) );

                        countForEachLabel.forEach( ( label, nkil ) -> {
                            final ErrorRateEstimation errorRateEstimation = errorRates.get(
                                    new ErrorRateId( observer, label, trueLabel ) );
                            final double pikjl = errorRateEstimation.getErrorRateEstimation();

                            final double pkjlPowerNkil = Math.pow( pikjl, nkil );
                            productPkjlPowerNkil.set( productPkjlPowerNkil.get() * pkjlPowerNkil );
                        } );
                    } );
                } );
                sumPjTimesPkjlPowerNkil.addAndGet( pj * productPkjlPowerNkil.get() );
            } );
            final double patientLikelihood = sumPjTimesPkjlPowerNkil.get();
            if (patientLikelihood != 0) {
                final double log = Math.log( patientLikelihood );
                likelihood.addAndGet( log );
            }
        } );

        return likelihood.get();
    }

    /**
     * Re-calculate the patient class estimates ^T_ij where i in patients and j in labels, i.e.,
     * the estimate for the indicate variable if j is the true label for patient i.
     * See Equation 2.5
     *
     * @param errorRateEstimations      error rate estimates
     * @param patientClassProbabilities probabilities of each label being "true"
     * @return patient class estimations for existent labels
     */
    private ImmutableMap<PatientId, ImmutableSet<IndicatorEstimation>> reCalculatePatientClassEstimatesForTruePatientClasses(
            final ImmutableMap<ErrorRateId, ErrorRateEstimation> errorRateEstimations,
            final ImmutableMap<Label, Double> patientClassProbabilities ) {
        return Maps.toMap( this.observations.getPatients(), patient -> {
            final ImmutableSet.Builder<IndicatorEstimation> estimations = ImmutableSet.builder();
            final Map<Label, Double> numerators = Maps.newHashMap();

            this.observations.getLabels().forEach( trueLabel -> {
                final AtomicDouble numerator = new AtomicDouble( 1.0 );

                this.observations.getObservers().forEach( observer -> {
                    this.observations.getObservations( observer, patient ).forEach( o -> {
                        final Map<Label, Long> countForEachLabel = o.getLabels().stream().collect(
                                Collectors.groupingBy( Function.identity(), Collectors.counting() ) );

                        countForEachLabel.forEach( ( label, nkil ) -> {
                            final Optional<Double> piKjl =
                                    Optional.ofNullable(
                                            errorRateEstimations.get( new ErrorRateId( observer, label, trueLabel ) ) )
                                            .map( ErrorRateEstimation::getErrorRateEstimation );

                            piKjl.ifPresent( pikjl -> {
                                final double pikjlTimesNkil = Math.pow( pikjl, nkil );
                                numerator.set( numerator.get() * pikjlTimesNkil );
                            } );
                        } );
                    } );

                } );

                final Double pj = patientClassProbabilities.get( trueLabel );
                numerator.set( numerator.get() * pj );
                numerators.put( trueLabel, numerator.get() );
            } );

            final double denominator = numerators.values().stream().mapToDouble( n -> n ).sum();

            numerators.forEach( ( trueLabel, numerator ) -> {
                estimations.add( new IndicatorEstimation( trueLabel, numerator / denominator ) );
            } );

            return estimations.build();
        } );
    }

    /**
     * Calculate maximum likelihood estimates for individual errors rates ^pi^k_jl where k in observer, j,l in label.
     * This is an estimate for the conditional probability, that observer k will answer with l given that j is the true
     * label class.
     * See Equation 2.3
     */
    private ImmutableMap<ErrorRateId, ErrorRateEstimation> calculateErrorRates(
            final ImmutableMap<PatientId, ImmutableSet<IndicatorEstimation>> patientClassEstimations ) {

        final Set<ErrorRateEstimation> estimations = Sets.newHashSet();

        this.observations.getObservers().forEach( observer -> { //k
            this.observations.getLabels().forEach( trueLabel -> { //l
                this.observations.getLabels().forEach( label -> { //j
                    final AtomicDouble numerator = new AtomicDouble( 0.0 );
                    final AtomicDouble denominator = new AtomicDouble( 0.0 );

                    this.observations.getPatients().forEach( patient -> {
                        final double Tij = patientClassEstimations.get( patient ).stream().filter(
                                p -> p.getLabel().equals( trueLabel ) ).findFirst().map(
                                IndicatorEstimation::getIndicatorEstimation ).orElse( 0.0 );
                        final ImmutableSet<Observation> observations = this.observations.getObservations( observer,
                                patient );

                        final long nkil = observations.stream().flatMap(
                                o -> o.getLabels().stream().filter( l -> l.equals( label ) ) ).count();

                        numerator.addAndGet( Tij * nkil );

                        observations.forEach( o -> {
                            final Map<Label, Long> countForEachLabel = o.getLabels().stream().collect(
                                    Collectors.groupingBy( Function.identity(), Collectors.counting() ) );

                            countForEachLabel.values().forEach(
                                    c -> denominator.addAndGet( Tij * c ) );
                        } );
                    } );

                    estimations.add( new ErrorRateEstimation( observer, label, trueLabel,
                            denominator.doubleValue() == 0 ? 0 :
                                    numerator.doubleValue() / denominator.doubleValue() ) );
                } );
            } );
        } );

        return estimations.stream().collect(
                ImmutableMap.toImmutableMap( ErrorRateEstimation::getErrorRateId, Function.identity() ) );
    }

    /**
     * Calculate ^p_j where j in labels, i.e., probability of label j being chosen for a random patient.
     * ^p_j's are marginal probabilities. ^p_j is an estimate for the probability of j being the true label for a random
     * patient.
     * See Equation 2.4.
     *
     * @param patientClassEstimations all ^T_ij
     */
    private ImmutableMap<Label, Double> calculatePatientClassProbabilities(
            final ImmutableMap<PatientId, ImmutableSet<IndicatorEstimation>> patientClassEstimations ) {
        final int nrPatients = this.observations.getPatients().size();

        final Map<Label, Double> sumOfPatientClassEstimations =
                patientClassEstimations.values().stream().flatMap(
                        p -> p.stream().map( e -> new AbstractMap.SimpleImmutableEntry<>( e.getLabel(),
                                e.getIndicatorEstimation() ) ) ).collect(
                        Collectors.groupingBy( AbstractMap.SimpleImmutableEntry::getKey, Collectors.summingDouble(
                                AbstractMap.SimpleImmutableEntry::getValue ) ) );

        return ImmutableMap.copyOf( Maps.transformValues( sumOfPatientClassEstimations, e -> e / nrPatients ) );
    }

    /**
     * Calculate the initial patient class estimates ^T_ij where i in patients and j in labels, i.e.,
     * the estimate for the indicate variable if j is the true label for patient i.
     * See Equation 3.1
     *
     * @param patientId id the patient
     * @return patient class estimations for existent labels
     */
    private ImmutableSet<IndicatorEstimation> getInitialEstimatesForTruePatientClasses( final PatientId patientId ) {
        final ImmutableSet<Observation> observations = this.observations.getObservations( patientId );
        final double totalNrObservations = observations.stream().mapToInt(
                o -> o.getLabels().size() ).sum();
        final Map<Label, Long> nrObservationsPerLabel = observations.stream().flatMap(
                o -> o.getLabels().stream() ).collect( Collectors.groupingBy( o -> o, Collectors.counting() ) );

        return nrObservationsPerLabel.entrySet().stream().map(
                e -> new IndicatorEstimation( e.getKey(), e.getValue() / totalNrObservations ) ).collect(
                ImmutableSet.toImmutableSet() );
    }

    public final class Output {
        ImmutableMap<Label, Double> patientClassProbabilities;

        ImmutableMap<ErrorRateId, ErrorRateEstimation> errorRates;

        ImmutableMap<PatientId, ImmutableSet<IndicatorEstimation>> patientClassEstimations;

        public Output(
                final ImmutableMap<Label, Double> patientClassProbabilities,
                final ImmutableMap<ErrorRateId, ErrorRateEstimation> errorRates,
                final ImmutableMap<PatientId, ImmutableSet<IndicatorEstimation>> patientClassEstimations ) {
            this.patientClassProbabilities = patientClassProbabilities;
            this.errorRates = errorRates;
            this.patientClassEstimations = patientClassEstimations;
        }

        public ImmutableMap<Label, Double> getPatientClassProbabilities() {
            return this.patientClassProbabilities;
        }

        public ImmutableMap<ErrorRateId, ErrorRateEstimation> getErrorRates() {
            return this.errorRates;
        }

        public ImmutableMap<PatientId, ImmutableSet<IndicatorEstimation>> getPatientClassEstimations() {
            return this.patientClassEstimations;
        }

        public boolean hasConverged( final Output other ) {
            final double deltaPatientClassProbabilities =
                    this.patientClassProbabilities.entrySet().stream().mapToDouble(
                            e -> Math.abs( other.patientClassProbabilities.get( e.getKey() ) - e.getValue() ) ).sum();
            final double deltaErrorRates = this.errorRates.entrySet().stream().mapToDouble(
                    e -> Math.abs( other.errorRates.get( e.getKey() ).getErrorRateEstimation() -
                            e.getValue().getErrorRateEstimation() ) ).sum();
            LOG.info( "Delta pj: {}. Delta pikjl: {}.", deltaPatientClassProbabilities, deltaErrorRates );

            return deltaPatientClassProbabilities < CONVERGENCE_THRESHOLD || deltaErrorRates < CONVERGENCE_THRESHOLD;
        }
    }

    public final class IndicatorEstimation {
        private final Label label;

        private final double indicatorEstimation;

        public IndicatorEstimation( final Label label, final double indicatorEstimation ) {
            this.label = label;
            this.indicatorEstimation = indicatorEstimation;
        }

        public Label getLabel() {
            return this.label;
        }

        public double getIndicatorEstimation() {
            return this.indicatorEstimation;
        }

        @Override
        public String toString() {
            return "IndicatorEstimation{" +
                    "label=" + this.label +
                    ", indicatorEstimation=" + this.indicatorEstimation +
                    '}';
        }
    }

    private class ErrorRateId extends Id<String> {
        public ErrorRateId( final ObserverId observerId, final Label answeredLabel,
                final Label estimatedTrueLabel ) {
            super( "k=" + observerId + "|l=" + answeredLabel + "j=" + estimatedTrueLabel );
        }
    }

    private class ErrorRateEstimation {
        private final ErrorRateId errorRateId;

        private final double errorRateEstimation;

        public ErrorRateEstimation( final ObserverId observerId, final Label answeredLabel,
                final Label estimatedTrueLabel, final double errorRateEstimation ) {
            this.errorRateId = new ErrorRateId( observerId, answeredLabel, estimatedTrueLabel );
            this.errorRateEstimation = errorRateEstimation;
        }

        public ErrorRateId getErrorRateId() {
            return this.errorRateId;
        }

        public double getErrorRateEstimation() {
            return this.errorRateEstimation;
        }
    }
}
