package com.jlc.sample;

import com.jlc.LockEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author lokesh
 */

public class SampleLockEvent implements LockEvent {

    private static final Logger logger = LogManager.getLogger(SampleLockEvent.class);

    String uuid = null;

    @Override
    public void setId() {

    }

    @Override
    public Object getId() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public void acquired() {
        logger.debug("lock acquired -> " + getId());
    }
}
