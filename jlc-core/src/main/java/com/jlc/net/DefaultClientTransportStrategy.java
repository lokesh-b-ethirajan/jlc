package com.jlc.net;

import com.jlc.event.LockEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * @author lokesh
 */

public class DefaultClientTransportStrategy extends AbstractTransportStrategy {

    private String host = null;

    public DefaultClientTransportStrategy(String host, int port) {
        super(port);
        this.host = host;
    }

    @Override
    protected Socket getSocket() throws IOException {

        if(socket == null || socket.isClosed()) {
            socket = new Socket(host, port);
        }

        return socket;
    }
}
