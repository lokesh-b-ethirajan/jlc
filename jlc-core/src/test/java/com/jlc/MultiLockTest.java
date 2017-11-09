package com.jlc;

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

public class MultiLockTest {

    private static final Logger logger = LogManager.getLogger(MultiLockTest.class);

    private LockPartitioner lockPartitioner = null;
    private UUID uuid1 = UUID.randomUUID();
    private UUID uuid2 = UUID.randomUUID();

    @BeforeClass
    public void beforeClass() {
        TestCounter.clear();
        lockPartitioner = new DefaultLockPartitioner(2);
    }

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

        while(TestCounter.get() != 16) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        TestCounter.clear();

        lockPartitioner.shutdown();
    }

}
