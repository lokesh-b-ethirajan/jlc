package com.jlc.net;

import com.jlc.LockEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author lokesh
 */

public class SimpleTransportListener implements TransportListener {

    private static final Logger logger = LogManager.getLogger(SimpleTransportListener.class);

    @Override
    public void received(LockEvent lockEvent) {

        if(logger.isDebugEnabled())
            logger.debug("lock event received -> " + lockEvent.getState());

        switch(lockEvent.getState()) {

            case REQUESTED:
                lockEvent.requested();
                break;
            case QUEUED:
                lockEvent.queued();
                break;
            case ACQUIRED:
                lockEvent.acquired();
                break;
            case RELEASED:
                lockEvent.released();
                break;

        }

    }
}
