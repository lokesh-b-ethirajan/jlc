package com.jlc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * @author lokesh
 */

public class MultiLockTest {

    private static final Logger logger = LogManager.getLogger(MultiLockTest.class);

    private LockPartitioner lockPartitioner = new DefaultLockPartitioner(2);
    private UUID uuid1 = UUID.randomUUID();
    private UUID uuid2 = UUID.randomUUID();

    @Test(threadPoolSize = 4, invocationCount = 8, timeOut = 1000)
    public void theTest() {

        SampleLockEvent lockEvent1 = new SampleLockEvent();
        lockEvent1.setId(uuid1);
        lockEvent1.setState(UUID.randomUUID().toString());
        lockPartitioner.getPartition(lockEvent1).lock(lockEvent1);

        SampleLockEvent lockEvent2 = new SampleLockEvent();
        lockEvent2.setId(uuid2);
        lockEvent2.setState(UUID.randomUUID().toString());
        lockPartitioner.getPartition(lockEvent2).lock(lockEvent2);

    }

    @AfterClass
    public void afterClass() {
        lockPartitioner.shutdown();
    }

}
