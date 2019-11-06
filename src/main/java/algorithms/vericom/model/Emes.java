package algorithms.vericom.model;

import algorithms.finaldefects.SemesterSettings;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author LinX
 */
public final class Emes {
    private static final Map<SemesterSettings, Emes> CACHED_EMES = Maps.newConcurrentMap();

    private final ImmutableBiMap<EmeId, Eme> emes;

    public Emes( final ImmutableBiMap<EmeId, Eme> emes ) {
        this.emes = emes;
    }

    public ImmutableBiMap<EmeId, Eme> getEmes() {
        return this.emes;
    }

    public Eme get( final EmeId emeId ) {
        return this.emes.get( emeId );
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Emes emes1 = (Emes) o;
        return Objects.equals( this.emes, emes1.emes );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.emes );
    }

    @Override
    public String toString() {
        return "Emes{" +
                "emes=" + this.emes +
                '}';
    }

    public static Emes fetchFromDb( final SemesterSettings settings ) {
        return CACHED_EMES.computeIfAbsent( settings, s -> new Emes( Eme.fetchEmes( settings )
                .collect( ImmutableBiMap
                        .toImmutableBiMap( Eme::getEmeId,
                                Function.identity() ) ) ) );
    }
}
