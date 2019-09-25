package me.ialistannen.mininbt;

import me.ialistannen.mininbt.reflection.BukkitReflection.ClassLookup;
import org.junit.jupiter.api.Assumptions;

class NMSPrecenseAssumption {

  /**
   * Assumes that spigot is available to test NMS features.
   */
  static void assumeSpigotIsAvailable() {
    Assumptions.assumeTrue(
        ClassLookup.NMS.forName("Entity").isPresent(),
        "NMS is not available as a dependency"
    );
  }
}
