package com.jlc;

import com.jlc.event.LockEvent;
import com.jlc.event.LockEventState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author lokesh
 */

public class SampleLockEvent extends LockEvent {

    private static final Logger logger = LogManager.getLogger(SampleLockEvent.class);

    private String payload = null;

    @Override
    public void acquired() {
        logger.info("lock acquired -> " + getId());
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

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
