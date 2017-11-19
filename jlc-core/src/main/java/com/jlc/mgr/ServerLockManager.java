package com.jlc.mgr;

import com.jlc.event.LockEvent;
import com.jlc.event.LockEventState;
import com.jlc.net.TransportListener;
import com.jlc.net.TransportStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author lokesh
 * handling lock requests from clients using a transport strategy
 */

public class ServerLockManager extends TransportLockManager implements TransportListener {

    private static final Logger logger = LogManager.getLogger(ServerLockManager.class);

    public ServerLockManager(TransportStrategy transportStrategy) {
        super(transportStrategy);
        transportStrategy.register(this);
    }

    @Override
    public void received(LockEvent lockEvent) {

        if(lockEvent != null) {
            if (logger.isDebugEnabled())
                logger.debug("received -> " + lockEvent);

            if (lockEvent.getLockEventState() == LockEventState.PEER_REQUESTED) {
                lockEvent.requested();
                queue.add(lockEvent);
                lockEvent.setLockEventState(LockEventState.PEER_QUEUED);
                lockEvent.queued();
                try {
                    send(lockEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void handle(LockEvent lockEvent) throws Exception {
        switch (lockEvent.getLockEventState()) {

            case QUEUED:
                super.handle(lockEvent);
                super.release(lockEvent);
                break;
            case PEER_QUEUED:
                lockEvent.setLockEventState(LockEventState.PEER_ACQUIRED);
                lockEvent.acquired();
                send(lockEvent);
                lockEvent.setLockEventState(LockEventState.PEER_RELEASED);
                queue.remove(lockEvent);
                lockEvent.released();
                send(lockEvent);
                break;
        }
    }

}
