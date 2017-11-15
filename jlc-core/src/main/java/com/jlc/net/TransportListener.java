package com.jlc.net;

import com.jlc.LockEvent;

/**
 * @author lokesh
 */

public interface TransportListener {

    void received(LockEvent lockEvent);
}
