package io.muudo.metastore.data;

import io.muudo.metastore.data.exception.DataSetExistsException;
import io.muudo.metastore.data.exception.DataSetNotExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DataSetManager {
    private static final Logger log = LoggerFactory.getLogger(DataSetManager.class);

    private HashMap<Long, DataSet> dataSetsId;
    private HashMap<String, DataSet> dataSetsName;
    private long lastModified;
    private AtomicLong nextId;

    public DataSetManager() {
        this.dataSetsId = new HashMap<>();
        this.dataSetsName = new HashMap<>();
    }

    public long createDataSet(String name) {
        synchronized (this) {
            if (dataSetsName.containsKey(name)) {
                throw DataSetExistsException.of("Dataset with name {} already exists.", name);
            }

            long id = nextId.incrementAndGet();
            DataSet set = new DataSet(id, name);
            dataSetsId.put(id, set);
            dataSetsName.put(name, set);
            touchUpdateTime();
            return id;
        }
    }

    public void addDataSet(DataSet set) {
        synchronized (this) {
            if (dataSetsId.containsKey(set.getId())) {
                throw DataSetExistsException.of("Dataset {} already exists.", set.getName());
            }

            dataSetsId.put(set.getId(), set);
            dataSetsName.put(set.getName(), set);
            touchUpdateTime();
        }
    }

    public DataSet getDataSet(long id) {
        return dataSetsId.get(id);
    }

    public DataSet getDataSetByName(String name) {
        return dataSetsName.get(name);
    }

    public List<DataSet> getDataSetList() {
        return new ArrayList<>(dataSetsName.values());
    }

    public void renameDataset(long id, String newName) {
        synchronized(this) {
            if (dataSetsName.containsKey(newName)) {
                throw DataSetExistsException.of("Dataset {} exist. Cannot rename.", id);
            }

            DataSet set = dataSetsId.get(id);
            if (set == null) {
                throw DataSetNotExistsException.of("Dataset with name {} doesn't exist. Cannot rename.", id);
            }

            String oldName = set.getName();
            dataSetsName.put(newName, set);
            set.setName(newName);
            dataSetsName.remove(oldName);
            touchUpdateTime();
        }
    }

    public void deleteDataset(long id) {
        synchronized(this) {
            DataSet set = dataSetsId.get(id);
            if (set == null) {
                throw DataSetNotExistsException.of("Dataset {} doesn't exist. Cannot Delete", id);
            }

            this.dataSetsId.remove(id);
            this.dataSetsName.remove(set.getName());
        }
    }

    private void touchUpdateTime() {
        lastModified = System.currentTimeMillis();
    }
}
