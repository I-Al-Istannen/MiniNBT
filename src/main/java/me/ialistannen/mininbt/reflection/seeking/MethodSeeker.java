package me.ialistannen.mininbt.reflection.seeking;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentMethod;
import me.ialistannen.mininbt.reflection.FluentReflection.ReflectiveResult;
import me.ialistannen.mininbt.reflection.ReflectionException;

/**
 * Helps search a method in a class.
 *
 * @param <C> the type of the class
 */
@SuppressWarnings("TypeParameterExtendsFinalClass") // Class has a recursive type param
public class MethodSeeker<C extends Class<C>> extends
    ExecutableSeeker<C, MethodSeeker<C>> implements ElementSeeker<FluentMethod> {

  private List<Predicate<Method>> filters;

  /**
   * Creates a new method seeker for a given class.
   *
   * @param clazz the class to create it for
   */
  public MethodSeeker(Class<C> clazz) {
    super(clazz);
    this.filters = new ArrayList<>();
  }

  @Override
  protected MethodSeeker<C> getSelf() {
    return this;
  }

  /**
   * Searches for a method with the given return type.
   *
   * @param returnType the return type
   * @return this seeker
   */
  public MethodSeeker<C> withReturnType(Class<?> returnType) {
    return matchingMethod(method -> method.getReturnType().equals(returnType));
  }

  /**
   * Searches for methods matching the given predicate. This is a specialized form of {@link
   * #matching(Predicate)}.
   *
   * @param predicate the predicate
   * @return this seeker
   */
  public MethodSeeker<C> matchingMethod(Predicate<Method> predicate) {
    filters.add(predicate);
    return this;
  }

  @Override
  public ReflectiveResult<List<FluentMethod>> findAll() {
    Set<FluentMethod> methods = findInMethods(clazz.getMethods());
    methods.addAll(findInMethods(clazz.getDeclaredMethods()));

    if (methods.isEmpty()) {
      return ReflectiveResult.failure(new ReflectionException("No methods found"));
    }
    return ReflectiveResult.success(new ArrayList<>(methods));
  }

  private Set<FluentMethod> findInMethods(Method[] methods) {
    return Arrays.stream(methods)
        .filter(method -> filters.stream().allMatch(it -> it.test(method)))
        .filter(super::matches)
        .map(FluentMethod::new)
        .collect(Collectors.toSet());
  }
}
