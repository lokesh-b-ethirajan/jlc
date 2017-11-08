package com.jlc.myevent;

import com.jlc.LockEvent;
import com.jlc.mymodel.DeviceState;
import com.jlc.myservice.DeviceStateService;
import com.jlc.myservice.MyServiceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author lokesh
 */

public class DeviceStateEvent implements LockEvent {

    private static final Logger logger = LogManager.getLogger(DeviceStateEvent.class);

    private DeviceState newDeviceState;

    public DeviceStateEvent(DeviceState newDeviceState) {
        this.newDeviceState = newDeviceState;
    }

    @Override
    public void setId(Object id) {

    }

    @Override
    public Object getId() {
        return newDeviceState.getDevice();
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
}


