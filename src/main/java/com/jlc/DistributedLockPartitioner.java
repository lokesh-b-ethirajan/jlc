package com.jlc;

import com.jlc.net.LockServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author lokesh
 */

public class DistributedLockPartitioner implements LockPartitioner {

    private static final Logger logger = LogManager.getLogger(DistributedLockPartitioner.class);

    private int partition;
    private LockManager[] lockManagers = null;
    private LockServer lockServer = null;

    public DistributedLockPartitioner(int partition) {

        this.partition = partition;
        lockManagers = new LockManager[this.partition];

        for(int i=0; i<this.partition; i++) {
            if(i == 0) {
                SimpleLockManager simpleLockManager = new SimpleLockManager();
                lockServer = new LockServer(8040);
                lockServer.setSimpleLockManager(simpleLockManager);
                lockManagers[i] = simpleLockManager;
            }
            else {
                lockManagers[i] = new ProxyLockManager("localhost", 8040);
            }
        }

    }

    public int getPartition() {
        return partition;
    }

    @Override
    public LockManager getPartition(LockEvent lockEvent) {
        int partitionNumber = lockEvent.getId().hashCode() % getPartition();
        if(logger.isDebugEnabled())
            logger.debug("Returning partition for " + lockEvent.getId() + " -> " + partitionNumber);
        return lockManagers[partitionNumber];
    }

    @Override
    public LockManager[] getAllPartitions() {
        return lockManagers;
    }

    @Override
    public void shutdown() {
        lockServer.shutdown();
        for(LockManager lockManager : getAllPartitions()) {
            lockManager.shutdown();
        }
    }

}
