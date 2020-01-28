/*
 * Pcd8544LcdDisplay.java
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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.pi4j.component.lcd.LCD;
import com.pi4j.component.lcd.LCDBase;
import com.pi4j.component.lcd.LCDTextAlignment;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.Spi;

import de.rose53.marvin.Display;
import de.rose53.marvin.Hardware;
import de.rose53.marvin.ReadMecanumMotorInfo;
import de.rose53.marvin.utils.ByteUtils;

/**
 * @author rose
 *
 * This code is inspired by the
 * <a href="https://github.com/adafruit/Adafruit-GFX-Library">Adafruit GFX-Library</a>
 *
 */
@ApplicationScoped
@Hardware(Hardware.hw.PI)
public class Pcd8544LcdDisplay implements LCD, Display {

    private final static int SPEED = 4000000;

    //White backlight
    private final static int CONTRAST = 0xaa;


    private final static int ROWS           = Pcd8544DisplayBuffer.HEIGHT / (Pcd8544Font.CHAR_HEIGHT + 1);
    private final static int COLUMNS        = Pcd8544DisplayBuffer.WIDTH / (Pcd8544Font.CHAR_WIDTH + 1);

    @Inject
    Logger logger;

    @Inject
    private GpioController gpio;

    @Inject
    private  Pcd8544DisplayBuffer displayBuffer;

    private final int            channel = 0;
    private GpioPinDigitalOutput dcPin;
    private GpioPinDigitalOutput rstPin;
    private GpioPinPwmOutput     ledPin;

    private final LCDDelegate    lcdDelegate = new LCDDelegate();

    private int textCursorRow    = 0;
    private int textCursorColumn = 0;

