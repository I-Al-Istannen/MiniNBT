package me.ialistannen.mininbt;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import me.ialistannen.mininbt.NBTWrappers.INBTBase;
import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.reflection.BukkitReflection.ClassLookup;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentMethod;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentType;
import me.ialistannen.mininbt.reflection.ReflectionException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * A utility to modify Entities NBT-tags. Uses reflection and scans through all methods to find the
 * right ones, so it might change in future releases.
 * <p>
 * The methods must only be called when at least one world is loaded, as it needs to spawn a sample
 * entity (ArmorStand). <br> It will be enforced by throwing an {@link IllegalStateException}.
 * <p>
 * <br>
 * <i><b>DISCLAIMER: </b></i> <br>
 * Doesn't allow for the addition of new tags. You can modify the tags of the TileEntity, but not
 * add new ones. This is a limitation of minecraft.
 */
@SuppressWarnings("unused") // just by me...
public class EntityNBTUtil {

  private static FluentMethod loadFromNbtMethod, saveToNbtMethod, getHandle;

  static {
    getHandle = ClassLookup.OBC.forName("entity.CraftEntity")
        .getOrThrow()
        .findMethod()
        .withName("getHandle")
        .findSingle()
        .getOrThrow();

    initializeLoadingMethods();

    if (loadFromNbtMethod == null || saveToNbtMethod == null) {
      throw new ReflectionException(
          "Load or save method not found: L|" + loadFromNbtMethod + " -> S|" + saveToNbtMethod
      );
    }
  }

  private static void initializeLoadingMethods() {
    if (Bukkit.getWorlds().isEmpty()) {
      throw new IllegalStateException("Called me before at least one world was loaded...");
    }
    Entity sample = Bukkit.getWorlds().get(0)
        .spawnEntity(Bukkit.getWorlds().get(0).getSpawnLocation(), EntityType.ARMOR_STAND);

    Object nmsSample = getHandle.invoke(sample).getOrThrow();

    try {
      FluentType<?> entityClass = ClassLookup.NMS.forName("Entity").getOrThrow();
      if (ReflectionUtil.getMajorVersion() > 1 || ReflectionUtil.getMinorVersion() > 8) {
        initializeHigherThan1_9(entityClass, sample, nmsSample);
      } else {
        initializeLowerThan1_9(entityClass, sample, nmsSample);
      }
    } finally {
      // kill it again, we are done with it
      sample.remove();
    }
  }

  /**
   * Gets the NMS handle of a bukkit entity
   *
   * @param entity The bukkit entity
   * @return The NMS entity
   */
  private static Object toNMSEntity(Entity entity) {
    return getHandle.invoke(entity).getOrThrow();
  }

  /**
   * Gets the NBT-Tag of an entity
   *
   * @param entity The entity to get the nbt tag for
   * @return The NBTTag of the entity
   * @throws NullPointerException if {@code entity} is null
   */
  public static NBTTagCompound getNbtTag(Entity entity) {
    Objects.requireNonNull(entity, "entity can not be null");

    Object nmsEntity = toNMSEntity(entity);
    NBTTagCompound entityNBT = new NBTTagCompound();

    Object nbtNMS = entityNBT.toNBT();
    saveToNbtMethod.invoke(nmsEntity, nbtNMS).ensureSuccessful();
    if (nbtNMS == null) {
      throw new NullPointerException(
          "SaveToNBT method set Nbt tag to null. Version incompatible?" + nmsEntity.getClass()
      );
    }
    entityNBT = (NBTTagCompound) INBTBase.fromNBT(nbtNMS);

    return entityNBT;
  }

