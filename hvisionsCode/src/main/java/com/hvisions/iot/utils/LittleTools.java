package com.hvisions.iot.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Array;

/**
 * ClassName: LittleTools
 * Package: IntelliJ IDEA
 * Description:
 *
 * @Author xhjing
 * @Create 2023/2/28 14:06
 * @Version 1.0
 */
@Slf4j
public class LittleTools {

    public static short[] convertShortArray(Object value) {
        if (value.getClass().isArray()) {
            int len = Array.getLength(value);
            short[] shorts = new short[len];
            for(int i = 0; i < len; i++) {
                shorts[i] = (short) Array.get(value, i);
            }
            return shorts;
        }
        throw new IllegalArgumentException(value + " convertShortArray failed ");
    }


    public static void closeAll(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    log.error("something wrong ,{} closeable not close", closeable,  e);
                }
            }
        }
    }
}
