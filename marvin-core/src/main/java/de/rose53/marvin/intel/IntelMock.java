/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rose53.marvin.intel;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import de.rose53.marvin.events.*;
import de.rose53.marvin.utils.LiPoStatus;
import org.slf4j.Logger;

import de.rose53.marvin.Hardware;
import de.rose53.marvin.MecanumDrive;
import de.rose53.marvin.PanTiltServos;
import de.rose53.marvin.ReadMecanumMotorInfo;
import de.rose53.marvin.Distance.Place;

/**
 *
 * @author rose
 */
@ApplicationScoped
@Hardware(Hardware.hw.INTEL)
public class IntelMock implements MecanumDrive, PanTiltServos, AutoCloseable {

    private short[] oldCurrent = new short[4];
    private ReadMecanumMotorInfo[] oldReadMecanumMotorInfo = new ReadMecanumMotorInfo[4];

    final ScheduledExecutorService clientProcessingPool = Executors.newScheduledThreadPool(1);

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
    Event<LiPoEvent> liPoEvent;

    @PostConstruct
    public void init() {
        logger.info("init:");
        clientProcessingPool.scheduleAtFixedRate(new ReadDataTask(), 5, 2, TimeUnit.SECONDS);
    }


    @PreDestroy
    public void close() {
        logger.info("close:");
        clientProcessingPool.shutdownNow();
    }


    @Override
    public void mecanumDrive(byte ch1, byte ch3, byte ch4) {
        logger.debug("mecanumDrive:");
    }

    @Override
    public short[] getCurrent() {
        logger.debug("getCurrent:");
        short[] retVal = new short[4];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = (short)(Math.random() * 2000);
        }
        return retVal;
    }

    @Override
    public ReadMecanumMotorInfo[] getReadMecanumMotorInfo() {
        logger.debug("getReadMecanumMotorInfo:");

        ReadMecanumMotorInfo[] retVal = new ReadMecanumMotorInfo[4];

        for (int i = 0; i < retVal.length; i++) {
            retVal[i] =  new ReadMecanumMotorInfo((Math.random() > 0.5), (short)(Math.random() * 100));
        }
        return retVal;
    }

    @Override
    public void setPan(short pan) {
        logger.debug("setPan: pan = >{}<",pan);
    }

    @Override
    public void incrementPan(short increment) {
        logger.debug("incrementPan: increment = >{}<",increment);
    }

    @Override
    public void decrementPan(short decrement) {
        logger.debug("decrementPan: decrement = >{}<",decrement);
    }

    @Override
    public void setTilt(short tilt) {
        logger.debug("setTilt: tilt = >{}<",tilt);
    }

    @Override
    public void incrementTilt(short increment) {
        logger.debug("incrementTilt: increment = >{}<",increment);
    }

    @Override
    public void decrementTilt(short decrement) {
        logger.debug("decrementTilt: decrement = >{}<",decrement);
    }


    public int getDistance() {
        logger.debug("getDistance:");
        return (int)(Math.random() * 2000);
    }

    private class ReadDataTask implements Runnable {

        @Override
        public void run() {

            short[] current = getCurrent();
            // check, if the values have changed
            if (!Arrays.equals(oldCurrent, current)) {
                readMecanumCurrentEvent.fire(new ReadMecanumCurrentEvent(current));
                oldCurrent = Arrays.copyOf(current,current.length);
            }
            ReadMecanumMotorInfo[] readMecanumMotorInfo = getReadMecanumMotorInfo();
            // 	check, if the values have changed
            if (!Arrays.equals(oldReadMecanumMotorInfo, readMecanumMotorInfo)) {
                readMecanumMotorInfoEvent.fire(new ReadMecanumMotorInfoEvent(readMecanumMotorInfo));
                oldReadMecanumMotorInfo = Arrays.copyOf(readMecanumMotorInfo,readMecanumMotorInfo.length);
            }

            distanceEvent.fire(new DistanceEvent((int)(Math.random() * 2000),Place.FRONT));
            headingEvent.fire(new HeadingEvent((int)(Math.random() * 360)));
            liPoEvent.fire(new LiPoEvent(new LiPoStatus( (float)(Math.random() * 2.0 + 2.0),(float)(Math.random() * 2.0 + 2.0))));
        }
    }


}
