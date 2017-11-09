package com.jlc.net;

import com.jlc.LockEvent;
import com.jlc.SimpleLockManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * @author lokesh
 */

public class LockServer implements Runnable {

    private static final Logger logger = LogManager.getLogger(LockServer.class);

    private int port;
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private ObjectInputStream objectInputStream = null;
    private SimpleLockManager simpleLockManager = null;

    private volatile boolean shutdown = false;
    private volatile boolean shutdownComplete = false;

    public LockServer(int port) {
        this.port = port;
        new Thread(this).start();
    }

    public void setSimpleLockManager(SimpleLockManager simpleLockManager) {
        this.simpleLockManager = simpleLockManager;
    }

    private ServerSocket getServerSocket() throws IOException {
        if(serverSocket == null) {
            serverSocket = new ServerSocket(port);
        }

        return serverSocket;
    }

    private Socket getSocket() throws IOException {
        if(socket == null) {
            socket = getServerSocket().accept();
        }

        return socket;
    }

    private ObjectInputStream getObjectInputStream() throws IOException {
        if(objectInputStream == null) {
            objectInputStream = new ObjectInputStream(getSocket().getInputStream());
        }

        return objectInputStream;
    }

    private void cleanup() {

        if(objectInputStream != null) {
            try {
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                objectInputStream = null;
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
                logger.debug("waiting for lock events..");
                LockEvent lockEvent = (LockEvent) getObjectInputStream().readObject();
                logger.debug(lockEvent);
                if(simpleLockManager != null)
                    simpleLockManager.lock(lockEvent);
            } catch (Exception e) {
                logger.error(e);
                cleanup();
                sleep(10);
            }
        }

        shutdownComplete = true;

    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
