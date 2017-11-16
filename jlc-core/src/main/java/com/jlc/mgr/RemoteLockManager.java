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
 * an extension of simple lock manager supporting remote lock requests using a transport strategy
 */

public class RemoteLockManager implements LockManager, TransportListener {

    private static final Logger logger = LogManager.getLogger(RemoteLockManager.class);

    private volatile boolean shutdown = false;
    private volatile boolean shutdownComplete = false;

    private Queue<LockEvent> queue = new ConcurrentLinkedQueue<>();
    private TransportStrategy transportStrategy;

    public RemoteLockManager(TransportStrategy transportStrategy) {
        this.transportStrategy = transportStrategy;
        transportStrategy.register(this);
        new Thread(this).start();
    }

    @Override
    public void run() {

        logger.info("Running remote lock manager..");

        while (!shutdown) {

            LockEvent lockEvent = queue.peek();
            if(lockEvent != null) {

                switch (lockEvent.getLockEventState()) {

                    case REQUESTED:
                    case QUEUED:
                        lockEvent.setLockEventState(LockEventState.ACQUIRED);
                        lockEvent.acquired();
                        lockEvent.setLockEventState(LockEventState.RELEASED);
                        release(lockEvent);
                        lockEvent.released();
                        break;
                    case PEER_REQUESTED:
                    case PEER_QUEUED:
                        lockEvent.setLockEventState(LockEventState.PEER_ACQUIRED);
                        lockEvent.acquired();
                        send(lockEvent);
                        lockEvent.setLockEventState(LockEventState.PEER_RELEASED);
                        release(lockEvent);
                        lockEvent.released();
                        send(lockEvent);
                        break;
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
            logger.debug("lock event received -> " + lockEvent.getLockEventState());

        if (lockEvent.getLockEventState()== LockEventState.PEER_REQUESTED) {
            lockEvent.requested();
            queueLockEvent(lockEvent);
            lockEvent.setLockEventState(LockEventState.PEER_QUEUED);
            lockEvent.queued();
            send(lockEvent);
        }
    }

    @Override
    public void lock(LockEvent lockEvent) {
        lockEvent.setLockEventState(LockEventState.REQUESTED);
        lockEvent.requested();
        queueLockEvent(lockEvent);
        lockEvent.setLockEventState(LockEventState.QUEUED);
        lockEvent.queued();
    }

    private void queueLockEvent(LockEvent lockEvent) {
        queue.add(lockEvent);
        if(logger.isDebugEnabled())
            logger.debug("added lock event to queue");
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

    private void send(LockEvent lockEvent) {
        try {
            if(transportStrategy != null && lockEvent != null) {
                transportStrategy.send(lockEvent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
