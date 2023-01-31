package com.hvisions.iot.core.logger;

import java.util.function.Consumer;

public interface Logger {
    void debug(String message);

    void debug(String format, Object... args);

    void info(String message);

    void info(String format, Object... args);

//    void warn(String message);

    void warn(ConnFailCategory exceptionType, String message);

//    void warn(String format, Object... args);

    /**
     * 增加异常类型
     * @param format
     * @param exceptionType
     * @param args
     */
    void warn(ConnFailCategory exceptionType, String format, Object... args);


//    void error(String message);
    void error(ConnFailCategory exceptionType, String message);

//    void error(String format, Object... args);

    /**
     * 增加异常类型
     * @param format
     * @param exceptionType
     * @param args
     */
    void error(ConnFailCategory exceptionType, String format, Object... args);

    void setListener(Consumer<ConnectionLog> listener);
}