    @PostConstruct
    public void init() {

        dcPin  = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "DC Pin", PinState.LOW);
        rstPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "RST Pin", PinState.LOW);
        ledPin = gpio.provisionPwmOutputPin(RaspiPin.GPIO_01,"LED Pin");

        // setup SPI for communication
        int fd = Spi.wiringPiSPISetup(channel, SPEED);
        if (fd <= -1) {
            throw new IllegalStateException("wiringPiSPISetup failed");
        }

        reset();

        // Extended mode, bias, vop, basic mode, non-inverted display.
        setContrast(CONTRAST);

        if (ledPin != null) {
            ledPin.setPwm(0);
        }

    }

    @PreDestroy
    public void close() {
        clear();
        setBacklight(false);
    }

    private void reset() {
        // Toggle RST low to reset.
        rstPin.setState(PinState.LOW);
        Gpio.delay(100);
        rstPin.setState(PinState.HIGH);
    }

    private final void writeData(byte[] data) {
        dcPin.setState(PinState.HIGH);
        Spi.wiringPiSPIDataRW(channel,data,data.length);
    }

    private final void writeCommand(byte[] value) {
        dcPin.setState(PinState.LOW);
        Spi.wiringPiSPIDataRW(channel,value,value.length);
    }

    public int getHeight() {
        return Pcd8544DisplayBuffer.HEIGHT;
    }

    public int getWidth() {
        return Pcd8544DisplayBuffer.WIDTH;
    }

    private Point getPointForCursor() {
        return new Point((Pcd8544Font.CHAR_WIDTH + 1) * textCursorColumn,(Pcd8544Font.CHAR_HEIGHT + 1) * textCursorRow);
    }

    private void gotoXY(int x, int y) {
        if ( (0 <= x && x < COLUMNS) && (0 <= y && y < ROWS)) {
            byte packet[] = new byte[2];
            packet[0] = ByteUtils.toUnsignedByte(x + 128);
            packet[1] = ByteUtils.toUnsignedByte(y + 64);
            writeCommand(packet);
        }
    }



    public void setBacklight(boolean backlight) {
        if (backlight) {
            setBrightness(256);
        } else {
            setBrightness(0);
        }
    }

    public void setBrightness(int ledValue) {
        if (ledPin == null) {
            return;
        }
        if (0 <= ledValue && ledValue < 1023) {
            ledPin.setPwm(ledValue);
        }
    }
    public void setContrast(int contrast) {
        if ( 0x80 <= contrast && contrast < 0xFF) {
            byte packet[] = new byte[5];

            packet[0] = 0x21;
            packet[1] = 0x14;
            packet[2] = ByteUtils.toUnsignedByte(contrast);
            packet[3] = 0x20;
            packet[4] = 0x0c;

            writeCommand(packet);
        }
    }

    public void setCursor(Point cursor) {
        displayBuffer.setCursor(cursor);
    }

    /**
     * Writes the {@linkplain Pcd8544DisplayBuffer display buffer} to
     * the screen.
     */
    public void display() {
        gotoXY(0, 0);
        writeData(displayBuffer.getBuffer());
        gotoXY(getWidth() - 1,getHeight() - 1);
    }

    /**
     * Clears the display
     */
    public void clearDisplay() {
        displayBuffer.clearDisplayBuffer();
        display();
        textCursorRow    = 0;
        textCursorColumn = 0;
    }

    /**
     * Fills the {@linkplain Pcd8544DisplayBuffer display buffer}
     * with the given color value
     * @param color
     */
    public void fillScreen(boolean color) {
        displayBuffer.fillScreen(color);
    }

    /**
     * Sets one pixel in the  {@linkplain Pcd8544DisplayBuffer display buffer}
     * to the given value
     * @param p the pixels to set
     * @param value if <code>true</code>, the pixel will be set
     */
    public void drawPixel(Point p, boolean value) {
        displayBuffer.setPixel(p, value);
    }



    public void drawLine(Point p0, Point p1, boolean color) {
        displayBuffer.drawLine(p0, p1, color);
    }

    public void drawRect(Rectangle r, boolean color) {
        displayBuffer.drawRect(r, color);
    }

    public void drawRoundRect(Rectangle r, int radius, boolean color) {
        displayBuffer.drawRoundRect(r,radius,color);
    }

    public void drawCircle(Point center, int radius, boolean color) {
        displayBuffer.drawCircle(center, radius, color);
    }

    public void drawTriangle(Point p0, Point p1, Point p2, boolean color) {
        displayBuffer.drawTriangle(p0, p1, p2, color);
    }

    public void fillRoundRect(Rectangle r, int radius, boolean color) {
        displayBuffer.fillRoundRect(r,radius,color);
    }

    public void fillRect(Rectangle r, boolean color) {
        displayBuffer.fillRect(r, color);
    }

    public void fillCircle(Point center, int radius, boolean color) {
        displayBuffer.fillCircle(center, radius, color);
    }

    public void fillTriangle(Point p0, Point p1, Point p2, boolean color) {
        displayBuffer.fillTriangle(p0, p1, p2, color);
    }

    public void drawImage(Point p, BufferedImage image) {
        displayBuffer.drawImage(p, image);
    }

    public void drawImage(Point p, byte[] bitmap, int w, int h, boolean color) {
        displayBuffer.drawImage(p, bitmap, w, h, color);
    }

    /**
     * Draws the given character at position p to the {@linkplain Pcd8544DisplayBuffer display buffer}
     * @param p the location of the charater
     * @param c the character to draw
     * @param size the size of the character
     */
    public void drawChar(Point p, char c, int size) {
        displayBuffer.drawChar(p, c, size);
    }

    public void invertDisplay(boolean invert) {
        displayBuffer.invertDisplay(invert);
        display();
    }

    /*
            setTextColor(uint16_t c),
            setTextColor(uint16_t c, uint16_t bg),
            setTextSize(uint8_t s),
            setTextWrap(boolean w),
            setRotation(uint8_t r);

    uint8_t getRotation(void);
    */


    @Override
    public void setName(String name) {
        lcdDelegate.setName(name);
    }

    @Override
    public String getName() {
        return lcdDelegate.getName();
    }

    @Override
    public void setTag(Object tag) {
       lcdDelegate.setTag(tag);
    }

    @Override
    public Object getTag() {
        return lcdDelegate.getTag();
    }

    @Override
    public void setProperty(String key, String value) {
        lcdDelegate.setProperty(key, value);
    }

    @Override
    public boolean hasProperty(String key) {
        return lcdDelegate.hasProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return lcdDelegate.getProperty(key, defaultValue);
    }

    @Override
    public String getProperty(String key) {
        return lcdDelegate.getProperty(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return lcdDelegate.getProperties();
    }

    @Override
    public void removeProperty(String key) {
        lcdDelegate.removeProperty(key);
    }

    @Override
    public void clearProperties() {
        lcdDelegate.clearProperties();
    }

    @Override
    public int getRowCount() {
        return lcdDelegate.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return lcdDelegate.getColumnCount();
    }

    @Override
    public void clear() {
        clearDisplay();
    }

    @Override
    public void clear(int row) {
        lcdDelegate.clear(row);
    }

    @Override
    public void clear(int row, int column, int length) {
        lcdDelegate.clear(row, column, length);
    }

    @Override
    public void setCursorHome() {
        lcdDelegate.setCursorHome();
    }

    @Override
    public void setCursorPosition(int row) {
        lcdDelegate.setCursorPosition(row);
    }

    @Override
    public void setCursorPosition(int row, int column) {
        lcdDelegate.setCursorPosition(row, column);
    }

    @Override
    public void write(String data) {
        lcdDelegate.write(data);
    }

    @Override
    public void write(String data, Object... arguments) {
        lcdDelegate.write(data, arguments);
    }

    @Override
    public void write(char[] data) {
        lcdDelegate.write(data);
    }

    @Override
    public void write(byte[] data) {
        lcdDelegate.write(data);
    }

    @Override
    public void write(char data) {
        lcdDelegate.write(data);
    }

    @Override
    public void write(byte data) {
        lcdDelegate.write(data);
    }

    @Override
    public void write(int row, String data, LCDTextAlignment alignment) {
        lcdDelegate.write(row, data, alignment);
    }

    @Override
    public void write(int row, String data, LCDTextAlignment alignment, Object... arguments) {
        lcdDelegate.write(row, data, arguments);
    }

    @Override
    public void write(int row, String data) {
        lcdDelegate.write(row, data);
    }

    @Override
    public void write(int row, String data, Object... arguments) {
        lcdDelegate.write(row, data, arguments);
    }

    @Override
    public void write(int row, char[] data) {
        lcdDelegate.write(row, data);
    }

    @Override
    public void write(int row, byte[] data) {
        lcdDelegate.write(row, data);
    }

    @Override
    public void write(int row, char data) {
        lcdDelegate.write(row, data);
    }

    @Override
    public void write(int row, byte data) {
        lcdDelegate.write(row, data);
    }

    @Override
    public void write(int row, int column, String data) {
        lcdDelegate.write(row, column, data);
    }

    @Override
    public void write(int row, int column, String data, Object... arguments) {
        lcdDelegate.write(row, column, data, arguments);
    }

    @Override
    public void write(int row, int column, char[] data) {
        lcdDelegate.write(row, column, data);
    }

    @Override
    public void write(int row, int column, byte[] data) {
        lcdDelegate.write(row, column, data);
    }

    @Override
    public void write(int row, int column, char data) {
        lcdDelegate.write(row, column, data);
    }

    @Override
    public void write(int row, int column, byte data) {
        lcdDelegate.write(row, column, data);
    }

    @Override
    public void writeln(int row, String data) {
        lcdDelegate.writeln(row, data);
    }

    @Override
    public void writeln(int row, String data, Object... arguments) {
        lcdDelegate.writeln(row, data, arguments);
    }

    @Override
    public void writeln(int row, String data, LCDTextAlignment alignment) {
        lcdDelegate.writeln(row, data, alignment);
    }

    @Override
    public void writeln(int row, String data, LCDTextAlignment alignment, Object... arguments) {
        lcdDelegate.writeln(row, data, alignment, arguments);
    }

    private class LCDDelegate extends LCDBase {

        @Override
        public void clear() {
            clearDisplay();
        }

        @Override
        public int getRowCount() {
            return ROWS;
        }

        @Override
        public int getColumnCount() {
            return COLUMNS;
        }

        @Override
        public void setCursorPosition(int row, int column) {
            textCursorRow    = row;
            textCursorColumn = column;
        }

        @Override
        public void write(byte data) {
            drawChar(getPointForCursor(),(char)data,1);
            textCursorColumn++;
        }

        @Override
        public void write(char data) {
            drawChar(getPointForCursor(),data,1);
            textCursorColumn++;
        }

        @Override
        // TODO
        public void write(byte[] data) {
            writeData(data);
        }

        @Override
        public void write(String data) {
            for (char c : data.toCharArray()) {
                write(c);
            }
            display();
        }

    }

    @Override
    public void welcome() {
        write(0, "Don't panic.");
        try {

            InetAddress adress = getFirstNonLoopbackAddress(true, false);
            if (adress != null) {
                write(1, adress.getHostAddress());
            }
        } catch (SocketException e) {
            logger.error("welcome:",e);
        }
    }

    @Override
    public void motorInformation(ReadMecanumMotorInfo[] mecanumMotorInfo) {
        write(2,"FL:" + (mecanumMotorInfo[0].isDirection()?'+':'-') + Short.toString(mecanumMotorInfo[0].getSpeed()) + "   ");
        write(3,"RL:" + (mecanumMotorInfo[1].isDirection()?'+':'-') + Short.toString(mecanumMotorInfo[1].getSpeed()) + "   ");
        write(4,"FR:" + (mecanumMotorInfo[2].isDirection()?'+':'-') + Short.toString(mecanumMotorInfo[2].getSpeed()) + "   ");
        write(5,"RR:" + (mecanumMotorInfo[3].isDirection()?'+':'-') + Short.toString(mecanumMotorInfo[3].getSpeed()) + "   ");
    }

    @Override
    public void distance(int distance) {
        write(1,"Distance:" + distance + "cm   ");
    }

    private InetAddress getFirstNonLoopbackAddress(boolean preferIpv4, boolean preferIPv6) throws SocketException {
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface i = (NetworkInterface) en.nextElement();
            for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements();) {
                InetAddress addr = (InetAddress) en2.nextElement();
                if (!addr.isLoopbackAddress()) {
                    if (addr instanceof Inet4Address) {
                        if (preferIPv6) {
                            continue;
                        }
                        return addr;
                    }
                    if (addr instanceof Inet6Address) {
                        if (preferIpv4) {
                            continue;
                        }
                        return addr;
                    }
                }
            }
        }
        return null;
    }
}
