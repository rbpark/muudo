package io.muudo.metastore.persistence;

public class MoveOperation extends Operation{
    public MoveOperation(long txnNum, long id, long timestamp) {
        super(txnNum, id, timestamp);
    }
}
