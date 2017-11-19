package com.jlc.mgr;

import com.jlc.event.LockEvent;

/**
 * @author lokesh
 */

public interface LockManager {

    void lock(LockEvent lockEvent);
    void release(LockEvent lockEvent);
    void shutdown();

}
