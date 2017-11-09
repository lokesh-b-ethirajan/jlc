package com.jlc.net;

import com.jlc.LockEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author lokesh
 */

public class LockClient {

    private static final Logger logger = LogManager.getLogger(LockClient.class);

    private String host = null;
    private int port;
    private Socket socket = null;
    private ObjectOutputStream objectOutputStream = null;

    public LockClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private Socket getSocket() throws IOException {

        if(socket == null || socket.isClosed()) {
            socket = new Socket(host, port);
        }

        return socket;
    }

    private ObjectOutputStream getObjectOutputStream() throws IOException {
        if(objectOutputStream == null) {
            objectOutputStream = new ObjectOutputStream(getSocket().getOutputStream());
        }

        return objectOutputStream;
    }

    public void lock(LockEvent lockEvent) throws IOException {
        try {
            logger.debug("writing.." + lockEvent);
            getObjectOutputStream().writeObject(lockEvent);
            logger.debug("finished writing.." + lockEvent);
        } catch (IOException e) {
            logger.error(e);
            shutdown();
            throw e;
        }
    }

    public void shutdown() {

        if(objectOutputStream != null) {
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                objectOutputStream = null;
            }
        }
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket = null;
            }
        }
    }

}
