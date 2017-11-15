package com.jlc.event;

import java.io.Serializable;

/**
 * @author lokesh
 */

public interface LockEvent extends Serializable {

    // globally unique id
    void setId(Object id);
    Object getId();

    // state management
    void setState(LockEventState lockEventState);
    LockEventState getState();

    // actions based on state changes
    void requested();
    void queued();
    void acquired();
    void released();
}