package me.ialistannen.mininbt.reflection.seeking;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Helps search a method in a class.
 *
 * @param <C> the type of the class
 */
public abstract class ExecutableSeeker<C extends Class<C>, T extends ExecutableSeeker<C, T>> {

  protected Class<C> clazz;
  private List<Predicate<Executable>> filters;

  /**
   * Creates a new method seeker for a given class.
   *
   * @param clazz the class to create it for
   */
  public ExecutableSeeker(Class<C> clazz) {
    this.clazz = clazz;
    this.filters = new ArrayList<>();
  }

  /**
   * Returns this type.
   *
   * @return this type
   */
  protected abstract T getSelf();

  /**
   * Checks if an executable matches.
   *
   * @param executable the executable
   * @return true if it matches, false otherwise
   */
  protected boolean matches(Executable executable) {
    return filters.stream().allMatch(pred -> pred.test(executable));
  }

  /**
   * Searches for a method with a given name.
   *
   * @param name the name of the method
   * @return this seeker
   */
  public T withName(String name) {
    return withName(name::equals);
  }

  /**
   * Searches for a method matching the given name predicate.
   *
   * @param namePredicate the name predicate
   * @return this seeker
   */
  public T withName(Predicate<String> namePredicate) {
    return matching(method -> namePredicate.test(method.getName()));
  }

  /**
   * Searches for a method with the given parameter type.
   *
   * @param parameters the parameters
   * @return this seeker
   */
  public T withParameters(Class<?>... parameters) {
    return matching(method -> Arrays.equals(method.getParameterTypes(), parameters));
  }

  /**
   * Searches for a method with the given {@link java.lang.reflect.Modifier}s.
   *
   * @param modifiers the modifiers
   * @return this seeker
   */
  public T withModifiers(int... modifiers) {
    return matching(executable -> {
      for (int modifier : modifiers) {
        if ((executable.getModifiers() & modifier) == 0) {
          return false;
        }
      }
      return true;
    });
  }

  /**
   * Searches for a method with the given {@link java.lang.reflect.Modifier}s.
   *
   * @param modifiers the modifiers
   * @return this seeker
   */
  public T withoutModifiers(int... modifiers) {
    return matching(executable -> {
      for (int modifier : modifiers) {
        if ((executable.getModifiers() & modifier) != 0) {
          return false;
        }
      }
      return true;
    });
  }

  /**
   * Searches for a method matching the given predicate.
   *
   * @param predicate the predicate
   * @return this seeker
   */
  public T matching(Predicate<Executable> predicate) {
    filters.add(predicate);
    return getSelf();
  }
}
