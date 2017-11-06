package com.myservice;

import com.mymodel.DeviceState;

import java.io.Serializable;
import java.util.List;

/**
 * @author lokesh
 */

public interface DeviceStateService extends Serializable {

    void add(DeviceState deviceState);
    DeviceState get(String device);
    List<DeviceState> list();
    void delete(String device);
}
