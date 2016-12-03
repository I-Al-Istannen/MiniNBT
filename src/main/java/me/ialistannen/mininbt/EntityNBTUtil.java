package me.ialistannen.mininbt;

import static me.ialistannen.mininbt.ReflectionUtil.NameSpace.NMS;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import me.ialistannen.mininbt.NBTWrappers.INBTBase;
import me.ialistannen.mininbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.mininbt.ReflectionUtil.NameSpace;

/**
 * A utility to modify Entities NBT-tags. Uses reflection and scans through all
 * methods to find the right ones, so it might change in future releases.
 * <p>
 * The methods must only be called when at least one world is loaded, as it
 * needs to spawn a sample entity (ArmorStand). <br>
 * It will be enforced by throwing an {@link IllegalStateException}.
 * <p>
 * <br>
 * <i><b>DISCLAIMER: </b></i> <br>
 * Doesn't allow for the addition of new tags. You can modify the tags of the
 * TileEntity, but not add new ones. This is a limitation of minecraft.
 */
@SuppressWarnings("unused") // just by me...
public class EntityNBTUtil {

    private static Method loadFromNbtMethod, saveToNbtMethod, getHandle;
    private static boolean error = false;

    static {
        Optional<Class<?>> craftEntityClass = ReflectionUtil.getClass(NameSpace.OBC, "entity.CraftEntity");
        if (!craftEntityClass.isPresent()) {
            error = true;
            System.out.println("Can't find CraftEntity class! @EntityNBTUtil static block");
        } else {
            ReflectionUtil.ReflectResponse<Method> getHandleMethod = ReflectionUtil
                      .getMethod(craftEntityClass.get(), new ReflectionUtil.MethodPredicate().withName("getHandle"));

            if (getHandleMethod.isValuePresent()) {
                getHandle = getHandleMethod.getValue();
            } else {
                System.out.println("getHandle not found: "
                          + Bukkit.getServer().getClass().getName());
                error = true;
            }
        }
        getLoadingMethods();
    }

    /**
     * Gets the NMS handle of a bukkit entity
     *
     * @param entity The Bukkit entity
     *
     * @return The NMS entity
     *
     * @throws IllegalStateException if {@link #ensureNoError()} throws it
     */
    private static Object toNMSEntity(Entity entity) {
        ensureNoError();
        return ReflectionUtil.invokeMethod(getHandle, entity).getValue();
    }

    /**
     * @throws IllegalStateException If {@link #error} is true
     */
    private static void ensureNoError() {
        if (error) {
            throw new IllegalStateException("A critical, non recoverable error occurred earlier.");
        }
    }

    /**
     * Gets the NBT-Tag of an entity
     *
     * @param entity The entity to get the nbt tag for
     *
     * @return The NBTTag of the entity
     *
     * @throws NullPointerException if {@code entity} is null
     * @throws IllegalStateException if a critical, non recoverable error
     * occurred earlier (loading methods).
     */
    @SuppressWarnings("WeakerAccess") // util,...
    public static NBTTagCompound getNbtTag(Entity entity) {
        Objects.requireNonNull(entity, "entity can not be null");

        ensureNoError();

        Object nmsEntity = toNMSEntity(entity);
        NBTTagCompound entityNBT = new NBTTagCompound();

        {
            Object nbtNMS = entityNBT.toNBT();
            ReflectionUtil.invokeMethod(saveToNbtMethod, nmsEntity, nbtNMS);
            if (nbtNMS == null) {
                throw new NullPointerException("SaveToNBT method set Nbt tag to null. Version incompatible?"
                          + nmsEntity.getClass());
            }
            entityNBT = (NBTTagCompound) INBTBase.fromNBT(nbtNMS);
        }

        return entityNBT;
    }

    /**
     * Applies the {@link NBTTagCompound} tp the passed {@link Entity}
     *
     * @param entity The entity to modify the nbt tag
     * @param compound The {@link NBTTagCompound} to set it to
     *
     * @throws NullPointerException if {@code entity} or {@code compound} is
     * null
     * @throws IllegalStateException if a critical, non recoverable error
     * occurred earlier (loading methods).
     */
    @SuppressWarnings("WeakerAccess") // util...
    public static void setNbtTag(Entity entity, NBTTagCompound compound) {
        Objects.requireNonNull(entity, "entity can not be null");
        Objects.requireNonNull(compound, "compound can not be null");

        ensureNoError();

        Object nmsEntity = toNMSEntity(entity);

        ReflectionUtil.invokeMethod(loadFromNbtMethod, nmsEntity, compound.toNBT());
    }

