package com.mymodel;

import javax.persistence.*;

/**
 * @author lokesh
 */

@Entity
@Table(name = "devicestate")
public class DeviceState {

    @Version
    private int version;

    @Id
    private String device;

    @Column(name = "state")
    private String state;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
