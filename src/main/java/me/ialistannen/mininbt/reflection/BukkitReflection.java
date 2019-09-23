package me.ialistannen.mininbt.reflection;

import static me.ialistannen.mininbt.reflection.UnsafeReflectiveAction.execute;

import java.util.function.Function;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentType;
import me.ialistannen.mininbt.reflection.FluentReflection.ReflectiveResult;
import org.bukkit.Bukkit;

/**
 * A reflection helper specially for Bukkit/Spigot
 */
public class BukkitReflection {

  private static final String SERVER_VERSION;

  // <editor-fold desc="INIT">
  // ==== INIT SERVER VERSION ====

  static {
    // included to allow tests to run. Will not be hit when deployed
    String name = Bukkit.getServer() == null
        ? "org.bukkit.craftbukkit.v1_14_R1"
        : Bukkit.getServer().getClass().getPackage().getName();
    String[] split = name.split("\\.");
    name = split[split.length - 1];

    SERVER_VERSION = name;
  }
  // </editor-fold>

  // <editor-fold desc="Version Validation Methods">
  // ==== VERSION VALIDATION METHODS ===

  /**
   * Returns the major version of the server
   *
   * @return The major version of the server
   */
  public static int getMajorVersion() {
    String name = Bukkit.getVersion();

    name = name.substring(name.indexOf("MC: ") + "MC: ".length());
    name = name.replace(")", "");

    return Integer.parseInt(name.split("\\.")[0]);
  }

  /**
   * Returns the minor version of the server
   *
   * @return The minor version of the server
   */
  public static int getMinorVersion() {
    String name = Bukkit.getVersion();

    name = name.substring(name.indexOf("MC: ") + "MC: ".length());
    name = name.replace(")", "");

    return Integer.parseInt(name.split("\\.")[1]);
  }

  /**
   * Returns the patch version of the server
   *
   * @return The patch version of the server
   */
  public static int getPatchVersion() {
    String name = Bukkit.getVersion();

    name = name.substring(name.indexOf("MC: ") + "MC: ".length());
    name = name.replace(")", "");

    String[] split = name.split("\\.");
    if (split.length < 3) {
      return 0;
    }
    return Integer.parseInt(split[2]);
  }
  // </editor-fold>

  /**
   * Looks up a class.
   */
  public static class ClassLookup {

    public static ClassLookup NMS = new ClassLookup(
        string -> "net.minecraft.server." + SERVER_VERSION + "." + string
    );
    public static ClassLookup OBC = new ClassLookup(
        string -> "org.bukkit.craftbukkit." + SERVER_VERSION + "." + string
    );

    private Function<String, String> nameResolver;

    private ClassLookup(Function<String, String> nameResolver) {
      this.nameResolver = nameResolver;
    }

    /**
     * Returns a class with the given mame.
     *
     * @param name the name of the class
     * @param <C> the type of the class
     * @return the class
     */
    public <C extends Class<C>> ReflectiveResult<FluentType<C>> forName(String name) {
      return execute(() -> {
        String resolvedName = nameResolver.apply(name);
        @SuppressWarnings("unchecked")
        C c = (C) Class.forName(resolvedName);
        return new FluentType<>(c);
      });
    }
  }
}
