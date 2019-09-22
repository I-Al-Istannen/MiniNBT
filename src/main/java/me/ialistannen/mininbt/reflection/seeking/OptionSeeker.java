package me.ialistannen.mininbt.reflection.seeking;

import java.util.ArrayList;
import java.util.List;
import me.ialistannen.mininbt.reflection.FluentReflection.ReflectiveResult;
import me.ialistannen.mininbt.reflection.ReflectionException;

/**
 * A seeker that combines multiple and picks the one that works.
 *
 * @param <T> the type of the seeker
 */
public class OptionSeeker<T> implements ElementSeeker<T> {

  private List<ElementSeeker<T>> seekers = new ArrayList<>();

  /**
   * Adds another possible seeker.
   *
   * @param other the new seeker
   * @return this seeker
   */
  public OptionSeeker<T> or(ElementSeeker<T> other) {
    seekers.add(other);
    return this;
  }

  @Override
  public ReflectiveResult<T> find() {
    for (ElementSeeker<T> seeker : seekers) {
      ReflectiveResult<T> result = seeker.find();
      if (result.isPresent()) {
        return result;
      }
    }
    return ReflectiveResult.failure(
        new ReflectionException("No seeker found any element matching its predicates")
    );
  }
}
