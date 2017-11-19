package com.jlc.mgr;

import com.jlc.event.LockEvent;
import com.jlc.net.TransportStrategy;

/**
 * @author lokesh
 * lock manager with a transport strategy
 */

public abstract class TransportLockManager extends SimpleLockManager {

    protected TransportStrategy transportStrategy;

    public TransportLockManager(TransportStrategy transportStrategy) {
        super();
        this.transportStrategy = transportStrategy;
    }

    @Override
    public void shutdown() {

        if(transportStrategy != null)
            transportStrategy.shutdown();

        super.shutdown();
    }

    protected void send(LockEvent lockEvent) throws Exception {
        transportStrategy.send(lockEvent);
    }
}
