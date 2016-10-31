package io.muudo.metastore.persistence;

import io.muudo.metastore.data.exception.DataSetExistsException;

import java.io.IOException;

public class CommitLoggerException extends IOException {
    public static CommitLoggerException of(Throwable t, String format, Object ... args) {
        return new CommitLoggerException(String.format(format, args), t);
    }

    public static CommitLoggerException of(String format, Object ... args) {
        return new CommitLoggerException(String.format(format, args));
    }

    public CommitLoggerException(String message) {
        super(message);
    }

    public CommitLoggerException(String message, Throwable error) {
        super(message, error);
    }
}
