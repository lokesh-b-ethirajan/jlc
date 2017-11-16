package com.jlc.partition;

import com.jlc.event.LockEvent;
import com.jlc.config.PartitionConfig;
import com.jlc.mgr.*;
import com.jlc.net.DefaultClientTransportStrategy;
import com.jlc.net.DefaultServerTransportStrategy;
import com.jlc.net.LockServer;
import com.jlc.net.TransportStrategy;
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

    public DistributedLockPartitioner(PartitionConfig[] partitionConfigs) {

        this.partition = partitionConfigs.length;
        lockManagers = new LockManager[this.partition];

        for(int i=0; i<partitionConfigs.length; i++) {
            PartitionConfig partitionConfig = partitionConfigs[i];
            if(partitionConfig.getType().equals("local")) {
                /*SimpleLockManager simpleLockManager = new SimpleLockManager();
                lockServer = new LockServer(partitionConfig.getPort());
                lockServer.setSimpleLockManager(simpleLockManager);*/
                TransportStrategy transportStrategy = transportStrategy = new DefaultServerTransportStrategy(partitionConfig.getPort());
                LockManager lockManager = new RemoteLockManager(transportStrategy);
                lockManagers[i] = lockManager;
            } else {
                TransportStrategy transportStrategy = transportStrategy = new DefaultClientTransportStrategy(partitionConfig.getHost(), partitionConfig.getPort());
                LockManager lockManager = new ProxyLockManager(transportStrategy);
                lockManagers[i] = lockManager;
                //lockManagers[i] = new ProxyLockManager1(partitionConfig.getHost(), partitionConfig.getPort());
            }
        }
    }

    public int getPartition() {
        return partition;
    }

    @Override
    public LockManager getPartition(LockEvent lockEvent) {
        int partitionNumber = Math.abs(lockEvent.getId().hashCode() % getPartition());
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
        //lockServer.shutdown();
        for(LockManager lockManager : getAllPartitions()) {
            logger.info("shutting down..." + lockManager);
            lockManager.shutdown();
        }
    }

}