package io.muudo.metastore.persistence;

import org.apache.zookeeper.Op;

import java.util.HashMap;

public class Operation {
    private long txnNum;
    private long id;
    private long timestamp;

    public Operation(long txnNum, long id, long timestamp) {
        this.txnNum = txnNum;
        this.id = id;
        this.timestamp = timestamp;
    }

    public long getTxnNum() {
        return txnNum;
    }

    public void setTxnNum(long txnNum) {
        this.txnNum = txnNum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
