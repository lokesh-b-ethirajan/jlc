package com.jlc.mgr;

import com.jlc.event.LockEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author lokesh
 */

public class SimpleLockManager implements LockManager {

    private static final Logger logger = LogManager.getLogger(SimpleLockManager.class);

    private volatile boolean shutdown = false;
    private volatile boolean shutdownComplete = false;

    private Queue<LockEvent> queue = new ConcurrentLinkedQueue<>();

    public SimpleLockManager() {
        new Thread(this).start();
    }

    @Override
    public void run() {

        logger.info("Running simple lock manager..");

        while (!shutdown) {

            LockEvent lockEvent = queue.peek();
            if(lockEvent != null) {
                lockEvent.acquired();
                release(lockEvent);
            } else {
                sleep(1);
            }
        }

        // TODO: consider persisting pending objects
        logger.error("Shutting down..objects pending in queue -> " + queue.size());

        shutdownComplete = true;
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
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
        queue.add(lockEvent);
        logger.info("added lock event to queue");
    }

    private void release(LockEvent lockEvent) {
        queue.remove(lockEvent);
    }
}
