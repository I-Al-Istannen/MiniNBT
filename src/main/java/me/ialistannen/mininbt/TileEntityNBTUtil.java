package me.ialistannen.mininbt;

import java.util.Map.Entry;
import java.util.Objects;
import me.ialistannen.mininbt.EntityMethodHelper.DeletableEntitySpawner;
import me.ialistannen.mininbt.NBTWrappers.INBTBase;
import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.reflection.BukkitReflection.ClassLookup;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentMethod;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentType;
import me.ialistannen.mininbt.reflection.FluentReflection.ReflectiveResult;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

/**
 * Allows modification of TileEntity data.
 * <p>
 * The methods must only be called when at least one world is loaded, as it needs to spawn a sample
 * entity (ArmorStand). <br> It will be enforced by throwing an {@link IllegalStateException}.
 * <p>
 * <br>
 * <i><b>DISCLAIMER: </b></i> <br>
 * Doesn't allow for the addition of new tags. You can modify the tags of the TileEntity, but not
 * add new ones. This is a limitation of minecraft.
 *
 * <p><br><em>All methods in this class may throw a
 * {@link me.ialistannen.mininbt.reflection.ReflectionException}</em></p>
 */
public class TileEntityNBTUtil {

  private static FluentMethod loadFromNBT, saveToNBT, getTileEntity;

  private static Class<?> CRAFT_BLOCK_STATE_CLASS;

  static {
    CRAFT_BLOCK_STATE_CLASS = ClassLookup.OBC.forName("block.CraftBlockState").getOrThrow()
        .getUnderlying();

    FluentType<?> TILE_CRAFT_BLOCK_STATE_CLASS = ClassLookup.OBC.forName("block.CraftBlockState")
        .getOrThrow();

    ReflectiveResult<FluentMethod> tileEntityOld = TILE_CRAFT_BLOCK_STATE_CLASS.findMethod()
        .withName("getTileEntity")
        .withParameters()
        .findSingle();
    if (tileEntityOld.isPresent()) {
      getTileEntity = tileEntityOld.getOrThrow();
    } else {
      TILE_CRAFT_BLOCK_STATE_CLASS = ClassLookup.OBC.forName("block.CraftBlockEntityState")
          .getOrThrow();

      // Modify the snapshot so BlockState#update does the physics and update work for us!
      getTileEntity = TILE_CRAFT_BLOCK_STATE_CLASS.findMethod()
          .withName("getSnapshot")
          .withParameters()
          .findSingle().getOrThrow();
    }

    EntityMethodHelper entityHelper = new EntityMethodHelper(
        new DeletableEntitySpawner() {

          private BlockState oldState;
          private FluentType<?> baseClass;

          @Override
          public Object spawn() {
            if (Bukkit.getWorlds().isEmpty()) {
              throw new IllegalStateException("Called me before at least one world was loaded...");
            }
            World world = Bukkit.getWorlds().get(0);
            Block block = world.getBlockAt(world.getSpawnLocation());

            // Save old state to later restore it
            oldState = block.getState();

            block.setType(Material.CHEST);

            BlockState chestState = block.getState();

            Object nmsSample = getTileEntity.invoke(chestState).getOrThrow();
            baseClass = FluentType.ofUnknown(nmsSample.getClass());
            return nmsSample;
          }

          @Override
          public void remove() {
            oldState.update(true);
          }

          @Override
          public FluentType<?> getBaseClassForLoadAndSaveMethods() {
            return baseClass;
          }
        }
    );

    loadFromNBT = entityHelper.getLoadFromNbtMethod();
    saveToNBT = entityHelper.getSaveToNbtMethod();
  }

  /**
   * Retrieves the NMS handle of a bukkit entity.
   *
   * @param blockState the Bukkit {@link BlockState}
   * @return the NMS tile entity
   */
  private static Object toTileEntity(BlockState blockState) {
    return getTileEntity.invoke(blockState).getOrThrow();
  }

  /**
   * Returns the {@link NBTTagCompound} of a {@link BlockState}.
   *
   * @param blockState the Bukkit {@link BlockState} to get an {@link NBTTagCompound} for.
   * @return the {@link NBTTagCompound} of the {@link BlockState}
   */
  public static NBTTagCompound getNbtTag(BlockState blockState) {
    Objects.requireNonNull(blockState, "blockState can not be null");
    ensureCorrectClass(blockState);

    Object tileEntity = toTileEntity(blockState);

    Object nbtTag = new NBTTagCompound().toNBT();

    // populate it
    saveToNBT.invoke(tileEntity, nbtTag).ensureSuccessful();

    return (NBTTagCompound) INBTBase.fromNBT(nbtTag);
  }

  /**
   * Sets the {@link NBTTagCompound} of a {@link BlockState}. And applies the changes by updating
   * the blockstate.
   *
   * @param blockState the Bukkit {@link BlockState} to get an {@link NBTTagCompound} for
   * @param compound the {@link NBTTagCompound} to set it to
   * @throws NullPointerException if blockState or compound is null
   * @throws IllegalArgumentException if {@link #isValidClass(BlockState)} returns false
   */
  public static void setNbtTag(BlockState blockState, NBTTagCompound compound) {
    Objects.requireNonNull(blockState, "blockState can not be null");
    Objects.requireNonNull(compound, "compound can not be null");
    ensureCorrectClass(blockState);

    Object tileEntity = toTileEntity(blockState);

    // store it
    loadFromNBT.invoke(tileEntity, compound.toNBT()).ensureSuccessful();

    blockState.update();
  }

  /**
   * Appends the {@link NBTTagCompound} to the Nbt tag of a {@link BlockState}. And applies the
   * changes by updating the blockstate.
   *
   * @param blockState the Bukkit {@link BlockState} to get an {@link NBTTagCompound} for
   * @param compound the {@link NBTTagCompound} to append
   * @throws NullPointerException if blockState or compound is null
   * @throws IllegalArgumentException if {@link #isValidClass(BlockState)} returns false
   */
  public static void appendNbtTag(BlockState blockState, NBTTagCompound compound) {
    Objects.requireNonNull(blockState, "blockState can not be null");
    Objects.requireNonNull(compound, "compound can not be null");
    ensureCorrectClass(blockState);

    NBTTagCompound tileNBT = getNbtTag(blockState);
    for (Entry<String, INBTBase> entry : compound.getAllEntries().entrySet()) {
      tileNBT.set(entry.getKey(), entry.getValue());
    }

    setNbtTag(blockState, tileNBT);
  }

  /**
   * Checks whether you can pass the {@link BlockState} to the {@link #setNbtTag(BlockState,
   * NBTTagCompound)} or {@link #getNbtTag(BlockState)} methods.
   *
   * <p>This is needed as not all BlockStates are created equal - some are just plain blocks without
   * any attached tile entity.</p>
   *
   * @param blockState the Bukkit {@link BlockState} to check
   * @return true if the {@link BlockState} has a TileEntity
   */
  public static boolean isValidClass(BlockState blockState) {
    // The default state returns null. Subclasses can and do override it
    // Since 1.14 it is split up to TileState, which this will cover as well
    return CRAFT_BLOCK_STATE_CLASS != blockState.getClass();
  }

  /**
   * @param state The {@link BlockState} to check. Non null.
   * @throws IllegalArgumentException If the state doesn't have a TileEntity
   */
  private static void ensureCorrectClass(BlockState state) {
    if (!isValidClass(state)) {
      throw new IllegalArgumentException(
          "The state is not a TileEntity. Valid is e.g. a Chest or a Furnace."
      );
    }
  }
}
