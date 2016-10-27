package io.muudo.metastore.persistence;

public class ModifyOperation extends Operation {
    public ModifyOperation(long txnNum, long id, long timestamp) {
        super(txnNum, id, timestamp);
    }
}
