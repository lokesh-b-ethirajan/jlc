package com.jlc.event;

import java.io.Serializable;

/**
 * @author lokesh
 */

public enum LockEventState implements Serializable {

    INIT,
    REQUESTED,
    QUEUED,
    ACQUIRED,
    RELEASED,
    PEER_REQUESTED,
    PEER_QUEUED,
    PEER_ACQUIRED,
    PEER_RELEASED
}
