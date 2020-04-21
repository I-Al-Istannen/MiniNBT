package me.ialistannen.mininbt;

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
import java.util.Set;
import java.util.function.BiConsumer;

import me.ialistannen.mininbt.reflection.BukkitReflection.ClassLookup;
import me.ialistannen.mininbt.reflection.FluentReflection;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentConstructor;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentMethod;
import me.ialistannen.mininbt.reflection.FluentReflection.FluentType;
import me.ialistannen.mininbt.reflection.FluentReflection.ReflectiveResult;

/**
 * Provides wrapper objects to abstract the NBT versions.
 *
 * <p><br><em>Class initialization may throw a
 * {@link me.ialistannen.mininbt.reflection.ReflectionException}</em></p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
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
    private static final FluentType<?> NBT_TAG_STRING_CLASS = ClassLookup.NMS
        .forName("NBTTagString").getOrThrow();

    private static final FluentConstructor<?> NBT_TAG_STRING_CONSTRUCTOR = NBT_TAG_STRING_CLASS
        .findConstructor()
        .withParameters(String.class)
        .findSingle().getOrThrow();

    private static final FluentReflection.FluentField DATA_FIELD = NBT_TAG_STRING_CLASS
        .findField()
        .withName("data")
        .findSingle().getOrThrow();

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
      return NBT_TAG_STRING_CONSTRUCTOR.createInstance(getString()).getOrThrow();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Object stringValue = DATA_FIELD
          .getValue(nbtObject).getOrThrow();
      return new NBTTagString((String) stringValue);
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
    private static final FluentType<?> NBT_BASE = ClassLookup.NMS
        .forName("NBTBase").getOrThrow();

    private static final FluentType<?> NBT_TAG_COMPOUND_CLASS = ClassLookup.NMS
        .forName("NBTTagCompound").getOrThrow();

    private static final FluentConstructor<?> NBT_TAG_COMPOUND_CONSTRUCTOR = NBT_TAG_COMPOUND_CLASS
        .findConstructor()
        .withParameters()
        .findSingle().getOrThrow();

    private static final FluentMethod SET_METHOD = NBT_TAG_COMPOUND_CLASS
        .findMethod()
        .withName("set")
        .withParameters(String.class, NBT_BASE.getUnderlying())
        .findSingle().getOrThrow();

    private static final FluentMethod GET_METHOD = NBT_TAG_COMPOUND_CLASS
        .findMethod()
        .withName("get")
        .withParameters(String.class)
        .findSingle().getOrThrow();

    private static final FluentMethod GET_KEYSET_METHOD = NBT_TAG_COMPOUND_CLASS.findMethod()
        .withModifiers(Modifier.PUBLIC)
        .matchingMethod(method -> Set.class.isAssignableFrom(method.getReturnType()))
        .findSingle().getOrThrow();

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
      Object compound = NBT_TAG_COMPOUND_CONSTRUCTOR.createInstance().getOrThrow();

      for (Map.Entry<String, INBTBase> entry : map.entrySet()) {
        SET_METHOD.invoke(compound, entry.getKey(), entry.getValue().toNBT())
            .ensureSuccessful();
      }

      return compound;
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Collection<String> keys = GET_KEYSET_METHOD
          .invoke(nbtObject)
          .mapNotNull(it -> {
            @SuppressWarnings("unchecked")
            Collection<String> casted = (Collection<String>) it;
            return new HashSet<>(casted);
          })
          .getOrThrow();

      NBTTagCompound compound = new NBTTagCompound();

      for (String key : keys) {
        Object value = GET_METHOD.invoke(nbtObject, key).getOrThrow();
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
    private static final FluentType<?> NBT_TAG_LIST_CLASS = ClassLookup.NMS
        .forName("NBTTagList").getOrThrow();

    private static final FluentReflection.FluentField LIST_FIELD = NBT_TAG_LIST_CLASS.findField()
        .withName("list")
        .findSingle().getOrThrow();

    private static final FluentType<?> NBT_BASE_CLASS = ClassLookup.NMS
        .forName("NBTBase").getOrThrow();

    private static final FluentConstructor<?> NBT_TAG_LIST_CONSTRUCTOR = NBT_TAG_LIST_CLASS.findConstructor()
        .withParameters()
        .findSingle().getOrThrow();

    private static final BiConsumer<List<INBTBase>, Object> appendAll;

    static {
      // Up to 1.14.4 it was "add(NBTBase)"
      ReflectiveResult<FluentMethod> addSingleParam = NBT_TAG_LIST_CLASS.findMethod()
          .withName("add")
          .withParameters(NBT_BASE_CLASS.getUnderlying())
          .findSingle();

      if (addSingleParam.isPresent()) {
        appendAll = (tags, nbtList) -> {
          for (INBTBase tag : tags) {
            addSingleParam.getOrThrow().invoke(nbtList, tag.toNBT()).ensureSuccessful();
          }
        };
      } else {
        // In 1.14.4 it is "add(int index, NBTBase)"
        FluentMethod addMultiParam = NBT_TAG_LIST_CLASS.findMethod()
            .withName("add")
            .withParameters(int.class, NBT_BASE_CLASS.getUnderlying())
            .findSingle().getOrThrow();

        appendAll = (tags, nbtList) -> {
          for (int i = 0; i < tags.size(); i++) {
            INBTBase tag = tags.get(i);
            addMultiParam.invoke(nbtList, i, tag.toNBT()).ensureSuccessful();
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
      Object nbtList = NBT_TAG_LIST_CONSTRUCTOR.createInstance().getOrThrow();

      appendAll.accept(list, nbtList);

      return nbtList;
    }

    public static INBTBase fromNBT(Object nbtObject) {
      NBTTagList list = new NBTTagList();

      Object originalList = LIST_FIELD
          .getValue(nbtObject).getOrThrow();

      List<?> savedList = (List<?>) originalList;
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
    private static final FluentType<?> NBT_TAG_DOUBLE_CLASS = ClassLookup.NMS
        .forName("NBTTagDouble").getOrThrow();

    private static final FluentConstructor<?> NBT_TAG_DOUBLE_CONSTRUCTOR = ClassLookup.NMS
        .forName("NBTTagDouble").getOrThrow()
        .findConstructor()
        .withParameters(double.class)
        .findSingle().getOrThrow();

    private static final FluentMethod NUMBER_GET_METHOD = findNBTNumberGetMethod(NBT_TAG_DOUBLE_CLASS, double.class);

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
      return NBT_TAG_DOUBLE_CONSTRUCTOR.createInstance(getAsDouble()).getOrThrow();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Object actualValue = NUMBER_GET_METHOD.invoke(nbtObject).getOrThrow();

      Double value = (Double) actualValue;
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
    private static final FluentType<?> NBT_TAG_INT_CLASS = ClassLookup.NMS
            .forName("NBTTagInt").getOrThrow();

    private static final FluentConstructor<?> NBT_TAG_INT_CONSTRUCTOR = NBT_TAG_INT_CLASS
        .findConstructor()
        .withParameters(int.class)
        .findSingle().getOrThrow();

    private static final FluentMethod NUMBER_GET_METHOD = findNBTNumberGetMethod(NBT_TAG_INT_CLASS, int.class);

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
      return NBT_TAG_INT_CONSTRUCTOR.createInstance(getAsInt()).getOrThrow();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      FluentMethod method = NUMBER_GET_METHOD;

      Object actualValue = method.invoke(nbtObject).getOrThrow();

      Integer value = (Integer) actualValue;

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
    private static final FluentType<?> NBT_TAG_INT_ARRAY_CLASS = ClassLookup.NMS
        .forName("NBTTagIntArray").getOrThrow();

    private static final FluentConstructor<?> NBT_TAG_INT_ARRAY_CONSTRUCTOR = NBT_TAG_INT_ARRAY_CLASS
        .findConstructor()
        .withParameters(int[].class)
        .findSingle().getOrThrow();

    private static final FluentMethod GETTER_METHOD = NBT_TAG_INT_ARRAY_CLASS
            .findMethod()
            .withReturnType(int[].class)
            .withoutModifiers(Modifier.STATIC)
            .findSingle().getOrThrow();

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
      return NBT_TAG_INT_ARRAY_CONSTRUCTOR.createInstance((Object) getValue()).getOrThrow();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Object actualValue = GETTER_METHOD.invoke(nbtObject).getOrThrow();

      return new NBTTagIntArray((int[]) actualValue);
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
    private static final FluentType<?> NBT_TAG_BYTE_CLASS = ClassLookup.NMS
        .forName("NBTTagByte").getOrThrow();

    private static final FluentConstructor<?> NBT_TAG_BYTE_CONSTRUCTOR = NBT_TAG_BYTE_CLASS
        .findConstructor()
        .withParameters(byte.class)
        .findSingle().getOrThrow();

    private static final FluentMethod NUMBER_GET_METHOD = findNBTNumberGetMethod(NBT_TAG_BYTE_CLASS, byte.class);

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
      return NBT_TAG_BYTE_CONSTRUCTOR.createInstance(getAsByte()).getOrThrow();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Object actualValue = NUMBER_GET_METHOD.invoke(nbtObject).getOrThrow();

      Byte value = (Byte) actualValue;
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
    private static final FluentType<?> NBT_TAG_BYTE_ARRAY_CLASS = ClassLookup.NMS
        .forName("NBTTagByteArray").getOrThrow();

    private static final FluentConstructor<?> NBT_TAG_BYTE_ARRAY_CONSTRUCTOR = NBT_TAG_BYTE_ARRAY_CLASS
        .findConstructor()
        .withParameters(byte[].class)
        .findSingle().getOrThrow();

    private static final FluentMethod GETTER_METHOD = NBT_TAG_BYTE_ARRAY_CLASS.findMethod()
        .withReturnType(byte[].class)
        .withoutModifiers(Modifier.STATIC)
        .findSingle().getOrThrow();

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
      return NBT_TAG_BYTE_ARRAY_CONSTRUCTOR.createInstance((Object) getValue()).getOrThrow();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Object actualValue = GETTER_METHOD.invoke(nbtObject).getOrThrow();

      return new NBTTagByteArray((byte[]) actualValue);
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
    private static final FluentType<?> NBT_TAG_SHORT_CLASS = ClassLookup.NMS
        .forName("NBTTagShort").getOrThrow();

    private static final FluentConstructor<?> NBT_TAG_SHORT_CONSTRUCTOR = NBT_TAG_SHORT_CLASS
        .findConstructor()
        .withParameters(short.class)
        .findSingle().getOrThrow();

    private static final FluentMethod NUMBER_GET_METHOD = findNBTNumberGetMethod(NBT_TAG_SHORT_CLASS, short.class);

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
      return NBT_TAG_SHORT_CONSTRUCTOR.createInstance(getAsShort()).getOrThrow();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Object actualValue = NUMBER_GET_METHOD.invoke(nbtObject).getOrThrow();

      Short value = (Short) actualValue;
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
    private static final FluentType<?> NBT_TAG_LONG_CLASS = ClassLookup.NMS
        .forName("NBTTagLong").getOrThrow();

    private static final FluentConstructor<?> NBT_TAG_LONG_CONSTRUCTOR = NBT_TAG_LONG_CLASS
        .findConstructor()
        .withParameters(long.class)
        .findSingle().getOrThrow();

    private static final FluentMethod NUMBER_GET_METHOD = findNBTNumberGetMethod(NBT_TAG_LONG_CLASS, long.class);

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
      return NBT_TAG_LONG_CONSTRUCTOR.createInstance(getAsLong()).getOrThrow();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Object actualValue = NUMBER_GET_METHOD.invoke(nbtObject).getOrThrow();

      Long value = (Long) actualValue;
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
    private static final FluentType<?> NBT_TAG_FLOAT_CLASS = ClassLookup.NMS
        .forName("NBTTagFloat").getOrThrow();

    private static final FluentConstructor<?> NBT_TAG_FLOAT_CONSTRUCTOR = NBT_TAG_FLOAT_CLASS
        .findConstructor()
        .withParameters(float.class)
        .findSingle().getOrThrow();

    private static final FluentMethod NUMBER_GET_METHOD = findNBTNumberGetMethod(NBT_TAG_FLOAT_CLASS, float.class);

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
      return NBT_TAG_FLOAT_CONSTRUCTOR.createInstance(getAsFloat()).getOrThrow();
    }

    public static INBTBase fromNBT(Object nbtObject) {
      Object actualValue = NUMBER_GET_METHOD.invoke(nbtObject).getOrThrow();

      Float value = (Float) actualValue;
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
   * @param returnClass The return class it should have
   * @return The found method
   */
  private static FluentMethod findNBTNumberGetMethod(FluentType<?> clazz, Class<?> returnClass) {
    return clazz.findMethod()
        .withModifiers(Modifier.PUBLIC)
        .withoutModifiers(Modifier.STATIC)
        .withReturnType(returnClass)
        .matchingMethod(method -> !method.getName().equals("hashCode"))
        .matchingMethod(method -> !method.getName().equals("getTypeId"))
        .matchingMethod(method ->
            // method exists in superclass too, i.e. is overwritten
            FluentType.ofUnknown(clazz.getUnderlying().getSuperclass())
                .findMethod()
                .withName(method.getName())
                .withParameters(method.getParameterTypes())
                .findSingle()
                .isPresent()
        )
        .findSingle()
        .getOrThrow();
  }
}
