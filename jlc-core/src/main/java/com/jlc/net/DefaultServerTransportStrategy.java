package com.jlc.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author lokesh
 */

public class DefaultServerTransportStrategy extends AbstractTransportStrategy {

    public DefaultServerTransportStrategy(int port) {
        super(port);
    }

    private ServerSocket getServerSocket() throws IOException {
        if(serverSocket == null) {
            serverSocket = new ServerSocket(port);
        }

        return serverSocket;
    }

    @Override
    protected Socket getSocket() throws IOException {
        if(socket == null) {
            socket = getServerSocket().accept();
        }

        return socket;
    }

    @Override
    protected void cleanup() {

        super.cleanup();

        if(serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                serverSocket = null;
            }
        }
    }
}
