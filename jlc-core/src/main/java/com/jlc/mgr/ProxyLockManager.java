package com.jlc.mgr;

import com.jlc.event.LockEvent;
import com.jlc.event.LockEventState;
import com.jlc.net.TransportListener;
import com.jlc.net.TransportStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author lokesh
 * an extension of simple lock manager proxying lock requests to remote nodes using a transport strategy
 */

public class ProxyLockManager extends SimpleLockManager implements TransportListener {

    private static final Logger logger = LogManager.getLogger(ProxyLockManager.class);

    private TransportStrategy transportStrategy;

    public ProxyLockManager(TransportStrategy transportStrategy) {
        this.transportStrategy = transportStrategy;
        transportStrategy.register(this);
    }

    @Override
    public void run() {

        logger.info("Running proxy lock manager..");

        while (!shutdown) {

            LockEvent lockEvent = queue.peek();
            if(lockEvent != null) {
                try
                {
                    send(lockEvent, LockEventState.PROXY_REQUESTED);
                    lockEvent.requested();
                    //TODO: need to release the lock only if ack received from remote peer
                    release(lockEvent);
                } catch (Exception e) {
                    logger.error("unable to send the lock request, will retry after sometime : " + e);
                    sleep(2);
                }
            } else {
                sleep(1);
            }
        }

        // TODO: consider persisting pending objects
        logger.error("Shutting down..objects pending in queue -> " + queue.size());

        shutdownComplete = true;
    }

    @Override
    public void received(LockEvent lockEvent) {

        if (logger.isDebugEnabled())
            logger.debug("lock event received -> " + lockEvent.getState());

        switch (lockEvent.getState()) {

            case REQUESTED:
                // lockEvent.requested(); N/A here
                break;
            case QUEUED:
                lockEvent.queued();
                break;
            case ACQUIRED:
                lockEvent.acquired();
                break;
            case RELEASED:
                lockEvent.released();
                break;
        }
    }

    @Override
    public void shutdown() {

        if(transportStrategy != null)
            transportStrategy.shutdown();

        super.shutdown();

        logger.info("Shutdown completed");
    }

    private void send(LockEvent lockEvent, LockEventState lockEventState) {
        lockEvent.setState(lockEventState);
        try {
            if(transportStrategy != null)
                transportStrategy.send(lockEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
