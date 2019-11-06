package algorithms.truthinference;

import algorithms.Id;
import com.google.common.collect.*;
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
 * Implements D&S algorithm for estimating the probability of patient classes from:
 *
 * Maximum Likelihood Estimation of Observer Error-Rates Using the EM Algorithm
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

    private final Answers observations;

    public DawidSkeneAlgorithm( final Answers observations ) {
        this.observations = observations;
    }

    public Output run() {
        int iteration = 0;

        ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> patientClassEstimations = Maps.toMap(
                this.observations.getQuestions(), this::getInitialEstimatesForTruePatientClasses );

        Optional<Output> output = Optional.empty();

        while (true) {
            iteration++;

            //m-step / maximization step
            //estimate for p_j
            final ImmutableMap<ChoiceId, Double> patientClassProbabilities = calculatePatientClassProbabilities(
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
            if (iteration >= MAXIMUM_NR_ITERATIONS || output.map( o -> o.hasConverged( newOutput ) ).orElse( false )) {
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
    private double calculateLogLikelihood( final ImmutableMap<ChoiceId, Double> patientClassProbabilities,  //pj
            final ImmutableMap<ErrorRateId, ErrorRateEstimation> errorRates ) {                          //piKjl
        final AtomicDouble likelihood = new AtomicDouble( 0.0 );
        this.observations.getQuestions().forEach( patient -> {
            final AtomicDouble sumPjTimesPkjlPowerNkil = new AtomicDouble( 0.0 );
            this.observations.getChoices().forEach( trueLabel -> {
                final AtomicDouble productPkjlPowerNkil = new AtomicDouble( 1.0 );
                final Double pj = patientClassProbabilities.get( trueLabel );

                this.observations.getParticipants().forEach( observer -> {
                    final Map<ChoiceId, Long> countForEachLabel = this.observations.getAnswers(
                            observer, patient ).stream().collect(
                            Collectors.groupingBy( Answer::getChoice, Collectors.counting() ) );

                    countForEachLabel.forEach( ( label, nkil ) -> {
                        final ErrorRateEstimation errorRateEstimation = errorRates.get(
                                new ErrorRateId( observer, label, trueLabel ) );
                        final double pikjl = errorRateEstimation.getErrorRateEstimation();

                        final double pkjlPowerNkil = Math.pow( pikjl, nkil );
                        productPkjlPowerNkil.set( productPkjlPowerNkil.get() * pkjlPowerNkil );
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
    private ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> reCalculatePatientClassEstimatesForTruePatientClasses(
            final ImmutableMap<ErrorRateId, ErrorRateEstimation> errorRateEstimations,
            final ImmutableMap<ChoiceId, Double> patientClassProbabilities ) {
        return Maps.toMap( this.observations.getQuestions(), patient -> {
            final ImmutableSet.Builder<IndicatorEstimation> estimations = ImmutableSet.builder();
            final Map<ChoiceId, Double> numerators = Maps.newHashMap();

            this.observations.getChoices().forEach( trueLabel -> {
                final AtomicDouble numerator = new AtomicDouble( 1.0 );

                this.observations.getParticipants().forEach( observer -> {
                    final Map<ChoiceId, Long> countForEachLabel = this.observations.getAnswers(
                            observer, patient ).stream().collect(
                            Collectors.groupingBy( Answer::getChoice, Collectors.counting() ) );

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
            final ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> patientClassEstimations ) {

        final Set<ErrorRateEstimation> estimations = Sets.newHashSet();

        this.observations.getParticipants().forEach( observer -> { //k
            this.observations.getChoices().forEach( trueLabel -> { //l
                this.observations.getChoices().forEach( label -> { //j
                    final AtomicDouble numerator = new AtomicDouble( 0.0 );
                    final AtomicDouble denominator = new AtomicDouble( 0.0 );

                    this.observations.getQuestions().forEach( patient -> {
                        final double Tij = patientClassEstimations.get( patient ).stream().filter(
                                p -> p.getLabel().equals( trueLabel ) ).findFirst().map(
                                IndicatorEstimation::getIndicatorEstimation ).orElse( 0.0 );
                        final ImmutableMultiset<Answer> observationsByObserverForPatient = this.observations.getAnswers(
                                observer, patient );

                        final long nkil = observationsByObserverForPatient.stream().filter(
                                o -> o.getChoice().equals( label ) ).count();

                        numerator.addAndGet( Tij * nkil );

                        final Map<ChoiceId, Long> countForEachLabel = observationsByObserverForPatient.stream().collect(
                                Collectors.groupingBy( Answer::getChoice, Collectors.counting() ) );

                        countForEachLabel.values().forEach(
                                c -> denominator.addAndGet( Tij * c ) );
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
    private ImmutableMap<ChoiceId, Double> calculatePatientClassProbabilities(
            final ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> patientClassEstimations ) {
        final int nrPatients = this.observations.getQuestions().size();

        final Map<ChoiceId, Double> sumOfPatientClassEstimations =
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
    private ImmutableSet<IndicatorEstimation> getInitialEstimatesForTruePatientClasses( final QuestionId patientId ) {
        final ImmutableMultiset<Answer> observations = this.observations.getAnswers( patientId );
        final double totalNrObservations = observations.size();
        final Map<ChoiceId, Long> nrObservationsPerLabel = observations.stream().collect( Collectors.groupingBy(
                Answer::getChoice, Collectors.counting() ) );

        return nrObservationsPerLabel.entrySet().stream().map(
                e -> new IndicatorEstimation( e.getKey(), e.getValue() / totalNrObservations ) ).collect(
                ImmutableSet.toImmutableSet() );
    }

    public final class Output {
        private final ImmutableMap<ChoiceId, Double> patientClassProbabilities;

        private final ImmutableMap<ErrorRateId, ErrorRateEstimation> errorRates;

        private final ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> patientClassEstimations;

        public Output(
                final ImmutableMap<ChoiceId, Double> patientClassProbabilities,
                final ImmutableMap<ErrorRateId, ErrorRateEstimation> errorRates,
                final ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> patientClassEstimations ) {
            this.patientClassProbabilities = patientClassProbabilities;
            this.errorRates = errorRates;
            this.patientClassEstimations = patientClassEstimations;
        }

        public ImmutableMap<ChoiceId, Double> getPatientClassProbabilities() {
            return this.patientClassProbabilities;
        }

        public ImmutableMap<ErrorRateId, ErrorRateEstimation> getErrorRates() {
            return this.errorRates;
        }

        public ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> getPatientClassEstimations() {
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

    public static final class IndicatorEstimation {
        private final ChoiceId label;

        private final double indicatorEstimation;

        public IndicatorEstimation( final ChoiceId label, final double indicatorEstimation ) {
            this.label = label;
            this.indicatorEstimation = indicatorEstimation;
        }

        public ChoiceId getLabel() {
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

    public static final class ErrorRateId extends Id<String> {
        public ErrorRateId( final ParticipantId observerId, final ChoiceId answeredLabel,
                final ChoiceId estimatedTrueLabel ) {
            super( "k=" + observerId + "|l=" + answeredLabel + "j=" + estimatedTrueLabel );
        }
    }

    public final class ErrorRateEstimation {
        private final ErrorRateId errorRateId;

        private final double errorRateEstimation;

        public ErrorRateEstimation( final ParticipantId observerId, final ChoiceId answeredLabel,
                final ChoiceId estimatedTrueLabel, final double errorRateEstimation ) {
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
