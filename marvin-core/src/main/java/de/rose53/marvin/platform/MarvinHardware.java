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
import static de.rose53.marvin.platform.EMessageType.*;
import static de.rose53.marvin.utils.StringUtils.*;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import de.rose53.marvin.Compass;
import de.rose53.marvin.Distance;
import de.rose53.marvin.Hardware;
import de.rose53.marvin.MecanumDrive;
import de.rose53.marvin.PanTiltServos;
import de.rose53.marvin.ReadMecanumMotorInfo;
import de.rose53.marvin.events.DistanceEvent;
import de.rose53.marvin.events.HeadingEvent;
import de.rose53.marvin.events.PanTiltEvent;
import de.rose53.marvin.events.ReadMecanumCurrentEvent;
import de.rose53.marvin.events.ReadMecanumMotorInfoEvent;
import de.rose53.marvin.platform.message.CloseMessage;
import de.rose53.marvin.platform.message.GetMessage;
import de.rose53.marvin.platform.message.HDGMessage;
import de.rose53.marvin.platform.message.MECCurrentMessage;
import de.rose53.marvin.platform.message.MECInfoMessage;
import de.rose53.marvin.platform.message.MECJoystickMessage;
import de.rose53.marvin.platform.message.OpenMessage;
import de.rose53.marvin.platform.message.PanMessage;
import de.rose53.marvin.platform.message.PanTiltIncMessage;
import de.rose53.marvin.platform.message.PanTiltInfoMessage;
import de.rose53.marvin.platform.message.TiltMessage;
import de.rose53.marvin.platform.message.USMessage;

/**
 * @author rose
 *
 */
@ApplicationScoped
@Hardware(Hardware.hw.PI)
public class MarvinHardware implements Runnable, MecanumDrive, Compass, Distance, PanTiltServos {

    private boolean shouldRun = true;

    private byte ch1 = 0;
    private byte ch3 = 0;
    private byte ch4 = 0;

    private short[] oldCurrent = new short[4];

    private LatestUniqueQueue<Message> sendQueue       = new LatestUniqueQueue<>();

    private ScheduledExecutorService              getMessageQueueExecutor = Executors.newScheduledThreadPool(1);
    private BlockingQueue<GetEventQueueEntry>     getMessageQueue         = new LinkedBlockingQueue<>(512);

    @Inject
    Logger logger;

    @Inject
    Event<ReadMecanumCurrentEvent> readMecanumCurrentEvent;

    @Inject
    Event<ReadMecanumMotorInfoEvent> readMecanumMotorInfoEvent;

    @Inject
    Event<DistanceEvent> distanceEvent;

    @Inject
    Event<HeadingEvent> headingEvent;

    @Inject
    Event<MECCurrentMessage> mecanumCurrentMessage;

    @Inject
    Event<PanTiltEvent> panTiltEvent;

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

            sendQueue.add(OpenMessage.OPEN);

            TimeUnit.SECONDS.sleep(2);

            byte zero = 0;
            mecanumDrive(zero,zero,zero);

