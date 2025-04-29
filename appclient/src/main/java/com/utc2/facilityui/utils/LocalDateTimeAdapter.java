package com.utc2.facilityui.utils;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    // Sử dụng định dạng ISO chuẩn mà DateTimeFormatter và JavaScript dễ dàng xử lý
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type typeOfSrc, JsonSerializationContext context) {
        // Chuyển LocalDateTime thành chuỗi String theo định dạng ISO
        return new JsonPrimitive(localDateTime.format(FORMATTER));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Chuyển chuỗi String JSON (định dạng ISO) thành LocalDateTime
        return LocalDateTime.parse(json.getAsString(), FORMATTER);
    }
}