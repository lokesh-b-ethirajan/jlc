package com.mytest;

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

public class MyFailingTest {

    private static final Logger logger = LogManager.getLogger(MyFailingTest.class);

    private AnnotationConfigApplicationContext context = null;
    private DeviceStateService deviceStateService = null;
    String device = "Apple-ipad";

    @BeforeClass
    public void beforeClass() {

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
    this test will throw several optimistic lock exceptions
    javax.persistence.OptimisticLockException
     */
    @Test(threadPoolSize = 4, invocationCount = 8, timeOut = 1000)
    public void theTest() {

        try {
            String state = UUID.randomUUID().toString();
            DeviceState deviceState = deviceStateService.get(device);
            String oldState = deviceState.getState();
            deviceState.setState(state);
            logger.info(oldState + " --> " + state);
            deviceStateService.add(deviceState);
        } catch (Exception e) {
            logger.error(e);
        }

    }

    @AfterClass
    public void afterClass() {
        for(DeviceState deviceState : deviceStateService.list()) {
            logger.info(deviceState.getDevice() + " : " + deviceState.getState());
            deviceStateService.delete(deviceState.getDevice());
        }

        logger.info(deviceStateService.list().size());

        context.close();
    }

}