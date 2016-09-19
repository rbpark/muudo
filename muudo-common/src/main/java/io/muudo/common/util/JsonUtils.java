package io.muudo.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static ObjectMapper getYamlMapper() {
        return yamlMapper;
    }
}
