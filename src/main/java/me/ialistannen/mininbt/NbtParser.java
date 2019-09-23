package me.ialistannen.mininbt;

import java.lang.reflect.InvocationTargetException;
import me.ialistannen.mininbt.NBTWrappers.INBTBase;
import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.reflection.BukkitReflection.ClassLookup;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentMethod;
import me.ialistannen.mininbt.reflection.FluentReflection.ReflectiveResult;

/**
 * A wrapper for the MojangsonParser used for parsing NBT.
 *
 * <p><br><em>All methods in this class may throw a
 * {@link me.ialistannen.mininbt.reflection.ReflectionException}</em></p>
 */
public class NbtParser {

  private static final FluentMethod PARSE_METHOD;

  static {
    PARSE_METHOD = ClassLookup.NMS.forName("MojangsonParser").getOrThrow()
        .findMethod()
        .withName("parse")
        .withParameters(String.class)
        .findSingle()
        .getOrThrow();
  }

  /**
   * Parses a String to an {@link NBTTagCompound}.
   *
   * @param nbt the nbt string to parse
   * @return the parsed NBTTagCompound
   * @throws NbtParseException if an error occurred while parsing the NBT tag
   */
  public static NBTTagCompound parse(String nbt) throws NbtParseException {
    ReflectiveResult<Object> result = PARSE_METHOD.invokeStatic(nbt);
    if (result.isPresent()) {
      return (NBTTagCompound) INBTBase.fromNBT(result.getOrThrow());
    }
    Throwable underlyingException = result.getError();
    if (!(underlyingException instanceof InvocationTargetException)) {
      throw new NbtParseException("Unknown error", null);
    }

    throw new NbtParseException(
        underlyingException.getCause().getMessage(),
        underlyingException.getCause()
    );
  }

  /**
   * An exception occurred while parsing a NBT tag.
   */
  public static class NbtParseException extends Exception {

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link
     *     #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()}
     *     method). (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent
     *     or unknown.)
     * @since 1.4
     */
    private NbtParseException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
