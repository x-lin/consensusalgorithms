package algorithms.catd;

import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author LinX
 */
public final class Claims {
    private static final Logger LOG = LoggerFactory.getLogger( Claims.class );

    private final ImmutableMap<EntityId, ImmutableSet<Claim>> byEntities;

    private final ImmutableMap<SourceId, ImmutableSet<Claim>> bySources;

    private final ImmutableMap<InformationId, ImmutableMultiset<Claim>> byInformationIds;

    private final ImmutableMap<SourceId, ImmutableMap<EntityId, ImmutableSet<Claim>>> bySourcesForEntities;

    Claims( final Set<Claim> claims ) {
        final Map<EntityId, Set<Claim>> byEntities = Maps.newHashMap();
        final Map<SourceId, Set<Claim>> bySources = Maps.newHashMap();
        final Map<InformationId, List<Claim>> byInformationIds = Maps.newHashMap();
        final Map<SourceId, Map<EntityId, Set<Claim>>> bySourcesForEntity = Maps.newHashMap();

        claims.forEach( claim -> {
            byEntities.computeIfAbsent( claim.getEntityId(), i -> Sets.newHashSet() ).add( claim );
            bySources.computeIfAbsent( claim.getSourceId(), i -> Sets.newHashSet() ).add( claim );
            claim.getInformationIds().forEach( l -> {
                byInformationIds.computeIfAbsent( l, id -> Lists.newArrayList() ).add( claim );
            } );
            bySourcesForEntity.computeIfAbsent( claim.getSourceId(), i -> Maps.newHashMap() )
                              .computeIfAbsent( claim.getEntityId(), i -> Sets.newHashSet() ).add( claim );
        } );

        this.byEntities = byEntities.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> ImmutableSet.copyOf( e.getValue() ) ) );
        this.bySources = bySources.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> ImmutableSet.copyOf( e.getValue() ) ) );
        this.byInformationIds = byInformationIds.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> ImmutableMultiset.copyOf( e.getValue() ) ) );
        this.bySourcesForEntities = bySourcesForEntity.entrySet().stream().collect(
                ImmutableMap.toImmutableMap( Map.Entry::getKey,
                        e -> e.getValue().entrySet().stream().collect( ImmutableMap
                                .toImmutableMap( Map.Entry::getKey, e2 -> ImmutableSet.copyOf( e2.getValue() ) ) ) ) );

        LOG.info( "Entities={} (#{}), sources={} (#{}), informationIds={} (#{})", this.byEntities.keySet(),
                this.byEntities.size(), this.bySources.keySet(), this.bySources.size(), this.byInformationIds,
                this.byInformationIds.size() );
    }

    public ImmutableSet<Claim> getClaims( final EntityId patientId ) {
        return this.byEntities.getOrDefault( patientId, ImmutableSet.of() );
    }

    public ImmutableSet<Claim> getClaims( final SourceId observerId ) {
        return this.bySources.getOrDefault( observerId, ImmutableSet.of() );
    }

    public ImmutableMultiset<Claim> getClaims( final InformationId label ) {
        return this.byInformationIds.getOrDefault( label, ImmutableMultiset.of() );
    }

    public ImmutableSet<Claim> getClaims( final SourceId observer, final EntityId patient ) {
        return this.bySourcesForEntities.getOrDefault( observer, ImmutableMap.of() ).getOrDefault( patient,
                ImmutableSet.of() );
    }

    public ImmutableSet<EntityId> getEntities() {
        return this.byEntities.keySet();
    }

    public ImmutableSet<SourceId> getSources() {
        return this.bySources.keySet();
    }

    public ImmutableSet<InformationId> getInformationIds() {
        return this.byInformationIds.keySet();
    }
}
