package com.myevent;

import com.jlc.LockEvent;
import com.mymodel.DeviceState;
import com.myservice.DeviceStateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author lokesh
 */

public class DeviceStateEvent implements LockEvent {

    private static final Logger logger = LogManager.getLogger(DeviceStateEvent.class);

    private DeviceStateService deviceStateService = null;
    private DeviceState newDeviceState;

    public DeviceStateEvent(DeviceStateService deviceStateService, DeviceState newDeviceState) {
        this.deviceStateService = deviceStateService;
        this.newDeviceState = newDeviceState;
    }

    @Override
    public void setId() {

    }

    @Override
    public Object getId() {
        return newDeviceState.getDevice();
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


