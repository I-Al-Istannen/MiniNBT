package me.ialistannen.mininbt;

import static me.ialistannen.mininbt.ReflectionUtil.NameSpace.OBC;

import org.bukkit.inventory.ItemStack;

import me.ialistannen.mininbt.NBTWrappers.INBTBase;
import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.ReflectionUtil.MethodPredicate;
import me.ialistannen.mininbt.ReflectionUtil.Modifier;

/**
 * A Util to save NBT data to ItemStacks
 */
@SuppressWarnings({ "unused", "WeakerAccess" })
public class ItemNBTUtil {

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static final Class<?> CRAFT_ITEM_STACK_CLASS = ReflectionUtil.getClass(OBC, "inventory.CraftItemStack").get();

    /**
     * @param itemStack The {@link ItemStack} to convert
     *
     * @return The NMS Item stack
     */
    private static Object asNMSCopy(ItemStack itemStack) {
        return ReflectionUtil.invokeMethod(CRAFT_ITEM_STACK_CLASS, new MethodPredicate()
                            .withName("asNMSCopy")
                            .withParameters(ItemStack.class),
                  null, itemStack).getValue();
    }

    /**
     * Only pass a NMS Itemstack!
     *
     * @param nmsItem The NMS item to convert
     *
     * @return The converted Item
     */
    private static ItemStack asBukkitCopy(Object nmsItem) {
        return (ItemStack) ReflectionUtil.invokeMethod(CRAFT_ITEM_STACK_CLASS, new MethodPredicate()
                            .withName("asBukkitCopy").withModifiers(Modifier.PUBLIC, Modifier.STATIC),
                  null, nmsItem).getValue();
    }

    /**
     * Sets the NBT tag of an item
     *
     * @param tag The new tag
     * @param itemStack The ItemStack
     *
     * @return The modified itemStack
     */
    public static ItemStack setNBTTag(NBTTagCompound tag, ItemStack itemStack) {
        Object nbtTag = tag.toNBT();
        Object nmsItem = asNMSCopy(itemStack);
        ReflectionUtil.invokeMethod(nmsItem.getClass(), new MethodPredicate()
                  .withName("setTag")
                  .withModifiers(Modifier.PUBLIC), nmsItem, nbtTag);

        return asBukkitCopy(nmsItem);
    }

    /**
     * Gets the NBTTag of an item. In case of any error it returns a blank one.
     *
     * @param itemStack The ItemStack to get the tag for
     *
     * @return The NBTTagCompound of the ItemStack or a new one if it had none
     * or an error occurred
     */
    public static NBTTagCompound getTag(ItemStack itemStack) {
        Object nmsItem = asNMSCopy(itemStack);
        Object tag = ReflectionUtil.invokeMethod(nmsItem.getClass(), new MethodPredicate()
                  .withName("getTag"), nmsItem).getValue();
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
