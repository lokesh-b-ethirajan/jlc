package com.mytest;

import com.jlc.LockEvent;
import com.jlc.LockManager;
import com.jlc.SimpleLockManager;
import com.mymodel.DeviceState;
import com.myservice.DeviceStateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * @author lokesh
 */

public class SingleLockTest {

    private static final Logger logger = LogManager.getLogger(SingleLockTest.class);

    private AnnotationConfigApplicationContext context = null;
    private DeviceStateService deviceStateService = null;
    String device = "Apple-ipad";

    private LockManager lockManager = new SimpleLockManager();

    class DeviceStateEvent implements LockEvent {

        private DeviceState newDeviceState;

        DeviceStateEvent(DeviceState newDeviceState) {
            this.newDeviceState = newDeviceState;
        }

        @Override
        public void setId() {

        }

        @Override
        public Object getId() {
            return device;
        }

        @Override
        public void acquired() {

            try {

                DeviceState persistedDeviceState = deviceStateService.get(newDeviceState.getDevice());
                String oldState = persistedDeviceState.getState();
                persistedDeviceState.setState(newDeviceState.getState());
                deviceStateService.add(persistedDeviceState);
                logger.info(oldState + " --> " + persistedDeviceState.getState());
            } catch (Exception e) {
                logger.info(e);
            }
        }
    }


    @BeforeClass
    public void beforeClass() {

        //new Thread(lockManager).start();

        context = new AnnotationConfigApplicationContext(AppConfig.class);
        deviceStateService = context.getBean(DeviceStateService.class);

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
    @Test(threadPoolSize = 4, invocationCount = 8, timeOut = 1000)
    public void theTest() {

        String state = UUID.randomUUID().toString();
        DeviceState newDeviceState = new DeviceState();
        newDeviceState.setDevice(device);
        newDeviceState.setState(state);

        DeviceStateEvent deviceStateEvent = new DeviceStateEvent(newDeviceState);
        lockManager.lock(deviceStateEvent);

    }

    @AfterClass
    public void afterClass() {

        lockManager.shutdown();

        for(DeviceState deviceState : deviceStateService.list()) {
            logger.info(deviceState.getDevice() + " : " + deviceState.getState());
            deviceStateService.delete(deviceState.getDevice());
        }
        context.close();
    }

}
