package me.ialistannen.mininbt;

import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.reflection.BukkitReflection;
import me.ialistannen.mininbt.reflection.FluentReflection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamNBTUtil {
  private static final FluentReflection.FluentType<?> NBT_COMPRESSED_STREAM_TOOLS = BukkitReflection.ClassLookup.NMS
      .forName("NBTCompressedStreamTools")
      .getOrThrow();

  private static final FluentReflection.FluentMethod FROM_STREAM = NBT_COMPRESSED_STREAM_TOOLS
      .findMethod()
      .withParameters(InputStream.class)
      .withReturnType(NBTTagCompound.NBT_TAG_COMPOUND_CLASS.getUnderlying())
      .findSingle()
      .getOrThrow();

  private static final FluentReflection.FluentMethod WRITE_STREAM = NBT_COMPRESSED_STREAM_TOOLS
      .findMethod()
      .withParameters(
          NBTTagCompound.NBT_TAG_COMPOUND_CLASS.getUnderlying(),
          OutputStream.class
      )
      .findSingle()
      .getOrThrow();

  // thrown by the underlying reflected method
  @SuppressWarnings("RedundantThrows")
  public static NBTTagCompound fromStream(InputStream inputStream) throws IOException {
    Object nbtObject = FROM_STREAM.invokeStatic(inputStream);

    return (NBTTagCompound) NBTTagCompound.fromNBT(nbtObject);
  }

  // thrown by the underlying reflected method
  @SuppressWarnings("RedundantThrows")
  public static void writeToStream(NBTTagCompound compound, OutputStream outputStream) throws IOException {
    Object nbtObject = FROM_STREAM.invokeStatic(compound.toNBT(), outputStream);
  }
}
