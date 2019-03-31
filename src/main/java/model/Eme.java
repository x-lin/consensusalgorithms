package model;

import com.google.common.collect.ImmutableSet;
import org.jooq.Record;
import org.jooq.impl.DSL;
import web.SemesterSettings;

import java.sql.Connection;
import java.util.Objects;
import java.util.Optional;

/**
 * @author LinX
 */
public class Eme {
    public static String EME_TABLE = "eme";

    public static String EME_ID_COLUMN = "eme_id";

    private static final String OLD_EME_TEXT_COLUMN = "old_eme_text";

    public static String EME_TEXT = "eme_text";

    public static String EME_TYPE_COLUMN = "eme_type";

    public static String EME_GROUP_COLUMN = "eme_group";

    private final String emeId;

    private final String emeText;

    private final EmeType emeType;

    private final Integer emeGroupId;

    public Eme( final Record record, final SemesterSettings settings ) {
        this.emeId = record.getValue( EME_ID_COLUMN, String.class );
        this.emeText = Optional.ofNullable( record.getValue( OLD_EME_TEXT_COLUMN, String.class ) ).filter( settings
                .useOldEmes() ).filter( t -> !t
                .equals( "NULL" ) ).orElseGet( () -> record.getValue( EME_TEXT, String.class ) );
        this.emeType = record.getValue( EME_TYPE_COLUMN, EmeType.class );
        this.emeGroupId = record.getValue( EME_GROUP_COLUMN, Integer.class );
    }

    private Eme( final Builder builder ) {
        this.emeId = builder.emeId;
        this.emeText = builder.emeText;
        this.emeType = builder.emeType;
        this.emeGroupId = builder.emeGroupId;
    }

    public String getEmeId() {
        return this.emeId;
    }

    public String getEmeText() {
        return this.emeText;
    }

    public EmeType getEmeType() {
        return this.emeType;
    }

    public Integer getEmeGroupId() {
        return this.emeGroupId;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Eme eme = (Eme) o;
        return this.emeGroupId == eme.emeGroupId &&
                Objects.equals( this.emeId, eme.emeId ) &&
                Objects.equals( this.emeText, eme.emeText ) &&
                this.emeType == eme.emeType;
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.emeId, this.emeText, this.emeType, this.emeGroupId );
    }

    @Override
    public String toString() {
        return "Eme{" +
                "emeId='" + this.emeId + '\'' +
                ", emeText='" + this.emeText + '\'' +
                ", emeType=" + this.emeType +
                ", emeGroupId=" + this.emeGroupId +
                '}';
    }

    public static Builder builder( final String emeId ) {
        return new Builder( emeId );
    }

    public static ImmutableSet<Eme> fetchEmes( final Connection connection, final SemesterSettings settings ) {
        final String sql = "select * from " + EME_TABLE;
        return DSL.using( connection )
                .fetch( sql )
                .map( r -> new Eme( r, settings ) ).stream().collect( ImmutableSet.toImmutableSet() );
    }

    public static class Builder {
        private final String emeId;

        private String emeText;

        private EmeType emeType;

        private Integer emeGroupId;

        private Builder( final String emeId ) {
            this.emeId = emeId;
        }

        public Builder withEmeText( final String emeText ) {
            this.emeText = emeText;
            return this;
        }

        public Builder withEmeType( final EmeType emeType ) {
            this.emeType = emeType;
            return this;
        }

        public Builder withEmeGroupId( final int emeGroupId ) {
            this.emeGroupId = emeGroupId;
            return this;
        }

        public Eme build() {
            return new Eme( this );
        }
    }
}
