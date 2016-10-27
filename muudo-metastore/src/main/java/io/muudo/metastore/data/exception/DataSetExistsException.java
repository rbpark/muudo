package io.muudo.metastore.data.exception;


public class DataSetExistsException extends RuntimeException {
    public static DataSetExistsException of(Throwable t, String format, Object ... args) {
        return new DataSetExistsException(String.format(format, args), t);
    }

    public static DataSetExistsException of(String format, Object ... args) {
        return new DataSetExistsException(String.format(format, args));
    }

    public DataSetExistsException(String message) {
        super(message);
    }

    public DataSetExistsException(String message, Throwable error) {
        super(message, error);
    }
}
