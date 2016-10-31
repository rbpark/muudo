package io.muudo.metastore.persistence;

import io.muudo.metastore.proto.OperationProto;

import java.io.IOException;

public class DeleteOperation extends Operation {
    public DeleteOperation(long txnNum, long id, long timestamp) {
        super(txnNum, id, timestamp);
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

        AddOperation.Builder builder = new AddOperation.Builder()
                .setId(proto.getId())
                .setParentId(proto.getParentId())
                .setName(proto.getPropertiesOrThrow(NAME_PROPERTY))
                .setTransactionId(proto.getTxnId())
                .setTimestamp(proto.getTimestamp())
                .setMetadata(proto.getPropertiesMap());

        return builder.build();
    }

}
