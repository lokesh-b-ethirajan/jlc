package com.jlc.partition;

import com.jlc.event.LockEvent;
import com.jlc.mgr.LockManager;

/**
 * @author lokesh
 */

public interface LockPartitioner {

    LockManager getPartition(LockEvent lockEvent);
    LockManager[] getAllPartitions();
    void shutdown();

}