            getMessageQueueExecutor.scheduleAtFixedRate(() -> getMessageQueue.removeIf(e -> System.currentTimeMillis() - e.timestamp > 10000 ), 10, 10, TimeUnit.SECONDS);

        } catch (SerialPortException | InterruptedException e) {
            logger.error("init:",e);
        }
    }

    @PreDestroy
    public void destroy() {
        shouldRun = false;
        try {
            sendQueue.add(CloseMessage.CLOSE);

            TimeUnit.SECONDS.sleep(5);

            serialPort.closePort();
            getMessageQueueExecutor.shutdown();
        } catch (SerialPortException | InterruptedException e) {
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

    @SuppressWarnings("unchecked")
    private <T extends Message> T readGetResponse(String messageUid) {
        logger.debug("readGetResponse: reading response message for messageUid = >{}<",messageUid);
        T response = null;

        long actTime = System.currentTimeMillis();
        do {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
            }
            GetEventQueueEntry entry = getMessageQueue.peek();
            if (entry != null && messageUid.equalsIgnoreCase(entry.message.getMessageUid())) {
                logger.debug("getCurrent: found our response message");
                getMessageQueue.remove(entry);
                response = (T) entry.message;
                break;
            }
        } while (System.currentTimeMillis() - actTime < 10000);
        logger.debug("readGetResponse: resonse for messageUid >{}< {} found",messageUid,(response == null?"not ":""));

        return response;
    }


    /**
     * Reads the current from the motors
     */
    @Override
    public short[] getCurrent() {
        logger.debug("getCurrent:");

        GetMessage message = new GetMessage(GET_MEC_CURR);
        sendQueue.add(message);

        MECCurrentMessage response = readGetResponse(message.getMessageUid());

        if (response == null) {
            return null;
        }
        return response.getCurrent();
    }

    /**
     * Reads the current from the motors
     */
    @Override
    synchronized public ReadMecanumMotorInfo[] getReadMecanumMotorInfo() {
        logger.debug("getReadMecanumMotorInfo:");

        GetMessage message = new GetMessage(GET_MEC_INFO);
        sendQueue.add(message);

        MECInfoMessage response = readGetResponse(message.getMessageUid());

        if (response == null) {
            return null;
        }
        return response.getReadMecanumMotorInfo();
    }

    @Override
    public Float getHeading() {
        logger.debug("getHeading:");

        GetMessage message = new GetMessage(GET_HDG);
        sendQueue.add(message);

        HDGMessage response = readGetResponse(message.getMessageUid());
        if (response == null) {
            return null;
        }
        return response.getHeading();
    }



    @Override
    public Float getDistance(Place place) {
        logger.debug("getDistance: place = >{}<", place);
        if (place == null) {
            logger.debug("getDistance: no place given, returning null");
            return null;
        }

        GetMessage message = new GetMessage(GET_US,place.getId());
        sendQueue.add(message);

        USMessage response = readGetResponse(message.getMessageUid());
        if (response == null) {
            return null;
        }
        return response.getDistance();
    }



    private static class GetEventQueueEntry {
        long    timestamp;
        Message message;

        public GetEventQueueEntry(Message message) {
            super();
            this.timestamp = System.currentTimeMillis();
            this.message = message;
        }


    }

    @Override
    public void setPan(short pan) {
        logger.debug("setPan: pan = >{}<",pan);
        PanMessage message = new PanMessage(pan);
        sendQueue.add(message);
    }

    @Override
    public void incrementPan(short increment) {
        logger.debug("incrementPan: increment = >{}<",increment);
        PanTiltIncMessage message = PanTiltIncMessage.buildPanIncMessage(increment);
        sendQueue.add(message);

    }

    @Override
    public void decrementPan(short decrement) {
        logger.debug("decrementPan: decrement = >{}<",decrement);
        PanTiltIncMessage message = PanTiltIncMessage.buildPanIncMessage((short)(-1 * decrement));
        sendQueue.add(message);
    }

    @Override
    public void setTilt(short tilt) {
        logger.debug("setTilt: tilt = >{}<",tilt);
        TiltMessage message = new TiltMessage(tilt);
        sendQueue.add(message);
    }

    @Override
    public void incrementTilt(short increment) {
        logger.debug("incrementTilt: increment = >{}<",increment);
        PanTiltIncMessage message = PanTiltIncMessage.buildTiltIncMessage(increment);
        sendQueue.add(message);
    }

    @Override
    public void decrementTilt(short decrement) {
        logger.debug("decrementTilt: decrement = >{}<",decrement);
        PanTiltIncMessage message = PanTiltIncMessage.buildTiltIncMessage((short)(-1 * decrement));
        sendQueue.add(message);
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
                        logger.trace("serialEvent: buffer = >{}<",buffer.length > 0?new String(buffer):"");
                        for (byte b : buffer) {
                            if (b != '\n') {
                                dataBuffer[dataBufferIdx++] = b;
                            } else {
                                String messageString = new String(dataBuffer,0,dataBufferIdx -1);
                                dataBufferIdx = 0;     // next byte goes to the start, because it is a new message
                                logger.debug("serialEvent: messageString = >{}<",messageString);
                                Message m = Message.build(messageString.trim());
                                if (m != null) {
                                    if (isNotEmpty(m.getMessageUid())) {
                                        getMessageQueue.offer(new GetEventQueueEntry(m), 1, TimeUnit.SECONDS);
                                    }
                                    if  (MEC_CURR == m.getMessageType()) {
                                        MECCurrentMessage mecCurrentMessage = (MECCurrentMessage)m;
                                        mecanumCurrentMessage.fire(mecCurrentMessage);
                                        short[] current = mecCurrentMessage.getCurrent();
                                        if (!Arrays.equals(oldCurrent, current)) {
                                            readMecanumCurrentEvent.fire(new ReadMecanumCurrentEvent(current));
                                            oldCurrent = Arrays.copyOf(current,current.length);
                                        }
                                    } else if (MEC_INFO == m.getMessageType()) {
                                        MECInfoMessage mecInfoMessage = (MECInfoMessage) m;
                                        readMecanumMotorInfoEvent.fire(new ReadMecanumMotorInfoEvent(mecInfoMessage.getReadMecanumMotorInfo()));
                                    } else if (US == m.getMessageType()) {
                                        USMessage usMessage = (USMessage) m;
                                        distanceEvent.fire(new DistanceEvent(usMessage.getDistance(),usMessage.getPlace()));
                                    } else if (HDG == m.getMessageType()) {
                                        HDGMessage hdgMessage = (HDGMessage) m;
                                        headingEvent.fire(new HeadingEvent(hdgMessage.getHeading()));
                                    } else if (PAN_TILT_INFO == m.getMessageType()) {
                                        PanTiltInfoMessage panTiltMessage = (PanTiltInfoMessage) m;
                                        panTiltEvent.fire(new PanTiltEvent(panTiltMessage.getPan(),panTiltMessage.getTilt()));
                                    }


                                }
                            }
                        }
                    } catch (SerialPortException | InvalidMessageException | ChecksumErrorMessageException | InterruptedException ex) {
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
                    logger.debug("run: message = >{}<",m.getTerminatedMessageString().trim());
                    serialPort.writeString(m.getTerminatedMessageString());
                }

                Thread.sleep(50);
            } catch (InterruptedException | SerialPortException e) {
                logger.error("run: error while sending message",e);
            }
        }
    }

}
