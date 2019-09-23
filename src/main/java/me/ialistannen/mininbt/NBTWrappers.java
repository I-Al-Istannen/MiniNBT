package me.ialistannen.mininbt;

import static me.ialistannen.mininbt.ReflectionUtil.NameSpace.NMS;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import me.ialistannen.mininbt.ReflectionUtil.MethodPredicate;
import me.ialistannen.mininbt.ReflectionUtil.ReflectResponse;

/**
 * Provides wrapper objects to abstract the NBT versions. Probably way too complicated...
 */
@SuppressWarnings({"WeakerAccess", "unused", "OptionalGetWithoutIsPresent"})
public class NBTWrappers {

  /**
   * A base class for the essential methods
   */
  public static abstract class INBTBase {

    public INBTBase() {
    }

    abstract Object toNBT();

    /**
     * @param nbtObject The NBT object
     * @return The correct {@link INBTBase} or null if the tag is not supported
     */
    public static INBTBase fromNBT(Object nbtObject) {
      switch (nbtObject.getClass().getSimpleName()) {
        case "NBTTagByte": {
          return NBTTagByte.fromNBT(nbtObject);
        }
        case "NBTTagShort": {
          return NBTTagShort.fromNBT(nbtObject);
        }
        case "NBTTagInt": {
          return NBTTagInt.fromNBT(nbtObject);
        }
        case "NBTTagLong": {
          return NBTTagLong.fromNBT(nbtObject);
        }
        case "NBTTagFloat": {
          return NBTTagFloat.fromNBT(nbtObject);
        }
        case "NBTTagDouble": {
          return NBTTagDouble.fromNBT(nbtObject);
        }
        case "NBTTagByteArray": {
          return NBTTagByteArray.fromNBT(nbtObject);
        }
        case "NBTTagIntArray": {
          return NBTTagIntArray.fromNBT(nbtObject);
        }
        case "NBTTagString": {
          return NBTTagString.fromNBT(nbtObject);
        }
        case "NBTTagCompound": {
          return NBTTagCompound.fromNBT(nbtObject);
        }
        case "NBTTagList": {
          return NBTTagList.fromNBT(nbtObject);
        }
      }
      return null;
    }
  }

  /**
   * A NBTTagString
   */
  public static class NBTTagString extends INBTBase {

    private static final Constructor<?> NBT_TAG_STRING_CONSTRUCTOR = ReflectionUtil
        .getConstructor(
            ReflectionUtil.getClass(NMS, "NBTTagString").get(), String.class)
        .getValue();

    private String string;

    /**
     * @param string The String value
     */
    public NBTTagString(String string) {
      Objects.requireNonNull(string, "string can not be null!");
      this.string = string;
    }

    /**
     * @param string The new value
     */
    public void setString(String string) {
      Objects.requireNonNull(string, "string can not be null!");
      this.string = string;
    }

    /**
     * @return The String value
     */
    public String getString() {
      return string;
    }

    @Override
    public Object toNBT() {
      return ReflectionUtil.instantiate(NBT_TAG_STRING_CONSTRUCTOR, getString()).getValue();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      ReflectResponse<Object> data = ReflectionUtil.getFieldValue("data",
          nbtObject.getClass(),
          nbtObject);
      if (!data.isValuePresent()) {
        System.err.println("An error occurred. Field not found in fromNBT String");
        return null;
      }
      return new NBTTagString((String) data.getValue());
    }