  /**
   * Applies the {@link NBTTagCompound} tp the passed {@link Entity}
   *
   * @param entity The entity to modify the nbt tag
   * @param compound The {@link NBTTagCompound} to set it to
   * @throws NullPointerException if {@code entity} or {@code compound} is null
   * @throws IllegalStateException if a critical, non recoverable error occurred earlier
   *     (loading methods).
   */
  @SuppressWarnings("WeakerAccess") // util...
  public static void setNbtTag(Entity entity, NBTTagCompound compound) {
    Objects.requireNonNull(entity, "entity can not be null");
    Objects.requireNonNull(compound, "compound can not be null");

    Object nmsEntity = toNMSEntity(entity);

    loadFromNbtMethod.invoke(nmsEntity, compound.toNBT()).ensureSuccessful();
  }

  /**
   * Appends the {@link NBTTagCompound} to the entities NBT tag, overwriting already set values
   *
   * @param entity The entity whose NbtTag to change
   * @param compound The {@link NBTTagCompound} whose values you want to add
   * @throws NullPointerException if {@code entity} or {@code compound} is null
   * @throws IllegalStateException if a critical, non recoverable error occurred earlier
   *     (loading methods).
   */
  public static void appendNbtTag(Entity entity, NBTTagCompound compound) {
    // yes, getNbtTag would throw them as well.
    Objects.requireNonNull(entity, "entity can not be null");
    Objects.requireNonNull(compound, "compound can not be null");

    NBTTagCompound entityData = getNbtTag(entity);

    for (Entry<String, INBTBase> entry : compound.getAllEntries().entrySet()) {
      entityData.set(entry.getKey(), entry.getValue());
    }

    setNbtTag(entity, entityData);
  }

  private static void initializeHigherThan1_9(FluentType<?> entityClass, Entity sample,
      Object nmsSample) {
    // load the loading method
    initializeLowerThan1_9(entityClass, sample, nmsSample);

    Class<?> tagClass = ClassLookup.NMS.forName("NBTTagCompound").getOrThrow().getUnderlying();

    List<FluentMethod> possibleMethods = entityClass.findMethod()
        .withReturnType(tagClass)
        .withParameters(tagClass)
        .withModifiers(Modifier.PUBLIC)
        .withoutModifiers(Modifier.STATIC)
        .findAll()
        .orElse(Collections.emptyList());

    for (FluentMethod method : possibleMethods) {
      // the save method : "public NBTTagCompound(final NBTTagCompound compound)"
      Object testCompound = new NBTTagCompound().toNBT();
      method.invoke(nmsSample, testCompound);

      NBTTagCompound compound = (NBTTagCompound) INBTBase.fromNBT(testCompound);

      if (compound == null) {
        continue;
      }

      if (!compound.isEmpty()) {
        if (saveToNbtMethod != null) {
          throw new ReflectionException("Duplicated save method (post 1.9)");
        }
        saveToNbtMethod = method;
      }
    }
  }

  private static void initializeLowerThan1_9(FluentType<?> entityClass, Entity sample,
      Object nmsSample) {

    Class<?> tagClass = ClassLookup.NMS.forName("NBTTagCompound").getOrThrow().getUnderlying();

    List<FluentMethod> possibleMethods = entityClass.findMethod()
        .withReturnType(Void.TYPE)
        .withParameters(tagClass)
        .withModifiers(Modifier.PUBLIC)
        .withoutModifiers(Modifier.STATIC)
        .findAll()
        .orElse(Collections.emptyList());

    for (FluentMethod method : possibleMethods) {
      // the load method : "public void (final NBTTagCompound compound)"
      // the save method : "public void (final NBTTagCompound compound)"
      Object testCompound = new NBTTagCompound().toNBT();
      method.invoke(nmsSample, testCompound);

      NBTTagCompound compound = (NBTTagCompound) INBTBase.fromNBT(testCompound);
      if (compound == null) {
        continue;
      }

      if (compound.isEmpty()) {
        if (loadFromNbtMethod != null) {
          throw new ReflectionException("Duplicated candidate for loading!");
        }
        loadFromNbtMethod = method;
      } else {
        if (saveToNbtMethod != null) {
          throw new ReflectionException("Duplicated candidate for saving!");
        }
        saveToNbtMethod = method;
      }
    }
  }

}
