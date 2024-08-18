package bulls.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public abstract class DefaultMapper {
    private static final String MONGO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static Gson gMapper;
    private static Gson mongoDocumentMapper;
    private static ObjectMapper mapper;

    public static Gson getMongoDocumentMapper() {
        if (mongoDocumentMapper == null) {
            mongoDocumentMapper = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
                        String timeStr = json.getAsString();
                        return LocalDateTime.parse(timeStr);
                    })
                    .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (localDateTime, type, jsonSerializationContext) -> {
                        String str = localDateTime.toString();
                        return jsonSerializationContext.serialize(str);
                    })
                    .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (localDate, type, jsonSerializationContext) -> {
                        String str = localDate.toString();
                        return jsonSerializationContext.serialize(str);
                    })
                    .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> {
                        String timeStr = json.getAsString();
                        return LocalDate.parse(timeStr);
                    }).registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, type, context) -> {
                        try {
                            Date date = new Date(json.getAsJsonObject().get("$date").getAsLong());
                            return date;
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        return null;
                    })
                    .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (src, type, context) -> {
                        if (src == null) {
                            return null;
                        } else {
                            SimpleDateFormat format = new SimpleDateFormat(MONGO_DATE_FORMAT);
                            JsonObject jo = new JsonObject();
                            jo.addProperty("$date", format.format(src));
                            return jo;
                        }
                    })
                    .create();
        }

        return mongoDocumentMapper;
    }

    public static Gson getGMapper() {
        if (gMapper == null) {
            gMapper = new GsonBuilder()
//                    .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
//                        @Override
//                        public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
//                            String timeStr = json.getAsString();
//                            return LocalDateTime.parse(timeStr);
//
//                        }
//                    })
//                    .registerTypeAdapter(CondOrderInfo.class, (JsonDeserializer<CondOrderInfo>) (json, type, jsonDeserializationContext) -> {
//                        String jsonStr = json.toString();
//                        JsonParser parser = new JsonParser();
//                        JsonObject jsonObj = parser.parse(jsonStr).getAsJsonObject();
//                        String trCode = jsonObj.get(JsonKey.COND_ORDER_TYPE).getAsString();
//                        Class<?> pojoClass = null;
//                        try {
//                            pojoClass = CondOrderType.getClassToMap(trCode);
//                        } catch (IllegalTrCodeException e) {
//                            return null;
//                        }
//                        CondOrderInfo object = (CondOrderInfo) DefaultMapper.getGMapper().fromJson(jsonStr, pojoClass);
//                        return object;
//                    })
                    .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (localDateTime, type, jsonSerializationContext) -> {
                        String str = localDateTime.toString();
                        return jsonSerializationContext.serialize(str);
                    })
                    .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> {
                        String timeStr = json.getAsString();
                        return LocalDate.parse(timeStr);

                    })
                    .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (localDate, type, jsonSerializationContext) -> {
                        String str = localDate.toString();
                        return jsonSerializationContext.serialize(str);
                    })
                    .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> {
                        String timeStr = json.getAsString();
                        return LocalDate.parse(timeStr);

                    })
                    .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (localDate, type, jsonSerializationContext) -> {
                        String str = localDate.toString();
                        return jsonSerializationContext.serialize(str);
                    })
//                    .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
//                        @Override
//                        public JsonElement serialize(Date src, Type type, JsonSerializationContext context) {
//                            if (src == null) {
//                                return null;
//                            } else {
//                                SimpleDateFormat format = new SimpleDateFormat(MONGO_DATE_FORMAT);
//                                JsonObject jo = new JsonObject();
//                                jo.addProperty("$date", format.format(src));
//                                return jo;
//                            }
//                        }
//                    })
                    .create();
        }
        return gMapper;
    }

    public static ObjectMapper getMapper() {

        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                    .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));
//                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
//                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE));
        }

        return mapper;
    }
}
