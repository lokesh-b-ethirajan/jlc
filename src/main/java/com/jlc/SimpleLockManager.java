package com.jlc;

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

        while (!shutdown || !queue.isEmpty()) {

            LockEvent lockEvent = queue.peek();
            if(lockEvent != null) {
                lockEvent.acquired();
                release(lockEvent);
            }
            else
            {
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        shutdownComplete = true;
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
