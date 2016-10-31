package io.muudo.metastore.persistence;

public class ReplaceOperation extends Operation{
    public ReplaceOperation(long txnNum, long id, long timestamp) {
        super(txnNum, id, timestamp);
    }
}
