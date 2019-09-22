package me.ialistannen.mininbt;

import static me.ialistannen.mininbt.ReflectionUtil.NameSpace.NMS;

import java.lang.reflect.Method;
import java.util.Optional;
import me.ialistannen.mininbt.NBTWrappers.INBTBase;
import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.ReflectionUtil.MethodPredicate;
import me.ialistannen.mininbt.ReflectionUtil.ReflectResponse;
import me.ialistannen.mininbt.ReflectionUtil.ReflectResponse.ResultType;
import org.bukkit.Bukkit;

/**
 * A wrapper for the MojangsonParser used for parsing NBT
 */
@SuppressWarnings("unused")
public class NbtParser {

  private static final Method PARSE_METHOD;
  private static boolean error = false;

  static {
    Optional<Class<?>> mojangsonParserClass = ReflectionUtil.getClass(NMS, "MojangsonParser");

    if (!mojangsonParserClass.isPresent()) {
      System.out.println("Can't find the class MojangsonParser: "
          + Bukkit.getServer().getClass().getName());
      error = true;
      PARSE_METHOD = null;
    } else {
      ReflectResponse<Method> parseMethod = ReflectionUtil.getMethod(mojangsonParserClass.get(),
          new MethodPredicate()
              .withName("parse")
              .withParameters(String.class));

      if (parseMethod.isValuePresent()) {
        PARSE_METHOD = parseMethod.getValue();
      } else {
        System.out.println("Can't find MojangsonParser's parse method: "
            + mojangsonParserClass.get().getName());
        error = true;
        PARSE_METHOD = null;
      }
    }
  }

  /**
   * @throws IllegalStateException If {@link #error} is true
   */
  private static void ensureNoError() {
    if (error) {
      throw new IllegalStateException("A critical, non recoverable error occurred earlier.");
    }
  }

  /**
   * Parses a String to an {@link NBTTagCompound}
   *
   * @param nbt The nbt to parse
   * @return The parsed NBTTagCompound
   * @throws NbtParseException if an error occurred while parsing the NBT tag
   */
  public static NBTTagCompound parse(String nbt) throws NbtParseException {
    ensureNoError();

    ReflectResponse<Object> response = ReflectionUtil.invokeMethod(PARSE_METHOD, null, nbt);

    if (!response.isSuccessful()) {
      if (response.getResultType() == ResultType.ERROR) {
        throw new NbtParseException(response.getException().getCause().getMessage(),
            response.getException().getCause());
      }
    }

    // is defined by the method and the only one making sense
    return (NBTTagCompound) INBTBase.fromNBT(response.getValue());
  }

  /**
   * An exception occurred while parsing a NBT tag. Checked.
   */
  @SuppressWarnings("WeakerAccess") // other classes outside need to access
  // it.
  public static class NbtParseException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -8761176772930529828L;

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
