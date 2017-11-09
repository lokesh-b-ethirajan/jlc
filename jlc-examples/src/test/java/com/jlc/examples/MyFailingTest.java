package com.jlc.examples;

import com.jlc.examples.mymodel.DeviceState;
import com.jlc.examples.myservice.DeviceStateService;
import com.jlc.examples.myservice.MyServiceFactory;
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

public class MyFailingTest {

    private static final Logger logger = LogManager.getLogger(MyFailingTest.class);

    String device = "Apple-ipad";

    @BeforeClass
    public void beforeClass() {

        String state = UUID.randomUUID().toString();
        DeviceState deviceState = new DeviceState();
        deviceState.setDevice(device);
        deviceState.setState(state);

        logger.info(state);

        MyServiceFactory.getMyServiceFactory().getDeviceStateService().add(deviceState);

    }

    /*
    this test will throw several optimistic lock exceptions
    javax.persistence.OptimisticLockException
     */
    @Test(threadPoolSize = 4, invocationCount = 8, timeOut = 1000)
    public void theTest() {

        try {
            String state = UUID.randomUUID().toString();
            DeviceState deviceState = MyServiceFactory.getMyServiceFactory().getDeviceStateService().get(device);
            String oldState = deviceState.getState();
            deviceState.setState(state);
            logger.info(oldState + " --> " + state);
            MyServiceFactory.getMyServiceFactory().getDeviceStateService().add(deviceState);
        } catch (Exception e) {
            logger.error(e);
        }

    }

    @AfterClass
    public void afterClass() {
        for(DeviceState deviceState : MyServiceFactory.getMyServiceFactory().getDeviceStateService().list()) {
            logger.info(deviceState.getDevice() + " : " + deviceState.getState());
            MyServiceFactory.getMyServiceFactory().getDeviceStateService().delete(deviceState.getDevice());
        }
    }

}
