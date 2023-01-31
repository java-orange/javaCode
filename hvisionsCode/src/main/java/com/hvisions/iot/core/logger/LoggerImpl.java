package com.hvisions.iot.core.logger;

import ch.qos.logback.classic.Level;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


/**
 * 带有过滤的日志log记录。
 *
 *  通过 setListener 即可实现将数据发送别处
 *  例如： 设置listener 为mq的发送， 通过fluentd 进行采集， 然后存储进入es中
 *
 */


@Slf4j
public class LoggerImpl implements Logger {
    private Consumer<ConnectionLog> listener;
    private final String connectionName;

    /**
     * 队列(message)和队列的映射关系，队列里面存储的是每一次通过时候的时间戳，这样可以使得程序里有多个限流队列
     */
    private Map<String, List<Long>> mapMessage = new ConcurrentHashMap<>();

    /**
     * 冻结序列
     */
    private Map<String, Long> mapFrozen = new ConcurrentHashMap<>();

    /**
     * 记录重复次数
     */
    private Integer count = 2;
    /**
     * 记录时间窗口
     */
    private Long timeWindow = 31 * 1000L;
    /**
     * 记录冷冻时间
     */
    private Long frozenTime = 5 * 60 * 1000L;

    public LoggerImpl(String connectionName) {
        this.connectionName = connectionName;
    }

    @Override
    public void debug(String message) {
        log.debug(message);

        // DEBUG 无需发送至es
//        logMessage(Level.DEBUG, message);
    }

    @Override
    public void debug(String format, Object... args) {
        log.debug(format, args);
//        logMessage(Level.DEBUG, format, args);
    }

    @Override
    public void info(String message) {
        if (filterMessage(message, count, timeWindow, frozenTime)) {
            log.info(message);
            logMessage(Level.INFO, message);
        }
    }

    @Override
    public void info(String format, Object... args) {
        filterLog(message -> {
            log.info(message);
            logMessage(Level.INFO, format, args);
        }, format, args);
    }

//    @Override
//    public void warn(String message) {
////        log.warn(message);
//        if (filterMessage(message, count, timeWindow, frozenTime)) {
//            log.warn(message);
//            logMessage(Level.WARN, message);
//        }
//    }

    @Override
    public void warn(ConnFailCategory exceptionType, String message) {
        if (filterMessage(message, count, timeWindow, frozenTime)) {
            log.warn(message);
            logMessage(Level.WARN, exceptionType, message);
        }
    }

//    @Override
//    public void warn(String format, Object... args) {
////        log.warn(format, args);
//        filterLog(message -> {
//            log.warn(format, args);
//            logMessage(Level.WARN, format, args);
//        }, format, args);
//
//    }

    @Override
    public void warn(ConnFailCategory exceptionType, String format, Object... args) {
        filterLog(message -> {
            log.warn(format, args);
            logMessage(Level.WARN, exceptionType, format, args);
        }, format, args);

    }

//    @Override
//    public void error(String message) {
//        if (filterMessage(message, count, timeWindow, frozenTime)) {
//            log.error(message);
//            logMessage(Level.ERROR, message);
//        }
//    }

    @Override
    public void error(ConnFailCategory exceptionType, String message) {
        if (filterMessage(message, count, timeWindow, frozenTime)) {
            log.error(message);
            logMessage(Level.ERROR, exceptionType, message);
        }
    }

//    @Override
//    public void error(String format, Object... args) {
//        filterLog(message -> {
//            log.error(message, args);
//            logMessage(Level.ERROR, format, args);
//        }, format, args);
//    }

    @Override
    public void error(ConnFailCategory exceptionType, String format, Object... args) {
        filterLog(message -> {
            log.warn(format, args);
            logMessage(Level.WARN, exceptionType, format, args);
        }, format, args);

    }

    // 过滤消息 , 多学习lambda，灵活运用。
    private void filterLog(Consumer<String> logFunc, String format, Object... args) {
        String message = MessageFormatter.arrayFormat(format, args).getMessage();
        // 如果允许通过
        if (filterMessage(message, count, timeWindow, frozenTime)) {
            logFunc.accept(message);
        }
    }

    @Override
    public void setListener(Consumer<ConnectionLog> listener) {
        this.listener = listener;
    }


    private void logMessage(Level level, String format, Object... args) {
        if (listener == null) {
            return;
        }

        String message = getMessage(format, args);

        ConnectionLog event = new ConnectionLog();
        event.setLevel(level.levelStr);
        event.setConnectionName(connectionName);
        event.setMessage(message);

        listener.accept(event);
    }

    private void logMessage(Level level, ConnFailCategory type, String format, Object... args) {
        if (listener == null) {
            return;
        }

        String message = getMessage(format, args);

        ConnectionLog event = new ConnectionLog();
        event.setLevel(level.levelStr);
        event.setConnectionName(connectionName);
        event.setConnFailCategory(type.getName());
        event.setMessage(message);

        listener.accept(event);
    }

    private String getMessage(String format, Object... args) {
        if (args == null) {
            return format;
        }
        int length = args.length;

        if (length <= 0) {
            return format;
        }

        if (args[args.length - 1] instanceof Exception) {
            length = args.length - 1;
        }

        String[] strArgs = new String[length];
        for (int i = 0; i <= length - 1; i++) {
            if (args[i] != null) {
                strArgs[i] = args[i].toString();
            } else {
                strArgs[i] = null;
            }

        }

        return String.format(format.replace("{}", "%s"), (Object[]) strArgs);
    }


    /**
     * 滑动时间窗口限流算法
     * 在指定时间窗口，指定限制次数内，是否允许通过
     *
     * @param message    消息标识
     * @param count      限制次数
     * @param timeWindow 时间窗口大小
     * @return 是否允许通过
     */
    private boolean filterMessage(String message, int count, long timeWindow, long frozenTime) {
        // 获取当前时间
        long nowTime = System.currentTimeMillis();
        // 根据消息，取出对应的时间戳队列，若没有则创建
        // 这里computeIfAbsent方法自动同步，所以list是唯一的。 可用sync 锁住
        List<Long> list = mapMessage.computeIfAbsent(message, k -> new LinkedList<>());
        // 如果队列还没满，则允许通过，并添加当前时间戳到队列开始位置
        if (list.size() < count) {
            synchronized (list) {
                if (list.size() < count) {
                    list.add(0, nowTime);
                    return true;
                }
            }
        }
        // 队列已满（达到限制次数），则获取队列中最早添加的时间戳
        Long farTime = list.get(count - 1);
        // 不在时间窗口中，  用当前时间戳 减去 最早添加的时间戳
        if (nowTime - farTime > timeWindow) {
            // 没有被冻住， 则可进行正常逻辑替换。
            if (!mapFrozen.containsKey(message)) {
                // 若结果大于timeWindow，则说明在timeWindow内，通过的次数小于等于count
                // 允许通过，并删除最早添加的时间戳，将当前时间添加到队列开始位置
                list.remove(count - 1);
                list.add(0, nowTime);
                return true;
            }
        }

        frozenMessage(message, frozenTime);
        return false;
    }


    /**
     * 冻结序列。
     *
     * @param message    消息标识
     * @param frozenTime 冷冻时间
     */
    private void frozenMessage(String message, long frozenTime) {
        // 获取当前时间
        long nowTime = System.currentTimeMillis();
        // 根据消息，取出对应的时间戳，若没有则创建为当前时间
        Long farTime = mapFrozen.computeIfAbsent(message, k -> nowTime);
        if (nowTime - farTime > frozenTime) {
            mapFrozen.remove(message);
        }
    }
}
