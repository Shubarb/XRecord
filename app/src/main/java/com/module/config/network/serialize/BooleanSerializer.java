package com.module.config.network.serialize;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/*
 * References: http://stackoverflow.com/a/12894916
 */
public class BooleanSerializer implements JsonSerializer<Boolean>, JsonDeserializer<Boolean> {

    @Override
    public JsonElement serialize(Boolean arg0, Type arg1, JsonSerializationContext arg2) {
        return new JsonPrimitive(arg0 ? 1 : 0);
    }

    @Override
    public Boolean deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws
            JsonParseException {
        return arg0.getAsInt() == 1;
    }
}
