package me.ialistannen.mininbt.reflection.seeking;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentConstructor;
import me.ialistannen.mininbt.reflection.FluentReflection.ReflectiveResult;
import me.ialistannen.mininbt.reflection.ReflectionException;

/**
 * Helps search a method in a class.
 *
 * @param <C> the type of the class
 */
@SuppressWarnings("TypeParameterExtendsFinalClass") // Class has a recursive type param
public class ConstructorSeeker<C extends Class<C>> extends
    ExecutableSeeker<C, ConstructorSeeker<C>> implements ElementSeeker<FluentConstructor<C>> {

  private List<Predicate<Constructor<C>>> filters;

  /**
   * Creates a new method seeker for a given class.
   *
   * @param clazz the class to create it for
   */
  public ConstructorSeeker(Class<C> clazz) {
    super(clazz);
    this.filters = new ArrayList<>();
  }

  @Override
  protected ConstructorSeeker<C> getSelf() {
    return this;
  }

  /**
   * Searches for methods matching the given predicate.
   *
   * @param predicate the predicate
   * @return this seeker
   */
  public ConstructorSeeker<C> matchingConstructor(Predicate<Constructor<C>> predicate) {
    filters.add(predicate);
    return this;
  }

  @Override
  public ReflectiveResult<List<FluentConstructor<C>>> findAll() {
    Set<FluentConstructor<C>> methods = findInConstructors(clazz.getConstructors());
    methods.addAll(findInConstructors(clazz.getDeclaredConstructors()));

    if (methods.isEmpty()) {
      return ReflectiveResult.failure(new ReflectionException("No constructor found"));
    }
    return ReflectiveResult.success(new ArrayList<>(methods));
  }

  private Set<FluentConstructor<C>> findInConstructors(Constructor<?>[] constructors) {
    return Arrays.stream(constructors)
        .map(it -> {
          @SuppressWarnings("unchecked")
          Constructor<C> constructor = (Constructor<C>) it;
          return constructor;
        })
        .filter(constructor -> filters.stream().allMatch(it -> it.test(constructor)))
        .filter(super::matches)
        .map(FluentConstructor::new)
        .collect(Collectors.toSet());
  }
}
