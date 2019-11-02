/*
* MarvinHardware.java
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

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import de.rose53.marvin.Hardware;
import de.rose53.marvin.MecanumDrive;
import de.rose53.marvin.PanTiltSensors;
import de.rose53.marvin.ReadMecanumMotorInfo;
import de.rose53.marvin.events.ReadDistanceEvent;
import de.rose53.marvin.events.ReadMecanumCurrentEvent;
import de.rose53.marvin.events.ReadMecanumMotorInfoEvent;
import de.rose53.marvin.utils.ByteUtils;

/**
 * @author rose
 *
 */
@ApplicationScoped
@Hardware(Hardware.hw.PI)
public class MarvinHardware implements MecanumDrive, PanTiltSensors {

    static private final int ARDUINO_I2C_ADDRESS = 0x04;

    static private final int COMMAND_WRITE_CONTROL_MOTORS = 3;
    static private final int COMMAND_WRITE_PAN_TILT = 4;
    static private final int COMMAND_WRITE_PAN = 5;
    static private final int COMMAND_WRITE_TILT = 6;
    static private final int COMMAND_WRITE_INC_PAN = 7;
    static private final int COMMAND_WRITE_DEC_PAN = 8;
    static private final int COMMAND_WRITE_INC_TILT = 9;
    static private final int COMMAND_WRITE_DEC_TILT = 10;


    static private final int COMMAND_READ_CURRENT = 64;
    static private final int COMMAND_READ_MECANUM_MOTOR_INFO = 65;
    static private final int COMMAND_READ_DISTANCE = 66;

    static final int DEAD_ZONE = 5;

    private byte ch1 = 0;
    private byte ch3 = 0;
    private byte ch4 = 0;

    private short[] oldCurrent = new short[4];
    private ReadMecanumMotorInfo[] oldReadMecanumMotorInfo = new ReadMecanumMotorInfo[4];
    private int oldDistance = 0;

    final ScheduledExecutorService clientProcessingPool = Executors.newScheduledThreadPool(1);

    @Inject
    Logger logger;

    @Inject
    Event<ReadMecanumCurrentEvent> readMecanumCurrentEvent;

    @Inject
    Event<ReadMecanumMotorInfoEvent> readMecanumMotorInfoEvent;

    @Inject
    Event<ReadDistanceEvent> readDistanceEvent;

    @Inject
    private GpioController gpio;

    private I2CBus bus;
    private I2CDevice device;

    private GpioPinDigitalOutput resetPin;

