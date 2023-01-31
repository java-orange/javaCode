package com.hvisions.iot.core.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hvisions.iot.core.base.*;
import com.hvisions.iot.core.connection.Connection;
import com.hvisions.iot.core.connection.ConnectionImpl;
import com.hvisions.iot.core.logger.ConnFailCategory;
import com.hvisions.iot.core.logger.ConnectionLog;
import com.hvisions.iot.core.logger.Logger;
import com.hvisions.iot.core.logger.LoggerImpl;
import com.hvisions.iot.core.thread.ConnectionExecutor;
import com.hvisions.iot.core.thread.ConnectionExecutorProducer;
import com.hvisions.iot.utils.BaseInfo;
import com.hvisions.iot.utils.ClassUtil;
import com.hvisions.iot.utils.Json;
import com.hvisions.iot.utils.thread.Timer;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * <p>Title: DriverManager2</p>
 * <p>Description: 驱动管理</p>
 * <p>create date: 2022/11/4</p>
 *
 * @author : xhjing
 * @version :1.0.0
 */

public class DriverManager {

    // 驱动扫描报
    private static final String DRIVER_SCAN_PACKAGE = "com.hvisions.iot.drivers";

    // 驱动名称
    private static final String DRIVER_NAME = "driverName";

    /**
     * driverName 与 driverItem 对应map
     */
    private Map<String, DriverItem> driverNameItemMap = new ConcurrentHashMap<>();

    /**
     * 调度器
     */
    private final Timer timer = new Timer(8);

    private Consumer<ConnectionLog> logListener;

    private static final Logger log = new LoggerImpl(DriverManager.class.getName());

    /**
     * 共享线程池
     */
    private static final ConnectionExecutor SHARE_EXECUTOR = ConnectionExecutor.inst();

    /**
     * connectionName 与 Connection 对应map
     */
    private Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    private DriverManager() {
        registerDrivers();
    }

    /**
     * 注册驱动
     *  1. 扫描指定包下 所有DriverConnection的实现类
     *  2. 抽象类抛弃
     *  3. 构建出DriverItem
     */
    private void registerDrivers() {

        List<Class<? extends DriverConnection>> classes = ClassUtil.getAllClasses(DRIVER_SCAN_PACKAGE, DriverConnection.class);

        for (Class<? extends DriverConnection> cls : classes) {
            if (Modifier.isAbstract(cls.getModifiers())) {
                continue;
            }
            buildDriverItem(cls);
        }
    }

    /**
     * 根据指定类 构建出 DriverItem
     *  放入 driverNameItemMap 中
     * @param cls DriverConnection 的实现类
     * @return
     */
    private DriverItem buildDriverItem(Class<? extends DriverConnection> cls) {

        String[] driverNames = {cls.getSimpleName().toLowerCase()};
        Boolean needNewExecutor = false;
        List<FieldConfigInfo> fieldConfigInfoList = null;
        for (Annotation annotation : cls.getAnnotations()) {
            if (annotation.annotationType() == Driver.class) {
                driverNames = ((Driver) annotation).names();
                needNewExecutor = ((Driver) annotation).needNewExecutor();
                fieldConfigInfoList = (initDriverSetting(((Driver) annotation).driverConfig()));
            }
        }


        final DriverItem driverItem = new DriverItem();
        final DriverInfo driverInfo = new DriverInfo(driverNames, needNewExecutor);
        driverItem.setDriverInfo(driverInfo);
        driverItem.setDriverClass(cls);
        driverItem.setDriverConfig(fieldConfigInfoList);

        for (String driverName : driverNames) {
            driverNameItemMap.put(driverName.toLowerCase(), driverItem);
            log.info("register driver by {}", driverName);
        }

        return driverItem;

    }



    public List<FieldConfigInfo> getDriverConfigByModel(String model) {
        DriverItem driverItem = null;

        for (DriverItem item: driverNameItemMap.values()) {
            if (Arrays.stream(item.driverInfo.getDriverNames()).anyMatch(driverModel -> Objects.equals(driverModel, model))) {
                driverItem = item;
                break;
            }
        }

        return driverItem.driverConfig;
    }


