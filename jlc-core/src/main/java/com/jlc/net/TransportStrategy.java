package com.jlc.net;

import com.jlc.LockEvent;

import java.io.IOException;

/**
 * @author lokesh
 */

public interface TransportStrategy {

    void register(TransportListener transportListener);
    void send(LockEvent lockEvent) throws IOException;
}
