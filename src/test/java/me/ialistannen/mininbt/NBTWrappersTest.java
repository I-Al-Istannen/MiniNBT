package me.ialistannen.mininbt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.ialistannen.mininbt.NBTWrappers.INBTBase;
import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.NBTWrappers.NBTTagInt;
import me.ialistannen.mininbt.NBTWrappers.NBTTagList;
import me.ialistannen.mininbt.NBTWrappers.NBTTagLong;
import me.ialistannen.mininbt.NBTWrappers.NBTTagString;
import org.junit.jupiter.api.Test;

class NBTWrappersTest {

  @Test
  public void testCompoundRoundTrip() {
    NBTTagCompound compound = new NBTTagCompound();
    compound.setString("Hey", "You");
    compound.setByte("Byte", (byte) 50);
    compound.setShort("Byte", (short) 50);
    compound.setInt("int", -2000);
    compound.setLong("long", 5009999998L);
    compound.setFloat("float", (float) 1.43);
    compound.setDouble("double", 1.535);
    compound.setIntArray("int array", new int[]{2, -200, 50, 2});
    compound.setByteArray("byte array", new byte[]{20, -5, 127});
    NBTTagList list = new NBTTagList();
    list.add(new NBTTagString("A string"));
    list.add(new NBTTagInt(200));
    list.add(new NBTTagLong(-200000009999L));
    compound.set("list", list);

    Object serialized = compound.toNBT();
    INBTBase deserialized = NBTTagCompound.fromNBT(serialized);

    assertEquals(
        compound,
        deserialized
    );
  }
}