    @PostConstruct
    public void init() {
        try {

            resetPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "Reset Pin", PinState.HIGH);


            bus = I2CFactory.getInstance(I2CBus.BUS_1);
            device = bus.getDevice(ARDUINO_I2C_ADDRESS);
            byte zero = 0;
            mecanumDrive(zero,zero,zero);
            // clientProcessingPool.scheduleAtFixedRate(new ReadDataTask(), 30, 4, TimeUnit.SECONDS);
        } catch (IOException | UnsupportedBusNumberException e) {
            logger.error("init:",e);
        }
    }

    @PreDestroy
    public void desproy() {
        clientProcessingPool.shutdown();
    }


    private void resetI2C() {
        resetPin.low();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }
        resetPin.high();
    }

    public int writeReadTest(byte[] data) throws IOException {
        device.write(data,0,data.length);
        return device.read();
    }

    synchronized public void controlMotors(short[] controlData) throws IOException {

        byte[] data = new byte[9];

        data[0] = 1;
        System.arraycopy(ByteUtils.toUnsignedByte(controlData), 0, data, 1, controlData.length);
        device.write(data,0,data.length);
    }

    synchronized public void controlMotors(byte[] controlData) throws IOException {

        byte[] data = new byte[5];

        data[0] = 2;
        System.arraycopy(controlData, 0, data, 1, controlData.length);
        device.write(data,0,data.length);
    }

    synchronized public void controlMotors(byte ch1, byte ch3, byte ch4) throws IOException {

        byte[] data = new byte[3];

        data[0] = ch1;
        data[1] = ch3;
        data[2] = ch4;

        device.write(COMMAND_WRITE_CONTROL_MOTORS,data,0,data.length);
    }

    private boolean isChanged(byte oldValue, byte newValue) {
        if (newValue < oldValue - DEAD_ZONE) {
            return true;
        }
        if (newValue > oldValue + DEAD_ZONE) {
            return true;
        }
        return false;
    }

    @Override
    public void mecanumDrive(byte ch1, byte ch3, byte ch4) {
        try {
            if (    !isChanged(this.ch1,ch1)
                 && !isChanged(this.ch3,ch3)
                 && !isChanged(this.ch4,ch4)) {
                return;
            }
            this.ch1 = ch1;
            this.ch3 = ch3;
            this.ch4 = ch4;
            controlMotors(ch1, ch3, ch4);
        } catch (IOException e) {
            logger.error("mecanumDrive:",e);
            logger.error("mecanumDrive: reset I2C....");
            resetI2C();
            logger.error("mecanumDrive: done.");
        }
    }

    /**
     * Reads the current from the motors
     */
    @Override
    synchronized public short[] getCurrent() {
        logger.debug("getCurrent:");
        byte[] buffer = new byte[8];
        short[] retVal = new short[4];
        try {
            device.read(COMMAND_READ_CURRENT, buffer, 0, buffer.length);

            for (int i = 0, j = retVal.length; i < j; i++) {
                logger.debug("getCurrent: msb = {}, lsb = {}",buffer[2 * i + 1],buffer[2 * i]);
                retVal[i] = ByteUtils.toShort(Arrays.copyOfRange(buffer,2 * i,2 * i + 2));
                logger.debug("getCurrent: retVal[{}] = {}",i,retVal[i]);
            }
        } catch (IOException e) {
            logger.error("getCurrent:",e);
        }
        return retVal;
    }

    /**
     * Reads the current from the motors
     */
    @Override
    synchronized public ReadMecanumMotorInfo[] getReadMecanumMotorInfo() {
        logger.debug("getReadMecanumMotorInfo:");
        byte[] buffer = new byte[12];
        ReadMecanumMotorInfo[] retVal = new ReadMecanumMotorInfo[4];
        try {
            device.read(COMMAND_READ_MECANUM_MOTOR_INFO, buffer, 0, buffer.length);

            for (int i = 0, j = retVal.length; i < j; i++) {
                logger.debug("getReadMecanumMotorInfo: direction = {}, msb = {}, lsb = {}",buffer[3 * i],buffer[3 * i + 2],buffer[3 * i + 1]);
                retVal[i] = new ReadMecanumMotorInfo(buffer[3 * i] > 0,ByteUtils.toShort(Arrays.copyOfRange(buffer,3 * i + 1,3 * i + 3)));
                logger.debug("getReadMecanumMotorInfo: retVal[{}] = {}",i,retVal[i]);
            }
        } catch (IOException e) {
            logger.error("getReadMecanumMotorInfo:",e);
        }
        return retVal;
    }

    @Override
    synchronized public void setPanTilt(short pan, short tilt) {
        logger.debug("setPanTilt: pan = >{}<, tilt = >{}<",pan,tilt);
        byte[] buffer = new byte[2];
        buffer[0] = ByteUtils.toUnsignedByte(pan);
        buffer[1] = ByteUtils.toUnsignedByte(tilt);
        try {
            device.write(COMMAND_WRITE_PAN_TILT, buffer, 0, buffer.length);
        } catch (IOException e) {
            logger.error("setPanTilt:",e);
        }
    }

    @Override
    synchronized public void setPan(short pan) {
        logger.debug("setPan: pan = >{}<",pan);
        try {
            device.write(COMMAND_WRITE_PAN, ByteUtils.toUnsignedByte(pan));
        } catch (IOException e) {
            logger.error("setPan:",e);
        }
    }

    @Override
    synchronized public void setTilt(short tilt) {
        logger.debug("setTilt: tilt = >{}<",tilt);
        try {
            device.write(COMMAND_WRITE_TILT, ByteUtils.toUnsignedByte(tilt));
        } catch (IOException e) {
            logger.error("setTilt:",e);
        }
    }

    @Override
    synchronized public void incrementPan(short increment) {
        logger.debug("incrementPan: increment = >{}<",increment);
        try {
            device.write(COMMAND_WRITE_INC_PAN, ByteUtils.toUnsignedByte(increment));
        } catch (IOException e) {
            logger.error("incrementPan:",e);
        }
    }

    @Override
    synchronized public void decrementPan(short decrement) {
        logger.debug("decrementPan: decrement = >{}<",decrement);
        try {
            device.write(COMMAND_WRITE_DEC_PAN, ByteUtils.toUnsignedByte(decrement));
        } catch (IOException e) {
            logger.error("decrementPan:",e);
        }
    }

    @Override
    synchronized public void incrementTilt(short increment) {
        logger.debug("incrementTilt: increment = >{}<",increment);
        try {
            device.write(COMMAND_WRITE_INC_TILT, ByteUtils.toUnsignedByte(increment));
        } catch (IOException e) {
            logger.error("incrementTilt:",e);
        }
    }

    @Override
    synchronized public void decrementTilt(short decrement) {
        logger.debug("decrementTilt: decrement = >{}<",decrement);
        try {
            device.write(COMMAND_WRITE_DEC_TILT, ByteUtils.toUnsignedByte(decrement));
        } catch (IOException e) {
            logger.error("decrementTilt:",e);
        }
    }

    @Override
    synchronized public int getDistance() {
        logger.debug("getReadMecanumMotorInfo:");
        byte[] buffer = new byte[2];
        int retVal = 0;
        try {
            device.read(COMMAND_READ_DISTANCE, buffer, 0, buffer.length);
            logger.debug("getDistance: msb = {}, lsb = {}",buffer[1],buffer[0]);
            retVal = ByteUtils.toShort(buffer);
            logger.debug("getDistance: retVal = {}",retVal);
        } catch (IOException e) {
            logger.error("getReadMecanumMotorInfo:",e);
        }
        return retVal;
    }

    private class ReadDataTask implements Runnable {

        @Override
        public void run() {
            try {

                short[] current = getCurrent();
                // check, if the values have changed
                if (!Arrays.equals(oldCurrent, current)) {
                    readMecanumCurrentEvent.fire(new ReadMecanumCurrentEvent(current));
                    oldCurrent = Arrays.copyOf(current,current.length);
                }
                Thread.sleep(50);
                ReadMecanumMotorInfo[] readMecanumMotorInfo = getReadMecanumMotorInfo();
                // 	check, if the values have changed
                if (!Arrays.equals(oldReadMecanumMotorInfo, readMecanumMotorInfo)) {
                    readMecanumMotorInfoEvent.fire(new ReadMecanumMotorInfoEvent(readMecanumMotorInfo));
                    oldReadMecanumMotorInfo = Arrays.copyOf(readMecanumMotorInfo,readMecanumMotorInfo.length);
                }
                Thread.sleep(50);
                int distance = getDistance();
                if (oldDistance != distance) {
                    readDistanceEvent.fire(new ReadDistanceEvent(distance));
                    oldDistance = distance;
                }
            } catch (InterruptedException e) {
                logger.info("run:",e);
            }
        }


    }

}
