package io.muudo.common.function;

/**
 * Class that encapsulates both a return value and a thrown exception.
 *
 * Allows to handle Exceptions cleanly without verbosity when Exceptions are expected.
 * Does not allow Equals or HashCode because there's no need as of now.
 */
public class Return<V> {
    private final V value;
    private final Exception e;

    public static <V> Return<V> of(V value, Exception e) {
        return new Return(value, e);
    }

    public Return(V value, Exception e) {
        this.value = value;
        this.e = e;
    }

    public V getValue() {
        return value;
    }

    public Exception getException() {
        return e;
    }

    public boolean hasException() {
        return e != null;
    }
}
