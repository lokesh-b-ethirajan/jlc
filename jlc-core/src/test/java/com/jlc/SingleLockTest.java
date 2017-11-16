package com.jlc;

import com.jlc.mgr.LockManager;
import com.jlc.mgr.SimpleLockManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author lokesh
 */

public class SingleLockTest {

    private static final Logger logger = LogManager.getLogger(SingleLockTest.class);

    private LockManager lockManager = null;
    private UUID uuid = UUID.randomUUID();

    @BeforeClass
    public void beforeClass() {
        TestCounter.clear();
        lockManager = new SimpleLockManager();
    }

    @Test(threadPoolSize = 4, invocationCount = 8, timeOut = 1000)
    public void theTest() {

        SampleLockEvent lockEvent = new SampleLockEvent();
        lockEvent.setId(uuid);
        lockEvent.setPayload(UUID.randomUUID().toString());

        lockManager.lock(lockEvent);
    }

    @AfterClass
    public void afterClass() {

        while(TestCounter.get() != 8) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        TestCounter.clear();

        lockManager.shutdown();
    }

}
