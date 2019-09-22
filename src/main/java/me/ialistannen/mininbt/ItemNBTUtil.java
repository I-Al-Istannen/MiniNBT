package me.ialistannen.mininbt;

import java.lang.reflect.Modifier;
import me.ialistannen.mininbt.NBTWrappers.INBTBase;
import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.reflection.BukkitReflection.ClassLookup;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentMethod;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentType;
import org.bukkit.inventory.ItemStack;

/**
 * A Util to save NBT data to ItemStacks
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
  private static final FluentMethod As_BUKKIT_COPY = ClassLookup.OBC
      .forName("inventory.CraftItemStack")
      .getOrThrow()
      .findMethod()
      .withName("asBukkitCopy")
      .withReturnType(ItemStack.class)
      .findSingle()
      .getOrThrow();

  /**
   * @param itemStack The {@link ItemStack} to convert
   * @return The NMS Item stack
   */
  private static Object asNMSCopy(ItemStack itemStack) {
    return AS_NMS_COPY.invokeStatic(itemStack).getOrThrow();
  }

  /**
   * Only pass a NMS Itemstack!
   *
   * @param nmsItem The NMS item to convert
   * @return The converted Item
   */
  private static ItemStack asBukkitCopy(Object nmsItem) {
    return (ItemStack) As_BUKKIT_COPY.invokeStatic(nmsItem).getOrThrow();
  }

  /**
   * Sets the NBT tag of an item
   *
   * @param tag The new tag
   * @param itemStack The ItemStack
   * @return The modified itemStack
   */
  public static ItemStack setNBTTag(NBTTagCompound tag, ItemStack itemStack) {
    Object nbtTag = tag.toNBT();
    Object nmsItem = asNMSCopy(itemStack);
    FluentType.ofUnknown(nmsItem.getClass()).findMethod()
        .withName("setTag")
        .withModifiers(Modifier.PUBLIC)
        .findSingle()
        .getOrThrow()
        .invoke(nmsItem, nbtTag)
        .ensureSuccessful();

    return asBukkitCopy(nmsItem);
  }

  /**
   * Gets the NBTTag of an item. In case of any error it returns a blank one.
   *
   * @param itemStack The ItemStack to get the tag for
   * @return The NBTTagCompound of the ItemStack or a new one if it had none or an error occurred
   */
  public static NBTTagCompound getTag(ItemStack itemStack) {
    Object nmsItem = asNMSCopy(itemStack);

    if (nmsItem == null) {
      throw new NullPointerException("Unable to find a nms item clone for " + itemStack);
    }

    Object tag = FluentType.ofUnknown(nmsItem.getClass()).findMethod()
        .withName("getTag")
        .findSingle().getOrThrow()
        .invoke(nmsItem).getOrThrow();

    if (tag == null) {
      return new NBTTagCompound();
    }
    INBTBase base = INBTBase.fromNBT(tag);
    if (base == null || base.getClass() != NBTTagCompound.class) {
      return new NBTTagCompound();
    }

    return (NBTTagCompound) base;
  }
}
