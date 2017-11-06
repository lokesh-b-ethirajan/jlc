package com.mytest;

import com.jlc.*;
import com.myevent.DeviceStateEvent;
import com.mymodel.DeviceState;
import com.myservice.DeviceStateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author lokesh
 */

public class DistributedLockTest {

    private static final Logger logger = LogManager.getLogger(DistributedLockTest.class);

    private AnnotationConfigApplicationContext context = null;
    private DeviceStateService deviceStateService = null;
    private String device = "Apple-ipad";
    private String device2 = "Microsoft-Surface";

    private LockPartitioner lockPartitioner = new DistributedLockPartitioner(2);

    @BeforeClass
    public void beforeClass() {

        context = new AnnotationConfigApplicationContext(AppConfig.class);
        deviceStateService = context.getBean(DeviceStateService.class);

        addDevice(device);
        addDevice(device2);

    }

    private void addDevice(String device) {
        String state = UUID.randomUUID().toString();
        DeviceState deviceState = new DeviceState();
        deviceState.setDevice(device);
        deviceState.setState(state);

        logger.info(state);

        deviceStateService.add(deviceState);
    }

    /*
    this test will NOT throw optimistic lock exceptions
    the db updates are staged in a lock-free queue and dispatched to db in the background
     */
    @Test
            //(threadPoolSize = 4, invocationCount = 8, timeOut = 1000)
    public void theTest() {

        updateDevice(device);
        updateDevice(device2);

    }

    private void updateDevice(String device) {
        String state = UUID.randomUUID().toString();
        DeviceState newDeviceState = new DeviceState();
        newDeviceState.setDevice(device);
        newDeviceState.setState(state);

        DeviceStateEvent deviceStateEvent = new DeviceStateEvent(deviceStateService, newDeviceState);
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

        for(DeviceState deviceState : deviceStateService.list()) {
            logger.info(deviceState.getDevice() + " : " + deviceState.getState());
            deviceStateService.delete(deviceState.getDevice());
        }

        context.close();
    }

}
