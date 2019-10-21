/*
* ByteUtils.java
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
package de.rose53.marvin.utils;

/**
 * @author rose
 *
 */
final public class ByteUtils {


    public static byte toUnsignedByte(short val) {
        return (byte)val;
    }

    public static byte[] toUnsignedByte(short[] val) {
        byte[] retVal = new byte[val.length];
        for (int i = 0, j = val.length; i < j; i++) {
            retVal[i] =  toUnsignedByte(val[i]);
        }
        return retVal;
    }

    public static byte toUnsignedByte(int val) {
        return (byte)val;
    }

    /**
     *
     * @param buffer at least lenght of two
     * @return
     */
    public static short toShort(byte[] buffer) {
        if (buffer == null || buffer.length < 2) {
            throw new IllegalArgumentException("Buffer is null or too small");
        }
        return (short)((buffer[1] << 8) + (buffer[0] & 0xff));
    }
}
