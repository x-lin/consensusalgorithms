package algorithms.finaldefects.majorityvoting.adaptive;

import algorithms.finaldefects.FinalDefectAggregationAlgorithm;
import algorithms.finaldefects.SemesterSettings;
import algorithms.finaldefects.WorkerDefectReports;
import algorithms.finaldefects.majorityvoting.basic.MajorityVotingAlgorithm;
import algorithms.model.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Filters out workers according to the following algorithm:
 * 1. Calculate final defects with majority voting algorithm
 * 2. Filter out workers whose identified final defects have an agreement score < threshold
 * 3. Calculate final defects with workers >= threshold
 * 4. Repeat 2 and 3 until no worker agreement score < threshold
 *
 * @author LinX
 */
public class AdaptiveMajorityVoting implements FinalDefectAggregationAlgorithm {
    private final double threshold;

    private final SemesterSettings settings;

    public AdaptiveMajorityVoting( final double threshold, final SemesterSettings settings ) {
        this.threshold = threshold;
        this.settings = settings;
    }

    @Override
    public ImmutableMap<EmeAndScenarioId, FinalDefect> getFinalDefects() {
        final Emes emes = Emes.fetchFromDb( this.settings );
        ImmutableMap<EmeAndScenarioId, FinalDefect> finalDefectsFiltered;
        final Map<TaskWorkerId, List<DefectReport>> defectReportsPerWorker = Maps.newHashMap(
                DefectReports.fetchFromDb( this.settings ).groupedByWorkerId() );

        final AtomicDouble lowestAgreement = new AtomicDouble( 0.0 );

        while (lowestAgreement.get() < this.threshold) {
            finalDefectsFiltered = aggregate( emes, new DefectReports( defectReportsPerWorker.values().stream().flatMap(
                    Collection::stream ).collect( ImmutableSet.toImmutableSet() ) ) );
            final ImmutableMap<TaskWorkerId, Double> agreement = getWorkerAgreement( defectReportsPerWorker,
                    finalDefectsFiltered );

            agreement.entrySet().stream().filter( a -> a.getValue() < this.threshold ).map( Map.Entry::getKey )
                     .forEach( defectReportsPerWorker::remove );

            lowestAgreement.set(
                    Collections.min( agreement.values().isEmpty() ? ImmutableSet.of( 0.0 ) : agreement.values() ) );
        }
        return aggregate( emes, new DefectReports( defectReportsPerWorker.values().stream().flatMap(
                Collection::stream ).collect( ImmutableSet.toImmutableSet() ) ) );
    }

    @Override
    public SemesterSettings getSettings() {
        return this.settings;
    }

    @Override
    public ImmutableMap<String, String> getParameters() {
        return ImmutableMap.of( "threshold", String.valueOf( this.threshold ) );
    }

    @Override
    public ImmutableMap<TaskWorkerId, WorkerDefectReports> getWorkerDefectReports() {
        return DefectReports.fetchFromDb( this.settings ).toWorkerDefectReports(
                MajorityVotingAlgorithm.PERFECT_WORKER_QUALITY );
    }

    private ImmutableMap<EmeAndScenarioId, FinalDefect> aggregate( final Emes emes,
            final DefectReports defectReports ) {
        return MajorityVotingAlgorithm.create( this.settings, emes, defectReports,
                MajorityVotingAlgorithm.PERFECT_WORKER_QUALITY ).getFinalDefects();
    }

    private static ImmutableMap<TaskWorkerId, Double> getWorkerAgreement( final Map<TaskWorkerId, List<DefectReport>>
            defectReportsPerWorker,
            final
            ImmutableMap<EmeAndScenarioId, FinalDefect> finalDefects ) {
        final Map<EmeAndScenarioId, FinalDefectType> finalDefectTypePerEmeAndScenario =
                Maps.transformValues( finalDefects, FinalDefect::getFinalDefectType );

        return defectReportsPerWorker.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> {
                    final long agreedDefectTypes = e.getValue().stream().filter( f -> {
                        final FinalDefectType agreedFinalDefectType = finalDefectTypePerEmeAndScenario.get(
                                f.getEmeAndScenarioId() );
                        return agreedFinalDefectType == FinalDefectType.UNDECIDABLE ||
                                f.getDefectType().toFinalDefectType() == agreedFinalDefectType;
                    } ).count();
                    return BigDecimal.valueOf( agreedDefectTypes ).divide( BigDecimal.valueOf( e.getValue().size() ),
                            4, RoundingMode.HALF_UP ).doubleValue();
                } ) );
    }
}
