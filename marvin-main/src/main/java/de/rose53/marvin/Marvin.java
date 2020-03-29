/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rose53.marvin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import de.rose53.marvin.events.*;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;

import de.rose53.marvin.joystick.Joystick;
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
    Instance<MecanumDrive> mecanumDrives;

    @Inject
    Display display;

    @Inject
    Camera camera;

    @Inject
    Webserver webServer;

    @Inject
    Joystick joystick;

    @Inject
    RestHelper restHelper;

    private ScheduledExecutorService cameraExecutor = Executors.newScheduledThreadPool(1);

    public void start() {

        try {
            System.out.print("Starting WebServer ...");
            webServer.start();
            System.out.println("\b\b\bdone.");

            System.out.print("Initializing Joystick ...");
            if (joystick.start()) {
                System.out.println("\b\b\bdone.");
            } else {
                System.out.println("\b\b\bno joystick found.");
            }
            // open communication
            MecanumDrive mecanumDrive = mecanumDrives.select(new HardwareInstance()).get();
            mecanumDrive.getCurrent();

            CameraStillOptions options = new CameraStillOptions();

            options.setTimeout(100);

            cameraExecutor.scheduleAtFixedRate(() -> camera.auqireImageAsByteArray(options), 10, 5, TimeUnit.SECONDS);

        } catch (Exception e) {
            logger.error("start:",e);
        }
        show();

        System.out.println("\n\n ####################################################### ");
        System.out.println(" ####                MARVIN IS ALIVE !!!             ### ");
        System.out.println(" ####################################################### ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        System.out.println(" ### Date: " + dateFormat.format(new Date()));
        running = true;
    }

    public void stop() {
        if (running) {
            running = false;
        }
        try {
            cameraExecutor.shutdown();
            webServer.stop();
            joystick.stop();
        } catch (Exception e) {
            logger.error("stop: ",e);
        }
    }

    @Override
    public void run() {
        start();
        while (running) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.warn("run:",e);
            }
        }
    }

    public void show() {
        display.welcome();
    }


    public void onReadMecanumCurrentEvent(@Observes ReadMecanumCurrentEvent event) {
        logger.debug("onReadMecanumCurrentEvent: ");
    }

    public void onReadMecanumMotorInfoEvent(@Observes ReadMecanumMotorInfoEvent event) {
        logger.debug("onReadMecanumMotorInfoEvent: ");
        display.motorInformation(event.getReadMecanumMotorInfo());
    }

    public void onReadDistanceEvent(@Observes DistanceEvent event) {
        logger.debug("onReadDistanceEvent: ");
        display.distance(event.getDistance(),event.getPlace());
    }

    public void onReadHeadingEvent(@Observes HeadingEvent event) {
        logger.debug("onReadHeadingEvent: ");
        display.heading(event.getHeading());
    }

    public void onPanTiltEvent(@Observes PanTiltEvent event) {
        logger.debug("onPanTiltEvent: ");
        display.panTilt(event.getPan(),event.getTilt());
    }

    public void onLiPoEvent(@Observes LiPoEvent event) {
        logger.debug("onLiPoEvent: ");
        display.liPoStatus(event.getLiPoStatus());
    }

    public void onImageEvent(@Observes ImageEvent event) {
        logger.debug("onImageEvent: ");
        display.image(event.getBufferedImage());
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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            marvin.logger.info("Shutdown Hook is running !");
            marvin.stop();
        }));
    }


}
