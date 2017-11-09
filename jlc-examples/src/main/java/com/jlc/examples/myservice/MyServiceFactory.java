package com.jlc.examples.myservice;

import com.jlc.examples.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author lokesh
 */

public class MyServiceFactory {

    private static final MyServiceFactory myServiceFactory = new MyServiceFactory();
    private AnnotationConfigApplicationContext context = null;

    private MyServiceFactory() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
    }

    public static MyServiceFactory getMyServiceFactory() {
        return myServiceFactory;
    }

    public DeviceStateService getDeviceStateService() {
        return context.getBean(DeviceStateService.class);
    }

    public void close() {
        context.close();
    }
}
