/*
* RestHelper.java
*
* Copyright (c) 2014, rose. All rights reserved.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
* MA 02110-1301 USA
*/
package de.rose53.marvin;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * @author rose
 *
 */
public class RestHelper {


    @Inject
    @Any
    Instance<MecanumDrive> mecanumDrives;

    @Inject
    @Any
    Instance<PanTiltSensors> panTiltSensors;

    static RestHelper instance;


    @PostConstruct
    public void test() {
        instance = this;
    }

    static public MecanumDrive getMecanumDrive() {
        return  instance.mecanumDrives.select(new HardwareInstance()).get();
    }

    static public PanTiltSensors getPanTiltSensor() {
        return  instance.panTiltSensors.select(new HardwareInstance()).get();
    }

}
