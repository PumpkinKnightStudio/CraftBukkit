package org.bukkit.craftbukkit;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.PathConverter;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;

public class Main {

    private static final int UPDATE_THRESHOLD = 5;

    public static boolean useJline = true;
    public static boolean useConsole = true;

    public static void main(String[] args) {
        // Todo: Installation script
        OptionParser parser = new OptionParser() {
            {
                acceptsAll(asList("?", "help"), "Show the help");

                acceptsAll(asList("c", "config"), "Properties file to use")
                        .withRequiredArg()
                        .ofType(File.class)
                        .defaultsTo(new File("server.properties"))
                        .describedAs("Properties file");

                acceptsAll(asList("P", "plugins"), "Plugin directory to use")
                        .withRequiredArg()
                        .ofType(File.class)
                        .defaultsTo(new File("plugins"))
                        .describedAs("Plugin directory");

                acceptsAll(asList("h", "host", "server-ip"), "Host to listen on")
                        .withRequiredArg()
                        .ofType(String.class)
                        .describedAs("Hostname or IP");

                acceptsAll(asList("W", "world-dir", "universe", "world-container"), "World container")
                        .withRequiredArg()
                        .ofType(File.class)
                        .defaultsTo(new File("."))
                        .describedAs("Directory containing worlds");

                acceptsAll(asList("w", "world", "level-name"), "World name")
                        .withRequiredArg()
                        .ofType(String.class)
                        .describedAs("World name");

                acceptsAll(asList("p", "port", "server-port"), "Port to listen on")
                        .withRequiredArg()
                        .ofType(Integer.class)
                        .describedAs("Port");

                accepts("serverId", "Server ID")
                        .withRequiredArg();

                accepts("jfrProfile", "Enable JFR profiling");

                accepts("pidFile", "pid File")
                        .withRequiredArg()
                        .withValuesConvertedBy(new PathConverter());

                acceptsAll(asList("o", "online-mode"), "Whether to use online authentication")
                        .withRequiredArg()
                        .ofType(Boolean.class)
                        .describedAs("Authentication");

                acceptsAll(asList("s", "size", "max-players"), "Maximum amount of players")
                        .withRequiredArg()
                        .ofType(Integer.class)
                        .describedAs("Server size");

                acceptsAll(asList("d", "date-format"), "Format of the date to display in the console (for log entries)")
                        .withRequiredArg()
                        .ofType(SimpleDateFormat.class)
                        .describedAs("Log date format");

                acceptsAll(asList("log-pattern"), "Specfies the log filename pattern")
                        .withRequiredArg()
                        .ofType(String.class)
                        .defaultsTo("server.log")
                        .describedAs("Log filename");

                acceptsAll(asList("log-limit"), "Limits the maximum size of the log file (0 = unlimited)")
                        .withRequiredArg()
                        .ofType(Integer.class)
                        .defaultsTo(0)
                        .describedAs("Max log size");

                acceptsAll(asList("log-count"), "Specified how many log files to cycle through")
                        .withRequiredArg()
                        .ofType(Integer.class)
                        .defaultsTo(1)
                        .describedAs("Log count");

                acceptsAll(asList("log-append"), "Whether to append to the log file")
                        .withRequiredArg()
                        .ofType(Boolean.class)
                        .defaultsTo(true)
                        .describedAs("Log append");

                acceptsAll(asList("log-strip-color"), "Strips color codes from log file");

                acceptsAll(asList("b", "bukkit-settings"), "File for bukkit settings")
                        .withRequiredArg()
                        .ofType(File.class)
                        .defaultsTo(new File("bukkit.yml"))
                        .describedAs("Yml file");

                acceptsAll(asList("C", "commands-settings"), "File for command settings")
                        .withRequiredArg()
                        .ofType(File.class)
                        .defaultsTo(new File("commands.yml"))
                        .describedAs("Yml file");

                acceptsAll(asList("forceUpgrade"), "Whether to force a world upgrade");
                acceptsAll(asList("eraseCache"), "Whether to force cache erase during world upgrade");
                acceptsAll(asList("nogui"), "Disables the graphical console");

                acceptsAll(asList("nojline"), "Disables jline and emulates the vanilla console");

                acceptsAll(asList("noconsole"), "Disables the console");

                acceptsAll(asList("v", "version"), "Show the CraftBukkit Version");

                acceptsAll(asList("demo"), "Demo mode");

                acceptsAll(asList("initSettings"), "Only create configuration files and then exit"); // SPIGOT-5761: Add initSettings option
            }
        };

        OptionSet options = null;

        try {
            options = parser.parse(args);
        } catch (joptsimple.OptionException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
        }

        if ((options == null) || (options.has("?"))) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (options.has("v")) {
            System.out.println(CraftServer.class.getPackage().getImplementationVersion());
        } else {
            // Do you love Java using + and ! as string based identifiers? I sure do!
            String path = new File(".").getAbsolutePath();
            if (path.contains("!") || path.contains("+")) {
                System.err.println("Cannot run server in a directory with ! or + in the pathname. Please rename the affected folders and try again.");
                return;
            }

            float javaVersion = Float.parseFloat(System.getProperty("java.class.version"));
            if (javaVersion < 61.0) {
                System.err.println("Unsupported Java detected (" + javaVersion + "). This version of Minecraft requires at least Java 17. Check your Java version with the command 'java -version'.");
                return;
            }
            if (javaVersion > 66.0) {
                System.err.println("Unsupported Java detected (" + javaVersion + "). Only up to Java 22 is supported.");
                return;
            }
            String javaVersionName = System.getProperty("java.version");
            // J2SE SDK/JRE Version String Naming Convention
            boolean isPreRelease = javaVersionName.contains("-");
            if (isPreRelease && javaVersion == 61.0) {
                System.err.println("Unsupported Java detected (" + javaVersionName + "). You are running an outdated, pre-release version. Only general availability versions of Java are supported. Please update your Java version.");
                return;
            }

            try {
                // This trick bypasses Maven Shade's clever rewriting of our getProperty call when using String literals
                String jline_UnsupportedTerminal = new String(new char[]{'j', 'l', 'i', 'n', 'e', '.', 'U', 'n', 's', 'u', 'p', 'p', 'o', 'r', 't', 'e', 'd', 'T', 'e', 'r', 'm', 'i', 'n', 'a', 'l'});
                String jline_terminal = new String(new char[]{'j', 'l', 'i', 'n', 'e', '.', 't', 'e', 'r', 'm', 'i', 'n', 'a', 'l'});

                useJline = !(jline_UnsupportedTerminal).equals(System.getProperty(jline_terminal));

                if (options.has("nojline")) {
                    System.setProperty("user.language", "en");
                    useJline = false;
                }

                if (useJline) {
                    AnsiConsole.systemInstall();
                } else {
                    // This ensures the terminal literal will always match the jline implementation
                    System.setProperty(jline.TerminalFactory.JLINE_TERMINAL, jline.UnsupportedTerminal.class.getName());
                }

                if (options.has("noconsole")) {
                    useConsole = false;
                }

                if (Main.class.getPackage().getImplementationVendor() != null && System.getProperty("IReallyKnowWhatIAmDoingISwear") == null) {
                    checkForUpdates();
                }

                System.out.println("Loading libraries, please wait...");
                net.minecraft.server.Main.main(options);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private static List<String> asList(String... params) {
        return Arrays.asList(params);
    }

    public static void checkForUpdates() throws InterruptedException {
        int behind = 0;
        String version = Main.class.getPackage().getImplementationVersion();
        System.out.println(version);
        if (version == null) version = "Custom";
        String[] parts;
        if (version.contains(" ")) {
            parts = version.substring(0, version.indexOf(' ')).split("-");
        } else {
            parts = version.split("-");
        }
        // Spigot version format.
        if (parts.length == 4) {
            int cbVersions = getDistance("craftbukkit", parts[3]);
            int spigotVersions = getDistance("spigot", parts[2]);
            if (cbVersions == -1 || spigotVersions == -1) {
                System.out.println("Error obtaining version information");
                return;
            }

            if (cbVersions != 0 || spigotVersions != 0) {
                behind = cbVersions + spigotVersions;
            }
        }

        // Bukkit / CraftBukkit version format.
        if (parts.length == 3) {
            int cbVersions = getDistance("craftbukkit", parts[2]);
            if (cbVersions == -1) {
                System.out.println("Error obtaining version information");
                return;
            }

            behind = cbVersions;
        } else {
            System.out.println("Unknown version, custom build?");
        }

        if (behind > 0) {
            System.err.println("*** Error, this build is outdated! You are " + behind + " version(s) behind ***");
            System.err.println("*** Please download a new build as per instructions from https://www.spigotmc.org/go/outdated-spigot ***");

            if (behind > UPDATE_THRESHOLD) {
                System.err.println("*** Server will start in 20 seconds ***");
                Thread.sleep(TimeUnit.SECONDS.toMillis(20));
            } else if (behind > 0) {
                System.err.println("*** Server will start in 10 seconds ***");
                Thread.sleep(TimeUnit.SECONDS.toMillis(10));
            }
        }
    }

    public static int getDistance(@NotNull String repo, @NotNull String hash) {
        try {
            BufferedReader reader = Resources.asCharSource(
                new URL("https://hub.spigotmc.org/stash/rest/api/1.0/projects/SPIGOT/repos/" + repo + "/commits?since=" + URLEncoder.encode(hash, StandardCharsets.UTF_8) + "&withCounts=true"),
                Charsets.UTF_8
            ).openBufferedStream();
            try {
                JsonObject obj = new Gson().fromJson(reader, JsonObject.class);
                return obj.get("totalCount").getAsInt();
            } catch (JsonSyntaxException ex) {
                ex.printStackTrace();
                return -1;
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
