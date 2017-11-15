package com.jlc;

import java.io.Serializable;

/**
 * @author lokesh
 */

public enum LockEventState implements Serializable {

    REQUESTED,
    QUEUED,
    ACQUIRED,
    RELEASED
}