    /**
     * Appends the {@link NBTTagCompound} to the entities NBT tag, overwriting
     * already set values
     *
     * @param entity The entity whose NbtTag to change
     * @param compound The {@link NBTTagCompound} whose values you want to add
     *
     * @throws NullPointerException if {@code entity} or {@code compound} is
     * null
     * @throws IllegalStateException if a critical, non recoverable error
     * occurred earlier (loading methods).
     */
    public static void appendNbtTag(Entity entity, NBTTagCompound compound) {
        // yes, getNbtTag would throw them as well.
        Objects.requireNonNull(entity, "entity can not be null");
        Objects.requireNonNull(compound, "compound can not be null");

        ensureNoError();

        NBTTagCompound entityData = getNbtTag(entity);

        for (Entry<String, INBTBase> entry : compound.getAllEntries().entrySet()) {
            entityData.set(entry.getKey(), entry.getValue());
        }

        setNbtTag(entity, entityData);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static void getLoadingMethods() {
        if (Bukkit.getWorlds().isEmpty()) {
            throw new IllegalStateException("Called me before at least one world was loaded...");
        }
        Entity sample = Bukkit.getWorlds().get(0)
                  .spawnEntity(Bukkit.getWorlds().get(0).getSpawnLocation(), EntityType.ARMOR_STAND);

        Object nmsSample = ReflectionUtil.invokeMethod(getHandle, sample).getValue();

        Optional<Class<?>> entityClass = ReflectionUtil.getClass(NMS, "Entity");
        if (!entityClass.isPresent()) {
            error = true;
            System.out.println("Couldn't find entity class");
            sample.remove();
            return;
        }

        if (ReflectionUtil.getMajorVersion() > 1 || ReflectionUtil.getMinorVersion() > 8) {
            initializeHigherThan1_9(entityClass.get(), sample, nmsSample);
        } else {
            initializeLowerThan1_9(entityClass.get(), sample, nmsSample);
        }

        if (saveToNbtMethod == null || loadFromNbtMethod == null) {
            System.out.println("Couldn't find the methods. This could help: "
                      + entityClass.get().getName()
                      + " save " + (saveToNbtMethod == null)
                      + " load " + (loadFromNbtMethod == null));
            error = true;
        }
        sample.remove();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static void initializeHigherThan1_9(Class<?> entityClass, Entity sample, Object nmsSample) {
        // load the loading method
        initializeLowerThan1_9(entityClass, sample, nmsSample);

        for (Method method : entityClass.getMethods()) {
            // the save method : "public NBTTagCompound(final NBTTagCompound
            // compound)"
            if (method.getReturnType().equals(ReflectionUtil.getClass(NMS, "NBTTagCompound").get())
                      && method.getParameterTypes().length == 1
                      && method.getParameterTypes()[0].equals(ReflectionUtil.getClass(NMS, "NBTTagCompound").get())
                      && Modifier.isPublic(method.getModifiers())
                      && !Modifier.isStatic(method.getModifiers())) {

                Object testCompound = new NBTTagCompound().toNBT();
                ReflectionUtil.invokeMethod(method, nmsSample, testCompound);

                NBTTagCompound compound = (NBTTagCompound) INBTBase.fromNBT(testCompound);

                if (compound == null) {
                    continue;
                }

                if (!compound.isEmpty()) {
                    if (saveToNbtMethod != null) {
                        saveToNbtMethod = null;
                        System.out.println("Couldn't find the saving method for an entity. This should help: "
                                  + entityClass.getName());
                        error = true;
                        return;
                    }
                    saveToNbtMethod = method;
                }
            }
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static void initializeLowerThan1_9(Class<?> entityClass, Entity sample, Object nmsSample) {

        for (Method method : entityClass.getMethods()) {
            if (method.getReturnType().equals(Void.TYPE)
                      && method.getParameterTypes().length == 1
                      && method.getParameterTypes()[0].equals(ReflectionUtil.getClass(NMS, "NBTTagCompound").get())
                      && Modifier.isPublic(method.getModifiers())
                      && !Modifier.isStatic(method.getModifiers())) {

                Object testCompound = new NBTTagCompound().toNBT();
                ReflectionUtil.invokeMethod(method, nmsSample, testCompound);

                NBTTagCompound compound = (NBTTagCompound) INBTBase.fromNBT(testCompound);
                if (compound == null) {
                    continue;
                }

                if (compound.isEmpty()) {
                    if (loadFromNbtMethod != null) {
                        System.out.println("Couldn't find the loading method for an entity. This should help: "
                                  + entityClass.getName()
                                  + " found methods: " + loadFromNbtMethod + " " + method);
                        loadFromNbtMethod = null;
                        error = true;
                        return;
                    }
                    loadFromNbtMethod = method;
                } else {
                    if (saveToNbtMethod != null) {
                        System.out.println("Couldn't find the saving method for an entity. This should help: "
                                  + entityClass.getName()
                                  + " found methods: " + saveToNbtMethod + " " + method);
                        error = true;
                        saveToNbtMethod = null;
                        return;
                    }
                    saveToNbtMethod = method;
                }
            }
        }
    }

}
