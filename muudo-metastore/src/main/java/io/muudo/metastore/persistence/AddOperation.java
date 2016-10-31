package io.muudo.metastore.persistence;

import io.muudo.metastore.proto.OperationProto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddOperation extends Operation {
    private static final String NAME_PROPERTY = "_name";
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

    @Override
    public OperationProto toProtobuff() {
        OperationProto.Builder protoBuilder = OperationProto.newBuilder()
                .setType(OperationProto.Type.ADD)
                .setTxnId(this.getTxnNum())
                .setTimestamp(this.getTimestamp())
                .setId(this.getId())
                .setParentId(this.getParentId())
                .putProperties(NAME_PROPERTY, this.getName())
                .putAllProperties(getMetadata());
        return protoBuilder.build();
    }

    @Override
    public Operation fromProtobuff(OperationProto proto) throws IOException {
        if (proto.getType() != OperationProto.Type.ADD) {
            throw CommitLoggerException.of("Expected Protobuff type 'ADD' but got %s.", OperationProto.Type.ADD.name());
        }

        Builder builder = new Builder()
                .setId(proto.getId())
                .setParentId(proto.getParentId())
                .setName(proto.getPropertiesOrThrow(NAME_PROPERTY))
                .setTransactionId(proto.getTxnId())
                .setTimestamp(proto.getTimestamp())
                .setMetadata(proto.getPropertiesMap());

        return builder.build();
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

        public Builder setTransactionId(long txnNum) {
            this.txnNum = txnNum;
            return this;
        }

        public Builder setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setParentId(long parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder setMetadata(Map<String, String> metadata) {
            this.metadata = new HashMap<>(metadata);
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public AddOperation build() {
            return new AddOperation(txnNum, id, parentId, timestamp, name, metadata);
        }
    }
}
