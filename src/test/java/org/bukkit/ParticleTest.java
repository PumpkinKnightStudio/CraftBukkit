package org.bukkit;

import static org.junit.jupiter.api.Assertions.*;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.core.particles.ParticleParamItem;
import net.minecraft.core.particles.ParticleParamRedstone;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.support.AbstractTestingBase;
import org.joml.Vector3f;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ParticleTest extends AbstractTestingBase {

    public static Stream<Arguments> data() {
        return Registry.PARTICLE_TYPE.stream().map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRightParticleParamCreation(Particle<?> bukkit) {
        bukkit = CraftParticle.convertLegacy(bukkit);
        net.minecraft.core.particles.Particle<?> minecraft = CraftParticle.bukkitToMinecraft(bukkit);

        if (bukkit.getDataType().equals(Void.class)) {
            testEmptyData((Particle<Void>) bukkit, minecraft);
            return;
        }

        if (bukkit.getDataType().equals(Particle.DustOptions.class)) {
            testDustOption((Particle<Particle.DustOptions>) bukkit, minecraft);
            return;
        }

        if (bukkit.getDataType().equals(ItemStack.class)) {
            testItemStack((Particle<ItemStack>) bukkit, minecraft);
            return;
        }

        if (bukkit.getDataType().equals(BlockData.class)) {
            testBlockData((Particle<BlockData>) bukkit, minecraft);
            return;
        }

        if (bukkit.getDataType().equals(Particle.DustTransition.class)) {
            testDustTransition((Particle<Particle.DustTransition>) bukkit, minecraft);
            return;
        }

        if (bukkit.getDataType().equals(Vibration.class)) {
            testVibration((Particle<Vibration>) bukkit, minecraft);
            return;
        }

        if (bukkit.getDataType().equals(Float.class)) {
            testFloat((Particle<Float>) bukkit, minecraft);
            return;
        }

        if (bukkit.getDataType().equals(Integer.class)) {
            testInteger((Particle<Integer>) bukkit, minecraft);
            return;
        }

        fail(String.format("""
                No test found for particle %s.
                Please add a test case for it here.
                """, bukkit.getKey()));
    }

    private <T extends ParticleParam> void testEmptyData(Particle<Void> bukkit, net.minecraft.core.particles.Particle<T> minecraft) {
        createAndTest(bukkit, minecraft, null, ParticleType.class);
    }

    private <T extends ParticleParam> void testDustOption(Particle<Particle.DustOptions> bukkit, net.minecraft.core.particles.Particle<T> minecraft) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(236, 28, 36), 0.1205f);
        ParticleParamRedstone param = createAndTest(bukkit, minecraft, dustOptions, ParticleParamRedstone.class);

        assertEquals(0.1205f, param.getScale(), 0.001, String.format("""
                Dust option scale for particle %s do not match.
                Did something change in the implementation or minecraft?
                """, bukkit.getKey()));

        Vector3f expectedColor = new Vector3f(0.92549f, 0.1098f, 0.14117647f);
        assertTrue(expectedColor.equals(param.getColor(), 0.001f), String.format("""
                Dust option color for particle %s do not match.
                Did something change in the implementation or minecraft?
                Expected: %s.
                Got: %s.
                """, bukkit.getKey(), expectedColor, param.getColor())); // Print expected and got since we use assert true
    }

    private <T extends ParticleParam> void testItemStack(Particle<ItemStack> bukkit, net.minecraft.core.particles.Particle<T> minecraft) {
        ItemStack itemStack = ItemStack.of(ItemType.STONE);
        ParticleParamItem param = createAndTest(bukkit, minecraft, itemStack, ParticleParamItem.class);

        assertEquals(itemStack, CraftItemStack.asBukkitCopy(param.getItem()), String.format("""
                ItemStack for particle %s do not match.
                Did something change in the implementation or minecraft?
                """, bukkit.getKey()));
    }

    private <T extends ParticleParam> void testBlockData(Particle<BlockData> bukkit, net.minecraft.core.particles.Particle<T> minecraft) {
        BlockData blockData = Bukkit.createBlockData(BlockType.STONE);
        ParticleParamBlock param = createAndTest(bukkit, minecraft, blockData, ParticleParamBlock.class);

        assertEquals(blockData, CraftBlockData.fromData(param.getState()), String.format("""
                Block data for particle %s do not match.
                Did something change in the implementation or minecraft?
                """, bukkit.getKey()));
    }

    private <T extends ParticleParam> void testDustTransition(Particle<Particle.DustTransition> bukkit, net.minecraft.core.particles.Particle<T> minecraft) {
        Particle.DustTransition dustTransition = new Particle.DustTransition(Color.fromRGB(236, 28, 36), Color.fromRGB(107, 159, 181), 0.1205f);
        DustColorTransitionOptions param = createAndTest(bukkit, minecraft, dustTransition, DustColorTransitionOptions.class);

        assertEquals(0.1205f, param.getScale(), 0.001, String.format("""
                Dust transition scale for particle %s do not match.
                Did something change in the implementation or minecraft?
                """, bukkit.getKey()));

        Vector3f expectedFrom = new Vector3f(0.92549f, 0.1098f, 0.14117647f);
        assertTrue(expectedFrom.equals(param.getFromColor(), 0.001f), String.format("""
                Dust transition from color for particle %s do not match.
                Did something change in the implementation or minecraft?
                Expected: %s.
                Got: %s.
                """, bukkit.getKey(), expectedFrom, param.getColor())); // Print expected and got since we use assert true

        Vector3f expectedTo = new Vector3f(0.4196f, 0.6235294f, 0.7098f);
        assertTrue(expectedTo.equals(param.getToColor(), 0.001f), String.format("""
                Dust transition to color for particle %s do not match.
                Did something change in the implementation or minecraft?
                Expected: %s.
                Got: %s.
                """, bukkit.getKey(), expectedTo, param.getColor())); // Print expected and got since we use assert true
    }

    private <T extends ParticleParam> void testVibration(Particle<Vibration> bukkit, net.minecraft.core.particles.Particle<T> minecraft) {
        Vibration vibration = new Vibration(new Location(null, 3, 1, 4), new Vibration.Destination.BlockDestination(new Location(null, 1, 5, 9)), 265);
        VibrationParticleOption param = createAndTest(bukkit, minecraft, vibration, VibrationParticleOption.class);

        assertEquals(265, param.getArrivalInTicks(), String.format("""
                Vibration ticks for particle %s do not match.
                Did something change in the implementation or minecraft?
                """, bukkit.getKey()));

        Optional<Vec3D> pos = param.getDestination().getPosition(null);
        assertTrue(pos.isPresent(), String.format("""
                Vibration position for particle %s is not present.
                Did something change in the implementation or minecraft?
                """, bukkit.getKey()));

        // Add 0.5 since it gets centered to the block
        assertEquals(new Vec3D(1.5, 5.5, 9.5), pos.get(), String.format("""
                Vibration position for particle %s do not match.
                Did something change in the implementation or minecraft?
                """, bukkit.getKey()));
    }

    private <T extends ParticleParam> void testFloat(Particle<Float> bukkit, net.minecraft.core.particles.Particle<T> minecraft) {
        float role = 0.1205f;
        SculkChargeParticleOptions param = createAndTest(bukkit, minecraft, role, SculkChargeParticleOptions.class);

        assertEquals(role, param.roll(), 0.001, String.format("""
                Float role for particle %s do not match.
                Did something change in the implementation or minecraft?
                """, bukkit.getKey()));
    }

    private <T extends ParticleParam> void testInteger(Particle<Integer> bukkit, net.minecraft.core.particles.Particle<T> minecraft) {
        int delay = 1205;
        ShriekParticleOption param = createAndTest(bukkit, minecraft, delay, ShriekParticleOption.class);

        assertEquals(delay, param.getDelay(), String.format("""
                Integer delay for particle %s do not match.
                Did something change in the implementation or minecraft?
                """, bukkit.getKey()));
    }

    private <D extends ParticleParam, T extends ParticleParam> D createAndTest(Particle<?> bukkit, net.minecraft.core.particles.Particle<T> minecraft, Object data, Class<D> paramClass) {
        @SuppressWarnings("unchecked")
        T particleParam = (T) assertDoesNotThrow(() -> CraftParticle.createParticleParam(bukkit, data), String.format("""
                Could not create particle param for particle %s.
                This can indicated, that the default particle param is used, but the particle requires extra data.
                Or that something is wrong with the logic which creates the particle param with extra data.
                Check in CraftParticle if the conversion is still correct.
                """, bukkit.getKey()));

        DataResult<NBTBase> encoded = assertDoesNotThrow(() -> minecraft.codec().encodeStart(DynamicOpsNBT.INSTANCE, particleParam),
                String.format("""
                        Could not encoded particle param for particle %s.
                        This can indicated, that the wrong particle param is created in CraftParticle.
                        Particle param is of type %s.
                        """, bukkit.getKey(), particleParam.getClass()));

        Optional<DataResult.PartialResult<NBTBase>> encodeError = encoded.error();
        assertTrue(encodeError.isEmpty(), () -> String.format("""
                Could not encoded particle param for particle %s.
                This is possible because the wrong particle param is created in CraftParticle.
                Particle param is of type %s.
                Error message: %s.
                """, bukkit.getKey(), particleParam.getClass(), encoded.error().get().message()));

        Optional<NBTBase> encodeResult = encoded.result();
        assertTrue(encodeResult.isPresent(), String.format("""
                Result is not present for particle %s.
                Even though there is also no error, this should not happen.
                Particle param is of type %s.
                """, bukkit.getKey(), particleParam.getClass()));

        DataResult<T> decoded = minecraft.codec().parse(DynamicOpsNBT.INSTANCE, encodeResult.get());

        Optional<DataResult.PartialResult<T>> decodeError = decoded.error();
        assertTrue(decodeError.isEmpty(), () -> String.format("""
                Could not decoded particle param for particle %s.
                This is possible because the wrong particle param is created in CraftParticle.
                Particle param is of type %s.
                Error message: %s.


                NBT data: %s.
                """, bukkit.getKey(), particleParam.getClass(), decodeError.get().message(), encodeResult.get()));

        Optional<T> decodeResult = decoded.result();
        assertTrue(decodeResult.isPresent(), String.format("""
                Result is not present for particle %s.
                Even though there is also no error, this should not happen.
                Particle param is of type %s.
                """, bukkit.getKey(), particleParam.getClass()));

        return assertInstanceOf(paramClass, decodeResult.get(), String.format("""
                Result is not of the right type for particle %s.
                """, bukkit.getKey()));
    }
}
