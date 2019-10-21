/*
* JoystickSocketServer.java
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
package de.rose53.marvin.joystick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.marvin.HardwareInstance;
import de.rose53.marvin.MecanumDrive;
import de.rose53.marvin.PanTiltSensors;

/**
 * @author rose
 *
 */
public class JoystickSocketServer implements Runnable {

    private final short INC_DEC = 10;

    @Inject
    Logger logger;

    @Inject
    @Any
    Instance<MecanumDrive> mecanumDrives;

    @Inject
    @Any
    Instance<PanTiltSensors> panTiltSensors;

    final ExecutorService clientProcessingPool = Executors.newSingleThreadExecutor();

    private  Process process;

    private boolean shouldRun = true;

    public void start() {
        Thread serverThread = new Thread(this);
        serverThread.start();

        try {
            // the thread the change to start
            Thread.sleep(2000);

                List<String> command = new ArrayList<String>();
                command.add("./marvin-ps3.sh");

                ProcessBuilder pb = new ProcessBuilder(command);
                logger.debug("start: starting joystick process ...");
                process = pb.start();

        } catch (InterruptedException | IOException e) {
            logger.error("start: Unable to start joystick process",e);
        }

    }


    public void stop() {
        if (process != null) {
            process.destroy();
        }
        shouldRun = false;
    }

    public void mecanumDrive(int ch1, int ch3 ,int ch4) {
        MecanumDrive mecanumDrive = mecanumDrives.select(new HardwareInstance()).get();

        mecanumDrive.mecanumDrive((byte)ch1, (byte)ch3, (byte)ch4);
    }

    public void incrementPan() {
        PanTiltSensors panTilt = panTiltSensors.select(new HardwareInstance()).get();

        panTilt.incrementPan(INC_DEC);
    }

    public void decrementPan() {
        PanTiltSensors panTilt = panTiltSensors.select(new HardwareInstance()).get();

        panTilt.decrementPan(INC_DEC);
    }

    public void incrementTilt() {
        PanTiltSensors panTilt = panTiltSensors.select(new HardwareInstance()).get();

        panTilt.incrementTilt(INC_DEC);
    }

    public void decrementTilt() {
        PanTiltSensors panTilt = panTiltSensors.select(new HardwareInstance()).get();

        panTilt.decrementTilt(INC_DEC);
    }


    public void tilt(short tilt) {
        PanTiltSensors panTilt = panTiltSensors.select(new HardwareInstance()).get();

        panTilt.setTilt(tilt);
    }


    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8000);
            serverSocket.setSoTimeout(2000);
            logger.debug("run: Waiting for clients to connect...");
            while (shouldRun) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientProcessingPool.submit(new ClientTask(clientSocket));
                } catch (SocketTimeoutException e) {
                    // now we are able to check, if we should close the socket
                }
            }
        } catch (IOException e) {
            logger.error("run: Unable to process client request",e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private class ClientTask implements Runnable {
        private final Socket clientSocket;

        private ClientTask(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            logger.debug("run: got connection from joystick handler.");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
                String str;

                String[] socketData;
                int ch1;
                int ch3;
                int ch4;
                while ((str = br.readLine()) != null) {
                    if ("bye".equals(str)) {
                        break;
                    }
                    socketData = str.trim().split(" ");
                    if (socketData == null || socketData.length < 1) {
                        continue;
                    }
                    if ("mecanum".equalsIgnoreCase(socketData[0].trim()) && socketData.length == 4) {
                        ch1 = Integer.parseInt(socketData[1].trim());
                        ch3 = Integer.parseInt(socketData[2].trim());
                        ch4 = Integer.parseInt(socketData[3].trim());
                        logger.debug("run: recieved mecanum: ch1 = >{}< ch3 = >{}< ch4 = >{}<",ch1,ch3,ch4);
                        mecanumDrive(ch1,ch3,ch4);
                    }

                    if ("pan_inc".equalsIgnoreCase(socketData[0].trim())) {
                        logger.debug("run: recieved pan_inc");
                        incrementPan();
                    }

                    if ("pan_dec".equalsIgnoreCase(socketData[0].trim())) {
                        logger.debug("run: recieved pan_dec");
                        decrementPan();
                    }

                    if ("tilt_inc".equalsIgnoreCase(socketData[0].trim())) {
                        logger.debug("run: recieved tilt_inc");
                        incrementTilt();
                    }

                    if ("tilt_dec".equalsIgnoreCase(socketData[0].trim())) {
                        logger.debug("run: recieved tilt_dec");
                        decrementTilt();
                    }
                }
                clientSocket.close();
            } catch (IOException e) {
                logger.error("run: Unable to process client request",e);
            }
        }
    }
}
