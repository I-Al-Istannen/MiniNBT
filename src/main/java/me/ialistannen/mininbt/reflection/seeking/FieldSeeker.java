package me.ialistannen.mininbt.reflection.seeking;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentField;
import me.ialistannen.mininbt.reflection.FluentReflection.ReflectiveResult;
import me.ialistannen.mininbt.reflection.ReflectionException;

/**
 * Searches a field. Walks along the inheritance hierarchy if the field can not be found if not
 * disabled.
 *
 * @param <C> the class the field is in
 */
public class FieldSeeker<C extends Class<C>> implements ElementSeeker<FluentField> {

  private List<Predicate<Field>> filters;
  private Class<C> clazz;
  private boolean walkHierarchy;

  /**
   * Creates a new field seeker for a given class.
   *
   * @param clazz the class to get it for
   */
  public FieldSeeker(Class<C> clazz) {
    this.clazz = clazz;
    this.filters = new ArrayList<>();
    this.walkHierarchy = true;
  }

  /**
   * Searches for a field with a given name.
   *
   * @param name the name
   * @return this field seeker
   */
  public FieldSeeker<C> withName(String name) {
    return withName(name::equals);
  }

  /**
   * Searches for a field with a name matching the predicate.
   *
   * @param namePredicate the name predicate
   * @return this field seeker
   */
  public FieldSeeker<C> withName(Predicate<String> namePredicate) {
    return matching(field -> namePredicate.test(field.getName()));
  }

  /**
   * Searches for a field with a given type.
   *
   * @param type the type of the field
   * @return this field seeker
   */
  public FieldSeeker<C> withType(Class<?> type) {
    return matching(field -> field.getType() == type);
  }

  /**
   * Searches for a field matching the predicate.
   *
   * @param predicate the predicate
   * @return this field seeker
   */
  public FieldSeeker<C> matching(Predicate<Field> predicate) {
    filters.add(predicate);
    return this;
  }

  /**
   * Makes the seeker search just in the given class and not also in super classes.
   *
   * @return this field seeker
   */
  public FieldSeeker<C> dontWalkHierarchy() {
    walkHierarchy = false;
    return this;
  }

  @Override
  public ReflectiveResult<List<FluentField>> findAll() {
    List<FluentField> fields = new ArrayList<>(findInClass(clazz));

    if (walkHierarchy) {
      Class<?> currentClass = clazz.getSuperclass();

      while (currentClass != null) {
        fields.addAll(findInClass(currentClass));
        currentClass = currentClass.getSuperclass();
      }
    }

    if (fields.isEmpty()) {
      return ReflectiveResult.failure(new ReflectionException("Field not found"));
    }

    return ReflectiveResult.success(fields);
  }

  private List<FluentField> findInClass(Class<?> clazz) {
    List<FluentField> fields = findInFields(clazz.getFields());
    fields.addAll(findInFields(clazz.getDeclaredFields()));
    return fields;
  }

  private List<FluentField> findInFields(Field[] fields) {
    return Arrays.stream(fields)
        .filter(field -> filters.stream().allMatch(it -> it.test(field)))
        .map(FluentField::new)
        .collect(Collectors.toList());
  }
}
