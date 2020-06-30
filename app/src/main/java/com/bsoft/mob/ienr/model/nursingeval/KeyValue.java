package com.bsoft.mob.ienr.model.nursingeval;


/**
 * Created by Classichu on 2017/10/29.
 */
public class KeyValue<K, V> {
    public K key = null;
    public V value = null;

    private KeyValue() {
    }

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof KeyValue)) {
            return false;
        }
        KeyValue<?, ?> p = (KeyValue<?, ?>) o;
        return objectsEqual(p.key, key) && objectsEqual(p.value, value);
    }

    private static boolean objectsEqual(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    @Override
    public int hashCode() {
        return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    @Override
    public String toString() {
        return "KeyValue{" + String.valueOf(key) + " " + String.valueOf(value) + "}";
    }

    public static <K, V> KeyValue<K, V> create(K key, V value) {
        return new KeyValue<K, V>(key, value);
    }
}
