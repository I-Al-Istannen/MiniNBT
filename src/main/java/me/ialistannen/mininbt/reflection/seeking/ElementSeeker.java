package me.ialistannen.mininbt.reflection.seeking;

import java.util.List;
import me.ialistannen.mininbt.reflection.FluentReflection.ReflectiveResult;
import me.ialistannen.mininbt.reflection.ReflectionException;

/**
 * A seeker that finds something.
 *
 * @param <T> the type of the element it finds
 */
public interface ElementSeeker<T> {

  /**
   * Finds the matching element, failing if there are multiple.
   *
   * @return the found element
   */
  default ReflectiveResult<T> findSingle() {
    ReflectiveResult<List<T>> result = findAll();
    if (result.isPresent()) {
      if (result.getOrThrow().size() > 1) {
        return ReflectiveResult.failure(new ReflectionException("Too many elements found!"));
      }
    }
    return result.map(it -> it.get(0));
  }

  /**
   * Finds the first matching element. Fails if there are none.
   *
   * @return the found element
   */
  default ReflectiveResult<T> findFirst() {
    return findAll().map(it -> it.get(0));
  }

  /**
   * Finds all matching elements and errors if there were none. This means an empty list will never
   * be returned.
   *
   * @return the found elements
   */
  ReflectiveResult<List<T>> findAll();
}
