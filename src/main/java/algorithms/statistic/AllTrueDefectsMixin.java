package algorithms.statistic;

import algorithms.finaldefects.SemesterSettings;
import algorithms.utils.UncheckedSQLException;
import algorithms.vericom.model.DatabaseConnector;
import algorithms.vericom.model.EmeId;
import algorithms.vericom.model.Emes;
import algorithms.vericom.model.TrueDefect;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Mixes in true defects fetched from other sources.
 *
 * @author LinX
 */
public class AllTrueDefectsMixin {
    private static final String ADDITIONAL_TRUE_DEFECTS_IN_CSV = "src/main/resources/additions/additionalTrueDefects" +
            ".csv";

    private static final String ALL_TRUE_DEFECTS_OUT_CSV = "output/allTrueDefects.csv";

    private final ImmutableSet<TrueDefect> allTrueDefects;

    public AllTrueDefectsMixin( final SemesterSettings settings ) {

        try (Connection connection = DatabaseConnector.createConnection()) {
            Files.createDirectories( Paths.get( "output" ) );
            final Map<EmeId, Set<TrueDefect>> trueDefectsByEme = TrueDefect.fetchTrueDefects( connection ).stream()
                    .collect( Collectors.groupingBy(
                            TrueDefect::getAboutEmEid,
                            Collectors.toSet() ) );

            try (Reader reader = Files.newBufferedReader( Paths.get( ADDITIONAL_TRUE_DEFECTS_IN_CSV ) ); CSVReader
                    additionalTdReader = new CSVReaderBuilder( reader ).withSkipLines( 1 ).build()) {
                additionalTdReader.readAll().forEach( l -> addAdditionalTrueDefects( trueDefectsByEme, l ) );
            }
            trueDefectsByEme.values().forEach( e -> e.removeIf( s -> s.getAboutEmEid().toString().equals( "NA" ) ) );
            trueDefectsByEme.values().removeIf( Set::isEmpty );

            final Emes emes = Emes.fetchFromDb( settings );
            try (CSVWriter allTrueDefects = new CSVWriter( Files.newBufferedWriter( Paths.get(
                    ALL_TRUE_DEFECTS_OUT_CSV ) ) )) {
                allTrueDefects.writeNext( new String[]{"about_em_eid", "eme_text", "code_td", "scenario", "defect_type",
                        "description"} );
                trueDefectsByEme.values().stream().flatMap( Collection::stream ).forEach( td -> allTrueDefects
                        .writeNext( new String[]{td.getAboutEmEid().toString(), emes.get(
                                td.getAboutEmEid() ).getEmeText(), td
                                .getCodeTd(), td.getScenario().toString(), td
                                .getDefectType().name(), td.getDescription()} ) );
            }

            this.allTrueDefects = trueDefectsByEme.values().stream().flatMap( Collection::stream ).collect( ImmutableSet
                    .toImmutableSet() );
        } catch (final IOException e) {
            throw new UncheckedIOException( e );
        } catch (final SQLException e) {
            throw new UncheckedSQLException( e );
        }
    }

    private ImmutableSet<TrueDefect> getAllTrueDefects() {
        return this.allTrueDefects;
    }

    public static ImmutableMap<EmeId, TrueDefect> findAllTrueDefects( final SemesterSettings settings ) {
        return new AllTrueDefectsMixin( settings ).getAllTrueDefects().stream().collect( ImmutableMap.toImmutableMap(
                TrueDefect::getAboutEmEid,
                Function.identity() ) );
    }

    private static void addAdditionalTrueDefects( final Map<EmeId, Set<TrueDefect>> trueDefectsByEme, final
    String[] csvLine ) {
        final AdditionalTrueDefectForEme additionalTrueDefectForEme = new AdditionalTrueDefectForEme(
                csvLine[1], Stream.of( csvLine[3],
                csvLine[5] ).filter( Objects::nonNull ).collect( ImmutableSet.toImmutableSet() ), csvLine
                [7] );
        final EmeId emeId = additionalTrueDefectForEme.getEmeId();
        final Set<TrueDefect> trueDefectsOfEme = trueDefectsByEme.computeIfAbsent(
                emeId, d -> Sets.newHashSet() );
        additionalTrueDefectForEme.getSynonymousEmeIds().stream().flatMap( synonym -> trueDefectsByEme
                .getOrDefault( synonym, Sets.newHashSet() ).stream() ).forEach( s -> trueDefectsOfEme
                .add( s.replaceEmeId( emeId ) ) );
        Optional.ofNullable( additionalTrueDefectForEme.getManualMatchTrueDefectId() ).map( Strings::emptyToNull )
                .ifPresent(
                        manualTd -> {
                            final TrueDefect trueDefect = trueDefectsByEme.values().stream().flatMap( Collection
                                    ::stream ).filter( d -> d.getCodeTd().equals(
                                    manualTd ) ).findFirst().orElseThrow( () -> new NoSuchElementException( "Unknown " +
                                    "true " +
                                    "defect " + manualTd ) );
                            trueDefectsOfEme.add( trueDefect.replaceEmeId( emeId ) );
                        } );
    }

    private static class AdditionalTrueDefectForEme {
        private final EmeId emeId;

        private final ImmutableSet<EmeId> synonymousEmeIds;

        private final String manualMatchTrueDefectId;

        public AdditionalTrueDefectForEme( final String emeId, final Set<String> synonymousEmeIds, final String
                manualMatchTrueDefectId ) {
            this.emeId = new EmeId( emeId );
            this.synonymousEmeIds = synonymousEmeIds.stream().map( EmeId::new ).collect(
                    ImmutableSet.toImmutableSet() );
            this.manualMatchTrueDefectId = manualMatchTrueDefectId;
        }

        public EmeId getEmeId() {
            return this.emeId;
        }

        public ImmutableSet<EmeId> getSynonymousEmeIds() {
            return this.synonymousEmeIds;
        }

        public String getManualMatchTrueDefectId() {
            return this.manualMatchTrueDefectId;
        }

        @Override
        public boolean equals( final Object o ) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final AdditionalTrueDefectForEme that = (AdditionalTrueDefectForEme) o;
            return Objects.equals( this.emeId, that.emeId ) &&
                    Objects.equals( this.synonymousEmeIds, that.synonymousEmeIds ) &&
                    Objects.equals( this.manualMatchTrueDefectId, that.manualMatchTrueDefectId );
        }

        @Override
        public int hashCode() {
            return Objects.hash( this.emeId, this.synonymousEmeIds, this.manualMatchTrueDefectId );
        }

        @Override
        public String toString() {
            return "AdditionalTrueDefectForEme{" +
                    "emeId='" + this.emeId + '\'' +
                    ", synonymousEmeIds=" + this.synonymousEmeIds +
                    ", manualMatchTrueDefectId='" + this.manualMatchTrueDefectId + '\'' +
                    '}';
        }
    }
}
