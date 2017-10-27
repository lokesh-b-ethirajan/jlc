package com.myservice;

import com.mymodel.DeviceState;

import java.util.List;

/**
 * @author lokesh
 */

public interface DeviceStateService {

    void add(DeviceState deviceState);
    DeviceState get(String device);
    List<DeviceState> list();
    void delete(String device);
}
