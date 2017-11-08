package com.jlc;

/**
 * @author lokesh
 */

public interface LockManager extends Runnable {

    void lock(LockEvent lockEvent);
    void shutdown();

}
