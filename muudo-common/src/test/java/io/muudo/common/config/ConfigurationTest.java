package io.muudo.common.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private static final String CONFIG_BASE_DIR = "src/test/files/";
    private static final String YAML_FILE = CONFIG_BASE_DIR + "myconf.yaml";

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
    public void testObject() throws Exception {
        Configuration conf = Configuration.loadFromYamlFile(new File(YAML_FILE));
        TestPair o = conf.getObject("nestedObject", TestPair.class);

        assertEquals(new TestPair("key3", "value3"), o);
    }

    @Test
    public void testObjectList() throws Exception {
        Configuration conf = Configuration.loadFromYamlFile(new File(YAML_FILE));
        List<TestPair> list = conf.getObjectList("complexArray", TestPair.class);

        assertArrayEquals(new TestPair[] {
                new TestPair("key1", "value1"),
                new TestPair("key2", "value2"),
                new TestPair("key3", "value3")},
                list.toArray());
    }

    @Test
    public void testConfigurationConversion() throws Exception {
        Configuration conf = Configuration.loadFromYamlFile(new File(CONFIG_BASE_DIR + "base.yaml"));
        TestDouble d = conf.as(TestDouble.class);
        assertEquals(new TestDouble("a", "b"), d);

        TestTriple e = conf.as(TestTriple.class);
        assertEquals(new TestTriple("a", "b", null), e);

        conf = Configuration.loadFromYamlFile(new File(CONFIG_BASE_DIR + "base2.yaml"));
        e = conf.as(TestTriple.class);
        assertEquals(new TestTriple("a", "b", "c"), e);

        d = conf.as(TestDouble.class);
        assertEquals(new TestDouble("a", "b"), d);
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestPair testPair = (TestPair) o;

            if (key != null ? !key.equals(testPair.key) : testPair.key != null) return false;
            return value != null ? value.equals(testPair.value) : testPair.value == null;

        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    public static class TestTriple {
        private String a;
        private String b;
        private String c;

        public TestTriple(String a, String b, String c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public TestTriple() {
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestTriple that = (TestTriple) o;

            if (a != null ? !a.equals(that.a) : that.a != null) return false;
            if (b != null ? !b.equals(that.b) : that.b != null) return false;
            return c != null ? c.equals(that.c) : that.c == null;

        }

        @Override
        public int hashCode() {
            int result = a != null ? a.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            result = 31 * result + (c != null ? c.hashCode() : 0);
            return result;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TestDouble {
        private String a;
        private String b;

        public TestDouble(String a, String b) {
            this.a = a;
            this.b = b;
        }

        public TestDouble() {
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestDouble that = (TestDouble) o;

            if (a != null ? !a.equals(that.a) : that.a != null) return false;
            return b != null ? b.equals(that.b) : that.b == null;

        }

        @Override
        public int hashCode() {
            int result = a != null ? a.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            return result;
        }
    }
}
