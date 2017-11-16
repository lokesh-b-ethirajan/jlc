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
 * an extension of simple lock manager supporting remote lock requests using a transport strategy
 */

public class RemoteLockManager extends SimpleLockManager implements TransportListener {

    private static final Logger logger = LogManager.getLogger(RemoteLockManager.class);

    private TransportStrategy transportStrategy;

    public RemoteLockManager(TransportStrategy transportStrategy) {
        this.transportStrategy = transportStrategy;
        transportStrategy.register(this);
    }

    @Override
    public void run() {

        logger.info("Running remote lock manager..");

        while (!shutdown) {

            LockEvent lockEvent = queue.peek();
            if(lockEvent != null) {

                lockEvent.acquired();

                if(lockEvent.getState() == LockEventState.PEER_REQUESTED)
                    send(lockEvent, LockEventState.PEER_ACQUIRED);
                else
                    lockEvent.setState(LockEventState.ACQUIRED);

                release(lockEvent);

                if(lockEvent.getState() == LockEventState.PEER_ACQUIRED)
                    send(lockEvent, LockEventState.PEER_RELEASED);
                else
                    lockEvent.setState(LockEventState.ACQUIRED);

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

        if (lockEvent.getState()== LockEventState.PEER_REQUESTED) {
            lockEvent.requested();
            lock(lockEvent);
            lockEvent.queued();
            send(lockEvent, LockEventState.QUEUED);
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
            transportStrategy.send(lockEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
