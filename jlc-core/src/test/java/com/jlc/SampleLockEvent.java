package com.jlc;

import com.jlc.event.LockEvent;
import com.jlc.event.LockEventState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author lokesh
 */

public class SampleLockEvent implements LockEvent {

    private static final Logger logger = LogManager.getLogger(SampleLockEvent.class);

    private Object id = null;
    private LockEventState lockEventState = LockEventState.REQUESTED;

    @Override
    public void setId(Object id) {
        this.id = id;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public void setState(LockEventState lockEventState) {
        this.lockEventState = lockEventState;
    }

    @Override
    public LockEventState getState() {
        return lockEventState;
    }

    @Override
    public void acquired() {
        logger.info("lock acquired -> " + id);
        TestCounter.increment();
    }

    @Override
    public void released() {

    }

    @Override
    public void requested() {

    }

    @Override
    public void queued() {

    }
}
