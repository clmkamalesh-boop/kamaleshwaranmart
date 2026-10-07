package com.kamaleshwaran.kamaleshwaranmart.util;

import com.google.gson.*;
import com.kamaleshwaran.kamaleshwaranmart.dto.ApiResponseDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * JSON serialization and API response utility using Google Gson.
 */
public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .registerTypeAdapter(Timestamp.class, new JsonSerializer<Timestamp>() {
                @Override
                public JsonElement serialize(Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
                    if (src == null) return JsonNull.INSTANCE;
                    return new JsonPrimitive(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(src));
                }
            })
            .registerTypeAdapter(Timestamp.class, new JsonDeserializer<Timestamp>() {
                @Override
                public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                        throws JsonParseException {
                    try {
                        return new Timestamp(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(json.getAsString()).getTime());
                    } catch (Exception e) {
                        return new Timestamp(System.currentTimeMillis());
                    }
                }
            })
            .serializeNulls()
            .create();

    private JsonUtil() {
    }

    public static Gson getGson() {
        return GSON;
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static void writeJsonResponse(HttpServletResponse response, int statusCode, ApiResponseDTO<?> apiResponse)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(toJson(apiResponse));
            writer.flush();
        }
    }
}
