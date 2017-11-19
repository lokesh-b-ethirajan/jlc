package com.jlc.mgr;

import com.jlc.event.LockEvent;
import com.jlc.event.LockEventState;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author lokesh
 */

public class SimpleLockManager implements LockManager, Runnable {

    protected volatile boolean shutdown = false;
    protected volatile boolean shutdownComplete = false;
    protected Queue<LockEvent> queue = new ConcurrentLinkedQueue<>();

    public SimpleLockManager() {
        new Thread(this).start();
    }

    @Override
    public void lock(LockEvent lockEvent) {
        if(lockEvent != null) {
            lockEvent.setLockEventState(LockEventState.REQUESTED);
            lockEvent.requested();
            queue.add(lockEvent);
            lockEvent.setLockEventState(LockEventState.QUEUED);
            lockEvent.queued();
        }
    }

    @Override
    public void release(LockEvent lockEvent) {
        if(lockEvent != null) {
            queue.remove(lockEvent);
            lockEvent.setLockEventState(LockEventState.RELEASED);
            lockEvent.released();
        }
    }

    protected void handle(LockEvent lockEvent) throws Exception {
        if(lockEvent != null) {
            lockEvent.setLockEventState(LockEventState.ACQUIRED);
            lockEvent.acquired();
        }
    }

    @Override
    public void shutdown() {
        this.shutdown = true;
        while(!shutdownComplete) {
            sleep(1);
        }
    }

    @Override
    public void run() {

        while (!shutdown) {
            LockEvent lockEvent = queue.peek();
            if(lockEvent != null) {
                try {
                    handle(lockEvent);
                    release(lockEvent);
                } catch (Exception e) {
                    sleep(2);
                }
            } else {
                sleep(1);
            }
        }

        shutdownComplete = true;
    }

    protected void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
