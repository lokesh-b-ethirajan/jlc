package com.jlc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author lokesh
 */

public class SampleLockEvent implements LockEvent {

    private static final Logger logger = LogManager.getLogger(SampleLockEvent.class);

    private Object id = null;
    private String state = null;

    @Override
    public void setId(Object id) {
        this.id = id;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public void acquired() {
        logger.info("lock acquired -> " + id);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
