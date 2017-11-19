package com.jlc.event;

import java.io.Serializable;

/**
 * @author lokesh
 */

public abstract class LockEvent implements Serializable {

    private Object id = null;
    private LockEventState lockEventState = LockEventState.INIT;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public LockEventState getLockEventState() {
        return lockEventState;
    }

    public void setLockEventState(LockEventState lockEventState) {
        this.lockEventState = lockEventState;
    }

    // actions based on state changes
    public abstract void requested();
    public abstract void queued();
    public abstract void acquired();
    public abstract void released();

    public String toString() {
        return getId() + " : " + getLockEventState();
    }
}