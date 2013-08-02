/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.theme;

import org.rapfx.client.transport.http.gson.serialization.ThemeMarshaller;

/**
 * Represents the Theme object(s) sent by the server.
 */
public class Theme {

    private Theme fallback;
    private final ThemeValues values;
    private final ThemeData data;

    /**
     * Creates a new {@link Theme} with the given values and data. The {@link ThemeData} will use
     * the {@link ThemeValues} to resolve named attribute values.
     * 
     * @param values
     *            the {@link ThemeValues} that contain all named values
     * @param data
     *            the {@link ThemeData} which ties theme attributes to named values from the
     *            {@link ThemeValues}
     */
    public Theme(ThemeValues values, ThemeData data) {
        this.data = data;
        this.values = values;

        data.setTheme(this);
    }

    /**
     * Registers a fallback {@link Theme} which will be used for attribute lookup if an attribute is
     * not found in this {@link Theme} directly.
     * 
     * @param other
     */
    public void setFallback(Theme other) {
        this.fallback = other;
    }

    /**
     * Tries to create a {@link Theme} object from raw JSON data.
     * 
     * @param json
     *            the JSON data received from the server
     * @return the {@link Theme} deserialized from the given JSON data
     */
    public static Theme fromJson(String json) {
        return ThemeMarshaller.fromJson(json);
    }

    /**
     * @return the {@link ThemeValues} backing all named values of this {@link Theme}
     */
    ThemeValues getValues() {
        return values;
    }

    /**
     * Looks up the {@link ThemeClass} with the given name. If not found directly, it is looked up
     * using the fallback {@link Theme} (see {@link #setFallback(Theme)}).
     * 
     * @param name
     *            the name of the {@link ThemeClass} to find.
     * @return the
     */
    public ThemeClass getThemeClass(String name) {
        ThemeClass result = data.getThemeClass(name);
        if (result == null) {
            result = fallback.getThemeClass(name);
        }
        return result;
    }

    @Override
    public String toString() {
        return data.toString() + "\n" + values.toString();
    }

}
