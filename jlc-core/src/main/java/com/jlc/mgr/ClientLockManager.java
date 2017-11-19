package com.jlc.mgr;

import com.jlc.event.LockEvent;
import com.jlc.event.LockEventState;
import com.jlc.net.TransportListener;
import com.jlc.net.TransportStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * @author lokesh
 * sending lock requests to remote nodes using a transport strategy
 */

public class ClientLockManager extends TransportLockManager implements TransportListener {

    private static final Logger logger = LogManager.getLogger(ClientLockManager.class);

    public ClientLockManager(TransportStrategy transportStrategy) {
        super(transportStrategy);
        transportStrategy.register(this);
    }

    @Override
    public void lock(LockEvent lockEvent) {
        if(lockEvent != null) {
            queue.add(lockEvent);
        }
    }

    @Override
    public void release(LockEvent lockEvent) {
        if(lockEvent != null) {
            queue.remove(lockEvent);
        }
    }

    @Override
    public void handle(LockEvent lockEvent) throws Exception {
        if(lockEvent != null) {
            lockEvent.setLockEventState(LockEventState.PEER_REQUESTED);
            send(lockEvent);
            lockEvent.requested();
        }
    }

    @Override
    public void received(LockEvent lockEvent) {

        if(logger.isDebugEnabled())
            logger.debug("received -> " + lockEvent);

        switch (lockEvent.getLockEventState()) {
            case PEER_QUEUED:
                lockEvent.queued();
                break;
            case PEER_ACQUIRED:
                lockEvent.acquired();
                break;
            case PEER_RELEASED:
                lockEvent.released();
                break;
        }
    }
}
