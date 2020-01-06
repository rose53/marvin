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

import static jssc.SerialPort.*;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;


import de.rose53.marvin.Hardware;
import de.rose53.marvin.MecanumDrive;
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
public class MarvinHardware implements Runnable, MecanumDrive {

    private boolean shouldRun = true;

    private byte ch1 = 0;
    private byte ch3 = 0;
    private byte ch4 = 0;

    private short[] oldCurrent = new short[4];
    private ReadMecanumMotorInfo[] oldReadMecanumMotorInfo = new ReadMecanumMotorInfo[4];
    private int oldDistance = 0;

    private LatestUniqueQueue<Message> sendQueue = new LatestUniqueQueue<Message>();

    @Inject
    Logger logger;

    @Inject
    Event<ReadMecanumCurrentEvent> readMecanumCurrentEvent;

    @Inject
    Event<ReadMecanumMotorInfoEvent> readMecanumMotorInfoEvent;

    @Inject
    Event<ReadDistanceEvent> readDistanceEvent;

    @Inject
    Event<MECCurrentMessage> mecanumCurrentMessage;

    private SerialPort serialPort;

    @PostConstruct
    public void init() {

        try {

            serialPort = new SerialPort("/dev/ttyAMA0");

            serialPort.openPort();
            serialPort.setParams(BAUDRATE_57600,DATABITS_8,STOPBITS_1,PARITY_NONE);
            serialPort.setEventsMask(MASK_RXCHAR);
            serialPort.addEventListener(new SerialPortReader());

            Thread serverThread = new Thread(this,"Send messages");

            serverThread.start();

            byte zero = 0;
            mecanumDrive(zero,zero,zero);
        } catch (SerialPortException e) {
            logger.error("init:",e);
        }
    }

    @PreDestroy
    public void destroy() {
        shouldRun = false;
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            logger.error("destroy:",e);
        }
    }

    private boolean isChanged(byte oldValue, byte newValue) {
        if (newValue < oldValue - 2) {
            return true;
        }
        if (newValue > oldValue + 2) {
            return true;
        }
        return false;
    }

    @Override
    public void mecanumDrive(byte ch1, byte ch3, byte ch4) {
        if (    !isChanged(this.ch1,ch1)
             && !isChanged(this.ch3,ch3)
             && !isChanged(this.ch4,ch4)) {
            return;
        }
        this.ch1 = ch1;
        this.ch3 = ch3;
        this.ch4 = ch4;
        logger.debug("mecanumDrive: ch1,ch3,ch4 = >{},{},{}<",ch1,ch3,ch4);
        MECJoystickMessage message = new MECJoystickMessage(ch1, ch3, ch4);
        sendQueue.add(message);
    }

    /**
     * Reads the current from the motors
     */
    @Override
    synchronized public short[] getCurrent() {
        logger.debug("getCurrent:");
        byte[] buffer = new byte[8];
        short[] retVal = new short[4];
        //try {
            //device.read(COMMAND_READ_CURRENT, buffer, 0, buffer.length);

            for (int i = 0, j = retVal.length; i < j; i++) {
                logger.debug("getCurrent: msb = {}, lsb = {}",buffer[2 * i + 1],buffer[2 * i]);
                retVal[i] = ByteUtils.toShort(Arrays.copyOfRange(buffer,2 * i,2 * i + 2));
                logger.debug("getCurrent: retVal[{}] = {}",i,retVal[i]);
            }
        /*} catch (IOException e) {
            logger.error("getCurrent:",e);
        }*/
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
        //try {
            //device.read(COMMAND_READ_MECANUM_MOTOR_INFO, buffer, 0, buffer.length);

            for (int i = 0, j = retVal.length; i < j; i++) {
                logger.debug("getReadMecanumMotorInfo: direction = {}, msb = {}, lsb = {}",buffer[3 * i],buffer[3 * i + 2],buffer[3 * i + 1]);
                retVal[i] = new ReadMecanumMotorInfo(buffer[3 * i] > 0,ByteUtils.toShort(Arrays.copyOfRange(buffer,3 * i + 1,3 * i + 3)));
                logger.debug("getReadMecanumMotorInfo: retVal[{}] = {}",i,retVal[i]);
            }
        /*} catch (IOException e) {
            logger.error("getReadMecanumMotorInfo:",e);
        }*/
        return retVal;
    }


/*
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
*/
    private class ReadDataTask implements Runnable {

        @Override
        public void run() {
            try {

                ReadMecanumMotorInfo[] readMecanumMotorInfo = getReadMecanumMotorInfo();
                // 	check, if the values have changed
                if (!Arrays.equals(oldReadMecanumMotorInfo, readMecanumMotorInfo)) {
                    readMecanumMotorInfoEvent.fire(new ReadMecanumMotorInfoEvent(readMecanumMotorInfo));
                    oldReadMecanumMotorInfo = Arrays.copyOf(readMecanumMotorInfo,readMecanumMotorInfo.length);
                }
                Thread.sleep(50);
                int distance = 0 /*getDistance()*/;
                if (oldDistance != distance) {
                    readDistanceEvent.fire(new ReadDistanceEvent(distance));
                    oldDistance = distance;
                }
            } catch (InterruptedException e) {
                logger.info("run:",e);
            }
        }


    }

    class SerialPortReader implements SerialPortEventListener {

        private byte[] dataBuffer = new byte[1024];
        private int    dataBufferIdx = 0;

        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR()) {//If data is available
                if (event.getEventValue() > 0) {//Check bytes count in the input buffer

                    try {
                        byte buffer[] = serialPort.readBytes(event.getEventValue());
                        logger.trace("serialEvent: buffer = >{}<",new String(buffer));
                        for (byte b : buffer) {
                            if (b != '\n') {
                                dataBuffer[dataBufferIdx++] = b;
                            } else {
                                String messageString = new String(dataBuffer,0,dataBufferIdx -1);
                                dataBufferIdx = 0;     // next byte goes to the start, because it is a new message
                                logger.debug("serialEvent: messageString = >{}<",messageString);
                                Message m = Message.build(messageString.trim());
                                if (m != null) {

                                    if  (EMessageType.MEC_CURR == m.getMessageType()) {
                                        MECCurrentMessage mecCurrentMessage = (MECCurrentMessage)m;
                                        mecanumCurrentMessage.fire(mecCurrentMessage);
                                        short[] current = mecCurrentMessage.getCurrent();
                                        if (!Arrays.equals(oldCurrent, current)) {
                                            readMecanumCurrentEvent.fire(new ReadMecanumCurrentEvent(current));
                                            oldCurrent = Arrays.copyOf(current,current.length);
                                        }
                                    }


                                }
                            }
                        }
                    } catch (SerialPortException | InvalidMessageException | ChecksumErrorMessageException ex) {
                        logger.error("serialEvent: ",ex);
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        while (shouldRun) {
            try {
                if (!sendQueue.isEmpty()) {
                    // send one message
                    Message m = sendQueue.poll();
                    logger.debug("controlMotors: message = >{}<",m.getTerminatedMessageString());
                    serialPort.writeString(m.getTerminatedMessageString());
                }

                Thread.sleep(50);
            } catch (InterruptedException | SerialPortException e) {
                logger.error("run: error while sending message",e);
            }
        }
    }
}
