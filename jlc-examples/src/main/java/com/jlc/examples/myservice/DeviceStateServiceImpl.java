package com.jlc.examples.myservice;

import com.jlc.examples.mydao.DeviceStateDAO;
import com.jlc.examples.mymodel.DeviceState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @author lokesh
 */

@Service
public class DeviceStateServiceImpl implements DeviceStateService {

    private static final Logger logger = LogManager.getLogger(DeviceStateServiceImpl.class);

    @Autowired
    private DeviceStateDAO deviceStateDAO;

    @Transactional
    @Override
    public void add(DeviceState deviceState) {
        deviceStateDAO.add(deviceState);
    }

    @Transactional(readOnly = true)
    @Override
    public DeviceState get(String device) {
        return deviceStateDAO.get(device);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DeviceState> list() {
        return deviceStateDAO.list();
    }

    @Transactional
    @Override
    public void delete(String device) {
        deviceStateDAO.delete(device);
    }
}
