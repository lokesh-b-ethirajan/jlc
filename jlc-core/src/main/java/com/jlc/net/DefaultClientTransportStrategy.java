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

public class DefaultClientTransportStrategy implements TransportStrategy, Runnable {

    private static final Logger logger = LogManager.getLogger(DefaultClientTransportStrategy.class);

    private String host = null;
    private int port;
    private Socket socket = null;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;
    private TransportListener transportListener;

    private volatile boolean shutdown = false;
    private volatile boolean shutdownCompleted = false;

    public DefaultClientTransportStrategy(String host, int port) {
        this.host = host;
        this.port = port;
        new Thread(this).start();
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
        objectOutputStream.reset();
        return objectOutputStream;
    }

    private ObjectInputStream getObjectInputStream() throws IOException {
        if(objectInputStream == null) {
            objectInputStream = new ObjectInputStream(getSocket().getInputStream());
        }

        return objectInputStream;
    }

    @Override
    public void shutdown() {

        cleanup();

        shutdown = true;

        while(!shutdownCompleted) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void cleanup() {

        if(objectInputStream != null) {
            try {
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                objectInputStream = null;
            }
        }
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

    @Override
    public void register(TransportListener transportListener) {
        this.transportListener = transportListener;
    }

    @Override
    public void send(LockEvent lockEvent) throws IOException {
        try {
            getObjectOutputStream().writeObject(lockEvent);
        } catch (IOException e) {
            logger.error(e);
            cleanup();
            throw e;
        }
    }

    @Override
    public void run() {

        while(!shutdown) {
            try {
                //TODO: figure out a way to interrupt socket wait
                LockEvent lockEvent = (LockEvent) getObjectInputStream().readObject();
                if(lockEvent != null) {
                    //logger.debug("received.." + lockEvent.getId() + " : " + lockEvent.getLockEventState());
                    transportListener.received(lockEvent);
                }
            } catch (Exception e) {
                //e.printStackTrace();
                logger.error(e);
                cleanup();
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

        }

        shutdownCompleted = true;

    }
}
