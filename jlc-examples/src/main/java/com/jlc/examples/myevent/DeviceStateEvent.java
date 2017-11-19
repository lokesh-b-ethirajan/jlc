package com.jlc.examples.myevent;

import com.jlc.event.LockEvent;
import com.jlc.examples.mymodel.DeviceState;
import com.jlc.examples.myservice.DeviceStateService;
import com.jlc.examples.myservice.MyServiceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author lokesh
 */

public class DeviceStateEvent extends LockEvent {

    private static final Logger logger = LogManager.getLogger(DeviceStateEvent.class);

    private DeviceState newDeviceState;

    public DeviceStateEvent(DeviceState newDeviceState) {
        this.newDeviceState = newDeviceState;
    }

    @Override
    public void requested() {

    }

    @Override
    public void queued() {

    }

    @Override
    public void acquired() {

        try {
            DeviceStateService deviceStateService = MyServiceFactory.getMyServiceFactory().getDeviceStateService();
            DeviceState persistedDeviceState = deviceStateService.get(newDeviceState.getDevice());
            String oldState = persistedDeviceState.getState();
            persistedDeviceState.setState(newDeviceState.getState());
            deviceStateService.add(persistedDeviceState);
            logger.info(oldState + " --> " + persistedDeviceState.getState());
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Override
    public void released() {

    }
}


