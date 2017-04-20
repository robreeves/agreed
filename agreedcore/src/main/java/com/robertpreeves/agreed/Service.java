package com.robertpreeves.agreed;

public interface Service<TKey> {
    /**
     * Gets the current value for the key
     * @param key The unique key for the value
     * @param valueClass The class definition for the value
     * @param <TValue> The value type
     * @return The current value associated with the key
     */
    <TValue> TValue get(TKey key, Class<TValue> valueClass);

    /**
     * Updates the value associated with a key
     * @param key The unique key for the value
     * @param value The new value to be associated with the key
     * @param <TValue> The value type
     * @return The current value associated with the key
     */
    <TValue> TValue put(TKey key, TValue value);
}
