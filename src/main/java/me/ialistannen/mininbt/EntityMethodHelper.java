package me.ialistannen.mininbt;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import me.ialistannen.mininbt.NBTWrappers.INBTBase;
import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.reflection.BukkitReflection.ClassLookup;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentMethod;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentType;
import me.ialistannen.mininbt.reflection.ReflectionException;
import org.bukkit.Bukkit;

/**
 * Helps with fetching the entity load and save NBT methods.
 */
public class EntityMethodHelper {

  private final DeletableEntitySpawner entitySpawner;
  private FluentMethod loadFromNbtMethod, saveToNbtMethod;

  /**
   * Creates a new helper for a given entity.
   *
   * @param entitySpawner the entity spawner
   */
  public EntityMethodHelper(DeletableEntitySpawner entitySpawner) {
    this.entitySpawner = entitySpawner;

    initializeLoadingMethods();

    if (loadFromNbtMethod == null || saveToNbtMethod == null) {
      throw new ReflectionException(
          "Load or save method not found: L|" + loadFromNbtMethod + " -> S|" + saveToNbtMethod
      );
    }
  }

  public FluentMethod getLoadFromNbtMethod() {
    return loadFromNbtMethod;
  }

  public FluentMethod getSaveToNbtMethod() {
    return saveToNbtMethod;
  }

  private void initializeLoadingMethods() {
    if (Bukkit.getWorlds().isEmpty()) {
      throw new IllegalStateException("Called me before at least one world was loaded...");
    }

    Object nmsSample = entitySpawner.spawn();

    try {
      FluentType<?> entityClass = FluentType.ofUnknown(nmsSample.getClass());
      if (ReflectionUtil.getMajorVersion() > 1 || ReflectionUtil.getMinorVersion() > 8) {
        initializeHigherThan1_9(entityClass, nmsSample);
      } else {
        initializeLowerThan1_9(entityClass, nmsSample);
      }
    } finally {
      // kill it again, we are done with it
      entitySpawner.remove();
    }
  }

  private void initializeHigherThan1_9(FluentType<?> entityClass, Object nmsSample) {
    // load the loading method
    initializeLowerThan1_9(entityClass, nmsSample);

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

  private void initializeLowerThan1_9(FluentType<?> entityClass, Object nmsSample) {
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

  /**
   * Spawns and deletes an entity so its actual NMS class and methods can be inspected.
   */
  public interface DeletableEntitySpawner {

    /**
     * Spawns an entity and returns it.
     *
     * @return the spawned entity's <strong>NMS handle</strong>
     */
    Object spawn();

    /**
     * Removes the created entity and restores the old state.
     */
    void remove();
  }
}
