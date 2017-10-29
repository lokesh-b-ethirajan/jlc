package com.jlc;

/**
 * @author lokesh
 */

public interface LockPartitioner {

    SimpleLockManager getPartition(LockEvent lockEvent);
    SimpleLockManager[] getAllPartitions();

}
