package me.ialistannen.mininbt;

import java.util.Map.Entry;
import java.util.Objects;
import me.ialistannen.mininbt.EntityMethodHelper.DeletableEntitySpawner;
import me.ialistannen.mininbt.NBTWrappers.INBTBase;
import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.reflection.BukkitReflection.ClassLookup;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentMethod;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentType;
import org.bukkit.Bukkit;
import org.bukkit.World;
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
public class EntityNBTUtil {

  private static FluentMethod loadFromNbtMethod, saveToNbtMethod, getHandle;

  static {
    getHandle = ClassLookup.OBC.forName("entity.CraftEntity")
        .getOrThrow()
        .findMethod()
        .withName("getHandle")
        .findSingle()
        .getOrThrow();

    EntityMethodHelper entityHelper = new EntityMethodHelper(
        new DeletableEntitySpawner() {

          private Entity sample;

          @Override
          public Object spawn() {
            if (Bukkit.getWorlds().isEmpty()) {
              throw new IllegalStateException("Called me before at least one world was loaded...");
            }
            World world = Bukkit.getWorlds().get(0);
            sample = world.spawnEntity(world.getSpawnLocation(), EntityType.ARMOR_STAND);

            return getHandle.invoke(sample).getOrThrow();
          }

          @Override
          public void remove() {
            sample.remove();
          }

          @Override
          public FluentType<?> getBaseClassForLoadAndSaveMethods() {
            return ClassLookup.NMS.forName("Entity").getOrThrow();
          }
        }
    );
    loadFromNbtMethod = entityHelper.getLoadFromNbtMethod();
    saveToNbtMethod = entityHelper.getSaveToNbtMethod();

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
}
