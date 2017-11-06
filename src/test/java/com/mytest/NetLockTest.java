package com.mytest;

import com.jlc.net.LockClient;
import com.jlc.net.LockServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author lokesh
 */

public class NetLockTest {

    private static final Logger logger = LogManager.getLogger(NetLockTest.class);

    @Test
    public void testNetLock() throws InterruptedException, IOException {
        logger.info("test net lock");

        LockServer lockServer = new LockServer(8040);
        TimeUnit.SECONDS.sleep(5);
        LockClient lockClient = new LockClient("localhost", 8040);
        lockClient.lock(new Message());
        TimeUnit.SECONDS.sleep(5);
        lockServer.shutdown();

    }

}
