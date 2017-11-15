package com.jlc.mgr;

import com.jlc.event.LockEvent;

/**
 * @author lokesh
 */

public interface LockManager extends Runnable {

    void lock(LockEvent lockEvent);
    void shutdown();

}
