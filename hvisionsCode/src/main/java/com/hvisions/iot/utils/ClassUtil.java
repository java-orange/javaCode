package com.hvisions.iot.utils;

import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ClassUtil {
    private ClassUtil() {
    }

    /**
     * 根据配置与对应class，利用反射创建出该对象
     */
    public static <T> T toObject(JsonNode json, Class<T> clazz) {
        try {
            T obj = ClassUtil.createInstance(clazz);

            Iterator<Map.Entry<String, JsonNode>> itItems = json.fields();
            while (itItems.hasNext()) {
                Map.Entry<String, JsonNode> entry = itItems.next();

                Field field = getField(clazz, entry);
                if (field == null) {
                    continue;
                }

                extractedSetMethod(clazz, obj, entry, field);

            }

            return obj;
        } catch (Exception e) {
            log.error("Failed to create object for class {}", clazz.getName());

            return null;
        }
    }

    /**
     * 利用反射，对属性进行赋值
     */
    private static <T> void extractedSetMethod(Class<T> clazz, T obj, Map.Entry<String, JsonNode> entry, Field field) {
        try {
            Method method = clazz.getMethod("set" + StringUtils.capitalize(field.getName()),
                    field.getType());

            method.invoke(obj, getValue(field.getType(), entry.getValue()));
        } catch (Exception e) {
            if (!(e instanceof NoSuchMethodException)) {
                log.error("Failed to set field {} value defined in class {}, error: {}", entry.getKey(),
                        clazz.getName(), e.getMessage());
            }
        }
    }

    /**
     * 根据配置解析出对应的属性
     */
    private static <T> Field getField(Class<T> clazz, Map.Entry<String, JsonNode> entry) {
        Field field;
        try {
            field = clazz.getDeclaredField(entry.getKey());
        } catch (Exception e) {
            log.error("There is no field {} defined in class {}, error: {}", entry.getKey(),
                    clazz.getName(), e.getMessage());

            return null;
        }
        return field;
    }

    /**
     * 将Json数组转换成List对象
     *
     * @param data 需要转换的Json数组
     * @param clazz 转换成的Class对象
     * @param <T> 转换成的对象类型
     *
     * @return 对象的List
     */
    public static <T> List<T> toList(ArrayNode data, Class<T> clazz) {
        List<T> result = new LinkedList<>();

        data.forEach(json -> {
            T obj = toObject(json, clazz);
            if (obj == null) {
                log.error("Failed to convert json to class {}, json: {}", clazz.getName(), json);

                return;
            }
            result.add(obj);
        });

        return result;
    }

    /**
     * 根据配置获取对应的值
     */
    public static Object getValue(Class<?> clazz, JsonNode value) {
        if (value == null || value.isNull()) {
            return null;
        }

        if (clazz == Integer.class) {
            return value.asInt();
        } else if (clazz == Short.class) {
            return value.shortValue();
        } else if (clazz == Byte.class) {
            return (byte) value.asInt();
        } else if (clazz == Long.class) {
            return value.longValue();
        } else if (clazz == Float.class) {
            return value.floatValue();
        } else if (clazz == Double.class) {
            return value.asDouble();
        } else if (clazz == String.class) {
            return value.asText();
        } else if (clazz == Boolean.class) {
            return value.asBoolean();
        } else if (clazz == BigDecimal.class) {
            return BigDecimal.valueOf(value.asDouble());
        } else if (clazz == LocalDate.class) {
            return Timestamp.toLocalDate(value.asText());
        } else if (clazz == LocalTime.class) {
            return Timestamp.toLocalTime(value.asText());
        } else if (clazz == LocalDateTime.class) {
            return Timestamp.toLocalDatetime(value.asText());
        } else if (clazz == ZonedDateTime.class) {
            return Timestamp.toZoneDateTime(value.asText(), null);
        }
        return Json.toObject(value, clazz);
    }

    /**
     * 扫描指定package，得到所需要匹配的class列表
     *
     * @param scanPackage 扫描的package名称
     * @param clazz Class对象
     * @param <T> 对象定义
     * @return 匹配的class列表
     */
    public static <T> List<Class<? extends T>> getAllClasses(String scanPackage, Class<T> clazz) {
        Reflections reflections = new Reflections(scanPackage);

        Set<Class<? extends T>> allClasses = reflections.getSubTypesOf(clazz);

        return new LinkedList<>(allClasses);
    }

    /**
     * 根据Type定义获取class的类型，一般用于List等泛型类型的处理上
     *
     * @param type Type定义
     * @return Class定义, 无法从Type中得到class类型，返回null
     */
    public static Class<?> getClassType(Type type) {


        if (type instanceof ParameterizedType) {

            ParameterizedTypeImpl pt = (ParameterizedTypeImpl)type;
            Type typeOfData = pt.getActualTypeArguments()[0];

            if (typeOfData instanceof ParameterizedTypeImpl) {
                Type actualType = ((ParameterizedTypeImpl) pt.getActualTypeArguments()[0]).getActualTypeArguments()[0];
                return (Class<?>)actualType;
            } else {
                return (Class<?>)typeOfData;
            }
        }

        return null;
    }
    public static List<Method> getMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        return Stream.of(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }


    public static List<Field> getFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        return Stream.of(getAllFields(clazz))
                .filter(field -> field.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    public static Field[] getAllFields(Object object){
        return getAllFields(object.getClass());
    }

    public static Field[] getAllFields(Class<?> clazz){
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null){
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    /**
     * 扫描整个工程包，得到所需要匹配的class列表
     *
     * @param clazz Class对象
     * @param <T> 对象定义
     * @return 匹配的class列表
     */
    public static <T> List<Class<? extends T>> getAllClasses(Class<T> clazz) {
        Reflections reflections = new Reflections();

        Set<Class<? extends T>> allClasses = reflections.getSubTypesOf(clazz);

        return new LinkedList<>(allClasses);
    }

    /**
     * 通过class创建类实例
     *
     * @param clazz 类class
     * @param param 构造函数的参数，如果存在多个参数的情况，统一放在一个json中进行处理
     * @param <T> class类型
     * @return 创建的新的类对象
     * @throws Exception 创建类发生错误
     */
    public static <T> T createInstance(Class<T> clazz, JsonNode param, Object... objects) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> constructor = getConstructor(clazz);
        if (constructor == null) {
            return null;
        }

        Class<?>[] paramTypes = constructor.getParameterTypes();
        Type[] genericParamTypes = constructor.getGenericParameterTypes();
        Parameter[] parameters = constructor.getParameters();


        Object[] params = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> typeClazz = ClassUtil.getClassType(genericParamTypes[i]);
            if (typeClazz == null) {
                typeClazz = paramTypes[i];
            }

            Object object = getParamValue(typeClazz, parameters[i].getName(), param, objects);
            params[i] = object;
        }
        /**
         * constructor.setAccessible(true);
         */
        return constructor.newInstance(params);
    }

    /**
     * 利用反射获取对应的参数值
     */
    public static Object getParamValue(Class<?> clazz, String name, JsonNode param, Object... objects) {
        if (param == null || param.isNull()) {
            return null;
        }

        for (Object object: objects) {
            if (clazz.isInstance(object)) {
                return object;
            }
        }

        JsonNode value = param;

        if (clazz == Integer.class ||
                clazz == Short.class ||
                clazz == Byte.class ||
                clazz == Long.class ||
                clazz == Float.class ||
                clazz == Double.class ||
                clazz == String.class ||
                clazz == BigDecimal.class ||
                clazz == LocalDate.class ||
                clazz == LocalTime.class ||
                clazz == LocalDateTime.class ||
                clazz == ZonedDateTime.class
        ) {
            value = Json.get(param, name);
        }

        return getValue(clazz, value);
    }

    /**
     * 通过class创建类实例，类的构造函数不带参数
     *
     * @param clazz 类class
     * @param <T> class类型
     * @return 创建的新的类对象
     * @throws Exception 创建类发生错误
     */
    public static <T> T createInstance(Class<T> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        // find the first constructor with no parameters
        Constructor<T> constructor = getConstructor(clazz);
        if (constructor == null) {
            return null;
        }

        Class<?>[] paramTypes = constructor.getParameterTypes();

        if (paramTypes.length > 0) {
            throw new RuntimeException("The constructor should has no parameter for class " + clazz.getName());
        }
        /**
         * constructor.setAccessible(true);
         */
        return constructor.newInstance();
    }

    /**
     * 获取类型的构造函数
     *
     * @param clazz 类class
     * @param <T> 类定义
     * @return 构造函数
     */
    public static <T> Constructor<T> getConstructor(Class<T> clazz) {
        Constructor[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length <= 0) {
            return null;
        }

        /**
         * notice
         * here just get the first constructor
         */
        return constructors[0];
    }

    /**
     * 获取main所在的package名称
     *
     * @return main package名称
     */
    public static String getMainPackage() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName()).getPackage().getName();
                }
            }
        }
        catch (ClassNotFoundException ex) {
            // Swallow and continue
        }
        return null;
    }

    public static String getMethodName(Method method) {
        return String.format("%s$%s", method.getDeclaringClass().getName(), method.getName());
    }
}
