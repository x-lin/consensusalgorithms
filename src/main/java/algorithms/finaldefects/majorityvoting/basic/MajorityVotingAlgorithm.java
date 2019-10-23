package algorithms.finaldefects.majorityvoting.basic;

import algorithms.finaldefects.FinalDefectAggregationAlgorithm;
import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.WorkerDefectReports;
import algorithms.finaldefects.WorkerQuality;
import algorithms.model.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implements majority voting algorithm for aggregating defect reports as described by:
 *
 * Improved Crowdsourced Software Inspection: Development of an Experimental Process Support Platform
 * P. Penzenstadler
 * 2018
 *
 * @author LinX
 */
public class MajorityVotingAlgorithm implements FinalDefectAggregationAlgorithm {
    private static final int ROUNDING_ACCURACY = 4;

    public static final Function<TaskWorkerId, WorkerQuality>
            PERFECT_WORKER_QUALITY = w -> new WorkerQuality( 1 );

    private ImmutableMap<EmeAndScenarioId, FinalDefect> finalDefects;

    private final SemesterSettings settings;

    private final Emes emes;

    private final DefectReports defectReports;

    private final Function<TaskWorkerId, WorkerQuality> workerQuality;

    private MajorityVotingAlgorithm( final SemesterSettings settings,
            final Emes emes, final DefectReports defectReports,
            final Function<TaskWorkerId, WorkerQuality> workerQuality ) {
        this.settings = settings;
        this.emes = emes;
        this.defectReports = new DefectReports( defectReports.getDefectReports().stream().filter( r -> {
            try {
                workerQuality.apply( r.getWorkerId() );
                return true;
            } catch (NoSuchElementException e) {
                return false;
            }
        } ).collect( ImmutableSet.toImmutableSet() ) );
        this.workerQuality = workerQuality;
    }

    @Override
    public ImmutableMap<EmeAndScenarioId, FinalDefect> getFinalDefects() {
        if (this.finalDefects == null) {
            this.finalDefects = calculateFinalDefects();
        }
        return this.finalDefects;
    }

    @Override
    public SemesterSettings getSettings() {
        return this.settings;
    }

    @Override
    public ImmutableMap<String, String> getParameters() {
        return ImmutableMap.of();
    }

    @Override
    public ImmutableMap<TaskWorkerId, WorkerDefectReports> getWorkerDefectReports() {
        final ImmutableMap<EmeAndScenarioId, FinalDefect> finalDefects = getFinalDefects();
        return new DefectReports( this.defectReports.getDefectReports().stream().filter(
                d -> finalDefects.containsKey( d.getEmeAndScenarioId() ) ).collect( ImmutableSet.toImmutableSet() ) )
                .toWorkerDefectReports( this.workerQuality );
    }

    private ImmutableMap<EmeAndScenarioId, FinalDefect> calculateFinalDefects() {
        return this.defectReports.groupedByEmeAndScenarioId().entrySet().stream().map(
                e -> getFinalDefectForEmeAndScenario( e.getKey(), e.getValue() ) ).collect(
                ImmutableMap.toImmutableMap( FinalDefect::getEmeAndScenarioId, Function.identity() ) );
    }

    private FinalDefect getFinalDefectForEmeAndScenario( final EmeAndScenarioId emeAndScenarioId,
            final Collection<DefectReport> defectReports ) {
        final ImmutableMap<DefectType, AgreementCoefficient> coefficientsByDefectType = calculateScoreByDefectType(
                defectReports, this.workerQuality );
        return calculateFinalDefect(
                coefficientsByDefectType, FinalDefect.builder( this.emes, emeAndScenarioId ) );
    }

    private static ImmutableMap<DefectType, AgreementCoefficient> calculateScoreByDefectType(
            final Collection<DefectReport> defectReports,
            final Function<TaskWorkerId, WorkerQuality> workerQuality ) {
        final Map<DefectType, List<DefectReport>> defectsForEachType = defectReports.stream().collect(
                Collectors.groupingBy( DefectReport::getDefectType ) );
        return ImmutableMap.copyOf( Maps.transformValues( defectsForEachType,
                d -> calculateAgreementCoefficient( workerQuality, defectReports.size(), d ) ) );
    }

    private static AgreementCoefficient calculateAgreementCoefficient(
            final Function<TaskWorkerId, WorkerQuality> workerQuality, final int nrOfReports,
            final List<DefectReport> defectReports ) {
        final double coefficient = BigDecimal.valueOf(
                defectReports.stream()
                             .map( r -> workerQuality.apply( r.getWorkerId() ) )
                             .reduce( 0.0, ( oq, q ) -> oq + q.toDouble(), ( q1, q2 ) -> q1 + q2 ) )
                                             .divide( BigDecimal.valueOf( nrOfReports ), ROUNDING_ACCURACY,
                                                     RoundingMode.HALF_UP ).doubleValue();
        return new AgreementCoefficient( coefficient );
    }

    private static FinalDefect calculateFinalDefect(
            final ImmutableMap<DefectType, AgreementCoefficient> agreementCoefficients,
            final FinalDefect.Builder builder ) {
        final Entry<DefectType, AgreementCoefficient> max = sortByAgreementCoefficientDesc( agreementCoefficients );
        final boolean hasOtherWithSameCoefficient = hasOtherWithSameValue( agreementCoefficients.values(),
                max.getValue() );
        return builder.withFinalDefectType( hasOtherWithSameCoefficient ? FinalDefectType.UNDECIDABLE :
                max.getKey().toFinalDefectType() ).withAgreementCoeff( max.getValue() ).build();
    }

    private static Entry<DefectType, AgreementCoefficient> sortByAgreementCoefficientDesc(
            final ImmutableMap<DefectType, AgreementCoefficient> agreementCoefficients ) {
        return Collections.max( agreementCoefficients.entrySet(),
                Comparator.comparingDouble( e -> e.getValue().toDouble() ) );
    }

    private static boolean hasOtherWithSameValue( final Collection<AgreementCoefficient> agreementCoefficients,
            final AgreementCoefficient value ) {
        return agreementCoefficients.stream().filter( c -> c.equals( value ) ).count() > 1L;
    }

    public static MajorityVotingAlgorithm create( final SemesterSettings settings ) {
        return create( settings, PERFECT_WORKER_QUALITY );
    }

    public static MajorityVotingAlgorithm create( final SemesterSettings settings,
            final Function<TaskWorkerId, WorkerQuality> workerQuality ) {
        return create( settings, Emes.fetchFromDb( settings ), DefectReports.fetchFromDb( settings ), workerQuality );
    }

    public static MajorityVotingAlgorithm create( final SemesterSettings settings, final Emes emes,
            final DefectReports defectReports, final Function<TaskWorkerId, WorkerQuality> workerQuality ) {
        return new MajorityVotingAlgorithm( settings, emes, defectReports, workerQuality );
    }
}