    @Override
    public String toString() {
      return "NBTTagString{" +
          "string='" + string + '\'' +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof NBTTagString)) {
        return false;
      }
      NBTTagString that = (NBTTagString) o;
      return Objects.equals(string, that.string);
    }

    @Override
    public int hashCode() {
      return Objects.hash(string);
    }
  }

  /**
   * A NBTTagCompound
   */
  public static class NBTTagCompound extends INBTBase {

    private static final Constructor<?> NBT_TAG_COMPOUND_CONSTRUCTOR = ReflectionUtil
        .getConstructor(
            ReflectionUtil.getClass(NMS, "NBTTagCompound").get())
        .getValue();

    private final Map<String, INBTBase> map = new HashMap<>();

    public void set(String key, INBTBase value) {
      Objects.requireNonNull(key, "key can not be null!");
      Objects.requireNonNull(value, "value can not be null!");
      map.put(key, value);
    }

    public void setByte(String key, byte value) {
      Objects.requireNonNull(key, "key can not be null!");
      map.put(key, new NBTTagByte(value));
    }

    public void setShort(String key, short value) {
      Objects.requireNonNull(key, "key can not be null!");
      map.put(key, new NBTTagShort(value));
    }

    public void setInt(String key, int value) {
      Objects.requireNonNull(key, "key can not be null!");
      map.put(key, new NBTTagInt(value));
    }

    public void setLong(String key, long value) {
      Objects.requireNonNull(key, "key can not be null!");
      map.put(key, new NBTTagLong(value));
    }

    public void setFloat(String key, float value) {
      Objects.requireNonNull(key, "key can not be null!");
      map.put(key, new NBTTagFloat(value));
    }

    public void setDouble(String key, double value) {
      Objects.requireNonNull(key, "key can not be null!");
      map.put(key, new NBTTagDouble(value));
    }

    public void setString(String key, String value) {
      Objects.requireNonNull(value, "value can not be null!");
      map.put(key, new NBTTagString(value));
    }

    public void setByteArray(String key, byte[] value) {
      Objects.requireNonNull(key, "key can not be null!");
      map.put(key, new NBTTagByteArray(value));
    }

    public void setIntArray(String key, int[] value) {
      Objects.requireNonNull(key, "key can not be null!");
      map.put(key, new NBTTagIntArray(value));
    }

    public void setBoolean(String key, boolean value) {
      setByte(key, (byte) (value ? 1 : 0));
    }

    public boolean hasKey(String key) {
      Objects.requireNonNull(key, "key can not be null!");
      return map.containsKey(key);
    }

    public boolean hasKeyOfType(String key, Class<? extends INBTBase> type) {
      Objects.requireNonNull(key, "key can not be null!");
      Objects.requireNonNull(type, "type can not be null!");
      return map.containsKey(key) && map.get(key) != null && map.get(key).getClass() == type;
    }

    public void remove(String key) {
      Objects.requireNonNull(key, "key can not be null!");
      map.remove(key);
    }

    /**
     * @param key The key
     * @return The assigned {@link INBTBase} or null if none
     */
    public INBTBase get(String key) {
      Objects.requireNonNull(key, "key can not be null!");
      return map.get(key);
    }

    /**
     * @param key The key
     * @return The number or 0 if not found.
     */
    public byte getByte(String key) {
      if (!hasKey(key) || !hasKeyOfType(key, NBTTagByte.class)) {
        return 0;
      }
      return ((NBTTagByte) get(key)).getAsByte();
    }

    /**
     * @param key The key
     * @return The number or 0 if not found.
     */
    public short getShort(String key) {
      if (!hasKey(key) || !hasKeyOfType(key, NBTTagShort.class)) {
        return 0;
      }
      return ((NBTTagShort) get(key)).getAsShort();
    }

    /**
     * @param key The key
     * @return The number or 0 if not found.
     */
    public int getInt(String key) {
      if (!hasKey(key) || !hasKeyOfType(key, NBTTagInt.class)) {
        return 0;
      }
      return ((NBTTagInt) get(key)).getAsInt();
    }

    /**
     * @param key The key
     * @return The number or 0 if not found.
     */
    public long getLong(String key) {
      if (!hasKey(key) || !hasKeyOfType(key, NBTTagLong.class)) {
        return 0;
      }
      return ((NBTTagLong) get(key)).getAsLong();
    }

    /**
     * @param key The key
     * @return The number or 0 if not found.
     */
    public float getFloat(String key) {
      if (!hasKey(key) || !hasKeyOfType(key, NBTTagFloat.class)) {
        return 0;
      }
      return ((NBTTagFloat) get(key)).getAsFloat();
    }

    /**
     * @param key The key
     * @return The number or 0 if not found.
     */
    public double getDouble(String key) {
      if (!hasKey(key) || !hasKeyOfType(key, NBTTagDouble.class)) {
        return 0;
      }
      return ((NBTTagDouble) get(key)).getAsDouble();
    }

    /**
     * @param key The key
     * @return The String or null if not found.
     */
    public String getString(String key) {
      if (!hasKey(key) || !hasKeyOfType(key, NBTTagString.class)) {
        return null;
      }
      return ((NBTTagString) get(key)).getString();
    }

    /**
     * @param key The key
     * @return The byte array or null if not found or wrong type.
     */
    public byte[] getByteArray(String key) {
      if (!hasKey(key) || !hasKeyOfType(key, NBTTagByteArray.class)) {
        return null;
      }
      return ((NBTTagByteArray) get(key)).getValue();
    }

    /**
     * @param key The key
     * @return The byte int or null if not found or wrong type.
     */
    public int[] getIntArray(String key) {
      if (!hasKey(key) || !hasKeyOfType(key, NBTTagIntArray.class)) {
        return null;
      }
      return ((NBTTagIntArray) get(key)).getValue();
    }

    /**
     * @param key The key
     * @return The boolean
     */
    public boolean getBoolean(String key) {
      return getByte(key) != 0;
    }

    /**
     * Checks if this compound is empty
     *
     * @return True if there are no keys
     */
    public boolean isEmpty() {
      return map.isEmpty();
    }

    /**
     * All the entries
     *
     * @return A Map with all the entries. Unmodifiable.
     */
    public Map<String, INBTBase> getAllEntries() {
      return Collections.unmodifiableMap(map);
    }

    /**
     * Returns a <b>reference</b> to the map
     *
     * @return The raw map. <b><i>Modify it at your own risk.</i></b>
     */
    public Map<String, INBTBase> getRawMap() {
      return map;
    }

    @Override
    public Object toNBT() {
      Object compound = ReflectionUtil.instantiate(NBT_TAG_COMPOUND_CONSTRUCTOR).getValue();

      Optional<Class<?>> nbtBase = ReflectionUtil.getClass(NMS, "NBTBase");
      if (!nbtBase.isPresent()) {
        System.out.println("Can't find NBTBase class from NBTTagCompound toNBT");
        return null;
      }

      ReflectResponse<Method> setMethod = ReflectionUtil.getMethod(compound.getClass(),
          new MethodPredicate()
              .withName("set")
              .withParameters(String.class, nbtBase.get()));
      for (Map.Entry<String, INBTBase> entry : map.entrySet()) {
        ReflectResponse<Object> result = ReflectionUtil.invokeMethod(
            setMethod.getValue(), compound, entry.getKey(), entry.getValue().toNBT()
        );
        if (!result.isSuccessful()) {
          result.getException().printStackTrace();
        }
      }

      return compound;
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Collection<String> keys = new HashSet<>();
      for (Method method : nbtObject.getClass().getMethods()) {
        if (Modifier.isPublic(method.getModifiers()) && Set.class.isAssignableFrom(
            method.getReturnType())) {
          @SuppressWarnings("unchecked")
          Collection<String> collection = (Collection<String>) ReflectionUtil.invokeMethod(
              method,
              nbtObject).getValue();
          if (collection != null) {
            keys.addAll(collection);
          }
        }
      }
      NBTTagCompound compound = new NBTTagCompound();

      ReflectResponse<Method> getMethod = ReflectionUtil.getMethod(nbtObject.getClass(),
          new MethodPredicate()
              .withName("get")
              .withParameters(String.class));

      if (!getMethod.isSuccessful()) {
        System.out.println("Didn't find get method in NBTTagCompound class fromNBT");
        return null;
      }

      for (String key : keys) {
        Object value = ReflectionUtil.invokeMethod(getMethod.getValue(), nbtObject, key)
            .getValue();
        INBTBase base = INBTBase.fromNBT(value);
        if (base != null) {
          compound.set(key, base);
        }
      }

      return compound;
    }

    @Override
    public String toString() {
      return "NBTTagCompound{" +
          "map=" + map +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof NBTTagCompound)) {
        return false;
      }
      NBTTagCompound compound = (NBTTagCompound) o;
      return Objects.equals(map, compound.map);
    }

    @Override
    public int hashCode() {
      return Objects.hash(map);
    }
  }

  /**
   * A NBTTagList.
   */
  public static class NBTTagList extends INBTBase {

    private static final Constructor<?> NBT_TAG_LIST_CONSTRUCTOR;
    private static final BiConsumer<List<INBTBase>, Object> appendAll;

    static {
      Class<?> nbtTagList = ReflectionUtil.getClass(NMS, "NBTTagList").get();
      NBT_TAG_LIST_CONSTRUCTOR = ReflectionUtil
          .getConstructor(nbtTagList)
          .getValue();

      Optional<Class<?>> nbtBase = ReflectionUtil.getClass(NMS, "NBTBase");
      if (!nbtBase.isPresent()) {
        throw new RuntimeException("Can't find NBTBase class from NBTTagList toNBT");
      }

      // Up to 1.14.4 it was "add(NBTBase)"
      ReflectResponse<Method> addSingleParam = ReflectionUtil.getMethod(
          nbtTagList,
          new MethodPredicate()
              .withName("add")
              .withParameters(nbtBase.get())
      );
      if (addSingleParam.isValuePresent()) {
        appendAll = (tags, nbtList) -> {
          for (INBTBase tag : tags) {
            ReflectionUtil.invokeMethod(addSingleParam.getValue(), nbtList,
                tag.toNBT());
          }
        };
      } else {
        // In 1.14.4 it is "add(int index, NBTBase)"
        ReflectResponse<Method> addMultiParam = ReflectionUtil.getMethod(
            nbtTagList,
            new MethodPredicate()
                .withName("add")
                .withParameters(int.class, nbtBase.get())
        );
        if (!addMultiParam.isValuePresent()) {
          throw new RuntimeException("Error fetching NBTTagList add method");
        }
        appendAll = (tags, nbtList) -> {
          for (int i = 0; i < tags.size(); i++) {
            INBTBase tag = tags.get(i);
            ReflectionUtil.invokeMethod(addMultiParam.getValue(), nbtList, i,
                tag.toNBT());
          }
        };
      }
    }

    private final List<INBTBase> list = new ArrayList<>();

    /**
     * Adds the {@link INBTBase}, if the type of the list is correct or the list is empty
     *
     * @param base The {@link INBTBase} to add
     * @return True if it was added.
     */
    public boolean add(INBTBase base) {
      Objects.requireNonNull(base, "base can not be null!");
      return isType(base.getClass()) && list.add(base);
    }

    /**
     * Removes an {@link INBTBase} from the list
     *
     * @param base The {@link INBTBase} to remove
     * @return {@code true} if this list contained the specified element
     */
    public boolean remove(INBTBase base) {
      Objects.requireNonNull(base, "base can not be null!");
      return list.remove(base);
    }

    /**
     * Returns the item
     *
     * @param index The index of the item
     * @return The item
     */
    public INBTBase get(int index) {
      return list.get(index);
    }

    /**
     * Returns the amount of items
     *
     * @return The amount of items
     */
    public int size() {
      return list.size();
    }

    /**
     * @param type The type to check for
     * @return True if the list is empty or this type
     */
    public boolean isType(Class<? extends INBTBase> type) {
      Objects.requireNonNull(type, "type can not be null!");
      return list.isEmpty() || list.get(0).getClass() == type;
    }

    /**
     * Returns the list.
     *
     * @return The list of NBT elements. Unmodifiable. Use the add and remove functions.
     */
    public List<INBTBase> getList() {
      return Collections.unmodifiableList(list);
    }

    /**
     * A direct reference to the internal list.
     *
     * @return A direct reference to the internal list. Modifiable
     */
    public List<INBTBase> getRawList() {
      return list;
    }

    @Override
    public Object toNBT() {
      Object nbtList = ReflectionUtil.instantiate(NBT_TAG_LIST_CONSTRUCTOR).getValue();

      appendAll.accept(list, nbtList);

      return nbtList;
    }

    public static INBTBase fromNBT(Object nbtObject) {
      NBTTagList list = new NBTTagList();
      ReflectResponse<Object> listResponse = ReflectionUtil.getFieldValue("list",
          nbtObject.getClass(), nbtObject);

      if (!listResponse.isValuePresent()) {
        System.out.println(
            "An error occurred reading an NBTTagList from nbt. Response: " + listResponse);
      }

      List<?> savedList = (List<?>) listResponse.getValue();
      if (savedList == null) {
        return list;
      }
      for (Object entry : savedList) {
        list.add(INBTBase.fromNBT(entry));
      }
      return list;
    }

    @Override
    public String toString() {
      return "NBTTagList{" +
          "list=" + list +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof NBTTagList)) {
        return false;
      }
      NBTTagList that = (NBTTagList) o;
      return Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {
      return Objects.hash(list);
    }
  }

  /**
   * A number.
   */
  public static abstract class INBTNumber extends INBTBase {

    /**
     * @return The number as an int
     */
    public int getAsInt() {
      return (int) Math.round(getAsDouble());
    }

    /**
     * @return The number as a long.
     */
    public long getAsLong() {
      return Math.round(getAsDouble());
    }

    /**
     * @return The number as a double.
     */
    public abstract double getAsDouble();

    /**
     * @return The number as a float
     */
    public float getAsFloat() {
      return (float) getAsDouble();
    }

    /**
     * @return The number as a byte
     */
    public byte getAsByte() {
      return (byte) getAsInt();
    }

    /**
     * @return The number as a short
     */
    public short getAsShort() {
      return (short) getAsInt();
    }

    /**
     * Sets the value
     *
     * @param number The new value
     */
    public abstract void set(Number number);
  }

  /**
   * A NBTTagDouble
   */
  public static class NBTTagDouble extends INBTNumber {

    private static final Constructor<?> NBT_TAG_DOUBLE_CONSTRUCTOR = ReflectionUtil
        .getConstructor(
            ReflectionUtil.getClass(NMS, "NBTTagDouble").get(), double.class)
        .getValue();

    private double value;

    /**
     * @param value The Double value
     */
    public NBTTagDouble(double value) {
      this.value = value;
    }

    /**
     * @param value The new value
     */
    @Override
    public void set(Number value) {
      Objects.requireNonNull(value, "value can not be null!");
      this.value = value.doubleValue();
    }

    /**
     * @return The Double value
     */
    @Override
    public double getAsDouble() {
      return value;
    }

    @Override
    public Object toNBT() {
      return ReflectionUtil.instantiate(NBT_TAG_DOUBLE_CONSTRUCTOR, getAsDouble()).getValue();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Optional<Class<?>> clazz = ReflectionUtil.getClass(NMS, "NBTTagDouble");
      if (!clazz.isPresent()) {
        System.out.println("Can't find NBTTagDouble class");
        return null;
      }
      Method method = findNBTNumberGetMethod(clazz.get(), double.class);
      ReflectResponse<Object> response = ReflectionUtil.invokeMethod(method, nbtObject);
      if (!response.isValuePresent()) {
        System.out.println(
            "An error occurred reading from an NBTTagDouble! The value was null. Response object: "
                + response);
      }

      Double value = (Double) response.getValue();
      return value == null ? new NBTTagDouble(-1) : new NBTTagDouble(value);
    }

    @Override
    public String toString() {
      return "NBTTagDouble{" +
          "value=" + value +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof NBTTagDouble)) {
        return false;
      }
      NBTTagDouble that = (NBTTagDouble) o;
      return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }
  }

  /**
   * A NBTTagInt
   */
  public static class NBTTagInt extends INBTNumber {

    private static final Constructor<?> NBT_TAG_INT_CONSTRUCTOR = ReflectionUtil
        .getConstructor(
            ReflectionUtil.getClass(NMS, "NBTTagInt").get(), int.class)
        .getValue();

    private int value;

    /**
     * @param value The Int value
     */
    public NBTTagInt(int value) {
      this.value = value;
    }

    /**
     * @param value The new value
     */
    @Override
    public void set(Number value) {
      Objects.requireNonNull(value, "value can not be null!");
      this.value = value.intValue();
    }

    /**
     * @return The double value
     */
    @Override
    public double getAsDouble() {
      return value;
    }

    @Override
    public int getAsInt() {
      return value;
    }

    @Override
    public Object toNBT() {
      return ReflectionUtil.instantiate(NBT_TAG_INT_CONSTRUCTOR, getAsInt()).getValue();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Optional<Class<?>> clazz = ReflectionUtil.getClass(NMS, "NBTTagInt");
      if (!clazz.isPresent()) {
        System.out.println("Can't find NBTTagInt class");
        return null;
      }
      Method method = findNBTNumberGetMethod(clazz.get(), int.class);
      ReflectResponse<Object> response = ReflectionUtil.invokeMethod(method, nbtObject);
      if (!response.isValuePresent()) {
        System.out.println(
            "An error occurred reading from an NBTTagInt! The value was null. Response object: "
                + response);
      }

      Integer value = (Integer) response.getValue();

      return new NBTTagInt(value == null ? 0 : value);
    }

    @Override
    public String toString() {
      return "NBTTagInt{" +
          "value=" + value +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof NBTTagInt)) {
        return false;
      }
      NBTTagInt nbtTagInt = (NBTTagInt) o;
      return value == nbtTagInt.value;
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }
  }

  /**
   * A NBTTagIntArray
   */
  public static class NBTTagIntArray extends INBTBase {

    private static final Constructor<?> NBT_TAG_INT_ARRAY_CONSTRUCTOR = ReflectionUtil
        .getConstructor(
            ReflectionUtil.getClass(NMS, "NBTTagIntArray").get(), int[].class)
        .getValue();

    private int[] value;

    public NBTTagIntArray() {
    }

    /**
     * @param value The Int value
     */
    public NBTTagIntArray(int[] value) {
      Objects.requireNonNull(value, "value can not be null!");
      this.value = value;
    }

    /**
     * @return The saved integer array
     */
    public int[] getValue() {
      return value;
    }

    @Override
    public Object toNBT() {
      return ReflectionUtil.instantiate(NBT_TAG_INT_ARRAY_CONSTRUCTOR, (Object) getValue())
          .getValue();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      ReflectResponse<Method> methodResponse = ReflectionUtil.getMethod(nbtObject.getClass(),
          new MethodPredicate().withReturnType(int[].class));

      if (!methodResponse.isValuePresent()) {
        System.out.println("No getter found for NBTTagIntArray");
        return null;
      }
      ReflectResponse<Object> dataResponse = ReflectionUtil.invokeMethod(
          methodResponse.getValue(),
          nbtObject);

      if (!dataResponse.isSuccessful()) {
        System.out.println(
            "NBTTagIntArray getter method raised an error. The response object: "
                + dataResponse);
      }

      return new NBTTagIntArray((int[]) dataResponse.getValue());
    }

    @Override
    public String toString() {
      return "NBTTagIntArray{" +
          "value=" + Arrays.toString(value) +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof NBTTagIntArray)) {
        return false;
      }
      NBTTagIntArray that = (NBTTagIntArray) o;
      return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(value);
    }
  }

  /**
   * A NBTTagByte
   */
  public static class NBTTagByte extends INBTNumber {

    private static final Constructor<?> NBT_TAG_BYTE_CONSTRUCTOR = ReflectionUtil
        .getConstructor(
            ReflectionUtil.getClass(NMS, "NBTTagByte").get(), byte.class)
        .getValue();

    private byte value;

    /**
     * @param value The Byte value
     */
    public NBTTagByte(byte value) {
      this.value = value;
    }

    /**
     * @param value The new value
     */
    @Override
    public void set(Number value) {
      Objects.requireNonNull(value, "value can not be null!");
      this.value = value.byteValue();
    }

    /**
     * @return The double value
     */
    @Override
    public double getAsDouble() {
      return value;
    }

    @Override
    public Object toNBT() {
      return ReflectionUtil.instantiate(NBT_TAG_BYTE_CONSTRUCTOR, getAsByte()).getValue();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Optional<Class<?>> clazz = ReflectionUtil.getClass(NMS, "NBTTagByte");
      if (!clazz.isPresent()) {
        System.out.println("Can't find NBTTagByte class");
        return null;
      }
      Method method = findNBTNumberGetMethod(clazz.get(), byte.class);
      ReflectResponse<Object> response = ReflectionUtil.invokeMethod(method, nbtObject);
      if (!response.isValuePresent()) {
        System.out.println(
            "An error occurred reading from an NBTTagByte! The value was null. Response object: "
                + response);
      }
      Byte value = (Byte) response.getValue();
      return new NBTTagByte(value == null ? 0 : value);
    }

    @Override
    public String toString() {
      return "NBTTagByte{" +
          "value=" + value +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof NBTTagByte)) {
        return false;
      }
      NBTTagByte that = (NBTTagByte) o;
      return value == that.value;
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }
  }

  /**
   * A NBTTagByteArray
   */
  public static class NBTTagByteArray extends INBTBase {

    private static final Constructor<?> NBT_TAG_BYTE_ARRAY_CONSTRUCTOR = ReflectionUtil
        .getConstructor(
            ReflectionUtil.getClass(NMS, "NBTTagByteArray").get(), byte[].class)
        .getValue();

    private byte[] value;

    public NBTTagByteArray() {
    }

    /**
     * @param value The Byte value
     */
    public NBTTagByteArray(byte[] value) {
      Objects.requireNonNull(value, "value can not be null!");
      this.value = value;
    }

    /**
     * @return The saved bytes
     */
    public byte[] getValue() {
      return value;
    }

    @Override
    public Object toNBT() {
      return ReflectionUtil.instantiate(NBT_TAG_BYTE_ARRAY_CONSTRUCTOR, (Object) getValue())
          .getValue();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      ReflectResponse<Method> methodResponse = ReflectionUtil.getMethod(nbtObject.getClass(),
          new MethodPredicate().withReturnType(byte[].class));

      if (!methodResponse.isValuePresent()) {
        System.out.println("No getter found for NBTTagByteArray!");
        return null;
      }
      ReflectResponse<Object> dataResponse = ReflectionUtil.invokeMethod(
          methodResponse.getValue(),
          nbtObject);

      if (!dataResponse.isSuccessful()) {
        System.out.println(
            "NBTTagByteArray getter method raised an error. The response object: "
                + dataResponse);
      }

      return new NBTTagByteArray((byte[]) dataResponse.getValue());
    }

    @Override
    public String toString() {
      return "NBTTagByteArray{" +
          "value=" + Arrays.toString(value) +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof NBTTagByteArray)) {
        return false;
      }
      NBTTagByteArray that = (NBTTagByteArray) o;
      return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(value);
    }
  }

  /**
   * A NBTTagShort
   */
  public static class NBTTagShort extends INBTNumber {

    private static final Constructor<?> NBT_TAG_SHORT_CONSTRUCTOR = ReflectionUtil
        .getConstructor(
            ReflectionUtil.getClass(NMS, "NBTTagShort").get(), short.class)
        .getValue();

    private short value;

    /**
     * @param value The Short value
     */
    public NBTTagShort(short value) {
      this.value = value;
    }

    /**
     * @param value The new value
     */
    @Override
    public void set(Number value) {
      Objects.requireNonNull(value, "value can not be null!");
      this.value = value.shortValue();
    }

    /**
     * @return The double value
     */
    @Override
    public double getAsDouble() {
      return value;
    }

    @Override
    public Object toNBT() {
      return ReflectionUtil.instantiate(NBT_TAG_SHORT_CONSTRUCTOR, getAsShort()).getValue();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Optional<Class<?>> clazz = ReflectionUtil.getClass(NMS, "NBTTagShort");
      if (!clazz.isPresent()) {
        System.out.println("Can't find NBTTagShort class");
        return null;
      }
      Method method = findNBTNumberGetMethod(clazz.get(), short.class);
      ReflectResponse<Object> response = ReflectionUtil.invokeMethod(method, nbtObject);
      if (!response.isValuePresent()) {
        System.out.println(
            "An error occurred reading from an NBTTagShort! The value was null. Response object: "
                + response);
      }
      Short value = (Short) response.getValue();
      return new NBTTagShort(value == null ? 0 : value);
    }

    @Override
    public String toString() {
      return "NBTTagShort{" +
          "value=" + value +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof NBTTagShort)) {
        return false;
      }
      NBTTagShort that = (NBTTagShort) o;
      return value == that.value;
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }
  }

  /**
   * A NBTTagLong
   */
  public static class NBTTagLong extends INBTNumber {

    private static final Constructor<?> NBT_TAG_LONG_CONSTRUCTOR = ReflectionUtil
        .getConstructor(
            ReflectionUtil.getClass(NMS, "NBTTagLong").get(), long.class)
        .getValue();

    private long value;

    /**
     * @param value The Long value
     */
    public NBTTagLong(long value) {
      this.value = value;
    }

    /**
     * @param value The new value
     */
    @Override
    public void set(Number value) {
      Objects.requireNonNull(value, "value can not be null!");
      this.value = value.longValue();
    }

    /**
     * @return The double value
     */
    @Override
    public double getAsDouble() {
      return value;
    }

    @Override
    public long getAsLong() {
      return value;
    }

    @Override
    public Object toNBT() {
      return ReflectionUtil.instantiate(NBT_TAG_LONG_CONSTRUCTOR, getAsLong()).getValue();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Optional<Class<?>> clazz = ReflectionUtil.getClass(NMS, "NBTTagLong");
      if (!clazz.isPresent()) {
        System.out.println("Can't find NBTTagLong class");
        return null;
      }
      Method method = findNBTNumberGetMethod(clazz.get(), long.class);
      ReflectResponse<Object> response = ReflectionUtil.invokeMethod(method, nbtObject);
      if (!response.isValuePresent()) {
        System.out.println(
            "An error occurred reading from an NBTTagLong! The value was null. Response object: "
                + response);
      }

      Long value = (Long) response.getValue();
      return new NBTTagLong(value == null ? 0 : value);
    }

    @Override
    public String toString() {
      return "NBTTagLong{" +
          "value=" + value +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof NBTTagLong)) {
        return false;
      }
      NBTTagLong that = (NBTTagLong) o;
      return value == that.value;
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }
  }

  /**
   * A NBTTagFloat
   */
  public static class NBTTagFloat extends INBTNumber {

    private static final Constructor<?> NBT_TAG_LONG_CONSTRUCTOR = ReflectionUtil
        .getConstructor(
            ReflectionUtil.getClass(NMS, "NBTTagFloat").get(), float.class)
        .getValue();

    private float value;

    /**
     * @param value The Float value
     */
    public NBTTagFloat(float value) {
      this.value = value;
    }

    /**
     * @param value The new value
     */
    @Override
    public void set(Number value) {
      Objects.requireNonNull(value, "value can not be null!");
      this.value = value.floatValue();
    }

    /**
     * @return The double value
     */
    @Override
    public double getAsDouble() {
      return value;
    }

    @Override
    public Object toNBT() {
      return ReflectionUtil.instantiate(NBT_TAG_LONG_CONSTRUCTOR, getAsFloat()).getValue();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Optional<Class<?>> clazz = ReflectionUtil.getClass(NMS, "NBTTagFloat");
      if (!clazz.isPresent()) {
        System.out.println("Can't find NBTTagFloat class");
        return null;
      }
      Method method = findNBTNumberGetMethod(clazz.get(), float.class);
      ReflectResponse<Object> response = ReflectionUtil.invokeMethod(method, nbtObject);
      if (!response.isValuePresent()) {
        System.out.println(
            "An error occurred reading from an NBTTagFloat! The value was null. Response object: "
                + response);
      }

      Float value = (Float) response.getValue();
      return new NBTTagFloat(value == null ? 0 : value);
    }

    @Override
    public String toString() {
      return "NBTTagFloat{" +
          "value=" + value +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof NBTTagFloat)) {
        return false;
      }
      NBTTagFloat that = (NBTTagFloat) o;
      return Float.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }
  }

  /**
   * Returns the method also existing in the Superclass
   *
   * @param clazz The Class to invoke it on
   * @param returnClass The Return class it should have
   * @return The found Method
   */
  private static Method findNBTNumberGetMethod(Class<?> clazz, Class<?> returnClass) {
    for (Method method : clazz.getDeclaredMethods()) {
      if (!method.getReturnType().equals(returnClass)) {
        continue;
      }
      if (!Modifier.isPublic(method.getModifiers())) {
        continue;
      }
      if (Modifier.isStatic(method.getModifiers())) {
        continue;
      }
      if (method.getName().equals("hashCode") || method.getName().equals("getTypeId")) {
        continue;
      }

      try {
        // noinspection ConfusingArgumentToVarargsMethod // The array is
        // desired. If it is in the Superclass too, it is out method
        clazz.getSuperclass().getMethod(method.getName(), method.getParameterTypes());
        return method;
      } catch (NoSuchMethodException ignored) {
      }
    }
    return null;
  }
}
