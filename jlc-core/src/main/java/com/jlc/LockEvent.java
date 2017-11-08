package com.jlc;

import java.io.Serializable;

/**
 * @author lokesh
 */

public interface LockEvent extends Serializable {

    void setId(Object id);
    Object getId();
    void acquired();
}