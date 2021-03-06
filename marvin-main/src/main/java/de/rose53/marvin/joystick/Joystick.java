package de.rose53.marvin.joystick;

import static net.java.games.input.Component.Identifier.Axis;
import static net.java.games.input.Component.Identifier.Button;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.marvin.HardwareInstance;
import de.rose53.marvin.MecanumDrive;
import de.rose53.marvin.PanTiltServos;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

/**
 * @author rose
 *
 */
public class Joystick implements Runnable {

    private final short INC_DEC = 10;

    @Inject
    Logger logger;

    @Inject
    @Any
    Instance<MecanumDrive> mecanumDrives;

    @Inject
    @Any
    Instance<PanTiltServos> panTiltServos;

    @Inject
    Controller controller;

    private boolean shouldRun = true;

    public boolean start() {

        if (controller == null) {
            logger.error("start: no Joystick connected.");
            return false;
        }

        Thread serverThread = new Thread(this,"Joystick");

        serverThread.start();

        return true;
    }


    public void stop() {
        shouldRun = false;
    }

    /**
     * Sets the three values representing a joystick driven approach
     * @param ch1 factor for turning left / right
     * @param ch3 factor for moving forward / backward
     * @param ch4 factor for moving left / right
     *
     * @see {@linkplain MecanumDrive#mecanumDrive(byte, byte, byte)}
     */
    private void mecanumDrive(byte ch1, byte ch3 ,byte ch4) {
        MecanumDrive mecanumDrive = mecanumDrives.select(new HardwareInstance()).get();

        mecanumDrive.mecanumDrive(ch1,ch3,ch4);
    }

    public void incrementPan() {
        PanTiltServos panTilt = panTiltServos.select(new HardwareInstance()).get();

        panTilt.incrementPan(INC_DEC);
    }

    public void decrementPan() {
        PanTiltServos panTilt = panTiltServos.select(new HardwareInstance()).get();

        panTilt.decrementPan(INC_DEC);
    }

    public void incrementTilt() {
        PanTiltServos panTilt = panTiltServos.select(new HardwareInstance()).get();

        panTilt.incrementTilt((short)(INC_DEC >> 1));
    }

    public void decrementTilt() {
        PanTiltServos panTilt = panTiltServos.select(new HardwareInstance()).get();

        panTilt.decrementTilt((short)(INC_DEC >> 1));
    }

    private byte convertValue(float value) {
        return ((Float)(value * 100)).byteValue();
    }

    private byte getLastValue(Identifier id) {
        return convertValue(controller.getComponent(id).getPollData());
    }

    @Override
    public void run() {

        Event event = new Event();

        while (shouldRun) {

            controller.poll();

            EventQueue queue = controller.getEventQueue();

            while (queue.getNextEvent(event)) {

                Component comp = event.getComponent();
                logger.trace("run: component name = >{}<",comp.getName());

                float value = event.getValue();
                logger.trace("run: event value = >{}<",value);

                if (comp.isAnalog()) {
                    // joystick
                    if (   Axis.RX == comp.getIdentifier()
                        || Axis.X == comp.getIdentifier()
                        || Axis.Y == comp.getIdentifier()) {

                        byte ch1 = Axis.RX == comp.getIdentifier()?convertValue(value):getLastValue(Axis.RX);
                        byte ch3 = (byte) (-1 * (Axis.Y == comp.getIdentifier()?convertValue(value):getLastValue(Axis.Y)));
                        byte ch4 = (Axis.X == comp.getIdentifier()?convertValue(value):getLastValue(Axis.X));

                        logger.trace("run: ch1 = >{}<, ch3 = >{}<, ch4 = >{}<",ch1,ch3,ch4);
                        mecanumDrive(ch1, ch3, ch4);
                    }
                } else {
                    // button

                    if (value == 1.0f) {
                        // on
                        if (Button.B == comp.getIdentifier()) {
                            // right
                            decrementPan();
                        } else if (Button.Y == comp.getIdentifier()) {
                            // left
                            incrementPan();
                        } if (Button.X == comp.getIdentifier()) {
                            // up
                            incrementTilt();
                        } if (Button.A == comp.getIdentifier()) {
                            // down
                            decrementTilt();
                        }
                    } else {
                        // off
                    }
                }
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
            }
        }
    }

}
