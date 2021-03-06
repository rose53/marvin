/*
* ReadMecanumMotorEvent.java
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
package de.rose53.marvin.events;

import static java.util.stream.Collectors.toMap;
import static java.util.Arrays.stream;

import java.util.Map;


import de.rose53.marvin.Motor;
import de.rose53.marvin.ReadMecanumMotorInfo;

/**
 * @author rose
 *
 */
public class ReadMecanumMotorInfoEvent {

    private ReadMecanumMotorInfo[] readMecanumMotorInfo;

    public ReadMecanumMotorInfoEvent() {
        readMecanumMotorInfo = new ReadMecanumMotorInfo[4];
    }

    public ReadMecanumMotorInfoEvent(ReadMecanumMotorInfo[] readMecanumMotorInfo) {
        this.readMecanumMotorInfo = readMecanumMotorInfo;
    }

    public ReadMecanumMotorInfo getReadMecanumMotorInfo(Motor motor) {
        if (motor == null) {
            return null;
        }
        ReadMecanumMotorInfo retVal = null;
        switch (motor) {
        case FRONT_LEFT:
            retVal = readMecanumMotorInfo[0];
            break;
        case FRONT_RIGHT:
            retVal = readMecanumMotorInfo[1];
            break;
        case REAR_LEFT:
            retVal = readMecanumMotorInfo[2];
            break;
        case REAR_RIGHT:
            retVal = readMecanumMotorInfo[3];
            break;
        default:
            break;
        }
        return retVal;
    }

    public Map<Motor, ReadMecanumMotorInfo> getReadMecanumMotorInfo() {
        return stream(Motor.values()).collect(toMap(m -> m, m -> getReadMecanumMotorInfo(m)));
    }
}
