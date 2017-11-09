package com.jlc.examples.mymodel;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lokesh
 */

@Entity
@Table(name = "devicestate")
public class DeviceState implements Serializable {

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