    /**
     * 解析yml文件并构建出对应的连接实例
     * @param connectionYMLFile 连接yml配置文件
     */
    public void parseAllConnectionByFile(String connectionYMLFile) {

        try {
            JsonNode ymlConfig = Config.loadResource(connectionYMLFile);

            ymlConfig.fields().forEachRemaining(driverNameConfig -> {
                // 连接名称
                final String connectionName = driverNameConfig.getKey();
                // 连接配置
                final JsonNode setting = driverNameConfig.getValue();
                // 构建连接
                buildConnection(connectionName, setting);

            });
        } catch (Exception e) {
            log.error(ConnFailCategory.INNER_ERROR, "parse yml file {} failed", connectionYMLFile, e);
        }
    }

    /**
     * 构建连接， 对于有相同连接名称则忽略
     *  1. 查询是否已有相同的连接名称，若存在则忽略
     *  2. 获取配置中的驱动类
     *  3. 根据配置构建连接
     *  4. 将连接放入  connectionMap 中
     * @param connectionName 连接名称
     * @param setting        对应配置
     */
    public void buildConnection(String connectionName, JsonNode setting) {

        // 查询是否已有相同的连接名称，若存在则忽略
        if (connectionMap.containsKey(connectionName)) {
            log.warn(ConnFailCategory.INNER_ERROR, "{} connection name is exist, ignore this buildConnection {}", connectionName, setting);
            return;
        }

        Json.put((ObjectNode) setting, ConnectionSetting.CONNECTION_NAME, connectionName);
        Json.put((ObjectNode) setting, ConnectionSetting.CONNECTION_ID, connectionName);

        // 获取配置中的驱动类
        String driverName = getDriverName(setting);

        // 根据配置构建连接
        final Connection connection = createConnection(driverName, setting);

        // 将连接放入  connectionMap 中
        if (connection != null) {
            connectionMap.put(connectionName, connection);
            try {
                connection.connect();
                log.info("init {} connect successful", connection);
            } catch (Exception e) {
                log.error(ConnFailCategory.INNER_ERROR, "init {} connect failed", connection, e);
            }
        }
    }

    /**
     * 根据 连接名称获取对应的连接
     * @param connectionName
     * @return 连接实例
     */
    public Connection getConnection(String connectionName) {
        return connectionMap.get(connectionName);
    }

    /**
     * 获取所有的 连接名称集合
     * @return 连接名称集合
     */
    public Set<String> getConnectionNames() {
        return connectionMap.keySet();
    }

    /**
     * 根据对应配置解析出对应的驱动名称
     * @param settings 连接配置
     * @return         驱动名称
     */
    private String getDriverName(JsonNode settings) {
        return Json.getString(settings, DRIVER_NAME);
    }

    /**
     * 创建连接实例
     *  1. 根据驱动名称获取  DriverItem
     *  2. 根据 DriverItem 创建连接
     * @param driverName  驱动名称
     * @param config      配置信息
     * @return      连接实例
     */
    private Connection createConnection(String driverName, JsonNode config) {
        if (driverName == null) {
            return null;
        }

        // 根据驱动名称获取  DriverItem
        final DriverItem driverItem = driverNameItemMap.get(driverName.toLowerCase());

        if (driverItem == null) {
            log.error(ConnFailCategory.INNER_ERROR, "There is no class implementation for driver name {}", driverName);
            return null;
        }

        // 根据 DriverItem 创建连接
        return createConnection(driverItem, config);
    }

    /**
     * 根据 指定的信息进行实例化连接
     *  1. 根据 driverItem 找到对应驱动的 class
     *  2. 根据 driverItem 决定线程池
     *  3. 利用ClassUtils工具类进行实例化连接 DriverConnection
     *  4. 使用 ConnectionImpl 包装
     * @param driverItem   驱动item
     * @param config       所需配置
     * @param objects      保留字段，用于后续扩展
     * @return
     */
    private Connection createConnection(DriverItem driverItem, JsonNode config, Object... objects) {
        Object[] params = objects;

        // 根据 driverItem 找到对应驱动的 class
        final Class<? extends DriverConnection> clazz = driverItem.getDriverClass();

        // 根据 driverItem 决定线程池
        final ExecutorService executor = getExecutorService(driverItem.getDriverInfo());

        try {

            Logger logger = new LoggerImpl(getDriverName(config));
            logger.setListener(connectionLog -> {
                if (logListener != null) {
                    logListener.accept(connectionLog);
                }
            });

            // 利用ClassUtils工具类进行实例化连接 DriverConnection
            DriverConnection driverConnection = ClassUtil.createInstance(clazz, config, executor, timer, logger, params);
            // 使用 ConnectionImpl 包装
            return new ConnectionImpl(driverConnection);
        } catch (Exception e) {
            log.error(ConnFailCategory.INNER_ERROR, "Failed to create instance for class {}", clazz, e);
            return null;
        }

    }

