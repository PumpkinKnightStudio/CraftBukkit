package org.bukkit.craftbukkit.util;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.bukkit.Material;
import org.bukkit.plugin.AuthorNagException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * This file is imported from Commodore.
 *
 * @author md_5
 */
// CHECKSTYLE:OFF
public class Commodore
{

    private static final Set<String> EVIL = new HashSet<>( Arrays.asList(
            "org/bukkit/World (III)I getBlockTypeIdAt",
            "org/bukkit/World (Lorg/bukkit/Location;)I getBlockTypeIdAt",
            "org/bukkit/block/Block ()I getTypeId",
            "org/bukkit/block/Block (I)Z setTypeId",
            "org/bukkit/block/Block (IZ)Z setTypeId",
            "org/bukkit/block/Block (IBZ)Z setTypeIdAndData",
            "org/bukkit/block/Block (B)V setData",
            "org/bukkit/block/Block (BZ)V setData",
            "org/bukkit/inventory/ItemStack ()I getTypeId",
            "org/bukkit/inventory/ItemStack (I)V setTypeId"
    ) );

    public static void main(String[] args)
    {
        OptionParser parser = new OptionParser();
        OptionSpec<File> inputFlag = parser.acceptsAll( Arrays.asList( "i", "input" ) ).withRequiredArg().ofType( File.class ).required();
        OptionSpec<File> outputFlag = parser.acceptsAll( Arrays.asList( "o", "output" ) ).withRequiredArg().ofType( File.class ).required();

        OptionSet options = parser.parse( args );

        File input = options.valueOf( inputFlag );
        File output = options.valueOf( outputFlag );

        if ( input.isDirectory() )
        {
            if ( !output.isDirectory() )
            {
                System.err.println( "If input directory specified, output directory required too" );
                return;
            }

            for ( File in : input.listFiles() )
            {
                if ( in.getName().endsWith( ".jar" ) )
                {
                    convert( in, new File( output, in.getName() ) );
                }
            }
        } else
        {
            convert( input, output );
        }
    }

    private static void convert(File in, File out)
    {
        System.out.println( "Attempting to convert " + in + " to " + out );

        try
        {
            try ( JarFile inJar = new JarFile( in, false ) )
            {
                JarEntry entry = inJar.getJarEntry( ".commodore" );
                if ( entry != null )
                {
                    return;
                }

                try ( JarOutputStream outJar = new JarOutputStream( new FileOutputStream( out ) ) )
                {
                    for ( Enumeration<JarEntry> entries = inJar.entries(); entries.hasMoreElements(); )
                    {
                        entry = entries.nextElement();

                        try ( InputStream is = inJar.getInputStream( entry ) )
                        {
                            byte[] b = ByteStreams.toByteArray( is );

                            if ( entry.getName().endsWith( ".class" ) )
                            {
                                b = convert( b, false );
                                entry = new JarEntry( entry.getName() );
                            }

                            outJar.putNextEntry( entry );
                            outJar.write( b );
                        }
                    }

                    outJar.putNextEntry( new ZipEntry( ".commodore" ) );
                }
            }
        } catch ( Exception ex )
        {
            System.err.println( "Fatal error trying to convert " + in );
            ex.printStackTrace();
        }
    }

