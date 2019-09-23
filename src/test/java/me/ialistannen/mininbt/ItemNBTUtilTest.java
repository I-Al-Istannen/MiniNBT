package me.ialistannen.mininbt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.logging.Logger;
import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemFactory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ItemNBTUtilTest {

  @Disabled // Pegs all cores at 100% and needs ~15 seconds and quite a lot of RAM
  @Test
  public void tagRoundTrip() {
    NamedTimer timer = new NamedTimer();
    timer.step("Starting");
    Server serverMock = Mockito.mock(Server.class);
    timer.step("Mock created");
    Mockito.when(serverMock.getItemFactory()).thenReturn(
        CraftItemFactory.instance()
    );
    Mockito.when(serverMock.getLogger()).thenReturn(Logger.getLogger("Test"));
    timer.step("Registered");

    new FluentType<>(Bukkit.class).findField()
        .withName("server")
        .findSingle().getOrThrow()
        .setValue(null, serverMock);

    timer.step("Set");

    NBTTagCompound baseTag = new NBTTagCompound();
    baseTag.setString("Hello world", "!!");

    timer.step("Tag created");
    ItemStack item = new ItemStack(Material.GOLDEN_AXE);
    timer.step("Item created");
    ItemStack changed = ItemNBTUtil.setNBTTag(baseTag, item);
    timer.step("Item tag set");

    assertEquals(
        new NBTTagCompound(),
        ItemNBTUtil.getTag(item)
    );

    assertEquals(
        baseTag,
        ItemNBTUtil.getTag(changed)
    );
  }

  private static class NamedTimer {

    private long current = System.currentTimeMillis();

    void step(String message) {
      long duration = System.currentTimeMillis() - current;
      System.out.println(message + " (" + duration + "ms)");
      current = System.currentTimeMillis();
    }
  }
}