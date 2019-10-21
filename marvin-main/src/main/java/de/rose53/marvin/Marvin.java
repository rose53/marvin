/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rose53.marvin;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;

import de.rose53.marvin.events.ReadDistanceEvent;
import de.rose53.marvin.events.ReadMecanumCurrentEvent;
import de.rose53.marvin.events.ReadMecanumMotorInfoEvent;
import de.rose53.marvin.joystick.JoystickSocketServer;
import de.rose53.marvin.server.Webserver;

/**
 *
 * @author rose
 */
@Singleton
public class Marvin implements Runnable {

    @Inject
    Logger logger;

    private boolean running;

    @Inject
    @Any
    Instance<PanTiltSensors> panTiltSensors;

    @Inject
    @Any
    Instance<Display> displays;


    @Inject
    Webserver webServer;

    @Inject
    JoystickSocketServer joystickServer;

    @Inject
    RestHelper restHelper;

    public Marvin() {

    }

    public void start() {

        try {
            System.out.print("Starting WebServer ...");
            webServer.start();
            System.out.println("\b\b\bdone.");

            System.out.print("Starting JoystickServer ...");
            joystickServer.start();
            System.out.println("\b\b\bdone.");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        show();

        System.out.println("\n\n ####################################################### ");
        System.out.println(" ####                MARVIN IS ALIVE !!!             ### ");
        System.out.println(" ####################################################### ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yy hh:mm:ss");
        System.out.println(" ### Date: " + dateFormat.format(new Date()));
        running = true;
    }

    public void stop() {
        if (running) {
            running = false;
        }
        try {
            webServer.stop();
            joystickServer.stop();
        } catch (Exception e) {
            logger.error("stop: ",e);
        }
    }

    @Override
    public void run() {
        start();

        // set the servos
        PanTiltSensors panTilt = panTiltSensors.select(new HardwareInstance()).get();
        panTilt.setPanTilt((short)90,(short)170);

        while (running) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.warn("run:",e);
            }
        }
    }

    public void show() {
        Display display = displays.select(new HardwareInstance()).get();

        display.welcome();
    }


    public void onReadMecanumCurrentEvent(@Observes ReadMecanumCurrentEvent event) {
        logger.debug("onReadMecanumCurrentEvent: ");
    }

    public void onReadMecanumMotorInfoEvent(@Observes ReadMecanumMotorInfoEvent event) {
        logger.debug("onReadMecanumMotorInfoEvent: ");
        Display display = displays.select(new HardwareInstance()).get();

        display.motorInformation(event.getReadMecanumMotorInfo());
    }

    public void onReadDistanceEvent(@Observes ReadDistanceEvent event) {
        logger.debug("onReadMecanumCurrentEvent: ");
        Display display = displays.select(new HardwareInstance()).get();

        display.distance(event.getDistance());
    }

    public static void main(String[] args) {
        System.out.println("Starting ...");

        System.out.print("Initializing CDI ...");
        final Weld weld = new Weld();
        WeldContainer container = weld.initialize();

        Marvin marvin = container.instance().select(Marvin.class).get();
        System.out.println("\b\b\bdone.");

        System.out.println("Starting Marvin ...");
        Thread thread = new Thread(marvin);
        thread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                marvin.logger.info("Shutdown Hook is running !");
                marvin.stop();
                weld.shutdown();
            }
        });

    }


}
