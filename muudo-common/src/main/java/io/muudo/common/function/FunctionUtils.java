package io.muudo.common.function;

import org.apache.commons.lang3.tuple.Pair;

import java.util.function.*;

/**
 * Function helper class
 *
 *
 */
public class FunctionUtils {
    /**
     * Used to invoke lambda suppliers to grab either the exception or the value without catching
     *
     * Example:
     * safeInfo(() -> { return method() }
     *
     * @param supplier The lambda supplier.
     * @param <R> The return type of the supplier.
     * @return Return object that may contains an object or an exception.
     */
    public static <R> Return<R> safeInvoke(Supplier<R> supplier) {
        try {
            R retValue = supplier.get();
            return Return.of(retValue, null);
        }
        catch (Exception e) {
            return Return.of(null, e);
        }
    }
}
