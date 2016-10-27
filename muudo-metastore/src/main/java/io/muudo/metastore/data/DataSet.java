package io.muudo.metastore.data;

import java.util.HashMap;
import java.util.Map;

public class DataSet {
    private final long id;
    private String name;

    private Map<String, DataSet> childDataSet;

    public DataSet(long id, String name) {
        this(id, name, new HashMap<>());
    }

    public DataSet(long id, String name, Map<String, DataSet> dataSet) {
        this.id = id;
        this.name = name;
        this.childDataSet = dataSet;
    }

    public boolean hasChildren() {
        return childDataSet == null || childDataSet.isEmpty();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /*package*/ void setName(String name) {
        this.name = name;
    }
}