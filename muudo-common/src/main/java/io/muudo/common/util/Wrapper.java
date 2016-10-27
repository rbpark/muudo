package io.muudo.common.util;

/**
 * Class used to wrap an object to get around inner classes where the object needs to be final.
 */
public class Wrapper<T> {
    private T obj;

    public Wrapper(T obj) {
        this.obj = obj;
    }

    public Wrapper() {
    }

    public void setObject(T obj) {
        this.obj = obj;
    }

    public T getObject() {
        return obj;
    }
}
