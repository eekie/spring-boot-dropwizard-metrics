package net.eekie.metrics.zoo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

abstract class ObservableSubject<L> {

    private List<L> listeners = new ArrayList<>();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    protected final Lock readLock = readWriteLock.readLock();
    protected final Lock writeLock = readWriteLock.writeLock();
    final ExecutorService executor = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder()
            .setNameFormat("metric-thread-%d").build());

    public L registerListener (L listener) {
        // Lock the list of listeners for writing
        this.writeLock.lock();
        try {
            // Add the listener to the list of registered listeners
            this.listeners.add(listener);
        }
        finally {
            // Unlock the writer lock
            this.writeLock.unlock();
        }
        return listener;
    }

    public void notifyListeners (Consumer<? super L> algorithm) {
        // Lock the list of listeners for reading
        this.readLock.lock();
        try {
            executor.submit(() -> {
                // Notify each of the listeners in the list of registered listeners
                this.listeners.forEach(algorithm);
            });
        }
        finally {
            // Unlock the reader lock
            this.readLock.unlock();
        }
    }

}
