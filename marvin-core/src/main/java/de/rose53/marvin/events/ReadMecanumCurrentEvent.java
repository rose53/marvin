/*
* ReadMecanumCurrentEvent.java
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

import java.util.Arrays;

import de.rose53.marvin.Motor;


/**
 * @author rose
 *
 */
public class ReadMecanumCurrentEvent {

    private short[] current;

    public ReadMecanumCurrentEvent() {

    }

    public ReadMecanumCurrentEvent(short[] current) {
        this.current = Arrays.copyOf(current,current.length);
    }

    public short getCurrent(Motor motor) {
        if (motor == null) {
            return 0;
        }
        short retVal = 0;
        switch (motor) {
        case FRONT_LEFT:
            retVal = current[0];
            break;
        case FRONT_RIGHT:
            retVal = current[1];
            break;
        case REAR_LEFT:
            retVal = current[2];
            break;
        case REAR_RIGHT:
            retVal = current[3];
            break;
        default:
            break;
        }
        return retVal;
    }

    public short[] getCurrent() {
        return current;
    }
}
