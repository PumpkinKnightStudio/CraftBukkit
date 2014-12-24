package org.bukkit.craftbukkit.metadata;

import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;

/**
 * An EntityMetadataStore stores metadata values for all {@link Entity} classes an their descendants.
 */
public class EntityMetadataStore extends MetadataStoreBase<Entity> implements MetadataStore<Entity> {
    /**
     * Generates a unique metadata key for an {@link Entity} UUID.
     *
     * @see MetadataStoreBase#disambiguate(Object, String)
     * @param entity the entity
     * @param metadataKey The name identifying the metadata value
     * @return a unique metadata key
     */
    @Override
    protected String disambiguate(Entity entity, String metadataKey) {
        return entity.getUniqueId().toString() + ":" + metadataKey;
    }

    // Unfortunately, there's no way to track all of the metadata attached to an
    // entity because apparently it was a good idea to combine the entity and
    // metadata identifiers. This is the bad solution to the metadata framework
    // being utterly terrible.

    @Override
    public synchronized void setMetadata(Entity subject, String metadataKey, org.bukkit.metadata.MetadataValue newMetadataValue) {
        super.setMetadata(subject, metadataKey, newMetadataValue);
        entityToKeys.put(subject.getUniqueId(), new Object[] { metadataKey, newMetadataValue.getOwningPlugin() });
    }

    private com.google.common.collect.Multimap<java.util.UUID, Object[]> entityToKeys = com.google.common.collect.HashMultimap.create();

    public void clearMetadata(Entity entity) {
        for(Object[] key : entityToKeys.removeAll(entity.getUniqueId())){
            this.removeMetadata(entity, ((String) key[0]), ((org.bukkit.plugin.Plugin) key[1]));
        }
    }
}
