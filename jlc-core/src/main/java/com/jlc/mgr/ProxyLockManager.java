package com.jlc.mgr;

import com.jlc.event.LockEvent;
import com.jlc.event.LockEventState;
import com.jlc.net.TransportListener;
import com.jlc.net.TransportStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author lokesh
 * proxying lock requests to remote nodes using a transport strategy
 */

public class ProxyLockManager implements LockManager, TransportListener {

    private static final Logger logger = LogManager.getLogger(ProxyLockManager.class);

    private volatile boolean shutdown = false;
    private volatile boolean shutdownComplete = false;

    private Queue<LockEvent> queue = new ConcurrentLinkedQueue<>();
    private TransportStrategy transportStrategy;

    public ProxyLockManager(TransportStrategy transportStrategy) {
        this.transportStrategy = transportStrategy;
        transportStrategy.register(this);
        new Thread(this).start();
    }

    @Override
    public void run() {

        logger.info("Running proxy lock manager..");

        while (!shutdown) {

            LockEvent lockEvent = queue.peek();
            if(lockEvent != null) {
                try
                {
                    send(lockEvent);
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
        logger.info("Shutting down..objects pending in queue -> " + queue.size());

        shutdownComplete = true;
    }

    @Override
    public void received(LockEvent lockEvent) {

        if (logger.isDebugEnabled())
            logger.debug("lock event received -> " + lockEvent.getId() + " : " + lockEvent.getLockEventState() + " : " + lockEvent.i);

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

    @Override
    public void shutdown() {

        if(transportStrategy != null)
            transportStrategy.shutdown();

        this.shutdown = true;

        while(!shutdownComplete) {
            try {
                logger.info("Waiting for shutdown..");
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        logger.info("Shutdown completed");
    }

    @Override
    public void lock(LockEvent lockEvent) {
        lockEvent.setLockEventState(LockEventState.PEER_REQUESTED);
        lockEvent.i++;
        queue.add(lockEvent);
    }

    private void send(LockEvent lockEvent) throws IOException {
        if(transportStrategy != null && lockEvent != null)
            transportStrategy.send(lockEvent);
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void release(LockEvent lockEvent) {
        queue.remove(lockEvent);
    }

}
