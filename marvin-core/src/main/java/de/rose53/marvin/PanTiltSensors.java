/*
* PanTiltSensors.java
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
public interface PanTiltSensors {

    /**
     * Sets the the pan and tilt values. The range of both vaues is between
     * 0 and 180 degrees
     * @param pan the angel in degrees (0-180)
     * @param tilt the angel in degrees (0-180)
     */
    void setPanTilt(short pan, short tilt);

    /**
     * Sets the the pan value.
     * @param pan the angel in degrees (0-180)
     *
     * @see #setPanTilt(short, short)
     * @see #setTilt(short)
     */
    void setPan(short pan);

    /**
     * Increments the pan value by the given increment.
     * @param increment the angel in degrees (0-180)
     *
     */
    void incrementPan(short increment);

    /**
     * Decrements the pan value by the given decrement.
     * @param decrement the angel in degrees (0-180)
     *
     */
    void decrementPan(short decrement);

    /**
     * Sets the the tilt value.
     * @param tilt the angel in degrees (0-180)
     *
     * @see #setPanTilt(short, short)
     * @see #setPan(short)
     */
    void setTilt(short tilt);

    /**
     * Increments the tilt value by the given increment.
     * @param increment the angel in degrees (0-180)
     *
     */
    void incrementTilt(short increment);

    /**
     * Decrements the tilt value by the given decrement.
     * @param decrement the angel in degrees (0-180)
     *
     */
    void decrementTilt(short decrement);


    /**
     * Returns the actual distance
     * @return the distance
     */
    int getDistance();

}
