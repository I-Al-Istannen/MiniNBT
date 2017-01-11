// @formatter:off
/**
 * This package handles the modification of NBT data.
 * <p>
 * <br><b>Basics:</b>
 * <br>I recreated the normal NBT classes quite closely, so there is not a big difference. All wrapper classes can be found in the
 * {@link me.ialistannen.mininbt.NBTWrappers NBTWrappers} class.
 * <br>
 * <p><b>Example:</b>
 * <br>{@code NBTTagString string = new NBTTagString("Hey");}
 * <br>{@code NBTTagCompound compound = new NBTTagCompound();}
 * <br>{@code compound.setString("Key", string);}
 * <p>
 * <br><b>Editing:</b>
 * <ul>
 *     <li>
 *         <b>Item NBT:</b>
 *         <ul>
 *             <li>
 *                 <b>Utility</b>:
 *                 <br>{@link me.ialistannen.mininbt.ItemNBTUtil ItemNBTUtil}
 *             </li>
 *             <li>
 *                 <b>Getting the tag:</b>
 *                 <br>{@link me.ialistannen.mininbt.ItemNBTUtil#getTag(org.bukkit.inventory.ItemStack)
 *                     ItemNBTUtil#getTag(ItemStack)}
 *             </li>
 *             <li>
 *                 <b>Setting the tag:</b>
 *                 <br>{@link me.ialistannen.mininbt.ItemNBTUtil#setNBTTag(me.ialistannen.mininbt.NBTWrappers.NBTTagCompound, org.bukkit.inventory.ItemStack) 
 *                     ItemNBTUtil#setTag(NBTTagCompound, ItemStack)}
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         <b>Entity NBT:</b>
 *         <ul>
 *             <li>
 *                 <b>Utility:</b>
 *                 <br>{@link me.ialistannen.mininbt.EntityNBTUtil EntityNBTUtil}
 *             </li>
 *             <li>
 *                 <b>Getting the tag:</b>
 *                 <br>{@link me.ialistannen.mininbt.EntityNBTUtil#getNbtTag(org.bukkit.entity.Entity)
 *                      EntityNBTUtil#getTag(Entity)}
 *             </li>
 *             <li>
 *                 <b>Setting the tag</b>
 *                 <br>{@link me.ialistannen.mininbt.EntityNBTUtil#setNbtTag(org.bukkit.entity.Entity, me.ialistannen.mininbt.NBTWrappers.NBTTagCompound)
 *                      EntityNBTUtil#setTag(Entity, NBTTagCompound}
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         <b>TileEntity NBT:</b>
 *         <ul>
 *             <li>
 *                 <b>Utility:</b>
 *                 <br>{@link me.ialistannen.mininbt.TileEntityNBTUtil}
 *             </li>
 *             <li>
 *                 <b>Getting the tag:</b>
 *                 <br>{@link me.ialistannen.mininbt.TileEntityNBTUtil#getNbtTag(org.bukkit.block.BlockState)
  *                     TileEntityNBTUtil#getNbtTag(BlockState)}
 *             </li>
 *             <li>
 *                 <b>Setting the tag:</b>
 *                 <br>{@link me.ialistannen.mininbt.TileEntityNBTUtil#setNbtTag(org.bukkit.block.BlockState, me.ialistannen.mininbt.NBTWrappers.NBTTagCompound)
 *                      TileEntityNBTUtil#setNbtTag(BlockState, NBTTagCompund)}
 *             </li>
 *         </ul>
 *     </li>
 * </ul>
 */
// @formatter:on
package me.ialistannen.mininbt;