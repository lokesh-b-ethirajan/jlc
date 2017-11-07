package com.mytest;

import com.jlc.*;
import com.jlc.config.JSONPartitionConfig;
import com.jlc.config.PartitionConfig;
import com.myevent.DeviceStateEvent;
import com.mymodel.DeviceState;
import com.myservice.DeviceStateService;
import com.myservice.MyServiceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author lokesh
 */

public class DistributedLockTest {

    private static final Logger logger = LogManager.getLogger(DistributedLockTest.class);

    private String device = "Apple-ipad";
    private String device2 = "Microsoft-Surface";

    private LockPartitioner lockPartitioner = null;

    @BeforeClass
    public void beforeClass() throws FileNotFoundException, URISyntaxException {

        URL resource = getClass().getClassLoader().getResource("partition-config.json");
        JSONPartitionConfig jsonPartitionConfig = new JSONPartitionConfig(new File(resource.getFile()));
        lockPartitioner = new DistributedLockPartitioner(jsonPartitionConfig.getPartitionConfigs());

        addDevice(device);
        addDevice(device2);

    }

    private void addDevice(String device) {
        String state = UUID.randomUUID().toString();
        DeviceState deviceState = new DeviceState();
        deviceState.setDevice(device);
        deviceState.setState(state);

        logger.info(state);

        MyServiceFactory.getMyServiceFactory().getDeviceStateService().add(deviceState);
    }

    /*
    this test will NOT throw optimistic lock exceptions
    the db updates are staged in a lock-free queue and dispatched to db in the background
     */
    @Test(threadPoolSize = 4, invocationCount = 8, timeOut = 1000)
    public void theTest() {

        updateDevice(device);
        updateDevice(device2);

    }

    private void updateDevice(String device) {
        String state = UUID.randomUUID().toString();
        DeviceState newDeviceState = new DeviceState();
        newDeviceState.setDevice(device);
        newDeviceState.setState(state);

        DeviceStateEvent deviceStateEvent = new DeviceStateEvent(newDeviceState);
        LockManager lockManager = lockPartitioner.getPartition(deviceStateEvent);
        lockManager.lock(deviceStateEvent);
    }

    @AfterClass
    public void afterClass() {

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lockPartitioner.shutdown();

        for(DeviceState deviceState : MyServiceFactory.getMyServiceFactory().getDeviceStateService().list()) {
            logger.info(deviceState.getDevice() + " : " + deviceState.getState());
            MyServiceFactory.getMyServiceFactory().getDeviceStateService().delete(deviceState.getDevice());
        }

        MyServiceFactory.getMyServiceFactory().close();

    }

}
