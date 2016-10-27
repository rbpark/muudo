package io.muudo.metastore.persistence;

import java.util.HashMap;
import java.util.Map;

public class AddOperation extends Operation {
    private long parentId;
    private String name;
    private Map<String, String> metadata;

    public AddOperation(
            long txnNum,
            long id,
            long timestamp,
            long parentId,
            String name,
            Map<String, String> metadata
            ) {
        super(txnNum, id, timestamp);
        this.parentId = parentId;
        this.name = name;
        this.metadata = metadata;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public static class Builder {
        private long txnNum;
        private long timestamp;
        private long id;
        private long parentId;
        private String name;
        private Map<String, String> metadata;

        public Builder() {
        }

        public Builder transactionNumber(long txnNum) {
            this.txnNum = txnNum;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder parentId(long parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            this.metadata = new HashMap<>(metadata);
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public AddOperation build() {
            return new AddOperation(txnNum, id, parentId, timestamp, name, metadata);
        }
    }
}
