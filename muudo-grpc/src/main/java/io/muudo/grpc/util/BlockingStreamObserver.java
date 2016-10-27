package io.muudo.grpc.util;

import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class BlockingStreamObserver<V> implements StreamObserver<V> {
    private V value;
    private Throwable error;
    private boolean isComplete = false;

    @Override
    public void onNext(V value) {
        this.value = value;
    }

    @Override
    public synchronized void onError(Throwable t) {
        this.error = t;
        isComplete = true;
        this.notify();
    }

    @Override
    public synchronized void onCompleted() {
        isComplete = true;
        this.notify();
    }

    public boolean isCompleted() {
        return isComplete;
    }

    public boolean isError() {
        return error != null;
    }

    public Throwable getError() {
        return error;
    }

    public V getValue() {
        return value;
    }

    public void waitForComplete(long timeout, TimeUnit unit) throws InterruptedException {
        // Double check to prevent sync when not necessary.
        if (isComplete) {
            return;
        }
        else {
            synchronized (this) {
                if (isComplete) {
                    return;
                }
                else {
                    try {
                        wait(unit.toMillis(timeout));
                    } catch (InterruptedException e) {
                        if (isComplete) {
                            return;
                        }
                        throw e;
                    }
                }
            }
        }
    }
}
