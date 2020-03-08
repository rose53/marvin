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
public interface Distance {

    static public enum Place {
        FRONT("FRONT"),
        LEFT("LEFT"),
        RIGHT("RIGHT"),
        BACK("BACK");

        final String id;

        Place(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static Place fromString(String place) {
            return valueOf(place.toUpperCase());
        }
    }

    /**
     * Returns the distance in [cm]
     * @return the distance in [cm]
     */
    Float getDistance(Place place);
}
