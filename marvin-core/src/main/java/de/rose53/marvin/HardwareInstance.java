/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rose53.marvin;

import java.lang.annotation.Annotation;

/**
 *
 * @author rose
 */
public class HardwareInstance implements Hardware {

    hw hardware;

    public HardwareInstance(hw hardware) {
        this.hardware = hardware;
    }

    public HardwareInstance() {
        String os = System.getProperty("os.name").toLowerCase();
        if ((os.indexOf("win") >= 0)) {
            this.hardware = hw.INTEL;
        } else {
            this.hardware = hw.PI;
        }
    }

    @Override
    public hw value() {
        return hardware;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Hardware.class;
    }

}
