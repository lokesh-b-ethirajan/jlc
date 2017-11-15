package com.jlc.mgr;

import com.jlc.event.LockEvent;
import com.jlc.event.LockEventState;
import com.jlc.net.TransportListener;
import com.jlc.net.TransportStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * @author lokesh
 * an extension of simple lock manager supporting a transport strategy
 */

public class RemoteLockManager extends SimpleLockManager implements TransportListener {

    private static final Logger logger = LogManager.getLogger(RemoteLockManager.class);

    private TransportStrategy transportStrategy;

    public RemoteLockManager(TransportStrategy transportStrategy) {
        this.transportStrategy = transportStrategy;
        transportStrategy.register(this);
    }

    @Override
    public void received(LockEvent lockEvent) {

        if (logger.isDebugEnabled())
            logger.debug("lock event received -> " + lockEvent.getState());

        switch (lockEvent.getState()) {

            case REQUESTED:

                lockEvent.requested();
                lock(lockEvent);
                lockEvent.queued();
                lockEvent.setState(LockEventState.QUEUED);
                try {
                    transportStrategy.send(lockEvent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case QUEUED:
                //lockEvent.queued(); N/A here
                break;
            case ACQUIRED:
                //lockEvent.acquired(); N/A here
                break;
            case RELEASED:
                //lockEvent.released(); N/A here
                break;
        }
    }

}
