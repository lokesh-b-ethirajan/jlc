package com.jlc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author lokesh
 */

public class SimpleLockManager implements LockManager {

    private static final Logger logger = LogManager.getLogger(SimpleLockManager.class);

    private boolean shutdown = false;
    private boolean shutdownComplete = false;

    private Queue<LockEvent> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void run() {
        while (!shutdown || !queue.isEmpty()) {

            LockEvent lockEvent = queue.peek();
            if(lockEvent != null) {
                lockEvent.acquired();
                release(lockEvent);
            }
            else
            {
                try {
                    Thread.sleep(1);
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
                Thread.sleep(10);
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
