package com.jlc;

/**
 * @author lokesh
 */

public interface LockEvent {

    void setId();
    Object getId();
    void acquired();
}