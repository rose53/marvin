/*
* ReadMecanumMotorInfo.java
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

/**
 * @author rose
 *
 */
public class ReadMecanumMotorInfo {

    private final boolean direction;
    private final short   speed;

    /**
     * @param direction
     * @param speed
     */
    public ReadMecanumMotorInfo(boolean direction, short speed) {
        super();
        this.direction = direction;
        this.speed = speed;
    }

    /**
     * @return the direction
     */
    public boolean isDirection() {
        return direction;
    }

    /**
     * @return the speed
     */
    public short getSpeed() {
        return speed;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (direction ? 1231 : 1237);
        result = prime * result + speed;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReadMecanumMotorInfo other = (ReadMecanumMotorInfo) obj;
        if (direction != other.direction)
            return false;
        if (speed != other.speed)
            return false;
        return true;
    }


}
