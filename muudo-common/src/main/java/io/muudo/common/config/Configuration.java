package io.muudo.common.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.muudo.common.util.Except;
import io.muudo.common.util.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {
    private final ObjectMapper mapper;
    private final String name;
    private final Map<String, Object> map;

    public static Configuration loadFromYamlFile(File file) throws IOException {
        Map<String, Object> map = JsonUtils.getYamlMapper().readValue(file, new TypeReference<HashMap<String, Object>>() {});

        return new Configuration(file.getName(), map, JsonUtils.getYamlMapper());
    }

    private Configuration(String name, Map<String, Object> map, ObjectMapper mapper) {
        this.map = map;
        this.name = name;
        this.mapper = mapper;
    }

    public boolean containsKey(String props) {
        return map.containsKey(props);
    }

    public <T> T as(Class<T> c) {
        return mapper.convertValue(map, c);
    }

    public String[] getStringArray(String prop) {
        if (!containsKey(prop)) {
            throw Except.newIllegalArgument("Config '%s' missing property '%s'", name, prop);
        }

        try {
            return mapper.readValue(getString(prop), String[].class);
        } catch (IOException e) {
            throw Except.newIllegalArgument(e,
                    "Config '%s' property '%s' can't be converted to string array", name, prop);
        }
    }

    public <T> T getObject(String prop, Class<T> clazz) {
        if (!containsKey(prop)) {
            throw Except.newIllegalArgument("Config '%s' missing property '%s'", name, prop);
        }

        try {
            return mapper.readValue(getString(prop), clazz);
        } catch (IOException e) {
            throw Except.newIllegalArgument(e,
                    "Config '%s' property '%s' can't be converted to object of type %s'", name, prop, clazz.getName());
        }
    }

//    public <T> List<T> getObjectList(String prop) {
//        if (!containsKey(prop)) {
//            throw Except.newIllegalArgument("Config '%s' missing property '%s'", name, prop);
//        }
//
//        try {
//            return mapper.readValue(getString(prop), new TypeReference<ArrayList<T>>() {});
//        } catch (IOException e) {
//            throw Except.newIllegalArgument(e,
//                    "Config '%s' property '%s' can't be converted to object array", name, prop);
//        }
//    }

    public int getInt(String props) {
        if (!containsKey(props)) {
            throw Except.newIllegalArgument("Config '%s' missing string property '%s'", name, props);
        }

        Object i = map.get(props);
        if (i instanceof Number) {
            return ((Number) i).intValue();
        }
        else if (i instanceof String){
            try {
                return Integer.valueOf((String) i);
            } catch (NumberFormatException e) {
                throw Except.newIllegalArgument("Config '%s' property '%s' is not an integer.", name, props);
            }
        }

        throw Except.newIllegalArgument("Config '%s' property '%s' is not an integer.", name, props);
    }

    public int getInt(String prop, int defaultValue) {
        Object i = map.get(prop);

        if (i == null) {
            return defaultValue;
        }
        else if (i instanceof Number) {
            return ((Number) i).intValue();
        }
        else if (i instanceof String){
            return Integer.valueOf((String)i);
        }
        else {
            return defaultValue;
        }
    }

    public String getString(String props) {
        String value = getString(props, null);
        if (value == null) {
            throw Except.newIllegalArgument("Config '%s' missing string property '%s'", name, props);
        }

        return value;
    }

    public String getString(String prop, String defaultValue) {
        Object i = map.get(prop);
        if (i == null) {
            return defaultValue;
        }
        else if (i instanceof String) {
            return (String)i;
        }
        else {
            return String.valueOf(i);
        }
    }

    public boolean getBool(String props) {
        if (!containsKey(props)) {
            throw Except.newIllegalArgument("Config '%s' missing string property '%s'", name, props);
        }

        return getBool(props, false);
    }

    public boolean getBool(String prop, boolean defaultValue) {
        Object i = map.get(prop);
        if (i == null) {
            return defaultValue;
        }
        else if (i instanceof Boolean) {
            return (Boolean)i;
        }
        else if (i instanceof String){
            return Boolean.valueOf((String)i);
        }
        return defaultValue;
    }

    public String getName() {
        return name;
    }
}
