package me.ialistannen.mininbt.reflection;

/**
 * An exception that occurred while doing <em>something</em> with the fluent reflection api.
 */
public class ReflectionException extends RuntimeException {

  public ReflectionException(String message) {
    super(message);
  }

  public ReflectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
