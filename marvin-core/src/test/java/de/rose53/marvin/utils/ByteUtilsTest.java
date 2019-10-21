package de.rose53.marvin.utils;
/*
* ByteUtilsTest.java
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


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author rose
 *
 */
public class ByteUtilsTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link de.rose53.pi4j.component.lcd.impl.Pcd8544Utils#toUnsignedByte(short)}.
     */
    @Test
    public void testToUnsignedByteShort() {
        short s = 0;

        assertEquals(0,ByteUtils.toUnsignedByte(s));

        s = 0xaa;
        assertEquals(-86,ByteUtils.toUnsignedByte(s));

        s = 0xff;
        assertEquals(-1,ByteUtils.toUnsignedByte(s));

        s = 0x80;
        assertEquals(-128,ByteUtils.toUnsignedByte(s));
    }

    /**
     * Test method for {@link de.rose53.pi4j.component.lcd.impl.Pcd8544Utils#toUnsignedByte(int)}.
     */
    @Test
    public void testToUnsignedByteInt() {
        assertEquals(0,ByteUtils.toUnsignedByte(0));
        assertEquals(-86,ByteUtils.toUnsignedByte(0xaa));
        assertEquals(-1,ByteUtils.toUnsignedByte(0xFF));
        assertEquals(-128,ByteUtils.toUnsignedByte(0x80));
    }

}
