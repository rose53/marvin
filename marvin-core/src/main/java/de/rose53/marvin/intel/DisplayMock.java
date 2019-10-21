/*
* DisplayMock.java
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
package de.rose53.marvin.intel;

import de.rose53.marvin.Display;
import de.rose53.marvin.Hardware;
import de.rose53.marvin.ReadMecanumMotorInfo;

/**
 * @author rose
 *
 */
@Hardware(Hardware.hw.INTEL)
public class DisplayMock implements Display {



    /* (non-Javadoc)
     * @see de.rose53.marvin.Display#welcome()
     */
    @Override
    public void welcome() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see de.rose53.marvin.Display#motorInformation(de.rose53.marvin.ReadMecanumMotorInfo[])
     */
    @Override
    public void motorInformation(ReadMecanumMotorInfo[] mecanumMotorInfo) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see de.rose53.marvin.Display#distance(int)
     */
    @Override
    public void distance(int distance) {
        // TODO Auto-generated method stub

    }

}
