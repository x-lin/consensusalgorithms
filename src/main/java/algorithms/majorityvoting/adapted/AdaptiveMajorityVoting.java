package algorithms.majorityvoting.adapted;

import algorithms.crowdtruth.WorkerId;
import algorithms.majorityvoting.MajorityVotingAggregator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import model.*;
import web.SemesterSettings;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Filters out workers according to the following algorithm:
 * 1. Calculate final defects with majority voting algorithm
 * 2. Filter out workers whose identified final defects have an agreement score < threshold
 * 3. Calculate final defects with workers >= threshold
 * 4. Repeat 2 and 3 until no worker agreement score < threshold
 *
 * @author LinX
 */
public class AdaptiveMajorityVoting {

    public ImmutableSet<FinalDefect> run( final double threshold, final SemesterSettings settings )
            throws IOException, SQLException {
        try (Connection connection = DatabaseConnector.createConnection()) {
            final ImmutableSet<Eme> emes = Eme.fetchEmes( connection, settings );

            final Set<DefectReport> defectReportsFiltered = DefectReport.fetchDefectReports( connection,
                    settings.getDefectReportFilter() );

            ImmutableSet<FinalDefect> finalDefectsFiltered;
            final Map<WorkerId, List<DefectReport>> initialDefectReportsPerWorker = defectReportsFiltered.stream()
                    .collect( Collectors.groupingBy( d -> new WorkerId( String.valueOf( d.getWorkerId() ) ) ) );

            final Map<WorkerId, List<DefectReport>> defectReportsPerWorker = Maps.newHashMap(
                    initialDefectReportsPerWorker );

            final AtomicDouble lowestAgreement = new AtomicDouble( 0.0 );

            while (lowestAgreement.get() < threshold) {
                finalDefectsFiltered = aggregate( emes, defectReportsPerWorker );
                final ImmutableMap<WorkerId, Double> agreement = getWorkerAgreement( defectReportsPerWorker,
                        finalDefectsFiltered );

                agreement.entrySet().stream().filter( a -> a.getValue() < threshold ).map( Map.Entry::getKey ).forEach(
                        defectReportsPerWorker::remove );

                lowestAgreement.set( Collections.min( agreement.values() ) );
            }
            return aggregate( emes, defectReportsPerWorker );
        }
    }

    private ImmutableSet<FinalDefect> aggregate( final ImmutableSet<Eme> emes, final Map<WorkerId, List<DefectReport
            >> defectReportsPerWorker ) {
        return new MajorityVotingAggregator( emes, defectReportsPerWorker
                .values().stream().flatMap( Collection::stream ).collect( ImmutableSet.toImmutableSet() ) )
                .aggregate();
    }

    private static ImmutableMap<WorkerId, Double> getWorkerAgreement( final Map<WorkerId, List<DefectReport>>
                                                                              defectReportsPerWorker,
                                                                      final
                                                                      ImmutableSet<FinalDefect> finalDefects ) {
        final ImmutableMap<String, FinalDefectType> finalDefectTypePerEme = finalDefects.stream().collect( ImmutableMap
                .toImmutableMap( FinalDefect::getEmeId, FinalDefect::getFinalDefectType ) );

        return defectReportsPerWorker.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> {
                    final long agreedDefectTypes = e.getValue().stream().filter( f -> {
                        final FinalDefectType agreedFinalDefectType = finalDefectTypePerEme.get( f.getEmeId() );
                        return agreedFinalDefectType == FinalDefectType.UNDECIDABLE || FinalDefectType.fromDefectType( f
                                .getDefectType() ) == agreedFinalDefectType;
                    } ).count();
                    return BigDecimal.valueOf( agreedDefectTypes ).divide( BigDecimal.valueOf( e.getValue().size() ),
                            4, RoundingMode.HALF_UP ).doubleValue();
                } ) );
    }

    public static void main( final String[] args ) throws IOException, SQLException {
        final ImmutableSet<FinalDefect> finalDefects = new AdaptiveMajorityVoting().run( 0.6, SemesterSettings.ws2017
                () );
    }
}
