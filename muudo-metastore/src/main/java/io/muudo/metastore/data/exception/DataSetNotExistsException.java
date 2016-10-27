package io.muudo.metastore.data.exception;


public class DataSetNotExistsException extends RuntimeException {
    public static DataSetNotExistsException of(Throwable t, String format, Object ... args) {
        return new DataSetNotExistsException(String.format(format, args), t);
    }

    public static DataSetNotExistsException of(String format, Object ... args) {
        return new DataSetNotExistsException(String.format(format, args));
    }

    public DataSetNotExistsException(String message) {
        super(message);
    }

    public DataSetNotExistsException(String message, Throwable error) {
        super(message, error);
    }
}
