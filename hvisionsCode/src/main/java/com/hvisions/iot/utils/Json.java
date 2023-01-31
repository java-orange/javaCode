package com.hvisions.iot.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
public class Json {
    private Json() {
    }

    private static final char KEY_NESTED = '.';
    private static final String JSON_NULL_VALUE = "null";

    public static boolean has(JsonNode json, String key) {
        return json.has(key) && !json.get(key).isNull() && !JSON_NULL_VALUE.equals(json.get(key).asText());
    }

    public static boolean isJson(String text) {
        boolean valid = false;
        try {
            new ObjectMapper().readTree(text);
            valid = true;
        } catch (Exception e) {
            // empty
        }

        return valid;
    }

    public static boolean isNull(JsonNode json) {
        if (json == null) {
            return true;
        }

        if (json.isNull()) {
            return true;
        }

        if (json.isTextual() && JSON_NULL_VALUE.equals(json.asText())) {
            return true;
        }

        if (json.isObject()) {
            boolean nullable = true;

            Iterator<Map.Entry<String, JsonNode>> itFields = json.fields();
            while (itFields.hasNext()) {
                Map.Entry<String, JsonNode> entry = itFields.next();
                if (!isNull(entry.getValue())) {
                    nullable = false;
                    break;
                }
            }

            return nullable;
        }

        return false;
    }

    public static Object getValue(JsonNode json, String name) {
        if (!has(json, name)) {
            return null;
        }

        return getValue(get(json, name));
    }

    public static Object getValue(JsonNode json) {
        if (json == null || json.isNull()) {
            return null;
        }

        if (json.isBoolean()) {
            return json.asBoolean();
        }

        if (json.isShort()) {
            return json.shortValue();
        }

        if (json.isInt()) {
            return json.asInt();
        }

        if (json.isLong()) {
            return json.asLong();
        }

        if (json.isBigInteger()) {
            return json.asLong();
        }

        if (json.isLong()) {
            return json.asLong();
        }

        if (json.isFloat() || json.isDouble()) {
            return json.asDouble();
        }

        if (json.isIntegralNumber()) {
            return new BigInteger(json.asText());
        }

        if (json.isNumber()) {
            return new BigDecimal(json.asText());
        }

        if (json.isTextual()) {
            return json.asText();
        }

        return json;
    }

    public static String getString(JsonNode json, String key) {
        if (!has(json, key)) {
            return null;
        }

        JsonNode value = get(json, key);

        if (value != null) {
            return value.asText();
        }
        return null;
    }

    public static Integer getInt(JsonNode json, String key) {
        if (!has(json, key)) {
            return null;
        }

        JsonNode value = get(json, key);

        if (value != null) {
            return value.asInt();
        }

        return null;
    }

    public static JsonNode get(JsonNode json, String name) {
        if (json == null) {
            return null;
        }

        return json.get(name);
    }

    /**
     * 获取json的值，name可以nested的，如request.name.permission
     *
     * @param json JsonNode对象
     * @param name 要获取的对象的名称
     * @return 名称对应得值，不存在返回null
     */
    public static JsonNode getNested(JsonNode json, String name) {
        if (name.indexOf(KEY_NESTED) == -1) {
            return json.get(name);
        }

        String[] names = StringUtils.split(name, KEY_NESTED);

        JsonNode result = json;
        for (String key: names) {
            result = result.path(key);
        }

        if (result.isMissingNode()) {
            return null;
        }

        return result;
    }

    public static void nestedPut(ObjectNode json, String name, Object value) {
        if (value == null) {
            return;
        }

        if (value instanceof JsonNode) {
            put(json, name, (JsonNode)value);

            return;
        }

        if (name.indexOf(KEY_NESTED) == -1) {
            put(json, name, value);
            return;
        }

        String[] names = StringUtils.split(name, KEY_NESTED);

        ObjectNode node = new ObjectMapper().createObjectNode();
        json.set(name, node);

        for (int i = 1; i < names.length; i++) {
            if (i == names.length - 1) {
                put(node, names[i], value);
            } else {
                ObjectNode nodeAdd = new ObjectMapper().createObjectNode();
                node.set(name, nodeAdd);

                node = nodeAdd;
            }
        }
    }

    public static void put(ObjectNode json, String name, JsonNode jsonNode) {
        if (name.indexOf(KEY_NESTED) == -1) {
            if (json.has(name) && jsonNode.isObject()) {
                merge((ObjectNode) json.get(name), (ObjectNode)jsonNode);
            } else {
                json.set(name, jsonNode);
            }
            return;
        }

        String[] names = StringUtils.split(name, KEY_NESTED);

        ObjectNode node = json;
        for (int i = 0; i < names.length; i++) {
            if (i == names.length - 1) {
                node.set(names[i], jsonNode);
            } else {
                if (!node.has(names[i])) {
                    ObjectNode on = new ObjectMapper().createObjectNode();
                    node.set(names[i], on);

                    node = on;
                } else {
                    node = (ObjectNode) node.get(names[i]);
                }
            }
        }
    }

