/*
* Pcd8544DisplayBufferTest.java
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
package de.rose53.marvin.platform;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rose53.marvin.platform.Pcd8544DisplayBuffer;


/**
 * @author rose
 *
 */
public class Pcd8544DisplayBufferTest {

    private static URL PI4J_LOGO    = Pcd8544DisplayBufferTest.class.getResource("/pi4j.jpg");
    private static URL ROSE_LOGO_BW = Pcd8544DisplayBufferTest.class.getResource("/rose.bmp");

    static byte[] logo16Bmp =
        { (byte) 0b00000000, (byte) 0b11000000,
          (byte) 0b00000001, (byte) 0b11000000,
          (byte) 0b00000001, (byte) 0b11000000,
          (byte) 0b00000011, (byte) 0b11100000,
          (byte) 0b11110011, (byte) 0b11100000,
          (byte) 0b11111110, (byte) 0b11111000,
          (byte) 0b01111110, (byte) 0b11111111,
          (byte) 0b00110011, (byte) 0b10011111,
          (byte) 0b00011111, (byte) 0b11111100,
          (byte) 0b00001101, (byte) 0b01110000,
          (byte) 0b00011011, (byte) 0b10100000,
          (byte) 0b00111111, (byte) 0b11100000,
          (byte) 0b00111111, (byte) 0b11110000,
          (byte) 0b01111100, (byte) 0b11110000,
          (byte) 0b01110000, (byte) 0b01110000,
          (byte) 0b00000000, (byte) 0b00110000 };

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
     * Test method for {@link de.rose53.marvin.platform.Pcd8544DisplayBuffer#clearDisplayBuffer()}.
     */
    @Test
    public void testClearDisplayBuffer() {
        Pcd8544DisplayBuffer buffer = new Pcd8544DisplayBuffer();

        assertNotNull(buffer);

        for (int x = 0; x < 16; x++) {
            buffer.setPixel(new Point(x, 0), true);
        }

        for (int x = 0; x < 16; x++) {
            assertTrue(buffer.getPixel(new Point(x, 0)));
        }

        buffer.clearDisplayBuffer();

        for (int x = 0; x < Pcd8544DisplayBuffer.WIDTH; x++) {
            for (int y = 0; y < Pcd8544DisplayBuffer.HEIGHT; y++) {
                assertFalse(buffer.getPixel(new Point(x, y)));
            }
        }
    }

    @Test
    public void testSetGetPixel() {

        Pcd8544DisplayBuffer buffer = new Pcd8544DisplayBuffer();

        assertNotNull(buffer);

        for (int x = 0; x < 16; x++) {
            buffer.setPixel(new Point(x, 0), true);
            assertTrue(buffer.getPixel(new Point(x, 0)));
        }

        for (int y = 0; y < 16; y++) {
            buffer.setPixel(new Point(0, y), true);
            assertTrue(buffer.getPixel(new Point(0, y)));
        }

    }

    @Test
    public void testSetGetPixel2() {

        Pcd8544DisplayBuffer buffer = new Pcd8544DisplayBuffer();

        assertNotNull(buffer);

        for (int x = 0; x < 16; x++) {
            buffer.setPixel(new Point(x, 0), (x & 0x01) > 0 );
        }
        for (int x = 0; x < 16; x++) {
            if ((x & 0x01) > 0) {
                assertEquals(buffer.getBuffer()[x],1);
            } else {
                assertEquals(buffer.getBuffer()[x],0);
            }

        }

        for (int y = 0; y < 16; y++) {
            buffer.setPixel(new Point(0, y), (y & 0x01) > 0 );
        }
        assertEquals((byte)0b10101010,buffer.getBuffer()[0]);
    }

    @Test
    public void testSetGetPixel3() {

        Pcd8544DisplayBuffer buffer = new Pcd8544DisplayBuffer();

        assertNotNull(buffer);
        buffer.fillRect(new Rectangle(0, 0,Pcd8544DisplayBuffer.WIDTH,Pcd8544DisplayBuffer.HEIGHT),true);

        for (int x = 0; x < 16; x++) {
            buffer.setPixel(new Point(x, 0), (x & 0x01) > 0 );
        }
        for (int x = 0; x < 16; x++) {
            if ((x & 0x01) > 0) {
                assertEquals(buffer.getBuffer()[x],-1);
            } else {
                assertEquals(buffer.getBuffer()[x],-2);
            }

        }

        for (int y = 0; y < 16; y++) {
            buffer.setPixel(new Point(0, y), (y & 0x01) > 0 );
        }
        assertEquals((byte)0b10101010,buffer.getBuffer()[0]);
    }

    @Test
    public void testSetGetPixel4() {

        Pcd8544DisplayBuffer buffer = new Pcd8544DisplayBuffer();

        assertNotNull(buffer);

        buffer.setPixel(new Point(1, 3), true);
        buffer.setPixel(new Point(1, 4), false);
        buffer.setPixel(new Point(1, 4), true);
        assertEquals(24,buffer.getBuffer()[1]);
    }

    @Test
    public void testDrawImageColor() throws IOException {
        Pcd8544DisplayBuffer buffer = new Pcd8544DisplayBuffer();

        assertNotNull(buffer);

        buffer.drawImage(new Point(),ImageIO.read(PI4J_LOGO));
    }

    @Test
    public void testDrawImageBw() throws IOException {
        Pcd8544DisplayBuffer buffer = new Pcd8544DisplayBuffer();

        assertNotNull(buffer);

        buffer.drawImage(new Point(),ImageIO.read(ROSE_LOGO_BW));
    }

    @Test
    public void testDrawImageByteArray() {
        Pcd8544DisplayBuffer buffer = new Pcd8544DisplayBuffer();

        assertNotNull(buffer);


        buffer.drawImage(new Point(30,16),logo16Bmp,16,16,true);
    }
}
