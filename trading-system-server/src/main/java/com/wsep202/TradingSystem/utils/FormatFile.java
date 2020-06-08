package com.wsep202.TradingSystem.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FormatFile {
    public final static ObjectMapper mapper =
            new ObjectMapper((new YAMLFactory().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)).
                    disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID));

    public final static String yaml = ".yaml";
    public final static String yml = ".yml";

    /**
     * get list path and return list of yaml files
     *
     * @param pathList list of path
     * @return filter list (yaml files)
     */
    public static List<Path> filterYamlFiles(List<Path> pathList) {
        return pathList.stream().filter(path -> path.toString().endsWith(yaml) || path.toString().endsWith(yml)).
                collect(Collectors.toList());
    }

    /**
     * convert string yaml into object
     */
    public static <T> T getObject(String yaml, Class<T> clazz)
            throws IOException {
        return mapper.readValue(yaml, clazz);

    }

}
