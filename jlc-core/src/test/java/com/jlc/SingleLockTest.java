package com.jlc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * @author lokesh
 */

public class SingleLockTest {

    private static final Logger logger = LogManager.getLogger(SingleLockTest.class);

    private LockManager lockManager = null;
    private UUID uuid = UUID.randomUUID();

    @BeforeClass
    public void beforeClass() {
        lockManager = new SimpleLockManager();
    }

    @Test(threadPoolSize = 4, invocationCount = 8, timeOut = 1000)
    public void theTest() {

        SampleLockEvent lockEvent = new SampleLockEvent();
        lockEvent.setId(uuid);
        lockEvent.setState(UUID.randomUUID().toString());

        lockManager.lock(lockEvent);
    }

    @AfterClass
    public void afterClass() {
        lockManager.shutdown();
    }

}
