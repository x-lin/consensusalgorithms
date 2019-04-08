package algorithms.majorityvoting;

import algorithms.crowdtruth.WorkerId;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.AbstractMap.SimpleImmutableEntry;
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
public class MajorityVotingAggregator {
    private static final int ROUNDING_ACCURACY = 4;

    static final Function<WorkerId, WorkerQuality>
            PERFECT_WORKER_QUALITY = w -> new WorkerQuality( 1 );

    private final Set<Eme> emes;

    private final Set<DefectReport> defectReports;

    private final Function<WorkerId, WorkerQuality> workerQuality;

    public MajorityVotingAggregator( final Set<Eme> emes, final Set<DefectReport> defectReports ) {
        this( emes, defectReports, PERFECT_WORKER_QUALITY );
    }

    public MajorityVotingAggregator( final Set<Eme> emes, final Set<DefectReport> defectReports,
            final Function<WorkerId, WorkerQuality> workerQuality ) {
        this.emes = emes;
        this.defectReports = defectReports;
        this.workerQuality = workerQuality;
    }

    public ImmutableSet<FinalDefect> aggregate() {
        return this.emes.stream().map( e -> {
            String id = e.getEmeId();
            FinalDefect.Builder builder = FinalDefect.builder( e );
            final ImmutableMap<DefectType, Double> coefficientsByDefectType = calculateScoreByDefectType( id, this
                    .defectReports, this.workerQuality );

            if (!coefficientsByDefectType.isEmpty()) {
                final Entry<FinalDefectType, Double> finalDefectType = calculateFinalDefectType(
                        coefficientsByDefectType );
                builder.withFinalDefectType( finalDefectType.getKey() ).withAgreementCoeff( finalDefectType.getValue
                        () );
                this.defectReports.stream().filter( d -> d.getEmeId().equals( id ) )
                                  .findFirst().ifPresent( d -> builder.withScenarioId( d.getScenarioId() ) );
            }

            return builder.build();
        } ).filter( f -> f.getFinalDefectType() != null ).collect( ImmutableSet.toImmutableSet() );
    }

    private static ImmutableMap<DefectType, Double> calculateScoreByDefectType( final String emeId, final
    Set<DefectReport> defectReports, final Function<WorkerId, WorkerQuality> workerQuality ) {
        final Map<DefectType, List<DefectReport>> defectsForEachType = defectReports.stream().filter( d -> Objects
                .equals( d.getEmeId
                        (), emeId ) ).collect( Collectors.groupingBy( DefectReport::getDefectType ) );
        final int nrOfReports = defectsForEachType.values().stream().mapToInt( List::size ).sum();
        return ImmutableMap.copyOf( Maps.transformValues( defectsForEachType,
                d -> BigDecimal.valueOf(
                        d.stream().map( r -> workerQuality.apply( new WorkerId( String.valueOf( r.getWorkerId() ) ) ) )
                         .reduce( 0.0, ( oq, q ) -> oq + q.toDouble(), ( q1, q2 ) -> q1 + q2 ) )
                               .divide( BigDecimal.valueOf( nrOfReports ), ROUNDING_ACCURACY,
                                       RoundingMode.HALF_UP ).doubleValue() ) );

//        return ImmutableMap.copyOf( Maps.transformValues( defectsForEachType, d -> BigDecimal.valueOf( d.size() )
//                                                                                             .divide( BigDecimal
//                                                                                                             .valueOf(
//                                                                                                                     nrOfReports ),
//                                                                                                     ROUNDING_ACCURACY,
//                                                                                                     RoundingMode.HALF_UP )
//                                                                                             .doubleValue() )
//        );
    }

//    private static ImmutableMap<DefectType, Double> calculateScoreByDefectType( final String emeId, final
//    Set<DefectReport> defectReports ) {
//        final Map<DefectType, List<DefectReport>> defectsForEachType = defectReports.stream().filter( d -> Objects
//                .equals( d.getEmeId
//                        (), emeId ) ).collect( Collectors.groupingBy( DefectReport::getDefectType ) );
//        final int nrOfReports = defectsForEachType.values().stream().mapToInt( List::size ).sum();
//        return ImmutableMap.copyOf( Maps.transformValues( defectsForEachType, d -> BigDecimal.valueOf( d.size() )
//                                                                                             .divide( BigDecimal
//                                                                                                             .valueOf(
//                                                                                                                     nrOfReports ),
//                                                                                                     ROUNDING_ACCURACY,
//                                                                                                     RoundingMode.HALF_UP )
//                                                                                             .doubleValue() )
//        );
//    }

    private static Entry<FinalDefectType, Double> calculateFinalDefectType( final ImmutableMap<DefectType, Double>
            agreementCoefficients ) {
        final Entry<DefectType, Double> max = Collections.max( agreementCoefficients.entrySet(), Comparator
                .comparingDouble
                        ( Entry::getValue ) );
        final boolean hasOtherDefectTypeWithSameCoefficient = hasOtherWithSameValue( agreementCoefficients.values(), max
                .getValue() );
        return new SimpleImmutableEntry<>( hasOtherDefectTypeWithSameCoefficient ? FinalDefectType.UNDECIDABLE :
                FinalDefectType.fromDefectType( max.getKey() ), max.getValue() );
    }

    private static boolean hasOtherWithSameValue( final Collection<Double> agreementCoefficients,
            final double value ) {
        return agreementCoefficients.stream().filter( c -> c.equals( value ) ).count() > 1L;
    }
}