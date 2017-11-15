package com.jlc.net;

import com.jlc.event.LockEvent;

/**
 * @author lokesh
 */

public interface TransportListener {

    void received(LockEvent lockEvent);
}
