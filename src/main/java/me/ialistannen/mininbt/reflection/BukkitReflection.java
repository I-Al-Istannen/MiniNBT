package me.ialistannen.mininbt.reflection;

import static me.ialistannen.mininbt.reflection.UnsafeReflectiveAction.execute;

import java.util.function.Function;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentType;
import me.ialistannen.mininbt.reflection.FluentReflection.ReflectiveResult;
import org.bukkit.Bukkit;

public class BukkitReflection {

  private static final String SERVER_VERSION;

  // <editor-fold desc="INIT">
  // ==== INIT SERVER VERSION ====

  static {
    String name = Bukkit.getServer() == null
        ? "org.bukkit.craftbukkit.v1_14_R1"
        : Bukkit.getServer().getClass().getPackage().getName();
    String[] split = name.split("\\.");
    name = split[split.length - 1];

    SERVER_VERSION = name;
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