    public static byte[] convert(byte[] b, final boolean modern)
    {
        ClassReader cr = new ClassReader( b );
        ClassWriter cw = new ClassWriter( cr, 0 );

        cr.accept( new ClassVisitor( Opcodes.ASM9, cw )
        {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
            {
                return new MethodVisitor( api, super.visitMethod( access, name, desc, signature, exceptions ) )
                {

                    @Override
                    public void visitFieldInsn(int opcode, String owner, String name, String desc)
                    {
                        if ( owner.equals( "org/bukkit/block/Biome" ) )
                        {
                            switch ( name )
                            {
                                case "NETHER":
                                    super.visitFieldInsn( opcode, owner, "NETHER_WASTES", desc );
                                    return;
                            }
                        }

                        if ( owner.equals( "org/bukkit/entity/EntityType" ) )
                        {
                            switch ( name )
                            {
                                case "PIG_ZOMBIE":
                                    super.visitFieldInsn( opcode, owner, "ZOMBIFIED_PIGLIN", desc );
                                    return;
                                case "DROPPED_ITEM":
                                    super.visitFieldInsn( opcode, owner, "ITEM", desc );
                                    return;
                                case "LEASH_HITCH":
                                    super.visitFieldInsn( opcode, owner, "LEASH_KNOT", desc );
                                    return;
                                case "ENDER_SIGNAL":
                                    super.visitFieldInsn( opcode, owner, "EYE_OF_ENDER", desc );
                                    return;
                                case "SPLASH_POTION":
                                    super.visitFieldInsn( opcode, owner, "POTION", desc );
                                    return;
                                case "THROWN_EXP_BOTTLE":
                                    super.visitFieldInsn( opcode, owner, "EXPERIENCE_BOTTLE", desc );
                                    return;
                                case "PRIMED_TNT":
                                    super.visitFieldInsn( opcode, owner, "TNT", desc );
                                    return;
                                case "FIREWORK":
                                    super.visitFieldInsn( opcode, owner, "FIREWORK_ROCKET", desc );
                                    return;
                                case "MINECART_COMMAND":
                                    super.visitFieldInsn( opcode, owner, "COMMAND_BLOCK_MINECART", desc );
                                    return;
                                case "MINECART_CHEST":
                                    super.visitFieldInsn( opcode, owner, "CHEST_MINECART", desc );
                                    return;
                                case "MINECART_FURNACE":
                                    super.visitFieldInsn( opcode, owner, "FURNACE_MINECART", desc );
                                    return;
                                case "MINECART_TNT":
                                    super.visitFieldInsn( opcode, owner, "TNT_MINECART", desc );
                                    return;
                                case "MINECART_HOPPER":
                                    super.visitFieldInsn( opcode, owner, "HOPPER_MINECART", desc );
                                    return;
                                case "MINECART_MOB_SPAWNER":
                                    super.visitFieldInsn( opcode, owner, "SPAWNER_MINECART", desc );
                                    return;
                                case "MUSHROOM_COW":
                                    super.visitFieldInsn( opcode, owner, "MOOSHROOM", desc );
                                    return;
                                case "SNOWMAN":
                                    super.visitFieldInsn( opcode, owner, "SNOW_GOLEM", desc );
                                    return;
                                case "ENDER_CRYSTAL":
                                    super.visitFieldInsn( opcode, owner, "END_CRYSTAL", desc );
                                    return;
                                case "FISHING_HOOK":
                                    super.visitFieldInsn( opcode, owner, "FISHING_BOBBER", desc );
                                    return;
                                case "LIGHTNING":
                                    super.visitFieldInsn( opcode, owner, "LIGHTNING_BOLT", desc );
                                    return;
                            }
                        }

                        if ( owner.equals( "org/bukkit/loot/LootTables" ) )
                        {
                            switch ( name )
                            {
                                case "ZOMBIE_PIGMAN":
                                    super.visitFieldInsn( opcode, owner, "ZOMBIFIED_PIGLIN", desc );
                                    return;
                            }
                        }

                        if ( owner.equals( "org/bukkit/potion/PotionEffectType" ) )
                        {
                            switch ( name )
                            {
                                case "SLOW":
                                    super.visitFieldInsn( opcode, owner, "SLOWNESS", desc );
                                    return;
                                case "FAST_DIGGING":
                                    super.visitFieldInsn( opcode, owner, "HASTE", desc );
                                    return;
                                case "SLOW_DIGGING":
                                    super.visitFieldInsn( opcode, owner, "MINING_FATIGUE", desc );
                                    return;
                                case "INCREASE_DAMAGE":
                                    super.visitFieldInsn( opcode, owner, "STRENGTH", desc );
                                    return;
                                case "HEAL":
                                    super.visitFieldInsn( opcode, owner, "INSTANT_HEALTH", desc );
                                    return;
                                case "HARM":
                                    super.visitFieldInsn( opcode, owner, "INSTANT_DAMAGE", desc );
                                    return;
                                case "JUMP":
                                    super.visitFieldInsn( opcode, owner, "JUMP_BOOST", desc );
                                    return;
                                case "CONFUSION":
                                    super.visitFieldInsn( opcode, owner, "NAUSEA", desc );
                                    return;
                                case "DAMAGE_RESISTANCE":
                                    super.visitFieldInsn( opcode, owner, "RESISTANCE", desc );
                                    return;
                            }
                        }

                        if ( owner.equals( "org/bukkit/enchantments/Enchantment" ) )
                        {
                            switch ( name )
                            {
                                case "PROTECTION_ENVIRONMENTAL":
                                    super.visitFieldInsn( opcode, owner, "PROTECTION", desc );
                                    return;
                                case "PROTECTION_FIRE":
                                    super.visitFieldInsn( opcode, owner, "FIRE_PROTECTION", desc );
                                    return;
                                case "PROTECTION_FALL":
                                    super.visitFieldInsn( opcode, owner, "FEATHER_FALLING", desc );
                                    return;
                                case "PROTECTION_EXPLOSIONS":
                                    super.visitFieldInsn( opcode, owner, "BLAST_PROTECTION", desc );
                                    return;
                                case "PROTECTION_PROJECTILE":
                                    super.visitFieldInsn( opcode, owner, "PROJECTILE_PROTECTION", desc );
                                    return;
                                case "OXYGEN":
                                    super.visitFieldInsn( opcode, owner, "RESPIRATION", desc );
                                    return;
                                case "WATER_WORKER":
                                    super.visitFieldInsn( opcode, owner, "AQUA_AFFINITY", desc );
                                    return;
                                case "DAMAGE_ALL":
                                    super.visitFieldInsn( opcode, owner, "SHARPNESS", desc );
                                    return;
                                case "DAMAGE_UNDEAD":
                                    super.visitFieldInsn( opcode, owner, "SMITE", desc );
                                    return;
                                case "DAMAGE_ARTHROPODS":
                                    super.visitFieldInsn( opcode, owner, "BANE_OF_ARTHROPODS", desc );
                                    return;
                                case "LOOT_BONUS_MOBS":
                                    super.visitFieldInsn( opcode, owner, "LOOTING", desc );
                                    return;
                                case "SWEEPING_EDGE":
                                    super.visitFieldInsn( opcode, owner, "SWEEPING", desc );
                                    return;
                                case "DIG_SPEED":
                                    super.visitFieldInsn( opcode, owner, "EFFICIENCY", desc );
                                    return;
                                case "DURABILITY":
                                    super.visitFieldInsn( opcode, owner, "UNBREAKING", desc );
                                    return;
                                case "LOOT_BONUS_BLOCKS":
                                    super.visitFieldInsn( opcode, owner, "FORTUNE", desc );
                                    return;
                                case "ARROW_DAMAGE":
                                    super.visitFieldInsn( opcode, owner, "POWER", desc );
                                    return;
                                case "ARROW_KNOCKBACK":
                                    super.visitFieldInsn( opcode, owner, "PUNCH", desc );
                                    return;
                                case "ARROW_FIRE":
                                    super.visitFieldInsn( opcode, owner, "FLAME", desc );
                                    return;
                                case "ARROW_INFINITE":
                                    super.visitFieldInsn( opcode, owner, "INFINITY", desc );
                                    return;
                                case "LUCK":
                                    super.visitFieldInsn( opcode, owner, "LUCK_OF_THE_SEA", desc );
                                    return;
                            }
                        }

                        if ( modern )
                        {
                            if ( owner.equals( "org/bukkit/Material" ) )
                            {
                                switch ( name )
                                {
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

                            super.visitFieldInsn( opcode, owner, name, desc );
                            return;
                        }

                        if ( owner.equals( "org/bukkit/Material" ) )
                        {
                            try
                            {
                                Material.valueOf( "LEGACY_" + name );
                            } catch ( IllegalArgumentException ex )
                            {
                                throw new AuthorNagException( "No legacy enum constant for " + name + ". Did you forget to define a modern (1.13+) api-version in your plugin.yml?" );
                            }

                            super.visitFieldInsn( opcode, owner, "LEGACY_" + name, desc );
                            return;
                        }

                        if ( owner.equals( "org/bukkit/Art" ) )
                        {
                            switch ( name )
                            {
                                case "BURNINGSKULL":
                                    super.visitFieldInsn( opcode, owner, "BURNING_SKULL", desc );
                                    return;
                                case "DONKEYKONG":
                                    super.visitFieldInsn( opcode, owner, "DONKEY_KONG", desc );
                                    return;
                            }
                        }

                        if ( owner.equals( "org/bukkit/DyeColor" ) )
                        {
                            switch ( name )
                            {
                                case "SILVER":
                                    super.visitFieldInsn( opcode, owner, "LIGHT_GRAY", desc );
                                    return;
                            }
                        }

                        if ( owner.equals( "org/bukkit/Particle" ) )
                        {
                            switch ( name )
                            {
                                case "BLOCK_CRACK":
                                case "BLOCK_DUST":
                                case "FALLING_DUST":
                                    super.visitFieldInsn( opcode, owner, "LEGACY_" + name, desc );
                                    return;
                            }
                        }

                        super.visitFieldInsn( opcode, owner, name, desc );
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
                    {
                        // SPIGOT-4496
                        if ( owner.equals( "org/bukkit/map/MapView" ) && name.equals( "getId" ) && desc.equals( "()S" ) )
                        {
                            // Should be same size on stack so just call other method
                            super.visitMethodInsn( opcode, owner, name, "()I", itf );
                            return;
                        }
                        // SPIGOT-4608
                        if ( (owner.equals( "org/bukkit/Bukkit" ) || owner.equals( "org/bukkit/Server" ) ) && name.equals( "getMap" ) && desc.equals( "(S)Lorg/bukkit/map/MapView;" ) )
                        {
                            // Should be same size on stack so just call other method
                            super.visitMethodInsn( opcode, owner, name, "(I)Lorg/bukkit/map/MapView;", itf );
                            return;
                        }

                        // Enums to class
                        if ( (owner.equals( "org/bukkit/block/Biome" )
                                || owner.equals( "org/bukkit/Art" )
                                || owner.equals( "org/bukkit/Fluid" )
                                || owner.equals( "org/bukkit/entity/EntityType" )
                                || owner.equals( "org/bukkit/Sound" )
                                || owner.equals( "org/bukkit/attribute/Attribute" )
                                || owner.equals( "org/bukkit/entity/Villager$Type" )
                                || owner.equals( "org/bukkit/entity/Villager$Profession" ) ) && name.equals( "compareTo" ) && desc.equals( "(Ljava/lang/Enum;)I" ) )
                        {
                            super.visitMethodInsn( opcode, owner, name, "(Ljava/lang/Object;)I", itf );
                            return;
                        }

                        if ( modern )
                        {
                            if ( owner.equals( "org/bukkit/Material" ) )
                            {
                                switch ( name )
                                {
                                    case "values":
                                        super.visitMethodInsn( opcode, "org/bukkit/craftbukkit/util/CraftLegacy", "modern_" + name, desc, itf );
                                        return;
                                    case "ordinal":
                                        super.visitMethodInsn( Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/util/CraftLegacy", "modern_" + name, "(Lorg/bukkit/Material;)I", false );
                                        return;
                                }
                            }

                            super.visitMethodInsn( opcode, owner, name, desc, itf );
                            return;
                        }

                        if ( owner.equals( "org/bukkit/ChunkSnapshot" ) && name.equals( "getBlockData" ) && desc.equals( "(III)I" ) )
                        {
                            super.visitMethodInsn( opcode, owner, "getData", desc, itf );
                            return;
                        }

                        Type retType = Type.getReturnType( desc );

                        if ( EVIL.contains( owner + " " + desc + " " + name )
                                || ( owner.startsWith( "org/bukkit/block/" ) && ( desc + " " + name ).equals( "()I getTypeId" ) )
                                || ( owner.startsWith( "org/bukkit/block/" ) && ( desc + " " + name ).equals( "(I)Z setTypeId" ) )
                                || ( owner.startsWith( "org/bukkit/block/" ) && ( desc + " " + name ).equals( "()Lorg/bukkit/Material; getType" ) ) )
                        {
                            Type[] args = Type.getArgumentTypes( desc );
                            Type[] newArgs = new Type[ args.length + 1 ];
                            newArgs[0] = Type.getObjectType( owner );
                            System.arraycopy( args, 0, newArgs, 1, args.length );

                            super.visitMethodInsn( Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/legacy/CraftEvil", name, Type.getMethodDescriptor( retType, newArgs ), false );
                            return;
                        }

                        if ( owner.equals( "org/bukkit/DyeColor" ) )
                        {
                            if ( name.equals( "valueOf" ) && desc.equals( "(Ljava/lang/String;)Lorg/bukkit/DyeColor;" ) )
                            {
                                super.visitMethodInsn( opcode, owner, "legacyValueOf", desc, itf );
                                return;
                            }
                        }

                        if ( owner.equals( "org/bukkit/Material" ) )
                        {
                            if ( name.equals( "getMaterial" ) && desc.equals( "(I)Lorg/bukkit/Material;" ) )
                            {
                                super.visitMethodInsn( opcode, "org/bukkit/craftbukkit/legacy/CraftEvil", name, desc, itf );
                                return;
                            }

                            switch ( name )
                            {
                                case "values":
                                case "valueOf":
                                case "getMaterial":
                                case "matchMaterial":
                                    super.visitMethodInsn( opcode, "org/bukkit/craftbukkit/legacy/CraftLegacy", name, desc, itf );
                                    return;
                                case "ordinal":
                                    super.visitMethodInsn( Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/legacy/CraftLegacy", "ordinal", "(Lorg/bukkit/Material;)I", false );
                                    return;
                                case "name":
                                case "toString":
                                    super.visitMethodInsn( Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/legacy/CraftLegacy", name, "(Lorg/bukkit/Material;)Ljava/lang/String;", false );
                                    return;
                            }
                        }

                        if ( retType.getSort() == Type.OBJECT && retType.getInternalName().equals( "org/bukkit/Material" ) && owner.startsWith( "org/bukkit" ) )
                        {
                            super.visitMethodInsn( opcode, owner, name, desc, itf );
                            super.visitMethodInsn( Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/legacy/CraftLegacy", "toLegacy", "(Lorg/bukkit/Material;)Lorg/bukkit/Material;", false );
                            return;
                        }

                        super.visitMethodInsn( opcode, owner, name, desc, itf );
                    }
                };
            }
        }, 0 );

        return cw.toByteArray();
    }
}