    public static void put(ObjectNode json, String name, Object value) {
        if (value == null) {
            json.set(name, NullNode.instance);
            return;
        }

        if (value.getClass().isArray()) {
            ArrayNode array = new ObjectMapper().valueToTree(value);
            json.set(name, array);
            return;
        }

        if (value instanceof List) {
            json.set(name, new ObjectMapper().valueToTree(value));
        }

        if (value instanceof Byte) {
            json.put(name, (Byte)value);
        } else if (value instanceof Short) {
            json.put(name, (Short)value);
        } else if (value instanceof Integer) {
            json.put(name, (Integer)value);
        } else if (value instanceof Long) {
            json.put(name, (Long)value);
        }  else if (value instanceof Boolean) {
            json.put(name, (Boolean)value);
        } else if (value instanceof Double) {
            json.put(name, (Double)value);
        } else if (value instanceof Float) {
            json.put(name, (Float)value);
        } else if (value instanceof ObjectNode) {
            convertObjectNode(json, name, (ObjectNode) value);
        } else {
            json.put(name, value.toString());
        }
    }

    private static void convertObjectNode(ObjectNode json, String name, ObjectNode value) {
        if (json.has(name)) {
            merge((ObjectNode) json.get(name), value);
        } else {
            json.set(name, value);
        }
    }

    public static void add(ArrayNode array, Object value) {
        if (value == null) {
            array.add(NullNode.instance);
            return;
        }

        if (value instanceof Byte) {
            array.add((Byte)value);
        } else if (value instanceof Short) {
            array.add((Short)value);
        } else if (value instanceof Integer) {
            array.add((Integer)value);
        } else if (value instanceof Long) {
            array.add((Long)value);
        } else if (value instanceof String) {
            array.add((String)value);
        } else if (value instanceof Boolean) {
            array.add((Boolean)value);
        } else if (value instanceof Double) {
            array.add((Double)value);
        } else if (value instanceof Float) {
            array.add((Float)value);
        } else if (value instanceof BigDecimal) {
            array.add(value.toString());
        } else if (value instanceof LocalDate) {
            array.add(value.toString());
        } else if (value instanceof LocalTime) {
            array.add(value.toString());
        } else if (value instanceof LocalDateTime) {
            array.add(value.toString());
        } else if (value instanceof ObjectNode) {
            array.add((ObjectNode)value);
        } else {
            log.warn("Unknown data type of {} for appending to json, just add it as string",
                    value.getClass());
            array.add(value.toString());
        }
    }

    public static JsonNode merge(ObjectNode base, ObjectNode merged) {
        base.setAll(merged);
        return base;
    }

    public static JsonNode filterNull(JsonNode json) {
        if (!(json instanceof ObjectNode)) {
            return json;
        }

        Iterator<Map.Entry<String, JsonNode>> itFields = json.fields();
        while (itFields.hasNext()) {
            Map.Entry<String, JsonNode> entry = itFields.next();

            if (isNull(entry.getValue())) {
                itFields.remove();
            }
        }

        return json;
    }

    public static Map<String, Object> toMap(JsonNode jsonNode) {
        return getMapper().convertValue(jsonNode, new TypeReference<Map<String, Object>>(){});
    }

    public static <T> Map<String, T> toMapData(JsonNode jsonNode) {
        return getMapper().convertValue(jsonNode, new TypeReference<Map<String, T>>(){});
    }

    public static <T> T toObject(Map<?, ?> map, Class<T> clazz) {
        return getMapper().convertValue(map, clazz);
    }

