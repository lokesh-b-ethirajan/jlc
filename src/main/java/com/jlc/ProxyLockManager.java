package com.jlc;

import com.jlc.net.LockClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author lokesh
 */

public class ProxyLockManager implements LockManager {

    private static final Logger logger = LogManager.getLogger(ProxyLockManager.class);

    private volatile boolean shutdown = false;
    private volatile boolean shutdownComplete = false;

    private Queue<LockEvent> queue = new ConcurrentLinkedQueue<>();
    private LockClient lockClient = null;

    public ProxyLockManager(String host, int port)
    {
        lockClient = new LockClient(host, port);
        new Thread(this).start();
    }

    @Override
    public void run() {

        logger.info("Running simple lock manager..");

        while (!shutdown || !queue.isEmpty()) {

            LockEvent lockEvent = queue.peek();
            if(lockEvent != null) {
                try {
                    lockClient.lock(lockEvent);
                    release(lockEvent);
                } catch (IOException e) {
                    e.printStackTrace();
                    sleep(1);
                }
            }
            else
            {
                sleep(1);
            }
        }

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
            logger.info("Waiting for shutdown..");
            sleep(10);
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
