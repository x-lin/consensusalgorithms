package algorithms.fastdawidskene;

import algorithms.Id;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implements FDS-DS hybrid algorithm as described by:
 *
 * Fast Dawid-Skene: A Fast Vote Aggregation Scheme for Sentiment Classification
 * V.B. Sinha et al.
 * 2018
 *
 * @author LinX
 */
public class HybridDawidSkeneAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( HybridDawidSkeneAlgorithm.class );

    //threshold under which the algorithm can be viewed as converged
    private static final double CONVERGENCE_THRESHOLD = 0.00001;

    private static final int MAXIMUM_NR_ITERATIONS = 100;

    private final Answers answers;

    private final double switchThreshold;

    public HybridDawidSkeneAlgorithm( final Set<Answer> answers, final double switchWhenBelowClassProbabilitiesDelta ) {
        this.answers = new Answers( answers );
        this.switchThreshold = switchWhenBelowClassProbabilitiesDelta;
    }

    public Output run() {
        int iteration = 0;

        ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> classEstimations =
                Maps.toMap( this.answers.getQuestions(), this::getInitialEstimatesForTrueClasses );

        Optional<Output> output = Optional.empty();
        boolean switchedToFDS = false;

        while (true) {
            iteration++;

            //m-step / maximization step
            //estimate for p_j
            final ImmutableMap<ChoiceId, Double> classProbabilities = calculateClassProbabilities( classEstimations );
            LOG.info( "estimation for pj done." );
            //estimate for pi^k_jl
            final ImmutableMap<ErrorRateId, ErrorRateEstimation> errorRates = calculateErrorRates( classEstimations );
            LOG.info( "Maximization step done." );

            //e-step / estimation step
            //estimate for T_ij
            classEstimations = reCalculateClassEstimates( errorRates, classProbabilities, switchedToFDS );
            LOG.info( "Estimation step done." );

            //calculate log likelihood -> should go up as algorithm proceeds
            final double logLikelihood = calculateLogLikelihood( classProbabilities, errorRates );
            LOG.info( "Log-Likelihood on iteration {}: {}", iteration, logLikelihood );

            final Output newOutput = new Output( classProbabilities, errorRates, classEstimations );
            if (iteration > MAXIMUM_NR_ITERATIONS || output.map( o -> o.hasConverged( newOutput ) ).orElse( false )) {
                break;
            }
            else if (!switchedToFDS && output.map( o -> o.getDeltaPatientClassProbabilities( newOutput ) <
                    this.switchThreshold ).orElse( false )) {
                switchedToFDS = true;
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
    private double calculateLogLikelihood( final ImmutableMap<ChoiceId, Double> classProbabilities,  //pj
            final ImmutableMap<ErrorRateId, ErrorRateEstimation> errorRates ) {                          //piKjl
        final AtomicDouble likelihood = new AtomicDouble( 0.0 );
        this.answers.getQuestions().forEach( question -> {
            final AtomicDouble sumPjTimesPkjlPowerNkil = new AtomicDouble( 0.0 );
            this.answers.getChoices().forEach( trueChoice -> {
                final AtomicDouble productPkjlPowerNkil = new AtomicDouble( 1.0 );
                final Double pj = classProbabilities.get( trueChoice );

                this.answers.getParticipants().forEach( participant -> {
                    this.answers.getAnswers( participant, question ).forEach( o -> {
                        final Map<ChoiceId, Long> countForEachLabel = o.getChoices().stream().collect(
                                Collectors.groupingBy( Function.identity(), Collectors.counting() ) );

                        countForEachLabel.forEach( ( choiceId, nkil ) -> {
                            final ErrorRateEstimation errorRateEstimation = errorRates.get(
                                    new ErrorRateId( participant, choiceId, trueChoice ) );
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
     * Re-calculate the question class estimates ^T_ij where i in patients and j in labels, i.e.,
     * the estimate for the indicate variable if j is the true choiceId for question i.
     * See Equation 2.5
     *
     * @param errorRateEstimations      error rate estimates
     * @param patientClassProbabilities probabilities of each choiceId being "true"
     * @return question class estimations for existent labels
     */
    private ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> reCalculateClassEstimates(
            final ImmutableMap<ErrorRateId, ErrorRateEstimation> errorRateEstimations,
            final ImmutableMap<ChoiceId, Double> patientClassProbabilities, final boolean switchedToFDS ) {
        //e-step
        final ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> estimates =
                Maps.toMap( this.answers.getQuestions(), question -> {
                    final ImmutableSet.Builder<IndicatorEstimation> estimations = ImmutableSet.builder();
                    final Map<ChoiceId, Double> numerators = Maps.newHashMap();

                    this.answers.getChoices().forEach( trueChoice -> {
                        final AtomicDouble numerator = new AtomicDouble( 1.0 );

                        this.answers.getParticipants().forEach( participant -> {
                            this.answers.getAnswers( participant, question ).forEach( o -> {
                                final Map<ChoiceId, Long> countForEachLabel = o.getChoices().stream().collect(
                                        Collectors.groupingBy( Function.identity(), Collectors.counting() ) );

                                countForEachLabel.forEach( ( choiceId, nkil ) -> {
                                    final Optional<Double> piKjl =
                                            Optional.ofNullable( errorRateEstimations
                                                    .get( new ErrorRateId( participant, choiceId,
                                                            trueChoice ) ) ).map(
                                                    ErrorRateEstimation::getErrorRateEstimation );

                                    piKjl.ifPresent( pikjl -> {
                                        final double pikjlTimesNkil = Math.pow( pikjl, nkil );
                                        numerator.set( numerator.get() * pikjlTimesNkil );
                                    } );
                                } );
                            } );

                        } );

                        final Double pj = patientClassProbabilities.get( trueChoice );
                        numerator.set( numerator.get() * pj );
                        numerators.put( trueChoice, numerator.get() );
                    } );

                    final double denominator = numerators.values().stream().mapToDouble( n -> n ).sum();

                    numerators.forEach( ( trueChoice, numerator ) -> {
                        estimations.add( new IndicatorEstimation( trueChoice, numerator / denominator ) );
                    } );

                    return estimations.build();
                } );

        //c-step (classification step), performed if switched to FDS
        return switchedToFDS ? performClassificationStep( estimates ) : estimates;
    }

    private ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> performClassificationStep(
            final ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> estimates ) {
        return ImmutableMap.copyOf( Maps.transformValues( estimates, e -> {
            final Map<ChoiceId, Double> estimatesPerChoice = e.stream().collect(
                    ImmutableMap.toImmutableMap( IndicatorEstimation::getChoice,
                            IndicatorEstimation::getIndicatorEstimation ) );
            final ChoiceId choiceWithHighestEstimate = estimatesPerChoice.entrySet().stream().max(
                    Comparator.comparingDouble( Map.Entry::getValue ) ).get().getKey();

            return estimatesPerChoice.keySet().stream().map(
                    c -> new IndicatorEstimation( c, c.equals( choiceWithHighestEstimate ) ? 1 : 0 ) ).collect(
                    ImmutableSet.toImmutableSet() );
        } ) );
    }

    /**
     * Calculate maximum likelihood estimates for individual errors rates ^pi^k_jl where k in participant, j,l in choiceId.
     * This is an estimate for the conditional probability, that participant k will answer with l given that j is the true
     * choiceId class.
     * See Equation 2.3
     *
     * TODO doc
     */
    private ImmutableMap<ErrorRateId, ErrorRateEstimation> calculateErrorRates(
            final ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> classEstimations ) {

        final Set<ErrorRateEstimation> estimations = Sets.newHashSet();

        this.answers.getParticipants().forEach( participant -> { //k
            this.answers.getChoices().forEach( trueChoice -> { //l
                this.answers.getChoices().forEach( choiceId -> { //j
                    final AtomicDouble numerator = new AtomicDouble( 0.0 );
                    final AtomicDouble denominator = new AtomicDouble( 0.0 );

                    this.answers.getQuestions().forEach( question -> {
                        final double Tij = classEstimations.get( question ).stream().filter(
                                p -> p.getChoice().equals( trueChoice ) ).findFirst().map(
                                IndicatorEstimation::getIndicatorEstimation ).orElse( 0.0 );
                        final ImmutableSet<Answer> answers = this.answers.getAnswers( participant, question );

                        final long nkil = answers.stream().flatMap(
                                o -> o.getChoices().stream().filter( l -> l.equals( choiceId ) ) ).count();

                        numerator.addAndGet( Tij * nkil );

                        answers.forEach( o -> {
                            final Map<ChoiceId, Long> countForEachChoice = o.getChoices().stream().collect(
                                    Collectors.groupingBy( Function.identity(), Collectors.counting() ) );

                            countForEachChoice.values().forEach(
                                    c -> denominator.addAndGet( Tij * c ) );
                        } );
                    } );

                    estimations.add( new ErrorRateEstimation( participant, choiceId, trueChoice,
                            denominator.doubleValue() == 0 ?
                                    0 :
                                    numerator.doubleValue() / denominator.doubleValue() ) );
                } );
            } );
        } );

        return estimations.stream().collect(
                ImmutableMap.toImmutableMap( ErrorRateEstimation::getErrorRateId, Function.identity() ) );
    }

    /**
     * Calculate ^p_j where j in labels, i.e., probability of choiceId j being chosen for a random question.
     * ^p_j's are marginal probabilities. ^p_j is an estimate for the probability of j being the true choiceId for a random
     * question.
     * See Equation 2.4.
     *
     * TODO fix doc
     *
     * @param classEstimations all ^T_ij
     */
    private ImmutableMap<ChoiceId, Double> calculateClassProbabilities(
            final ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> classEstimations ) {
        final int nrQuestions = this.answers.getQuestions().size();

        final Map<ChoiceId, Double> sumOfClassEstimations =
                classEstimations.values().stream().flatMap(
                        p -> p.stream().map( e -> new AbstractMap.SimpleImmutableEntry<>( e.getChoice(),
                                e.getIndicatorEstimation() ) ) ).collect(
                        Collectors.groupingBy( AbstractMap.SimpleImmutableEntry::getKey, Collectors.summingDouble(
                                AbstractMap.SimpleImmutableEntry::getValue ) ) );

        return ImmutableMap.copyOf( Maps.transformValues( sumOfClassEstimations, e -> e / nrQuestions ) );
    }

    /**
     * Calculate the initial class estimates ^T_qc where q in questions and c in choices.
     * Counts the number of answers containing each choice for the specified question and estimates the true choice being the
     * one that was contained in the maximum of answers. In case more than one choice contains the same agreement coefficient,
     * any of these choices is returned.
     * Same as D&S Equation 3.1
     *
     * @param questionId question id
     * @return choice id with the highest number of occurrence for question
     */
    private ImmutableSet<IndicatorEstimation> getInitialEstimatesForTrueClasses(
            final QuestionId questionId ) {
        final ImmutableSet<Answer> answers = this.answers.getAnswers( questionId );
        final double totalNrAnswers = answers.stream().mapToInt(
                o -> o.getChoices().size() ).sum();
        final Map<ChoiceId, Long> nrAnswersPerChoice = answers.stream().flatMap(
                o -> o.getChoices().stream() ).collect( Collectors.groupingBy( o -> o, Collectors.counting() ) );

        return nrAnswersPerChoice.entrySet().stream().map(
                e -> new IndicatorEstimation( e.getKey(), e.getValue() / totalNrAnswers ) )
                                 .collect( ImmutableSet.toImmutableSet() );
    }

    public final class Output {
        private final ImmutableMap<ChoiceId, Double> classProbabilities;

        private final ImmutableMap<ErrorRateId, ErrorRateEstimation> errorRates;

        private final ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> classEstimations;

        public Output(
                final ImmutableMap<ChoiceId, Double> classProbabilities,
                final ImmutableMap<ErrorRateId, ErrorRateEstimation> errorRates,
                final ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> classEstimations ) {
            this.classProbabilities = classProbabilities;
            this.errorRates = errorRates;
            this.classEstimations = classEstimations;
        }

        public ImmutableMap<ChoiceId, Double> getClassProbabilities() {
            return this.classProbabilities;
        }

        public ImmutableMap<ErrorRateId, ErrorRateEstimation> getErrorRates() {
            return this.errorRates;
        }

        public ImmutableMap<QuestionId, ImmutableSet<IndicatorEstimation>> getClassEstimations() {
            return this.classEstimations;
        }

        private boolean hasConverged( final Output other ) {
            final double deltaPatientClassProbabilities = getDeltaPatientClassProbabilities( other );
            final double deltaErrorRates = this.errorRates.entrySet().stream().mapToDouble(
                    e -> Math.abs( other.errorRates.get( e.getKey() ).getErrorRateEstimation() -
                            e.getValue().getErrorRateEstimation() ) ).sum();
            LOG.info( "Delta pj: {}. Delta pikjl: {}.", deltaPatientClassProbabilities, deltaErrorRates );

            return deltaPatientClassProbabilities < CONVERGENCE_THRESHOLD || deltaErrorRates < CONVERGENCE_THRESHOLD;
        }

        private double getDeltaPatientClassProbabilities( final Output other ) {
            return this.classProbabilities.entrySet().stream().mapToDouble(
                    e -> Math.abs( other.classProbabilities.get( e.getKey() ) - e.getValue() ) ).sum();
        }
    }

    public static final class IndicatorEstimation {
        private final ChoiceId choice;

        private final double indicatorEstimation;

        public IndicatorEstimation( final ChoiceId choice, final double indicatorEstimation ) {
            this.choice = choice;
            this.indicatorEstimation = indicatorEstimation;
        }

        public ChoiceId getChoice() {
            return this.choice;
        }

        public double getIndicatorEstimation() {
            return this.indicatorEstimation;
        }

        @Override
        public String toString() {
            return "IndicatorEstimation{" +
                    "choice=" + this.choice +
                    ", indicatorEstimation=" + this.indicatorEstimation +
                    '}';
        }
    }

    public static final class ErrorRateId extends Id<String> {
        public ErrorRateId( final ParticipantId participantId, final ChoiceId answeredLabel,
                final ChoiceId estimatedTrueLabel ) {
            super( "k=" + participantId + "|l=" + answeredLabel + "j=" + estimatedTrueLabel );
        }
    }

    public final class ErrorRateEstimation {
        private final ErrorRateId errorRateId;

        private final double errorRateEstimation;

        public ErrorRateEstimation( final ParticipantId participantId, final ChoiceId answeredChoice,
                final ChoiceId estimatedTrueChoice, final double errorRateEstimation ) {
            this.errorRateId = new ErrorRateId( participantId, answeredChoice, estimatedTrueChoice );
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