    public static <T> T toObject(Object object, Class<T> clazz) {
        try {
            return toObject(toJsonNode(object), clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T toObject(String object, Class<T> clazz) {
        try {
            return toObject(toJsonNode(object), clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> List<T> toList(ArrayNode arrayNode) {
        if (arrayNode == null) {
            return Collections.emptyList();
        }

        ObjectMapper mapper = getMapper();

        try {
            return mapper.readValue(arrayNode.toString(),
                    new TypeReference<List<T>>() {});

        } catch (JsonProcessingException e) {
            log.error("Failed to convert json array to list object: {}", arrayNode);
            return Collections.emptyList();
        }
    }

    public static List<Object> toList(ArrayNode arrayNode, Class<?> clazz) {
        return toList(arrayNode, clazz, new StdDateFormat());
    }

    public static List<Object> toList(ArrayNode arrayNode, Class<?> clazz, DateFormat dateFormat) {
        if (arrayNode == null) {
            return Collections.emptyList();
        }
        List<Object> result = new LinkedList<>();

        arrayNode.forEach(jsonNode -> result.add(toObject(jsonNode, clazz, dateFormat)));

        return result;
    }

    public static <T> List<T> toListWithGenerics(ArrayNode arrayNode, Class<T> clazz) {
        if (arrayNode == null) {
            return Collections.emptyList();
        }
        List<T> result = new LinkedList<>();

        arrayNode.forEach(jsonNode -> result.add(toObject(jsonNode, clazz)));

        return result;
    }


    public static <T> List<T> toList(List<?> listData, Class<T> clazz) {
        if (listData == null) {
            return Collections.emptyList();
        }

        List<T> result = new LinkedList<>();

        for (Object source : listData) {
            result.add(toObject(source, clazz));
        }

        return result;
    }

    public static <T> T toObject(JsonNode jsonNode, Class<T> clazz) {
        return toObject(jsonNode, clazz, new StdDateFormat());
    }

    public static <T> T toObject(JsonNode jsonNode, Class<T> clazz, DateFormat dateFormat) {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.DELEGATING))
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mapper.setDateFormat(dateFormat);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        if (jsonNode.isArray() && (!clazz.isArray() && List.class.isAssignableFrom(clazz))) {
            // 如果数组类型不匹配，不做转换

            return null;
        }

        try {
            return mapper.treeToValue(jsonNode, clazz);
        } catch (JsonProcessingException e) {
            log.error("json to object failed: {}", jsonNode.toString(), e);
            return null;
        }
    }

    /**
     * 将json转化为对象，并忽略ignoredFields定义的字段
     *
     * @param jsonNode JsonNode对象
     * @param clazz 转换得到的class
     * @param ignoredFields 忽略掉的字class
     * @param <T> class的定义
     * @return clazz对象
     * @throws JsonProcessingException Json处理异常
     */
    public static <T> T toObject(JsonNode jsonNode, Class<T> clazz,
                                 List<String> ignoredFields) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public boolean hasIgnoreMarker(final AnnotatedMember m) {
                return ignoredFields.contains(m.getName())|| super.hasIgnoreMarker(m);
            }
        });


        try {
            return mapper.treeToValue(jsonNode, clazz);
        } catch (JsonProcessingException e) {
            log.error("json to object failed: {}", jsonNode.toString(), e);
            return null;
        }
    }

    public static JsonNode toJsonNode(byte[] data) {
        try {
            return getMapper().readTree(data);
        } catch (IOException e) {
            log.error(e.getMessage(), e);

            return null;
        }
    }

    public static JsonNode toJsonNode(String data) {
        return toJsonNode(data.getBytes());
    }

    public static JsonNode toJsonNode(Object object) {
        ObjectMapper mapper = getMapper();

        return mapper.convertValue(object, JsonNode.class);
    }

    public static ObjectMapper getMapperWithoutEnum() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

        mapper.setDateFormat(new StdDateFormat());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }

    public static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.DELEGATING))
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

        mapper.setDateFormat(new StdDateFormat());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }

    public static String toString(Object object) {
        try {
            return getMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            // normally should not happen here
            log.error("Convert object to string failed: {}", e.getMessage(), e);
            return "";
        }
    }

    public static String toString(Object object, boolean withEnumLowercase) {
        try {
            if (withEnumLowercase) {
                return getMapper().writeValueAsString(object);
            } else {
                return getMapperWithoutEnum().writeValueAsString(object);
            }
        } catch (JsonProcessingException e) {
            // normally should not happen here
            log.error(e.getMessage(), e);
            return "";
        }
    }

    public static void writeToFile(Object object, String filename) throws IOException {
        writeToFile(toJsonNode(object), filename);
    }

    public static void writeToFile(Map<?, ?> map, String fileName) throws IOException {
        writeToFile(toJsonNode(map), fileName);
    }

    public static void writeToFile(JsonNode json, String filename) throws IOException {
        ObjectMapper mapper = getMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(new File(filename), filterNull(json));
    }

    public static JsonNode readFile(String filename) throws IOException {
        ObjectMapper mapper = getMapper();
        return mapper.readTree(new File(filename));
    }

    public static String toPrettyString(Object object) {
        try {
            return getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            // normally should not happen here
            log.error(e.getMessage(), e);
            return "";
        }
    }

    public static JsonNode empty() {
        return new ObjectMapper().createObjectNode();
    }


    @Data
    public static class TestDTO {
        private Date createTime;
    }
}
