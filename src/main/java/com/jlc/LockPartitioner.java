package com.jlc;

/**
 * @author lokesh
 */

public interface LockPartitioner {

    LockManager getPartition(LockEvent lockEvent);
    LockManager[] getAllPartitions();
    void shutdown();

}
