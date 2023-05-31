package org.bukkit.craftbukkit.util;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.bukkit.Material;
import org.bukkit.craftbukkit.legacy.EnumEvil;
import org.bukkit.plugin.AuthorNagException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * This file is imported from Commodore.
 *
 * @author md_5
 */
// CHECKSTYLE:OFF
public class Commodore {
    private static final String SUFFIX = "_BUKKIT_REMAPPED";
    private static final Predicate<String> all = value -> true;
    private static final Function<String, String> same = value -> value;
    private static final List<InvocationInfo> INVOCATIONS = new ArrayList<>();
    private static final List<TypeInstructionInfo> TYPE_INSTRUCTIONS = new ArrayList<>();
    private static final List<MethodInfo> METHOD_INFO = new ArrayList<>();

    private static final Set<String> EVIL = new HashSet<>(Arrays.asList(
            "org/bukkit/World (III)I getBlockTypeIdAt",
            "org/bukkit/World (Lorg/bukkit/Location;)I getBlockTypeIdAt",
            "org/bukkit/block/Block ()I getTypeId",
            "org/bukkit/block/Block (I)Z setTypeId",
            "org/bukkit/block/Block (IZ)Z setTypeId",
            "org/bukkit/block/Block (IBZ)Z setTypeIdAndData",
            "org/bukkit/block/Block (B)V setData",
            "org/bukkit/block/Block (BZ)V setData",
            "org/bukkit/inventory/ItemStack ()I getTypeId",
            "org/bukkit/inventory/ItemStack (I)V setTypeId",
            "org/bukkit/inventory/ItemStack ()Lorg/bukkit/Material; getType"
    ));

