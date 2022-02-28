package org.bukkit.support;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.server.DataPackResources;
import net.minecraft.server.DispenserRegistry;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.ResourcePackVanilla;
import net.minecraft.server.packs.repository.ResourcePackSourceVanilla;
import net.minecraft.server.packs.resources.ResourceManager;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.legacy.CraftLegacyMaterial;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.junit.Assert;

/**
 *  If you are getting: java.lang.ExceptionInInitializerError
 *    at net.minecraft.server.StatisticList.&lt;clinit&gt;(SourceFile:58)
 *    at net.minecraft.server.Item.&lt;clinit&gt;(SourceFile:252)
 *    at net.minecraft.server.Block.&lt;clinit&gt;(Block.java:577)
 *
 *  extend this class to solve it.
 */
public abstract class AbstractTestingBase {
    // Materials that only exist in block form (or are legacy)
    public static final List<Material> INVALIDATED_MATERIALS;

    public static final DataPackResources DATA_PACK;
    public static final IRegistryCustom REGISTRY_CUSTOM;

    static {
        SharedConstants.tryDetectVersion();
        DispenserRegistry.bootStrap();
        REGISTRY_CUSTOM = IRegistryCustom.builtin();
        // Set up resource manager
        ResourceManager resourceManager = new ResourceManager(EnumResourcePackType.SERVER_DATA, Collections.singletonList(new ResourcePackVanilla(ResourcePackSourceVanilla.BUILT_IN_METADATA, "minecraft")));
        // add tags and loot tables for unit tests
        IRegistryCustom.Dimension registry = IRegistryCustom.builtinCopy().freeze();
        // Register vanilla pack
        DATA_PACK = DataPackResources.loadResources(resourceManager, registry, CommandDispatcher.ServerType.DEDICATED, 0, MoreExecutors.directExecutor(), MoreExecutors.directExecutor()).join();
        // Bind tags
        DATA_PACK.updateRegistryTags(registry);

        DummyServer.setup();

        ImmutableList.Builder<Material> builder = ImmutableList.builder();
        for (Iterator<Material> it = Iterators.concat(Registry.MATERIAL.iterator(), CraftLegacyMaterial.getLegacyMaterials().iterator()); it.hasNext(); ) {
            Material m = it.next();
            if (m.isLegacy() || CraftMagicNumbers.getItem(m) == null) {
                builder.add(m);
            }
        }
        INVALIDATED_MATERIALS = builder.build();
        Assert.assertEquals("Expected 590 invalidated materials (got " + INVALIDATED_MATERIALS.size() + ")", 590, INVALIDATED_MATERIALS.size());
    }
}
