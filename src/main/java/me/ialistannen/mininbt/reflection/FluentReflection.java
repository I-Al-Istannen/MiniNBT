package me.ialistannen.mininbt.reflection;

import static me.ialistannen.mininbt.reflection.UnsafeReflectiveAction.execute;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import me.ialistannen.mininbt.reflection.seeking.ConstructorSeeker;
import me.ialistannen.mininbt.reflection.seeking.FieldSeeker;
import me.ialistannen.mininbt.reflection.seeking.MethodSeeker;

/**
 * A fluent reflection helper.
 *
 * @param <T> the type of the class
 */
@SuppressWarnings("TypeParameterExtendsFinalClass")
public abstract class FluentReflection<T extends FluentReflection<T>> {

  /**
   * Returns this object.
   *
   * @return this object
   */
  protected abstract T getSelf();

  /**
   * A fluent type.
   */
  public static class FluentType<C extends Class<C>> extends FluentReflection<FluentType<C>> {

    private C underlying;

    /**
     * Creates a new fluent type for a given class.
     *
     * @param underlying the class to create it for
     */
    public FluentType(C underlying) {
      this.underlying = underlying;
    }

    @Override
    protected FluentType<C> getSelf() {
      return this;
    }

    /**
     * Returns the underlying class.
     *
     * @return the underlying class
     */
    public C getUnderlying() {
      return underlying;
    }

    /**
     * Finds a field in the class.
     *
     * @return a field seeker
     */
    public FieldSeeker<C> findField() {
      return new FieldSeeker<>(underlying);
    }

    /**
     * Finds a method in the class.
     *
     * @return a method seeker
     */
    public MethodSeeker<C> findMethod() {
      return new MethodSeeker<>(underlying);
    }

    /**
     * Finds a method in the class.
     *
     * @return a method seeker
     */
    public ConstructorSeeker<C> findConstructor() {
      return new ConstructorSeeker<>(underlying);
    }

    /**
     * Creates a new fluent type for an unknown class.
     *
     * @param clazz the class
     * @param <C> the type of it, inferred to be whatever
     * @return the fluent type for it
     */
    public static <C extends Class<C>> FluentType<C> ofUnknown(Class<?> clazz) {
      @SuppressWarnings("unchecked")
      C c = (C) clazz;
      return new FluentType<>(c);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      FluentType<?> that = (FluentType<?>) o;
      return Objects.equals(underlying, that.underlying);
    }

    @Override
    public int hashCode() {
      return Objects.hash(underlying);
    }
  }

  /**
   * An invocable.
   *
   * @param <T> the type of the invocable.
   */
  public static abstract class FluentInvokable<T extends FluentInvokable<T>> extends
      FluentReflection<FluentInvokable<T>> {

    private Executable underlying;

    /**
     * Creates a new fluent invocable.
     *
     * @param underlying the underlying executable
     */
    protected FluentInvokable(Executable underlying) {
      this.underlying = underlying;
    }

    /**
     * Returns the name of the underlying executable.
     *
     * @return the name of the underlying executable.
     */
    public String getName() {
      return underlying.getName();
    }

    /**
     * Returns the parameter types.
     *
     * @return the parameter types
     */
    public Class<?>[] getParameterTypes() {
      return underlying.getParameterTypes();
    }

    /**
     * Returns the unlderying executable.
     *
     * @return the underlying executable
     */
    public Executable getUnderlying() {
      return underlying;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      FluentInvokable<?> that = (FluentInvokable<?>) o;
      return Objects.equals(underlying, that.underlying);
    }

    @Override
    public int hashCode() {
      return Objects.hash(underlying);
    }
  }

  /**
   * A fluent method.
   */
  public static class FluentMethod extends FluentInvokable<FluentMethod> {

    /**
     * Creates a new fluent method.
     *
     * @param underlying the underlying method
     */
    public FluentMethod(Method underlying) {
      super(underlying);
    }

    @Override
    protected FluentInvokable<FluentMethod> getSelf() {
      return this;
    }

    @Override
    public Method getUnderlying() {
      return (Method) super.getUnderlying();
    }

    /**
     * Invokes this executable.
     *
     * @param handle the handle
     * @param arguments the arguments
     * @param <R> the return type
     * @return the return value
     */
    public <R> ReflectiveResult<R> invoke(Object handle, Object... arguments) {
      return execute(() -> {
        Method underlying = getUnderlying();
        underlying.setAccessible(true);

        @SuppressWarnings("unchecked")
        R r = (R) underlying.invoke(handle, arguments);
        return r;
      });
    }

    /**
     * Invokes this executable as a static method.
     *
     * @param arguments the arguments
     * @param <R> the return type
     * @return the return value
     */
    public <R> ReflectiveResult<R> invokeStatic(Object... arguments) {
      return invoke(null, arguments);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      FluentMethod that = (FluentMethod) o;
      return Objects.equals(getUnderlying(), that.getUnderlying());
    }

    @Override
    public int hashCode() {
      return Objects.hash(getUnderlying());
    }
  }

  /**
   * A fluent constructor.
   */
  public static class FluentConstructor<I> extends FluentInvokable<FluentConstructor<I>> {

    /**
     * Creates a new fluent constructor.
     *
     * @param underlying the underlying constructor
     */
    public FluentConstructor(Constructor<I> underlying) {
      super(underlying);
    }