    /**
     * 根据 driverInfo 决定线程池
     *  若需要新的线程池则创建出新的线程池，默认使用共享线程池
     * @param driverInfo 驱动信息
     * @return  对应的线程池
     */
    private ExecutorService getExecutorService(DriverInfo driverInfo) {
        // 若需要新的线程池则创建出新的线程池，
        final Boolean needNewExecutor = driverInfo.getNeedNewExecutor();
        if (Boolean.TRUE.equals(needNewExecutor)) {
            return ConnectionExecutorProducer.getDefaultExecutor();
        }
        return SHARE_EXECUTOR.getExecutor();

    }

    public static DriverManager inst() {
        return DriverManagerHolder.instance;
    }

    private static class DriverManagerHolder {
        private static final DriverManager instance = new DriverManager();
    }


    public Consumer<ConnectionLog> getLogListener() {
        return logListener;
    }

    public void setLogListener(Consumer<ConnectionLog> logListener) {
        this.logListener = logListener;
    }


    /**
     *  初始化 驱动设置
     * @param clazz
     * @return
     */
    private List<FieldConfigInfo> initDriverSetting(Class<?> clazz) {
        List<FieldConfigInfo> driverConfig = new LinkedList<>();
        List<Field> fields = ClassUtil.getFieldsWithAnnotation(clazz, FieldConfig.class);
        for (Field field : fields) {
            FieldConfig fieldConfig = field.getAnnotation(FieldConfig.class);
            if (driverConfig.stream().anyMatch(config -> config.getLabel().equals(fieldConfig.label()))) {
                continue;
            }
            FieldConfigInfo fieldConfigInfo = new FieldConfigInfo();
            fieldConfigInfo.setKey(field.getName());
            fieldConfigInfo.setLabel(fieldConfig.label());
            fieldConfigInfo.setDescription(fieldConfig.description());
            fieldConfigInfo.setConfigDataType(fieldConfig.configDataType());
            fieldConfigInfo.setRequired(fieldConfig.required());
            fieldConfigInfo.setDisplay(fieldConfig.display());
            fieldConfigInfo.setIsArray(fieldConfig.isArray());
            fieldConfigInfo.setOrder(fieldConfig.order());
            fieldConfigInfo.setCondition(fieldConfig.condition());
            fieldConfigInfo.setGroup(fieldConfig.group());

            if (field.getType().isEnum()) {
                try {
                    Class descriptionClass = field.getType();
                    List<BaseInfo> baseInfos = BaseInfo.enumInfo(descriptionClass);
                    fieldConfigInfo.setOptions(baseInfos);
                } catch (Exception e) {
                    log.error(ConnFailCategory.INNER_ERROR, "An exception occurred while processing enum type {}", field.getName(), e);
                }
            }

            String initialValue = fieldConfig.initialValue();
            switch (fieldConfig.configDataType()) {
                case OBJECT:
                    if (!fieldConfig.isArray()) {
                        fieldConfigInfo.setObjectConfig(initDriverSetting(field.getType()));
                    } else {
                        fieldConfigInfo.setObjectConfig(initDriverSetting((Class<?>) ((ParameterizedTypeImpl) field.getGenericType()).getActualTypeArguments()[0]));
                    }
                    break;
                case BOOL:
                    fieldConfigInfo.setInitialValue(BooleanUtils.toBooleanObject(initialValue));
                    break;
                case NUMBER:
                    fieldConfigInfo.setInitialValue(NumberUtils.toInt(initialValue));
                    break;
                case MAP:
                    break;
                case ENUM:
                case TEXT:
                case SCRIPT:
                default:
                    if (StringUtils.isBlank(initialValue)) {
                        fieldConfigInfo.setInitialValue(null);
                    } else {
                        fieldConfigInfo.setInitialValue(initialValue);
                    }
                    break;
            }
            driverConfig.add(fieldConfigInfo);
        }
        return driverConfig;
    }



    /**
     * 驱动Item
     */
    @Data
    private static class DriverItem {
        // 驱动对应的 class
        private Class<? extends DriverConnection> driverClass;
        // 驱动信息
        private DriverInfo driverInfo;
        // 驱动所需配置
        private List<FieldConfigInfo> driverConfig;
    }

}