package io.muudo.common.config;

import io.muudo.common.function.FunctionUtils;
import io.muudo.common.function.Return;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.muudo.common.util.Utils.printf;
import static org.junit.Assert.*;


public class ConfigurationTest {
    private static final String YAML_FILE = "src/test/files/myconf.yaml";

    @Test
    public void testStrings() throws Exception {
        Configuration conf = Configuration.loadFromYamlFile(new File(YAML_FILE));

        // Test Strings
        assertEquals("myconf.yaml", conf.getName());
        assertEquals("Martin D'vloper", conf.getString("name"));
        assertEquals("Martin D'vloper", conf.getString("name", "unusedDefault"));
        assertEquals("234", conf.getString("val"));

        // Doesn't exist, so it should default.
        assertEquals("lala", conf.getString("haha", "lala"));

        // Doesn't exist so it should throw exception
        Return<String> result = FunctionUtils.safeInvoke(() -> {return conf.getString("Blah");});
        assertTrue(result.hasException());
        assertTrue(result.getException() instanceof IllegalArgumentException);
        assertTrue(result.getException().getMessage().contains("Blah"));
        assertTrue(result.getException().getMessage().contains(conf.getName()));
        assertTrue(result.getException().getMessage().contains("missing"));
    }

    @Test
    public void testInteger() throws Exception {
        Configuration conf = Configuration.loadFromYamlFile(new File(YAML_FILE));

        // Test Integer
        assertEquals("myconf.yaml", conf.getName());
        assertEquals(234, conf.getInt("val"));
        assertEquals(321, conf.getInt("notAnInt", 321));

        // Doesn't exist, so it should default.
        Return<Integer> result = FunctionUtils.safeInvoke(() -> {return conf.getInt("Blah");});
        assertTrue(result.hasException());
        assertTrue(result.getException() instanceof IllegalArgumentException);
        assertTrue(result.getException().getMessage().contains("Blah"));
        assertTrue(result.getException().getMessage().contains(conf.getName()));
        assertTrue(result.getException().getMessage().contains("missing"));

        // Exists but not a string
        Return<Integer> result2 = FunctionUtils.safeInvoke(() -> {return conf.getInt("name");});
        assertTrue(result2.hasException());
        assertTrue(result2.getException() instanceof IllegalArgumentException);
        assertTrue(result2.getException().getMessage().contains("name"));
        assertTrue(result2.getException().getMessage().contains(conf.getName()));
        assertTrue(result2.getException().getMessage().contains("not an integer"));
    }

    @Test
    public void testBoolean() throws Exception {
        Configuration conf = Configuration.loadFromYamlFile(new File(YAML_FILE));

        // Test Boolean
        assertEquals("myconf.yaml", conf.getName());
        assertTrue(conf.getBool("employed"));
        assertFalse(conf.getBool("bool2"));
        assertTrue(conf.getBool("notAnBoolean", true));
        assertFalse(conf.getBool("notAnBoolean", false));

        // Doesn't exist, so it should default.
        Return<Boolean> result = FunctionUtils.safeInvoke(() -> {return conf.getBool("Blah");});
        assertTrue(result.hasException());
        assertTrue(result.getException() instanceof IllegalArgumentException);
        assertTrue(result.getException().getMessage().contains("Blah"));
        assertTrue(result.getException().getMessage().contains(conf.getName()));
        assertTrue(result.getException().getMessage().contains("missing"));

        // Exists. It automatically is false because we use Boolean.valueOf, which is true iff value of string
        // is 'true'
        Return<Boolean> result2 = FunctionUtils.safeInvoke(() -> {return conf.getBool("name");});
        assertFalse(result2.hasException());
        assertFalse(result2.getValue());
    }

    @Test
    public void testStringArray() throws Exception {
        Configuration conf = Configuration.loadFromYamlFile(new File(YAML_FILE));

        // Test string arrays
        String[] args = conf.getStringArray("foods");
        assertArrayEquals(new String[] {"Apple", "Orange", "Strawberry", "Mango"}, args);

        // Missing result
        Return<String[]> result = FunctionUtils.safeInvoke(() -> {return conf.getStringArray("Blah");});
        assertTrue(result.hasException());
        assertTrue(result.getException() instanceof IllegalArgumentException);
        assertTrue(result.getException().getMessage().contains("Blah"));
        assertTrue(result.getException().getMessage().contains(conf.getName()));
        assertTrue(result.getException().getMessage().contains("missing"));
    }

    @Test
    public void testObjectList() throws Exception {
        Configuration conf = Configuration.loadFromYamlFile(new File(YAML_FILE));

        // Test string arrays
        String args = conf.getString("complexArray");
        printf(args);

        List<TestPair> list = conf.getObject("complexArray", ArrayList.class);
        for (TestPair pair: list) {
            printf(pair.toString() + "\n");
        }

//        assertArrayEquals(new TestPair[] {
//                new TestPair("key1", "value1"),
//                new TestPair("key2", "value2"),
//                new TestPair("key3", "value3")},
//                list);
    }

    public static class TestPair {
        private String key;
        private String value;

        public TestPair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public TestPair() {
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setValue(String value) {
            this.value = value;
        }


        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            return "{" + key + "," + value + "}";
        }
    }
}
