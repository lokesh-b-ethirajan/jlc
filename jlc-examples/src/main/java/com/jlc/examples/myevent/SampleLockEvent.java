package com.jlc.examples.myevent;

import com.jlc.event.LockEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author lokesh
 */

public class SampleLockEvent extends LockEvent {

    private static final Logger logger = LogManager.getLogger(SampleLockEvent.class);

    @Override
    public void requested() {

    }

    @Override
    public void queued() {

    }

    @Override
    public void acquired() {
        logger.debug("lock acquired -> " + this);
    }

    @Override
    public void released() {

    }
}
