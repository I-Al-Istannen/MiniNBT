package me.ialistannen.mininbt;

import me.ialistannen.mininbt.NBTWrappers.INBTBase;
import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.reflection.BukkitReflection.ClassLookup;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentMethod;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentType;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Modifier;

/**
 * A Util to manipulate NBT data of ItemStacks.
 *
 * <p><br><em>All methods in this class may throw a
 * {@link me.ialistannen.mininbt.reflection.ReflectionException}</em></p>
 */
public class ItemNBTUtil {

  private static final FluentMethod AS_NMS_COPY = ClassLookup.OBC
      .forName("inventory.CraftItemStack")
      .getOrThrow()
      .findMethod()
      .withName("asNMSCopy")
      .withParameters(ItemStack.class)
      .findSingle()
      .getOrThrow();
  private static final FluentMethod AS_BUKKIT_COPY = ClassLookup.OBC
      .forName("inventory.CraftItemStack")
      .getOrThrow()
      .findMethod()
      .withName("asBukkitCopy")
      .withReturnType(ItemStack.class)
      .findSingle()
      .getOrThrow();

  private static final FluentMethod GET_TAG = ClassLookup.NMS
      .forName("ItemStack")
      .getOrThrow()
      .findMethod()
      .withName("getTag")
      .findSingle()
      .getOrThrow();
  private static final FluentMethod SET_TAG = ClassLookup.NMS
      .forName("ItemStack")
      .getOrThrow()
      .findMethod()
      .withName("setTag")
      .withModifiers(Modifier.PUBLIC)
      .findSingle()
      .getOrThrow();

  /**
   * Converts an {@link ItemStack} to its nms counterpart.
   *
   * @param itemStack the {@link ItemStack} to convert
   * @return the NMS Item stack
   */
  private static Object asNMSCopy(ItemStack itemStack) {
    return AS_NMS_COPY.invokeStatic(itemStack).getOrThrow();
  }

  /**
   * Converts an nms ItemStack to a bukkit {@link ItemStack}.
   *
   * @param nmsItem the NMS item to convert
   * @return the converted Item
   */
  private static ItemStack asBukkitCopy(Object nmsItem) {
    return (ItemStack) AS_BUKKIT_COPY.invokeStatic(nmsItem).getOrThrow();
  }

  /**
   * Sets the NBT tag of an item.
   *
   * @param tag the new tag
   * @param itemStack the item to set it on
   * @return the modified item
   */
  public static ItemStack setNBTTag(NBTTagCompound tag, ItemStack itemStack) {
    Object nbtTag = tag.toNBT();
    Object nmsItem = asNMSCopy(itemStack);

    SET_TAG.invoke(nmsItem, nbtTag).ensureSuccessful();

    return asBukkitCopy(nmsItem);
  }

  /**
   * Retrieves the NBTTag of an item. Returns a blank one if there is none present.
   *
   * @param itemStack the item to get the tag for
   * @return the retrieved tag or a new one if it had none
   * @throws IllegalArgumentException if the tag was no compound tag or could not be converted
   */
  public static NBTTagCompound getTag(ItemStack itemStack) {
    Object nmsItem = asNMSCopy(itemStack);

    if (nmsItem == null) {
      throw new NullPointerException("Unable to find a nms item clone for " + itemStack);
    }

    Object tag = GET_TAG.invoke(nmsItem).getOrThrow();

    if (tag == null) {
      return new NBTTagCompound();
    }
    INBTBase base = INBTBase.fromNBT(tag);
    if (base == null || base.getClass() != NBTTagCompound.class) {
      throw new IllegalArgumentException("The tag I received was not valid: " + tag);
    }

    return (NBTTagCompound) base;
  }
}
