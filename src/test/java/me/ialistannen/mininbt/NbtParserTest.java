package me.ialistannen.mininbt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.NBTWrappers.NBTTagList;
import me.ialistannen.mininbt.NBTWrappers.NBTTagString;
import me.ialistannen.mininbt.NbtParser.NbtParseException;
import org.junit.jupiter.api.Test;

class NbtParserTest {

  @Test
  public void roundtripSimpleCompound() throws NbtParseException {
    NMSPrecenseAssumption.assumeSpigotIsAvailable();

    NBTTagCompound compound = new NBTTagCompound();
    compound.setString("hello", "World");
    compound.setBoolean("you", true);
    compound.setByte("byte", (byte) 20);
    compound.setDouble("double", 1.245);
    compound.setIntArray("ints", new int[]{1, 5, 1, -20});
    NBTTagList list = new NBTTagList();
    list.add(new NBTTagString("MyList"));
    list.add(new NBTTagString("There"));
    compound.set("list", list);

    String asString = compound.toNBT().toString();

    assertEquals(
        compound,
        NbtParser.parse(asString)
    );
  }
}