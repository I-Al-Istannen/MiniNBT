package me.ialistannen.mininbt.reflection;

import me.ialistannen.mininbt.reflection.FluentReflection.ReflectiveResult;

/**
 * An unsafe reflection operation.
 *
 * @param <T> the type of the result
 */
interface UnsafeReflectiveAction<T> {

  /**
   * The SAM call method.
   *
   * @param <T> the type of the result
   * @return the result
   * @throws Throwable if an error occurs
   */
  T run() throws Throwable;

  /**
   * Executes the given action.
   *
   * @param action the action
   * @return the action result
   */
  static <T> ReflectiveResult<T> execute(UnsafeReflectiveAction<T> action) {
    try {
      return ReflectiveResult.success(action.run());
    } catch (Throwable throwable) {
      throw new ReflectionException("Error running reflective action!", throwable);
    }
  }
}
