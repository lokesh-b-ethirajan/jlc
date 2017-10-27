package com.mydao;

import com.mymodel.DeviceState;
import java.util.List;

/**
 * @author lokesh
 */

public interface DeviceStateDAO {

    void add(DeviceState deviceState);
    DeviceState get(String device);
    List<DeviceState> list();
    void delete(String device);

}