    static {
        Predicate<String> enumOrNormalMap = eq("java/util/EnumMap").or(eq("java/util/Map"));
        Function<String, String> imposterEnumMap = cons("org/bukkit/craftbukkit/legacy/ImposterEnumMap");

        rerouteToStatic(true, true, enumOrNormalMap, eq("put"), all, imposterEnumMap, append("ToMap"), cons("(Ljava/util/Map;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
        rerouteToStatic(true, true, enumOrNormalMap, eq("putAll"), all, imposterEnumMap, append("ToMap"), cons("(Ljava/util/Map;Ljava/util/Map;)V"));
        rerouteToStatic(true, true, enumOrNormalMap, eq("putIfAbsent"), all, imposterEnumMap, append("ToMap"), cons("(Ljava/util/Map;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
        rerouteToStatic(true, true, enumOrNormalMap, eq("replace"), eq("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Z"), imposterEnumMap, append("ToMap"), cons("(Ljava/util/Map;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Z"));
        rerouteToStatic(true, true, enumOrNormalMap, eq("replace"), eq("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), imposterEnumMap, append("ToMap"), cons("(Ljava/util/Map;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
        rerouteToStatic(true, true, enumOrNormalMap, eq("computeIfAbsent"), all, imposterEnumMap, append("ToMap"), cons("(Ljava/util/Map;Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"));
        rerouteToStatic(true, true, enumOrNormalMap, eq("computeIfPresent"), all, imposterEnumMap, append("ToMap"), cons("(Ljava/util/Map;Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;"));
        rerouteToStatic(true, true, enumOrNormalMap, eq("compute"), all, imposterEnumMap, append("ToMap"), cons("(Ljava/util/Map;Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;"));
        rerouteToStatic(true, true, enumOrNormalMap, eq("merge"), all, imposterEnumMap, append("ToMap"), cons("(Ljava/util/Map;Ljava/lang/Object;Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;"));

        rerouteToStatic(true, true, eq("com/google/common/collect/Maps"), eq("newEnumMap"), all, cons("org/bukkit/craftbukkit/legacy/ImposterEnumMap"), same, same);

        rerouteToStatic(true, true, eq("java/lang/Class"), eq("getEnumConstants"), all, cons("org/bukkit/craftbukkit/legacy/EnumEvil"), same, cons("(Ljava/lang/Class;)Ljava/lang/Object;"));

        rerouteClass(true, true, true, "java/util/EnumMap", "org/bukkit/craftbukkit/legacy/ImposterEnumMap", same);

        Predicate<String> enumSet = eq("java/util/EnumSet");
        // Need to do it in such a way for maven to correctly insert the version in the package
        String imposterSetString = "org/bukkit/craftbukkit/legacy/ImposterEnumSet";
        String imposterReturn = "L" + imposterSetString + ";";
        Function<String, String> imposterSet = cons("org/bukkit/craftbukkit/legacy/ImposterEnumSet");

        rerouteToStatic(true, true, enumSet, eq("noneOf"), all, imposterSet, same, cons("(Ljava/lang/Class;)" + imposterReturn));
        rerouteToStatic(true, true, enumSet, eq("allOf"), all, imposterSet, same, cons("(Ljava/lang/Class;)" + imposterReturn));
        rerouteToStatic(true, true, enumSet, eq("copyOf"), eq("(Ljava/util/EnumSet;)Ljava/util/EnumSet;"), imposterSet, same, cons("(Ljava/util/Set;)" + imposterReturn));
        rerouteToStatic(true, true, enumSet, eq("copyOf"), eq("(Ljava/util/Collection;)Ljava/util/EnumSet;"), imposterSet, same, cons("(Ljava/util/Collection;)" + imposterReturn));
        rerouteToStatic(true, true, enumSet, eq("complementOf"), all, imposterSet, same, cons("(Ljava/util/Set;)" + imposterReturn));
        rerouteToStatic(true, true, enumSet, eq("of"), eq("(Ljava/lang/Enum;)Ljava/util/EnumSet;"), imposterSet, same, cons("(Ljava/lang/Object;)" + imposterReturn));
        rerouteToStatic(true, true, enumSet, eq("of"), eq("(Ljava/lang/Enum;Ljava/lang/Enum;)Ljava/util/EnumSet;"), imposterSet, same, cons("(Ljava/lang/Object;Ljava/lang/Object;)" + imposterReturn));
        rerouteToStatic(true, true, enumSet, eq("of"), eq("(Ljava/lang/Enum;Ljava/lang/Enum;Ljava/lang/Enum;)Ljava/util/EnumSet;"), imposterSet, same, cons("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)" + imposterReturn));
        rerouteToStatic(true, true, enumSet, eq("of"), eq("(Ljava/lang/Enum;Ljava/lang/Enum;Ljava/lang/Enum;Ljava/lang/Enum;)Ljava/util/EnumSet;"), imposterSet, same, cons("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)" + imposterReturn));
        rerouteToStatic(true, true, enumSet, eq("of"), eq("(Ljava/lang/Enum;Ljava/lang/Enum;Ljava/lang/Enum;Ljava/lang/Enum;Ljava/lang/Enum;)Ljava/util/EnumSet;"), imposterSet, same, cons("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)" + imposterReturn));
        rerouteToStatic(true, true, enumSet, eq("of"), eq("(Ljava/lang/Enum;[Ljava/lang/Enum;)Ljava/util/EnumSet;"), imposterSet, same, cons("(Ljava/lang/Object;[Ljava/lang/Object;)" + imposterReturn));
        rerouteToStatic(true, true, enumSet, eq("range"), all, imposterSet, same, cons("(Ljava/lang/Object;Ljava/lang/Object;)" + imposterReturn));
        rerouteClass(true, true, true, "java/util/EnumSet", "org/bukkit/craftbukkit/legacy/ImposterEnumSet", same);

        rerouteToStatic(true, true, eq("java/lang/Enum"), eq("name"), all, cons("org/bukkit/craftbukkit/legacy/EnumEvil"), same, cons("(Ljava/lang/Object;)Ljava/lang/String;"));
        rerouteToStatic(true, true, eq("java/lang/Enum"), eq("compareTo"), all, cons("org/bukkit/craftbukkit/legacy/EnumEvil"), same, cons("(Ljava/lang/Object;Ljava/lang/Object;)I"));
        rerouteToStatic(true, true, eq("java/lang/Enum"), eq("getDeclaringClass"), all, cons("org/bukkit/craftbukkit/legacy/EnumEvil"), same, cons("(Ljava/lang/Object;)Ljava/lang/Class;"));
        rerouteToStatic(true, true, eq("java/lang/Enum"), eq("describeConstable"), all, cons("org/bukkit/craftbukkit/legacy/EnumEvil"), same, cons("(Ljava/lang/Object;)Ljava/util/Optional;"));
        rerouteToStatic(true, true, eq("java/lang/Enum"), eq("valueOf"), all, cons("org/bukkit/craftbukkit/legacy/EnumEvil"), same, cons("(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;"));
        rerouteToStatic(true, true, eq("java/lang/Enum"), eq("toString"), all, cons("org/bukkit/craftbukkit/legacy/EnumEvil"), same, cons("(Ljava/lang/Object;)Ljava/lang/String;"));
        rerouteToStatic(true, true, eq("java/lang/Enum"), eq("ordinal"), all, cons("org/bukkit/craftbukkit/legacy/EnumEvil"), same, cons("(Ljava/lang/Object;)I"));

        renameMethod(true, true, all, contains("java/lang/Enum"), append(SUFFIX));
        rerouteType(true, true, "java/lang/Enum", "java/lang/Object");
    }

    public static Predicate<String> eq(String value) {
        return value::equals;
    }

    public static Predicate<String> contains(String value) {
        return val -> val.contains(value);
    }

    public static Function<String, String> cons(String value) {
        return val -> value;
    }

    public static Function<String, String> append(String value) {
        return val -> val + value;
    }

    public static void renameMethod(boolean preEnumKill, boolean enumCompatibility, Predicate<String> name, Predicate<String> desc, Function<String, String> newName) {
        METHOD_INFO.add(new MethodInfo(preEnumKill, enumCompatibility, name, desc, newName));
    }

    public static void rerouteToStatic(boolean preEnumKill, boolean enumCompatibility, Predicate<String> owner, Predicate<String> name, Predicate<String> desc, Function<String, String> newOwner, Function<String, String> newName, Function<String, String> newDesc) {
        INVOCATIONS.add(new InvocationInfo(preEnumKill, enumCompatibility, false, false, true, false, owner, name, desc, newOwner, newName, newDesc));
    }

    public static void rerouteClass(boolean preEnumKill, boolean enumCompatibility, boolean toSpecial, String owner, String newOwner, Function<String, String> newDesc) {
        INVOCATIONS.add(new InvocationInfo(preEnumKill, enumCompatibility, true, toSpecial, false, false, eq(owner), all, all, cons(newOwner), same, newDesc));
        INVOCATIONS.add(new InvocationInfo(preEnumKill, enumCompatibility, false, false, false, false, eq(owner), all, all, cons(newOwner), same, newDesc));
        TYPE_INSTRUCTIONS.add(new TypeInstructionInfo(preEnumKill, enumCompatibility, owner, newOwner));
    }

    public static void rerouteType(boolean preEnumKill, boolean enumCompatibility, String owner, String newOwner) {
        TYPE_INSTRUCTIONS.add(new TypeInstructionInfo(preEnumKill, enumCompatibility, owner, newOwner));
    }

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        OptionSpec<File> inputFlag = parser.acceptsAll(Arrays.asList("i", "input")).withRequiredArg().ofType(File.class).required();
        OptionSpec<File> outputFlag = parser.acceptsAll(Arrays.asList("o", "output")).withRequiredArg().ofType(File.class).required();

        OptionSet options = parser.parse(args);

        File input = options.valueOf(inputFlag);
        File output = options.valueOf(outputFlag);

        if (input.isDirectory()) {
            if (!output.isDirectory()) {
                System.err.println("If input directory specified, output directory required too");
                return;
            }

            for (File in : input.listFiles()) {
                if (in.getName().endsWith(".jar")) {
                    convert(in, new File(output, in.getName()));
                }
            }
        } else {
            convert(input, output);
        }
    }

    private static void convert(File in, File out) {
        System.out.println("Attempting to convert " + in + " to " + out);

        try {
            try (JarFile inJar = new JarFile(in, false)) {
                JarEntry entry = inJar.getJarEntry(".commodore");
                if (entry != null) {
                    return;
                }

                try (JarOutputStream outJar = new JarOutputStream(new FileOutputStream(out))) {
                    for (Enumeration<JarEntry> entries = inJar.entries(); entries.hasMoreElements(); ) {
                        entry = entries.nextElement();

                        try (InputStream is = inJar.getInputStream(entry)) {
                            byte[] b = ByteStreams.toByteArray(is);

                            if (entry.getName().endsWith(".class")) {
                                b = convert(b, false, false, true);
                                entry = new JarEntry(entry.getName());
                            }

                            outJar.putNextEntry(entry);
                            outJar.write(b);
                        }
                    }

                    outJar.putNextEntry(new ZipEntry(".commodore"));
                }
            }
        } catch (Exception ex) {
            System.err.println("Fatal error trying to convert " + in);
            ex.printStackTrace();
        }
    }

    public static byte[] convert(byte[] b, final boolean modern, final boolean preEnumKilling, final boolean enumCompatibility) {
        ClassReader cr = new ClassReader(b);
        ClassWriter cw = new ClassWriter(cr, 0);

        cr.accept(new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                for (TypeInstructionInfo typeInstructionInfo : TYPE_INSTRUCTIONS) {
                    if (preEnumKilling != typeInstructionInfo.preEnumKill) {
                        continue;
                    }

                    if (enumCompatibility != typeInstructionInfo.enumCompatibility) {
                        continue;
                    }

                    desc = desc.replace(typeInstructionInfo.owner, typeInstructionInfo.newOwner);
                    signature = signature == null ? null : signature.replace(typeInstructionInfo.owner, typeInstructionInfo.newOwner);
                }
                return super.visitField(access, name, desc, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (!name.equals("<init>")) {
                    for (MethodInfo methodInfo : METHOD_INFO) {
                        if (preEnumKilling != methodInfo.preEnumKill) {
                            continue;
                        }

                        if (enumCompatibility != methodInfo.enumCompatibility) {
                            continue;
                        }

                        if (!methodInfo.name.test(name)) {
                            continue;
                        }

                        if (!methodInfo.desc.test(desc)) {
                            if (signature == null) {
                                continue;
                            }
                            if (!methodInfo.desc.test(signature)) {
                                continue;
                            }
                        }

                        name = methodInfo.newName.apply(name);

                        break;
                    }
                }

                for (TypeInstructionInfo typeInstructionInfo : TYPE_INSTRUCTIONS) {
                    if (preEnumKilling != typeInstructionInfo.preEnumKill) {
                        continue;
                    }

                    if (enumCompatibility != typeInstructionInfo.enumCompatibility) {
                        continue;
                    }

                    desc = desc.replace(typeInstructionInfo.owner, typeInstructionInfo.newOwner);
                    signature = signature == null ? null : signature.replace(typeInstructionInfo.owner, typeInstructionInfo.newOwner);
                }

                return new MethodVisitor(api, super.visitMethod(access, name, desc, signature, exceptions)) {
                    @Override
                    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                        if (owner.equals("org/bukkit/block/Biome")) {
                            switch (name) {
                                case "NETHER":
                                    super.visitFieldInsn(opcode, owner, "NETHER_WASTES", desc);
                                    return;
                                case "TALL_BIRCH_FOREST":
                                    super.visitFieldInsn(opcode, owner, "OLD_GROWTH_BIRCH_FOREST", desc);
                                    return;
                                case "GIANT_TREE_TAIGA":
                                    super.visitFieldInsn(opcode, owner, "OLD_GROWTH_PINE_TAIGA", desc);
                                    return;
                                case "GIANT_SPRUCE_TAIGA":
                                    super.visitFieldInsn(opcode, owner, "OLD_GROWTH_SPRUCE_TAIGA", desc);
                                    return;
                                case "SNOWY_TUNDRA":
                                    super.visitFieldInsn(opcode, owner, "SNOWY_PLAINS", desc);
                                    return;
                                case "JUNGLE_EDGE":
                                    super.visitFieldInsn(opcode, owner, "SPARSE_JUNGLE", desc);
                                    return;
                                case "STONE_SHORE":
                                    super.visitFieldInsn(opcode, owner, "STONY_SHORE", desc);
                                    return;
                                case "MOUNTAINS":
                                    super.visitFieldInsn(opcode, owner, "WINDSWEPT_HILLS", desc);
                                    return;
                                case "WOODED_MOUNTAINS":
                                    super.visitFieldInsn(opcode, owner, "WINDSWEPT_FOREST", desc);
                                    return;
                                case "GRAVELLY_MOUNTAINS":
                                    super.visitFieldInsn(opcode, owner, "WINDSWEPT_GRAVELLY_HILLS", desc);
                                    return;
                                case "SHATTERED_SAVANNA":
                                    super.visitFieldInsn(opcode, owner, "WINDSWEPT_SAVANNA", desc);
                                    return;
                                case "WOODED_BADLANDS_PLATEAU":
                                    super.visitFieldInsn(opcode, owner, "WOODED_BADLANDS", desc);
                                    return;
                            }
                        }

                        if (owner.equals("org/bukkit/entity/EntityType")) {
                            switch (name) {
                                case "PIG_ZOMBIE":
                                    super.visitFieldInsn(opcode, owner, "ZOMBIFIED_PIGLIN", desc);
                                    return;
                                case "DROPPED_ITEM":
                                    super.visitFieldInsn(opcode, owner, "ITEM", desc);
                                    return;
                                case "LEASH_HITCH":
                                    super.visitFieldInsn(opcode, owner, "LEASH_KNOT", desc);
                                    return;
                                case "ENDER_SIGNAL":
                                    super.visitFieldInsn(opcode, owner, "EYE_OF_ENDER", desc);
                                    return;
                                case "SPLASH_POTION":
                                    super.visitFieldInsn(opcode, owner, "POTION", desc);
                                    return;
                                case "THROWN_EXP_BOTTLE":
                                    super.visitFieldInsn(opcode, owner, "EXPERIENCE_BOTTLE", desc);
                                    return;
                                case "PRIMED_TNT":
                                    super.visitFieldInsn(opcode, owner, "TNT", desc);
                                    return;
                                case "FIREWORK":
                                    super.visitFieldInsn(opcode, owner, "FIREWORK_ROCKET", desc);
                                    return;
                                case "MINECART_COMMAND":
                                    super.visitFieldInsn(opcode, owner, "COMMAND_BLOCK_MINECART", desc);
                                    return;
                                case "MINECART_CHEST":
                                    super.visitFieldInsn(opcode, owner, "CHEST_MINECART", desc);
                                    return;
                                case "MINECART_FURNACE":
                                    super.visitFieldInsn(opcode, owner, "FURNACE_MINECART", desc);
                                    return;
                                case "MINECART_TNT":
                                    super.visitFieldInsn(opcode, owner, "TNT_MINECART", desc);
                                    return;
                                case "MINECART_HOPPER":
                                    super.visitFieldInsn(opcode, owner, "HOPPER_MINECART", desc);
                                    return;
                                case "MINECART_MOB_SPAWNER":
                                    super.visitFieldInsn(opcode, owner, "SPAWNER_MINECART", desc);
                                    return;
                                case "MUSHROOM_COW":
                                    super.visitFieldInsn(opcode, owner, "MOOSHROOM", desc);
                                    return;
                                case "SNOWMAN":
                                    super.visitFieldInsn(opcode, owner, "SNOW_GOLEM", desc);
                                    return;
                                case "ENDER_CRYSTAL":
                                    super.visitFieldInsn(opcode, owner, "END_CRYSTAL", desc);
                                    return;
                                case "FISHING_HOOK":
                                    super.visitFieldInsn(opcode, owner, "FISHING_BOBBER", desc);
                                    return;
                                case "LIGHTNING":
                                    super.visitFieldInsn(opcode, owner, "LIGHTNING_BOLT", desc);
                                    return;
                            }
                        }

                        if (owner.equals("org/bukkit/loot/LootTables")) {
                            switch (name) {
                                case "ZOMBIE_PIGMAN":
                                    super.visitFieldInsn(opcode, owner, "ZOMBIFIED_PIGLIN", desc);
                                    return;
                            }
                        }

                        if (owner.equals("org/bukkit/potion/PotionEffectType")) {
                            switch (name) {
                                case "SLOW":
                                    super.visitFieldInsn(opcode, owner, "SLOWNESS", desc);
                                    return;
                                case "FAST_DIGGING":
                                    super.visitFieldInsn(opcode, owner, "HASTE", desc);
                                    return;
                                case "SLOW_DIGGING":
                                    super.visitFieldInsn(opcode, owner, "MINING_FATIGUE", desc);
                                    return;
                                case "INCREASE_DAMAGE":
                                    super.visitFieldInsn(opcode, owner, "STRENGTH", desc);
                                    return;
                                case "HEAL":
                                    super.visitFieldInsn(opcode, owner, "INSTANT_HEALTH", desc);
                                    return;
                                case "HARM":
                                    super.visitFieldInsn(opcode, owner, "INSTANT_DAMAGE", desc);
                                    return;
                                case "JUMP":
                                    super.visitFieldInsn(opcode, owner, "JUMP_BOOST", desc);
                                    return;
                                case "CONFUSION":
                                    super.visitFieldInsn(opcode, owner, "NAUSEA", desc);
                                    return;
                                case "DAMAGE_RESISTANCE":
                                    super.visitFieldInsn(opcode, owner, "RESISTANCE", desc);
                                    return;
                            }
                        }

                        if (owner.equals("org/bukkit/enchantments/Enchantment")) {
                            switch (name) {
                                case "PROTECTION_ENVIRONMENTAL":
                                    super.visitFieldInsn(opcode, owner, "PROTECTION", desc);
                                    return;
                                case "PROTECTION_FIRE":
                                    super.visitFieldInsn(opcode, owner, "FIRE_PROTECTION", desc);
                                    return;
                                case "PROTECTION_FALL":
                                    super.visitFieldInsn(opcode, owner, "FEATHER_FALLING", desc);
                                    return;
                                case "PROTECTION_EXPLOSIONS":
                                    super.visitFieldInsn(opcode, owner, "BLAST_PROTECTION", desc);
                                    return;
                                case "PROTECTION_PROJECTILE":
                                    super.visitFieldInsn(opcode, owner, "PROJECTILE_PROTECTION", desc);
                                    return;
                                case "OXYGEN":
                                    super.visitFieldInsn(opcode, owner, "RESPIRATION", desc);
                                    return;
                                case "WATER_WORKER":
                                    super.visitFieldInsn(opcode, owner, "AQUA_AFFINITY", desc);
                                    return;
                                case "DAMAGE_ALL":
                                    super.visitFieldInsn(opcode, owner, "SHARPNESS", desc);
                                    return;
                                case "DAMAGE_UNDEAD":
                                    super.visitFieldInsn(opcode, owner, "SMITE", desc);
                                    return;
                                case "DAMAGE_ARTHROPODS":
                                    super.visitFieldInsn(opcode, owner, "BANE_OF_ARTHROPODS", desc);
                                    return;
                                case "LOOT_BONUS_MOBS":
                                    super.visitFieldInsn(opcode, owner, "LOOTING", desc);
                                    return;
                                case "SWEEPING_EDGE":
                                    super.visitFieldInsn(opcode, owner, "SWEEPING", desc);
                                    return;
                                case "DIG_SPEED":
                                    super.visitFieldInsn(opcode, owner, "EFFICIENCY", desc);
                                    return;
                                case "DURABILITY":
                                    super.visitFieldInsn(opcode, owner, "UNBREAKING", desc);
                                    return;
                                case "LOOT_BONUS_BLOCKS":
                                    super.visitFieldInsn(opcode, owner, "FORTUNE", desc);
                                    return;
                                case "ARROW_DAMAGE":
                                    super.visitFieldInsn(opcode, owner, "POWER", desc);
                                    return;
                                case "ARROW_KNOCKBACK":
                                    super.visitFieldInsn(opcode, owner, "PUNCH", desc);
                                    return;
                                case "ARROW_FIRE":
                                    super.visitFieldInsn(opcode, owner, "FLAME", desc);
                                    return;
                                case "ARROW_INFINITE":
                                    super.visitFieldInsn(opcode, owner, "INFINITY", desc);
                                    return;
                                case "LUCK":
                                    super.visitFieldInsn(opcode, owner, "LUCK_OF_THE_SEA", desc);
                                    return;
                            }
                        }

                        if (preEnumKilling && owner.equals("org/bukkit/block/banner/PatternType")) {
                            switch (name) {
                                case "STRIPE_SMALL":
                                    super.visitFieldInsn(opcode, owner, "SMALL_STRIPES", desc);
                                    return;
                                case "DIAGONAL_RIGHT":
                                    super.visitFieldInsn(opcode, owner, "DIAGONAL_UP_RIGHT", desc);
                                    return;
                                case "DIAGONAL_LEFT_MIRROR":
                                    super.visitFieldInsn(opcode, owner, "DIAGONAL_UP_LEFT", desc);
                                    return;
                                case "DIAGONAL_RIGHT_MIRROR":
                                    super.visitFieldInsn(opcode, owner, "DIAGONAL_RIGHT", desc);
                                    return;
                                case "CIRCLE_MIDDLE":
                                    super.visitFieldInsn(opcode, owner, "CIRCLE", desc);
                                    return;
                                case "RHOMBUS_MIDDLE":
                                    super.visitFieldInsn(opcode, owner, "RHOMBUS", desc);
                                    return;
                                case "HALF_VERTICAL_MIRROR":
                                    super.visitFieldInsn(opcode, owner, "HALF_VERTICAL_RIGHT", desc);
                                    return;
                                case "HALF_HORIZONTAL_MIRROR":
                                    super.visitFieldInsn(opcode, owner, "HALF_HORIZONTAL_BOTTOM", desc);
                                    return;
                            }
                        }

                        if (owner.equals("org/bukkit/Particle")) {
                            switch (name) {
                                case "EXPLOSION_NORMAL":
                                    super.visitFieldInsn(opcode, owner, "POOF" + name, desc);
                                    return;
                                case "EXPLOSION_LARGE":
                                    super.visitFieldInsn(opcode, owner, "EXPLOSION" + name, desc);
                                    return;
                                case "EXPLOSION_HUGE":
                                    super.visitFieldInsn(opcode, owner, "EXPLOSION_EMITTER" + name, desc);
                                    return;
                                case "FIREWORKS_SPARK":
                                    super.visitFieldInsn(opcode, owner, "FIREWORK" + name, desc);
                                    return;
                                case "WATER_BUBBLE":
                                    super.visitFieldInsn(opcode, owner, "BUBBLE" + name, desc);
                                    return;
                                case "WATER_SPLASH":
                                    super.visitFieldInsn(opcode, owner, "SPLASH" + name, desc);
                                    return;
                                case "WATER_WAKE":
                                    super.visitFieldInsn(opcode, owner, "FISHING" + name, desc);
                                    return;
                                case "SUSPENDED":
                                    super.visitFieldInsn(opcode, owner, "UNDERWATER" + name, desc);
                                    return;
                                case "SUSPENDED_DEPTH":
                                    super.visitFieldInsn(opcode, owner, "UNDERWATER" + name, desc);
                                    return;
                                case "CRIT_MAGIC":
                                    super.visitFieldInsn(opcode, owner, "ENCHANTED_HIT" + name, desc);
                                    return;
                                case "SMOKE_NORMAL":
                                    super.visitFieldInsn(opcode, owner, "SMOKE" + name, desc);
                                    return;
                                case "SMOKE_LARGE":
                                    super.visitFieldInsn(opcode, owner, "LARGE_SMOKE" + name, desc);
                                    return;
                                case "SPELL":
                                    super.visitFieldInsn(opcode, owner, "EFFECT" + name, desc);
                                    return;
                                case "SPELL_INSTANT":
                                    super.visitFieldInsn(opcode, owner, "INSTANT_EFFECT" + name, desc);
                                    return;
                                case "SPELL_MOB":
                                    super.visitFieldInsn(opcode, owner, "ENTITY_EFFECT" + name, desc);
                                    return;
                                case "SPELL_MOB_AMBIENT":
                                    super.visitFieldInsn(opcode, owner, "AMBIENT_ENTITY_EFFECT" + name, desc);
                                    return;
                                case "SPELL_WITCH":
                                    super.visitFieldInsn(opcode, owner, "WITCH" + name, desc);
                                    return;
                                case "DRIP_WATER":
                                    super.visitFieldInsn(opcode, owner, "DRIPPING_WATER" + name, desc);
                                    return;
                                case "DRIP_LAVA":
                                    super.visitFieldInsn(opcode, owner, "DRIPPING_LAVA" + name, desc);
                                    return;
                                case "VILLAGER_ANGRY":
                                    super.visitFieldInsn(opcode, owner, "ANGRY_VILLAGER" + name, desc);
                                    return;
                                case "VILLAGER_HAPPY":
                                    super.visitFieldInsn(opcode, owner, "HAPPY_VILLAGER" + name, desc);
                                    return;
                                case "TOWN_AURA":
                                    super.visitFieldInsn(opcode, owner, "MYCELIUM" + name, desc);
                                    return;
                                case "ENCHANTMENT_TABLE":
                                    super.visitFieldInsn(opcode, owner, "ENCHANT" + name, desc);
                                    return;
                                case "REDSTONE":
                                    super.visitFieldInsn(opcode, owner, "DUST" + name, desc);
                                    return;
                                case "SNOWBALL":
                                    super.visitFieldInsn(opcode, owner, "ITEM_SNOWBALL" + name, desc);
                                    return;
                                case "SNOW_SHOVEL":
                                    super.visitFieldInsn(opcode, owner, "ITEM_SNOWBALL" + name, desc);
                                    return;
                                case "SLIME":
                                    super.visitFieldInsn(opcode, owner, "ITEM_SLIME" + name, desc);
                                    return;
                                case "ITEM_CRACK":
                                    super.visitFieldInsn(opcode, owner, "ITEM" + name, desc);
                                    return;
                                case "BLOCK_CRACK":
                                    super.visitFieldInsn(opcode, owner, "BLOCK" + name, desc);
                                    return;
                                case "BLOCK_DUST":
                                    super.visitFieldInsn(opcode, owner, "BLOCK" + name, desc);
                                    return;
                                case "WATER_DROP":
                                    super.visitFieldInsn(opcode, owner, "RAIN" + name, desc);
                                    return;
                                case "MOB_APPEARANCE":
                                    super.visitFieldInsn(opcode, owner, "ELDER_GUARDIAN" + name, desc);
                                    return;
                                case "TOTEM":
                                    super.visitFieldInsn(opcode, owner, "TOTEM_OF_UNDYING" + name, desc);
                                    return;
                            }
                        }

                        if (owner.equals("org/bukkit/potion/PotionType")) {
                            switch (name) {
                                case "UNCRAFTABLE":
                                    super.visitFieldInsn(opcode, owner, "EMPTY", desc);
                                    return;
                                case "JUMP":
                                    super.visitFieldInsn(opcode, owner, "LEAPING", desc);
                                    return;
                                case "SPEED":
                                    super.visitFieldInsn(opcode, owner, "SWIFTNESS", desc);
                                    return;
                                case "INSTANT_HEAL":
                                    super.visitFieldInsn(opcode, owner, "HEALING", desc);
                                    return;
                                case "INSTANT_DAMAGE":
                                    super.visitFieldInsn(opcode, owner, "HARMING", desc);
                                    return;
                                case "REGEN":
                                    super.visitFieldInsn(opcode, owner, "REGENERATION", desc);
                                    return;
                            }
                        }

                        if (owner.equals("org/bukkit/GameEvent")) {
                            switch (name) {
                                case "BLOCK_PRESS":
                                    super.visitFieldInsn(opcode, owner, "BLOCK_ACTIVATE", desc);
                                    return;
                                case "BLOCK_SWITCH":
                                    super.visitFieldInsn(opcode, owner, "BLOCK_ACTIVATE", desc);
                                    return;
                                case "BLOCK_UNPRESS":
                                    super.visitFieldInsn(opcode, owner, "BLOCK_DEACTIVATE", desc);
                                    return;
                                case "BLOCK_UNSWITCH":
                                    super.visitFieldInsn(opcode, owner, "BLOCK_DEACTIVATE", desc);
                                    return;
                                case "DRINKING_FINISH":
                                    super.visitFieldInsn(opcode, owner, "DRINK", desc);
                                    return;
                                case "ELYTRA_FREE_FALL":
                                    super.visitFieldInsn(opcode, owner, "ELYTRA_GLIDE", desc);
                                    return;
                                case "ENTITY_DAMAGED":
                                    super.visitFieldInsn(opcode, owner, "ENTITY_DAMAGE", desc);
                                    return;
                                case "ENTITY_DYING":
                                    super.visitFieldInsn(opcode, owner, "ENTITY_DIE", desc);
                                    return;
                                case "ENTITY_KILLED":
                                    super.visitFieldInsn(opcode, owner, "ENTITY_DIE", desc);
                                    return;
                                case "MOB_INTERACT":
                                    super.visitFieldInsn(opcode, owner, "ENTITY_INTERACT", desc);
                                    return;
                                case "RAVAGER_ROAR":
                                    super.visitFieldInsn(opcode, owner, "ENTITY_ROAR", desc);
                                    return;
                                case "RING_BELL":
                                    super.visitFieldInsn(opcode, owner, "BLOCK_CHANGE", desc);
                                    return;
                                case "SHULKER_CLOSE":
                                    super.visitFieldInsn(opcode, owner, "CONTAINER_CLOSE", desc);
                                    return;
                                case "SHULKER_OPEN":
                                    super.visitFieldInsn(opcode, owner, "CONTAINER_OPEN", desc);
                                    return;
                                case "WOLF_SHAKING":
                                    super.visitFieldInsn(opcode, owner, "ENTITY_SHAKE", desc);
                                    return;
                            }
                        }

                        if (owner.equals("org/bukkit/potion/PotionType")) {
                            switch (name) {
                                case "PONDER":
                                    super.visitFieldInsn(opcode, owner, "PONDER_GOAT_HORN", desc);
                                    return;
                                case "SING":
                                    super.visitFieldInsn(opcode, owner, "SING_GOAT_HORN", desc);
                                    return;
                                case "SEEK":
                                    super.visitFieldInsn(opcode, owner, "SEEK_GOAT_HORN", desc);
                                    return;
                                case "FEEL":
                                    super.visitFieldInsn(opcode, owner, "FEEL_GOAT_HORN", desc);
                                    return;
                                case "ADMIRE":
                                    super.visitFieldInsn(opcode, owner, "ADMIRE_GOAT_HORN", desc);
                                    return;
                                case "CALL":
                                    super.visitFieldInsn(opcode, owner, "CALL_GOAT_HORN", desc);
                                    return;
                                case "YEARN":
                                    super.visitFieldInsn(opcode, owner, "YEARN_GOAT_HORN", desc);
                                    return;
                                case "DREAM":
                                    super.visitFieldInsn(opcode, owner, "DREAM_GOAT_HORN", desc);
                                    return;
                            }
                        }

                        // SPIGOT-7335
                        if (owner.equals("org/bukkit/entity/TextDisplay$TextAligment")) {
                            super.visitFieldInsn(opcode, "org/bukkit/entity/TextDisplay$TextAlignment", name, desc);
                            return;
                        }

                        if (modern) {
                            if (owner.equals("org/bukkit/Material")) {
                                switch (name) {
                                    case "CACTUS_GREEN":
                                        name = "GREEN_DYE";
                                        break;
                                    case "DANDELION_YELLOW":
                                        name = "YELLOW_DYE";
                                        break;
                                    case "ROSE_RED":
                                        name = "RED_DYE";
                                        break;
                                    case "SIGN":
                                        name = "OAK_SIGN";
                                        break;
                                    case "WALL_SIGN":
                                        name = "OAK_WALL_SIGN";
                                        break;
                                    case "ZOMBIE_PIGMAN_SPAWN_EGG":
                                        name = "ZOMBIFIED_PIGLIN_SPAWN_EGG";
                                        break;
                                    case "GRASS_PATH":
                                        name = "DIRT_PATH";
                                        break;
                                }
                            }
                        } else {

                            if (owner.equals("org/bukkit/Material")) {
                                try {
                                    Material.valueOf("LEGACY_" + name);
                                } catch (IllegalArgumentException ex) {
                                    throw new AuthorNagException("No legacy enum constant for " + name + ". Did you forget to define a modern (1.13+) api-version in your plugin.yml?");
                                }

                                super.visitFieldInsn(opcode, owner, "LEGACY_" + name, desc);
                                return;
                            }

                            if (owner.equals("org/bukkit/Art")) {
                                switch (name) {
                                    case "BURNINGSKULL":
                                        super.visitFieldInsn(opcode, owner, "BURNING_SKULL", desc);
                                        return;
                                    case "DONKEYKONG":
                                        super.visitFieldInsn(opcode, owner, "DONKEY_KONG", desc);
                                        return;
                                }
                            }

                            if (owner.equals("org/bukkit/DyeColor")) {
                                switch (name) {
                                    case "SILVER":
                                        super.visitFieldInsn(opcode, owner, "LIGHT_GRAY", desc);
                                        return;
                                }
                            }
                        }

                        for (TypeInstructionInfo typeInstructionInfo : TYPE_INSTRUCTIONS) {
                            if (preEnumKilling != typeInstructionInfo.preEnumKill) {
                                continue;
                            }

                            if (enumCompatibility != typeInstructionInfo.enumCompatibility) {
                                continue;
                            }

                            desc = desc.replace(typeInstructionInfo.owner, typeInstructionInfo.newOwner);
                        }

                        super.visitFieldInsn(opcode, owner, name, desc);
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                        // SPIGOT-4496
                        if (owner.equals("org/bukkit/map/MapView") && name.equals("getId") && desc.equals("()S")) {
                            // Should be same size on stack so just call other method
                            super.visitMethodInsn(opcode, owner, name, "()I", itf);
                            return;
                        }
                        // SPIGOT-4608
                        if ((owner.equals("org/bukkit/Bukkit") || owner.equals("org/bukkit/Server")) && name.equals("getMap") && desc.equals("(S)Lorg/bukkit/map/MapView;")) {
                            // Should be same size on stack so just call other method
                            super.visitMethodInsn(opcode, owner, name, "(I)Lorg/bukkit/map/MapView;", itf);
                            return;
                        }
                        // SPIGOT-7335
                        if (owner.equals("org/bukkit/entity/TextDisplay$TextAligment")) {
                            super.visitMethodInsn(opcode, "org/bukkit/entity/TextDisplay$TextAlignment", name, desc, itf);
                            return;
                        }
                        if (desc.equals("(Lorg/bukkit/entity/TextDisplay$TextAligment;)V")) {
                            super.visitMethodInsn(opcode, owner, name, "(Lorg/bukkit/entity/TextDisplay$TextAlignment;)V", itf);
                            return;
                        }
                        if (desc.equals("()Lorg/bukkit/entity/TextDisplay$TextAligment;")) {
                            super.visitMethodInsn(opcode, owner, name, "()Lorg/bukkit/entity/TextDisplay$TextAlignment;", itf);
                            return;
                        }

                        // Enums to class
                        if ((owner.equals("org/bukkit/block/Biome")
                                || owner.equals("org/bukkit/Art")
                                || owner.equals("org/bukkit/Fluid")
                                || owner.equals("org/bukkit/entity/EntityType")
                                || owner.equals("org/bukkit/Statistic")
                                || owner.equals("org/bukkit/Sound")
                                || owner.equals("org/bukkit/Material")
                                || owner.equals("org/bukkit/attribute/Attribute")
                                || owner.equals("org/bukkit/entity/Villager$Type")
                                || owner.equals("org/bukkit/entity/Villager$Profession")
                                || owner.equals("org/bukkit/entity/Frog$Variant")
                                || owner.equals("org/bukkit/entity/Cat$Type")
                                || owner.equals("org/bukkit/block/banner/PatternType")
                                || owner.equals("org/bukkit/Particle")
                                || owner.equals("org/bukkit/potion/PotionType")) && name.equals("compareTo") && desc.equals("(Ljava/lang/Enum;)I")) {
                            super.visitMethodInsn(opcode, owner, name, "(Ljava/lang/Object;)I", itf);
                            return;
                        }

                        if (modern) {
                            if (owner.equals("org/bukkit/Material")) {
                                switch (name) {
                                    case "values":
                                        super.visitMethodInsn(opcode, "org/bukkit/craftbukkit/util/CraftLegacy", "modern_" + name, desc, itf);
                                        return;
                                    case "ordinal":
                                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/util/CraftLegacy", "modern_" + name, "(Lorg/bukkit/Material;)I", false);
                                        return;
                                }
                            }
                        } else {

                            if (owner.equals("org/bukkit/ChunkSnapshot") && name.equals("getBlockData") && desc.equals("(III)I")) {
                                super.visitMethodInsn(opcode, owner, "getData", desc, itf);
                                return;
                            }

                            Type retType = Type.getReturnType(desc);

                            if (EVIL.contains(owner + " " + desc + " " + name)
                                    || (owner.startsWith("org/bukkit/block/") && (desc + " " + name).equals("()I getTypeId"))
                                    || (owner.startsWith("org/bukkit/block/") && (desc + " " + name).equals("(I)Z setTypeId"))
                                    || (owner.startsWith("org/bukkit/block/") && (desc + " " + name).equals("()Lorg/bukkit/Material; getType"))) {
                                Type[] args = Type.getArgumentTypes(desc);
                                Type[] newArgs = new Type[args.length + 1];
                                newArgs[0] = Type.getObjectType(owner);
                                System.arraycopy(args, 0, newArgs, 1, args.length);

                                super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/legacy/CraftEvil", name, Type.getMethodDescriptor(retType, newArgs), false);
                                return;
                            }

                            if (owner.equals("org/bukkit/DyeColor")) {
                                if (name.equals("valueOf") && desc.equals("(Ljava/lang/String;)Lorg/bukkit/DyeColor;")) {
                                    super.visitMethodInsn(opcode, owner, "legacyValueOf", desc, itf);
                                    return;
                                }
                            }

                            if (owner.equals("org/bukkit/Material")) {
                                if (name.equals("getMaterial") && desc.equals("(I)Lorg/bukkit/Material;")) {
                                    super.visitMethodInsn(opcode, "org/bukkit/craftbukkit/legacy/CraftEvil", name, desc, itf);
                                    return;
                                }

                                switch (name) {
                                    case "values":
                                    case "valueOf":
                                    case "getMaterial":
                                    case "matchMaterial":
                                        super.visitMethodInsn(opcode, "org/bukkit/craftbukkit/legacy/CraftLegacy", name, desc, itf);
                                        return;
                                    case "ordinal":
                                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/legacy/CraftLegacy", "ordinal", "(Lorg/bukkit/Material;)I", false);
                                        return;
                                    case "name":
                                    case "toString":
                                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/legacy/CraftLegacy", name, "(Lorg/bukkit/Material;)Ljava/lang/String;", false);
                                        return;
                                }
                            }


                        }

                        if (preEnumKilling) {
                            if ((owner.startsWith("org/bukkit") && desc.contains("org/bukkit/Material")) || owner.equals("org/bukkit/Tag") || owner.equals("org/bukkit/entity/Piglin")) {
                                if (replaceMaterialMethod(owner, name, desc, (newName, newDesc) ->
                                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/legacy/EnumEvil", newName, newDesc, false))) {
                                    return;
                                }
                            }

                            if (owner.equals("org/bukkit/Bukkit") && name.equals("createBlockData") && desc.contains("org/bukkit/Material")) {
                                super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/legacy/EnumEvil", name, desc, false);
                                return;
                            }

                            if (owner.equals("org/bukkit/scoreboard/Criteria") && name.equals("statistic") && desc.contains("Material")) {
                                super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/legacy/EnumEvil", name, desc, false);
                                return;
                            }
                        }

                        for (InvocationInfo invocationInfo : INVOCATIONS) {
                            if (preEnumKilling != invocationInfo.preEnumKill) {
                                continue;
                            }

                            if (enumCompatibility != invocationInfo.enumCompatibility) {
                                continue;
                            }

                            if ((opcode == Opcodes.INVOKESPECIAL) != invocationInfo.fromSpecial) {
                                continue;
                            }

                            if (!invocationInfo.owner.test(owner)) {
                                continue;
                            }

                            if (!invocationInfo.name.test(name)) {
                                continue;
                            }

                            if (!invocationInfo.desc.test(desc)) {
                                continue;
                            }

                            if (invocationInfo.staticCall == null) {
                                opcode = opcode;
                            } else if (invocationInfo.staticCall) {
                                opcode = Opcodes.INVOKESTATIC;
                            } else if (invocationInfo.isInterface) {
                                opcode = Opcodes.INVOKEINTERFACE;
                            } else if (invocationInfo.toSpecial) {
                                opcode = Opcodes.INVOKESPECIAL;
                            } else {
                                opcode = Opcodes.INVOKEVIRTUAL;
                            }

                            owner = invocationInfo.newOwner.apply(owner);
                            name = invocationInfo.newName.apply(name);
                            desc = invocationInfo.newDesc.apply(desc);
                            itf = invocationInfo.isInterface == null ? itf : invocationInfo.isInterface;

                            break;
                        }

                        if (!name.equals("<init>")) {
                            for (MethodInfo methodInfo : METHOD_INFO) {
                                if (preEnumKilling != methodInfo.preEnumKill) {
                                    continue;
                                }

                                if (enumCompatibility != methodInfo.enumCompatibility) {
                                    continue;
                                }

                                if (!methodInfo.name.test(name)) {
                                    continue;
                                }

                                if (!methodInfo.desc.test(desc)) {
                                    continue;
                                }

                                name = methodInfo.newName.apply(name);

                                break;
                            }
                        }

                        for (TypeInstructionInfo typeInstructionInfo : TYPE_INSTRUCTIONS) {
                            if (preEnumKilling != typeInstructionInfo.preEnumKill) {
                                continue;
                            }

                            if (enumCompatibility != typeInstructionInfo.enumCompatibility) {
                                continue;
                            }

                            desc = desc.replace(typeInstructionInfo.owner, typeInstructionInfo.newOwner);
                        }

                        if (!modern) {
                            Type retType = Type.getReturnType(desc);
                            if (retType.getSort() == Type.OBJECT && retType.getInternalName().equals("org/bukkit/Material") && owner.startsWith("org/bukkit")) {
                                super.visitMethodInsn(opcode, owner, name, desc, itf);
                                super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/legacy/CraftLegacy", "toLegacy", "(Lorg/bukkit/Material;)Lorg/bukkit/Material;", false);
                                return;
                            }
                        }

                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                    }

                    @Override
                    public void visitLdcInsn(Object value) {
                        if (value instanceof String && ((String) value).equals("com.mysql.jdbc.Driver")) {
                            super.visitLdcInsn("com.mysql.cj.jdbc.Driver");
                            return;
                        }

                        super.visitLdcInsn(value);
                    }

                    @Override
                    public void visitTypeInsn(int opcode, String type) {
                        for (TypeInstructionInfo typeInstructionInfo : TYPE_INSTRUCTIONS) {
                            if (preEnumKilling != typeInstructionInfo.preEnumKill) {
                                continue;
                            }

                            if (enumCompatibility != typeInstructionInfo.enumCompatibility) {
                                continue;
                            }

                            type = type.replace(typeInstructionInfo.owner, typeInstructionInfo.newOwner);
                        }

                        super.visitTypeInsn(opcode, type);
                    }

                    @Override
                    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
                        for (TypeInstructionInfo typeInstructionInfo : TYPE_INSTRUCTIONS) {
                            if (preEnumKilling != typeInstructionInfo.preEnumKill) {
                                continue;
                            }

                            if (enumCompatibility != typeInstructionInfo.enumCompatibility) {
                                continue;
                            }

                            descriptor = descriptor == null ? null : descriptor.replace(typeInstructionInfo.owner, typeInstructionInfo.newOwner);
                            signature = signature == null ? null : signature.replace(typeInstructionInfo.owner, typeInstructionInfo.newOwner);
                        }
                        super.visitLocalVariable(name, descriptor, signature, start, end, index);
                    }

                    @Override
                    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {

                        if (bootstrapMethodHandle.getOwner().equals("java/lang/invoke/LambdaMetafactory")
                                && bootstrapMethodHandle.getName().equals("metafactory") && bootstrapMethodArguments.length == 3) {
                            Type samMethodType = (Type) bootstrapMethodArguments[0];
                            Handle implMethod = (Handle) bootstrapMethodArguments[1];
                            Type instantiatedMethodType = (Type) bootstrapMethodArguments[2];

                            List<Object> newTypes = new ArrayList<>();
                            newTypes.add(samMethodType);

                            if (preEnumKilling) {
                                if ((implMethod.getOwner().startsWith("org/bukkit") && implMethod.getDesc().contains("org/bukkit/Material")) || implMethod.getOwner().equals("org/bukkit/Tag") || implMethod.getOwner().equals("org/bukkit/entity/Piglin")) {

                                    Handle[] handle = new Handle[1];

                                    if (replaceMaterialMethod(implMethod.getOwner(), implMethod.getName(), implMethod.getDesc(), (newName, newDesc) ->
                                            handle[0] = new Handle(Opcodes.H_INVOKESTATIC, "org/bukkit/craftbukkit/legacy/EnumEvil", newName, newDesc, false))) {
                                        implMethod = handle[0];
                                    }
                                } else if (implMethod.getOwner().equals("org/bukkit/Bukkit") && implMethod.getName().equals("createBlockData")) {

                                    implMethod = new Handle(Opcodes.H_INVOKESTATIC, "org/bukkit/craftbukkit/legacy/EnumEvil", implMethod.getName(), implMethod.getDesc(), false);


                                } else if (implMethod.getOwner().equals("org/bukkit/scoreboard/Criteria") && implMethod.getName().equals("statistic") && implMethod.getDesc().contains("Material")) {

                                    implMethod = new Handle(Opcodes.H_INVOKESTATIC, "org/bukkit/craftbukkit/legacy/EnumEvil", implMethod.getName(), implMethod.getDesc(), false);
                                }
                            }

                            for (InvocationInfo invocationInfo : INVOCATIONS) {
                                if (preEnumKilling != invocationInfo.preEnumKill) {
                                    continue;
                                }

                                if (enumCompatibility != invocationInfo.enumCompatibility) {
                                    continue;
                                }

                                if ((implMethod.getTag() == Opcodes.H_INVOKESPECIAL) != invocationInfo.fromSpecial) {
                                    continue;
                                }

                                if (!invocationInfo.owner.test(implMethod.getOwner())) {
                                    continue;
                                }

                                if (!invocationInfo.name.test(implMethod.getName())) {
                                    continue;
                                }

                                if (!invocationInfo.desc.test(implMethod.getDesc())) {
                                    continue;
                                }

                                int opcode;
                                if (invocationInfo.staticCall == null) {
                                    opcode = implMethod.getTag();
                                } else if (invocationInfo.staticCall) {
                                    opcode = Opcodes.H_INVOKESTATIC;
                                } else if (invocationInfo.isInterface) {
                                    opcode = Opcodes.H_INVOKEINTERFACE;
                                } else if (invocationInfo.toSpecial) {
                                    opcode = Opcodes.H_INVOKESPECIAL;
                                } else {
                                    opcode = Opcodes.H_INVOKEVIRTUAL;
                                }

                                String newOwner = invocationInfo.newOwner.apply(implMethod.getOwner());
                                String newName = invocationInfo.newName.apply(implMethod.getName());
                                String newDesc = invocationInfo.newDesc.apply(implMethod.getDesc());

                                implMethod = new Handle(opcode, newOwner, newName, newDesc, invocationInfo.isInterface);

                                break;
                            }

                            for (TypeInstructionInfo typeInstructionInfo : TYPE_INSTRUCTIONS) {
                                if (preEnumKilling != typeInstructionInfo.preEnumKill) {
                                    continue;
                                }

                                if (enumCompatibility != typeInstructionInfo.enumCompatibility) {
                                    continue;
                                }

                                implMethod = new Handle(implMethod.getTag(), implMethod.getOwner(), implMethod.getName(), implMethod.getDesc().replace(typeInstructionInfo.owner, typeInstructionInfo.newOwner), implMethod.isInterface());
                                instantiatedMethodType = Type.getType(instantiatedMethodType.getDescriptor().replace(typeInstructionInfo.owner, typeInstructionInfo.newOwner));
                            }

                            newTypes.add(implMethod);
                            newTypes.add(instantiatedMethodType);

                            super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, newTypes.toArray());
                            return;
                        }

                        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
                    }

                    @Override
                    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
                        Object[] newLocal = new Object[local.length];
                        Object[] newStack = new Object[stack.length];

                        for (int i = 0; i < newLocal.length; i++) {
                            if (!(local[i] instanceof String value)) {
                                newLocal[i] = local[i];
                                continue;
                            }
                            for (TypeInstructionInfo typeInstructionInfo : TYPE_INSTRUCTIONS) {
                                if (preEnumKilling != typeInstructionInfo.preEnumKill) {
                                    continue;
                                }

                                if (enumCompatibility != typeInstructionInfo.enumCompatibility) {
                                    continue;
                                }
                                value = value.replace(typeInstructionInfo.owner, typeInstructionInfo.newOwner);
                            }

                            newLocal[i] = value;
                        }

                        for (int i = 0; i < newStack.length; i++) {
                            if (!(stack[i] instanceof String value)) {
                                newStack[i] = stack[i];
                                continue;
                            }
                            for (TypeInstructionInfo typeInstructionInfo : TYPE_INSTRUCTIONS) {
                                if (preEnumKilling != typeInstructionInfo.preEnumKill) {
                                    continue;
                                }

                                if (enumCompatibility != typeInstructionInfo.enumCompatibility) {
                                    continue;
                                }
                                value = value.replace(typeInstructionInfo.owner, typeInstructionInfo.newOwner);
                            }

                            newStack[i] = value;
                        }
                        super.visitFrame(type, numLocal, newLocal, numStack, newStack);
                    }
                };
            }
        }, 0);

        return cw.toByteArray();
    }

    /*
    This method looks (and probably is) overengineered, but it gives the most flexible when it comes to remapping normal methods to static one.
    The problem with normal owner and desc replacement is that child classes have them as an owner, instead there parents for there parents methods

    For example, if we have following two interfaces org.bukkit.BlockData and org.bukkit.Orientable extents BlockData
    and BlockData has the method org.bukkit.Material getType which we want to reroute to the static method
    org.bukkit.Material org.bukkit.craftbukkit.legacy.EnumEvil#getType(org.bukkit.BlockData)

    If we now call BlockData#getType we get as the owner org/bukkit/BlockData and as desc ()Lorg/bukkit/Material;
    Which we can nicely reroute by checking if the owner is BlockData and the name getType
    The problem, starts if we use Orientable#getType no we get as owner org/bukkit/Orientable and as desc ()Lorg/bukkit/Material;
    Now we can now longer safely say to which getType method we need to reroute (assume there are multiple getType methods from different classes,
    which are not related to BlockData), simple using the owner class will not work, since would reroute to
    EnumEvil#getType(org.bukkit.Orientable) which is not EnumEvil#getType(org.bukkit.BlockData) and will throw a method not found error
    at runtime.

    Meaning we would need to add checks for each subclass, which would be pur insanity.

    To solve this, we go through each super class and interfaces (and their super class and interfaces etc.) and try to get an owner
    which matches with one of our replacement methods. Based on how inheritance works in java, this method should be safe to use.

    As a site note: This method could also be used for the other method reroute, e.g. legacy method rerouting, where only the replacement
    method needs to be written, and this method figures out the rest, which could reduce the size and complexity of the Commodore class.
    The question then becomes one about performance (since this is not the most performance way) and convenience.
    But since it is only applied for each class and method call once when they get first loaded, it should not be that bad.
    (Although some load time testing could be done)
     */
    public static boolean replaceMaterialMethod(String owner, String name, String desc, BiConsumer<String, String> consumer) {
        Type[] args = Type.getArgumentTypes(desc);
        Type ownerType = Type.getObjectType(owner);
        Class<?> ownerClass;
        try {
            ownerClass = Class.forName(ownerType.getClassName());
        } catch (ClassNotFoundException e) {
            return false;
        }

        List<Class<?>> argClass = new ArrayList<>();
        for (Type arg : args) {
            try {
                argClass.add(Class.forName(arg.getClassName()));
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        Class<?>[] argClassArray = argClass.toArray(new Class<?>[0]);

        ClassTraverser it = new ClassTraverser(ownerClass);
        while (it.hasNext()) {
            Class<?> clazz = it.next();
            Class<?>[] newArgsClasses = new Class[argClassArray.length + 1];
            System.arraycopy(argClassArray, 0, newArgsClasses, 1, argClassArray.length);
            newArgsClasses[0] = clazz;
            try {
                EnumEvil.class.getMethod(name, newArgsClasses);
            } catch (NoSuchMethodException e) {
                continue;
            }

            Type retType = Type.getReturnType(desc);
            Type[] newArgs = new Type[args.length + 1];
            newArgs[0] = Type.getType(clazz);
            System.arraycopy(args, 0, newArgs, 1, args.length);
            consumer.accept(name, Type.getMethodDescriptor(retType, newArgs));
            return true;
        }

        return false;
    }

    public record InvocationInfo(boolean preEnumKill, boolean enumCompatibility, boolean fromSpecial, boolean toSpecial,
                                 Boolean staticCall, Boolean isInterface, Predicate<String> owner,
                                 Predicate<String> name, Predicate<String> desc, Function<String, String> newOwner,
                                 Function<String, String> newName, Function<String, String> newDesc) {
    }

    public record TypeInstructionInfo(boolean preEnumKill, boolean enumCompatibility, String owner, String newOwner) {
    }

    public record MethodInfo(boolean preEnumKill, boolean enumCompatibility, Predicate<String> name,
                             Predicate<String> desc, Function<String, String> newName) {

    }
}
