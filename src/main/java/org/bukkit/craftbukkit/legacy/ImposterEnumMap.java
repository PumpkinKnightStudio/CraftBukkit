package org.bukkit.craftbukkit.legacy;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The "I can't believe it works" map.
 * It replaces every EnumMap with the ImposterEnumMap and uses a HashMap instead of an object array.
 * Used so that plugins which use an EnumMap still work.
 *
 * @deprecated only for legacy use, do not use
 */
@Deprecated
public class ImposterEnumMap extends EnumMap<DummyEnum, Object> {

    private static boolean WARN_PUT = true;

    private final Class<?> objectClass;
    private final Map map;

    public ImposterEnumMap(Class<?> objectClass) {
        super(DummyEnum.class);
        this.objectClass = objectClass;
        this.map = getMap(objectClass);
    }

    public ImposterEnumMap(EnumMap enumMap) {
        super(DummyEnum.class);

        if (enumMap instanceof ImposterEnumMap) {
            this.objectClass = ((ImposterEnumMap) enumMap).objectClass;
            this.map = getMap(objectClass);
            this.map.putAll(enumMap);
        } else {
            this.objectClass = DummyEnum.class;
            this.map = enumMap.clone();
        }
    }

    public ImposterEnumMap(Map map) {
        super(DummyEnum.class);

        if (map instanceof ImposterEnumMap) {
            this.objectClass = ((ImposterEnumMap) map).objectClass;
            this.map = getMap(objectClass);
        } else {
            this.objectClass = DummyEnum.class;
            this.map = new HashMap();
        }

        this.map.putAll(map);
    }

    private static Map getMap(Class<?> objectClass) {
        // Since we replace every enum map we might also replace some maps which are for real enums.
        // If this is the case use a EnumMap instead of a HashMap
        if (objectClass.isEnum()) {
            return new EnumMap(objectClass);
        } else {
            return new HashMap();
        }
    }

    // The normal put method relies as key on an Enum, which causes a ClasCastException
    // So we rout every put call to this method and handle it accordingly
    public static Object putToMap(Map map, Object key, Object value) {
        if (map instanceof ImposterEnumMap) {
            return ((ImposterEnumMap) map).map.put(key, value);
        }

        return map.put(key, value);
    }

    public static void putAllToMap(Map map, Map other) {
        if (map instanceof ImposterEnumMap) {
            ((ImposterEnumMap) map).map.putAll(other);
            return;
        }

        map.putAll(other);
    }

    public static Object putIfAbsentToMap(Map map, Object key, Object value) {
        if (map instanceof ImposterEnumMap) {
            return ((ImposterEnumMap) map).map.putIfAbsent(key, value);
        }

        return map.putIfAbsent(key, value);
    }

    public static boolean replaceToMap(Map map, Object key, Object oldValue, Object value) {
        if (map instanceof ImposterEnumMap) {
            return ((ImposterEnumMap) map).map.replace(key, oldValue, value);
        }

        return map.replace(key, oldValue, value);
    }

    public static Object replaceToMap(Map map, Object key, Object value) {
        if (map instanceof ImposterEnumMap) {
            return ((ImposterEnumMap) map).map.replace(key, value);
        }

        return map.replace(key, value);
    }

    public static Object computeIfAbsentToMap(Map map, Object key, Function function) {
        if (map instanceof ImposterEnumMap) {
            return ((ImposterEnumMap) map).map.computeIfAbsent(key, function);
        }

        return map.computeIfAbsent(key, function);
    }

    public static Object computeIfPresentToMap(Map map, Object key, BiFunction function) {
        if (map instanceof ImposterEnumMap) {
            return ((ImposterEnumMap) map).map.computeIfPresent(key, function);
        }

        return map.computeIfPresent(key, function);
    }

    public static Object computeToMap(Map map, Object key, BiFunction function) {
        if (map instanceof ImposterEnumMap) {
            return ((ImposterEnumMap) map).map.compute(key, function);
        }

        return map.compute(key, function);
    }

    public static Object mergeToMap(Map map, Object key, Object value, BiFunction function) {
        if (map instanceof ImposterEnumMap) {
            return ((ImposterEnumMap) map).map.merge(key, value, function);
        }

        return map.merge(key, value, function);
    }

    public static EnumMap newEnumMap(Class<?> objectClass) {
        return new ImposterEnumMap(objectClass);
    }

    public static EnumMap newEnumMap(Map map) {
        return new ImposterEnumMap(map);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public Object put(DummyEnum key, Object value) {
        // Should never be called since we rout every put call to #putToMap
        if (WARN_PUT) {
            System.err.println("Got a direct put call to an ImposterEnumMap, this should not happen. Trying to put anyway.");
            new Exception().printStackTrace();
            WARN_PUT = false;
        }

        return map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends DummyEnum, ?> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<DummyEnum> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public Set<Entry<DummyEnum, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public EnumMap<DummyEnum, Object> clone() {
        if (map instanceof EnumMap) {
            return ((EnumMap) map).clone();
        }

        ImposterEnumMap enumMap = new ImposterEnumMap(objectClass);
        enumMap.putAll(map);
        return enumMap;
    }
}
