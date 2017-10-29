package com.jlc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author lokesh
 */

public class DefaultLockPartitioner implements LockPartitioner {

    private static final Logger logger = LogManager.getLogger(DefaultLockPartitioner.class);

    private int partition = 1;
    private SimpleLockManager[] simpleLockManagers = null;

    public DefaultLockPartitioner(int partition) {

        this.partition = partition;
        simpleLockManagers = new SimpleLockManager[this.partition];

        for(int i=0; i<this.partition; i++) {
            simpleLockManagers[i] = new SimpleLockManager();
        }

    }

    public int getPartition() {
        return partition;
    }

    @Override
    public SimpleLockManager getPartition(LockEvent lockEvent) {
        int partitionNumber = lockEvent.getId().hashCode() % getPartition();
        if(logger.isDebugEnabled())
            logger.debug("Returning partition for " + lockEvent.getId() + " -> " + partitionNumber);
        return simpleLockManagers[partitionNumber];
    }

    @Override
    public SimpleLockManager[] getAllPartitions() {
        return simpleLockManagers;
    }

}
