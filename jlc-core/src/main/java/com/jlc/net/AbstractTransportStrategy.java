package com.jlc.net;

import com.jlc.event.LockEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * @author lokesh
 */

public abstract class AbstractTransportStrategy implements TransportStrategy, Runnable {

    protected int port;
    protected ServerSocket serverSocket = null;
    protected Socket socket = null;
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;
    private TransportListener transportListener = null;

    private volatile boolean shutdown = false;
    private volatile boolean shutdownComplete = false;

    public AbstractTransportStrategy(int port) {
        this.port = port;
        new Thread(this).start();
    }

    abstract protected Socket getSocket() throws IOException;

    private ObjectInputStream getObjectInputStream() throws IOException {
        if(objectInputStream == null) {
            objectInputStream = new ObjectInputStream(getSocket().getInputStream());
        }

        return objectInputStream;
    }

    private ObjectOutputStream getObjectOutputStream() throws IOException {
        if(objectOutputStream == null) {
            objectOutputStream = new ObjectOutputStream(getSocket().getOutputStream());
        }
        objectOutputStream.reset();
        return objectOutputStream;
    }

    protected void cleanup() {

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
    public void shutdown() {

        cleanup();

        shutdown = true;

        while (!shutdownComplete) {
            sleep(1);
        }
    }

    @Override
    public void run() {

        while(!shutdown) {
            try {
                LockEvent lockEvent = (LockEvent) getObjectInputStream().readObject();
                transportListener.received(lockEvent);
            } catch (Exception e) {
                cleanup();
                sleep(1);
            }
        }

        shutdownComplete = true;

    }

    protected void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            cleanup();
            throw e;
        }
    }
}
