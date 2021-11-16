package org.bukkit.support;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.inventory.CraftItemFactory;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.Versioning;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public final class DummyServer {

    static {
        try {
            Server instance = Mockito.mock(Server.class);

            Mockito.when(instance.getItemFactory()).then(mock -> CraftItemFactory.instance());

            Mockito.when(instance.getName()).then(mock -> DummyServer.class.getSimpleName());

            Mockito.when(instance.getVersion()).then(mock -> DummyServer.class.getPackage().getImplementationVersion());

            Mockito.when(instance.getBukkitVersion()).then(mock -> Versioning.getBukkitVersion());

            Mockito.when(instance.getLogger()).then(new Answer<Logger>() {
                final Logger logger = Logger.getLogger(DummyServer.class.getCanonicalName());
                @Override
                public Logger answer(InvocationOnMock invocationOnMock) {
                    return logger;
                }
            });

            Mockito.when(instance.getUnsafe()).then(mock -> CraftMagicNumbers.INSTANCE);

            Mockito.when(instance.createBlockData((Material) Mockito.any())).then(mock -> CraftBlockData.newData(mock.getArgument(0), null));

            Mockito.when(instance.getLootTable(Mockito.any())).then(mock -> {
                NamespacedKey key = mock.getArgument(0);
                return new CraftLootTable(key, AbstractTestingBase.LOOT_TABLE_REGISTRY.getLootTable(CraftNamespacedKey.toMinecraft(key)));
            });

            Mockito.when(instance.getRegistry(Mockito.any())).then(new Answer<Registry<?>>() {
                private final Map<Class<?>, Registry<?>> registers = new HashMap<>();
                @Override
                public Registry<?> answer(InvocationOnMock mock) {
                    Class<? extends Keyed> aClass = mock.getArgument(0);
                    return registers.computeIfAbsent(aClass, key -> CraftRegistry.createRegistry(aClass, AbstractTestingBase.REGISTRY_CUSTOM));
                }
            });

            Bukkit.setServer(instance);
        } catch (Throwable t) {
            throw new Error(t);
        }
    }

    public static void setup() {}

    private DummyServer() {};
}
