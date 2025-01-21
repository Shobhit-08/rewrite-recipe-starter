package com.yourorg;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


public class UnmodifiableFixedOrderMap {

    private UnmodifiableFixedOrderMap() {
    }

    public static <K,V> UnmodifiableFixedOrderMapBuilder<K,V> builder() {
        return new UnmodifiableFixedOrderMapBuilder<>();
    }

    public static class UnmodifiableFixedOrderMapBuilder<K, V> {

        private final LinkedHashMap<K,V> map;

        UnmodifiableFixedOrderMapBuilder() {
            this.map = new LinkedHashMap<>();
        }

        public UnmodifiableFixedOrderMapBuilder<K,V> put(K key, V value) {
            this.map.put(key, value);
            return this;
        }

        public Map<K,V> build() {
            return Collections.unmodifiableMap(this.map);
        }
    }
}
