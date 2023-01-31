package com.hvisions.iot.core.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.hvisions.iot.utils.Json;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * <p>Title: ConnectionImpl</p>
 * <p>Description: 解析可配置</p>
 * <p>create date: 2022/11/4</p>
 *
 * @author : xhjing
 * @version :1.0.0
 */
public class Config {
    private Config() {
    }

    // 解析yml文件
    public static JsonNode loadResource(String resName) throws IOException {
        Yaml yaml = new Yaml();

        InputStream inputStream = Config.class
                .getClassLoader()
                .getResourceAsStream(resName);

        Map<String, Object> config = yaml.load(inputStream);

        return Json.toJsonNode(config);
    }
}