    @Override
    protected FluentInvokable<FluentConstructor<I>> getSelf() {
      return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Constructor<I> getUnderlying() {
      return (Constructor<I>) super.getUnderlying();
    }

    /**
     * Creates a new instance.
     *
     * @param arguments the arguments
     * @return the created instance
     */
    public ReflectiveResult<I> createInstance(Object... arguments) {
      return execute(() -> {
        Constructor<I> underlying = getUnderlying();
        underlying.setAccessible(true);
        return underlying.newInstance(arguments);
      });
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      FluentConstructor<?> that = (FluentConstructor<?>) o;
      return Objects.equals(getUnderlying(), that.getUnderlying());
    }

    @Override
    public int hashCode() {
      return Objects.hash(getUnderlying());
    }
  }

  /**
   * A fluent field.
   */
  public static class FluentField extends FluentReflection<FluentField> {

    private final Field underlying;

    /**
     * Creates a new fluent field.
     *
     * @param underlying the underlying field
     */
    public FluentField(Field underlying) {
      this.underlying = underlying;
    }

    /**
     * Returns the underlying field.
     *
     * @return the underlying field
     */
    public Field getUnderlying() {
      return underlying;
    }

    @Override
    protected FluentField getSelf() {
      return this;
    }

    /**
     * Sets the value of a field.
     *
     * @param handle the handle object to set it on
     * @param value the value
     * @return a result with a null value if it is successful
     */
    public ReflectiveResult<Void> setValue(Object handle, Object value) {
      return execute(() -> {
        underlying.setAccessible(true);
        underlying.set(handle, value);

        return null;
      });
    }

    /**
     * Returns the field value.
     *
     * @param handle the handle object to get it for
     * @param <T> the type of the result. Unsafely cast
     * @return the value
     */
    @SuppressWarnings("unchecked")
    public <T> ReflectiveResult<T> getValue(Object handle) {
      return execute(() -> {
        underlying.setAccessible(true);
        return (T) underlying.get(handle);
      });
    }

    /**
     * Returns the value of a static field.
     *
     * @param <T> the type of the result. Unsafely cast
     * @return the value
     */
    public <T> ReflectiveResult<T> getStaticValue() {
      return getValue(null);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      FluentField that = (FluentField) o;
      return Objects.equals(underlying, that.underlying);
    }

    @Override
    public int hashCode() {
      return Objects.hash(underlying);
    }
  }


  /**
   * The result of a reflective operation.
   *
   * @param <T> the type of the result
   */
  public static class ReflectiveResult<T> {

    private T value;
    private Throwable error;

    /**
     * Creates a new reflective result.
     *
     * @param value the value or null
     * @param error the error or null if none
     */
    private ReflectiveResult(T value, Throwable error) {
      this.value = value;
      this.error = error;
    }

    /**
     * Returns the value or throws an exception if it was not present.
     *
     * @return the value
     * @throws ReflectionException wrapping the underlying exception
     */
    public T getOrThrow() {
      ensureSuccessful();
      return value;
    }

    /**
     * Returns the error or null.
     *
     * @return the error or null
     */
    public Throwable getError() {
      return error;
    }

    /**
     * Returns the value or a given other value.
     *
     * @param other the other value
     * @return the value or the given other if there was no value present
     */
    public T orElse(T other) {
      if (isPresent()) {
        return value;
      }
      return other;
    }

    /**
     * Returns this result if it is present and the other one otherwise.
     *
     * @param other the other result
     * @return the new result
     */
    public ReflectiveResult<T> or(ReflectiveResult<T> other) {
      if (isPresent()) {
        return this;
      }
      return other;
    }

    /**
     * Merges the values in two results. Returns just one unmapped if only one is present.
     *
     * @param other the other result
     * @param merge the merge function
     * @return the merged result
     */
    public ReflectiveResult<T> merge(ReflectiveResult<T> other, BiFunction<T, T, T> merge) {
      if (!isPresent()) {
        return other;
      }
      if (!other.isPresent()) {
        return this;
      }
      return success(merge.apply(getOrThrow(), other.getOrThrow()));
    }

    /**
     * Ensures the result contains a value or throws an exception otherwise.
     *
     * @throws ReflectionException wrapping the underlying exception
     */
    public void ensureSuccessful() {
      if (!isPresent()) {
        throw new ReflectionException("Error retrieving value", error);
      }
    }

    /**
     * Returns true if a value is present.
     *
     * @return true if a value is present.
     */
    public boolean isPresent() {
      return error == null;
    }

    /**
     * Maps a function over this result.
     *
     * @param mapper the function
     * @param <R> the new result type
     * @return the resulting action
     */
    public <R> ReflectiveResult<R> map(Function<T, R> mapper) {
      if (isPresent()) {
        return success(mapper.apply(value));
      }
      return failure(error);
    }

    /**
     * Maps a function over this result, if the value is not null. Returns a null success
     * otherwise.
     *
     * @param mapper the function
     * @param <R> the new result type
     * @return the resulting action
     */
    public <R> ReflectiveResult<R> mapNotNull(Function<T, R> mapper) {
      if (isPresent()) {
        if (value == null) {
          return success(null);
        }
        return success(mapper.apply(value));
      }
      return failure(error);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      ReflectiveResult<?> that = (ReflectiveResult<?>) o;
      return Objects.equals(value, that.value) &&
          Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
      return Objects.hash(value, error);
    }

    /**
     * Creates a successful result.
     *
     * @param value the value
     * @param <T> the type of the result
     * @return the result
     */
    public static <T> ReflectiveResult<T> success(T value) {
      return new ReflectiveResult<>(value, null);
    }

    /**
     * Creates a new failure result.
     *
     * @param error the error
     * @param <T> the type of the return value. Anything you wish, you can not get it out
     * @return the result
     */
    public static <T> ReflectiveResult<T> failure(Throwable error) {
      return new ReflectiveResult<>(null, error);
    }
  }

}
