package com.jlc;

import com.jlc.config.JSONPartitionConfig;
import com.jlc.mgr.LockManager;
import com.jlc.partition.DistributedLockPartitioner;
import com.jlc.partition.LockPartitioner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author lokesh
 */

public class DistributedLockTest {

    private static final Logger logger = LogManager.getLogger(DistributedLockTest.class);

    private LockPartitioner lockPartitioner = null;
    private UUID uuid1 = UUID.randomUUID();
    private UUID uuid2 = UUID.randomUUID();

    @BeforeClass
    public void beforeClass() throws FileNotFoundException, URISyntaxException {
        TestCounter.clear();
        URL resource = getClass().getClassLoader().getResource("partition-config.json");
        JSONPartitionConfig jsonPartitionConfig = new JSONPartitionConfig(new File(resource.getFile()));
        lockPartitioner = new DistributedLockPartitioner(jsonPartitionConfig.getPartitionConfigs());
        logger.info("no of partitions -> " + lockPartitioner.getAllPartitions().length);

    }

    @Test
            //(threadPoolSize = 4, invocationCount = 8, timeOut = 1000)
    public void theTest() {

        SampleLockEvent lockEvent1 = new SampleLockEvent();
        lockEvent1.setId(uuid1);
        lockEvent1.setPayload(UUID.randomUUID().toString());
        lockPartitioner.getPartition(lockEvent1).lock(lockEvent1);

        SampleLockEvent lockEvent2 = new SampleLockEvent();
        lockEvent2.setId(uuid2);
        lockEvent2.setPayload(UUID.randomUUID().toString());
        lockPartitioner.getPartition(lockEvent2).lock(lockEvent2);

    }

    @AfterClass
    public void afterClass() {

        while(TestCounter.get() < 2) {
            try {
                logger.info("test counter -> " + TestCounter.get());
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        TestCounter.clear();

        lockPartitioner.shutdown();
    }

}
