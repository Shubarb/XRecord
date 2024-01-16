package com.module.config.network.serialize;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

/**
 *
 */
public class BitmapSerializer implements JsonSerializer<Bitmap>, JsonDeserializer<Bitmap> {

    @Override
    public Bitmap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return decodeBase64(json.getAsString());
    }

    @Override
    public JsonElement serialize(Bitmap src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(encodeToBase64(src));
    }

    private String encodeToBase64(Bitmap source) {
        if (source == null) {
            return "";
        }
        Bitmap bitmapCompress = source;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapCompress.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    private static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
