/*
 * PiCam.java
 *
 * Copyright (c) 2013, rose. All rights reserved.
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
package de.rose53.marvin;

import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.marvin.Hardware.hw;
import de.rose53.marvin.events.ImageEvent;

/**
 * @author rose
 *
 */
@ApplicationScoped
public class Camera {

    @Inject
    Logger logger;

    @Inject
    Event<ImageEvent> imageEvent;

    public byte[] getImageAsByteArray() throws IOException {
        return getImageAsByteArray(null);
    }

    public byte[] getImageAsByteArray(CameraStillOptions options) throws IOException {
        return auqireImage(options);
    }

    public void auqireImageAsByteArray(final CameraStillOptions options) {
        new Thread(new Runnable() {
            public void run() {
                try {
                     imageEvent.fire(new ImageEvent(auqireImage(options)));
                } catch (IOException e) {
                    logger.error("auqireImageAsByteArray: ",e);
                }
            }
        }).start();

    }

    public synchronized byte[] auqireImage(CameraStillOptions options) throws IOException {

        if (new HardwareInstance().hardware == hw.INTEL) {
            try {
                return Files.readAllBytes(Paths.get(Camera.class.getResource("/marvin_camera.jpg").toURI()));
            } catch (URISyntaxException e) {
                logger.error("auqireImage: ",e);
            }
        }

        logger.debug("auqireImage: called with options ...");
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        List<String> command = new ArrayList<>();
        command.add("raspistill");
        command.add("-o");
        command.add("-");
        if (options != null) {
            options.addOptions(command);
        }
        ProcessBuilder pb = new ProcessBuilder(command);
        logger.debug("auqireImage: starting process ...");
        pb.redirectErrorStream(false);
        Process process = pb.start();

        copy(new BufferedInputStream(process.getInputStream()), o);
        byte[] retVal = null;
        try {
            if (process.waitFor() == 0) {
                logger.debug("auqireImage: process terminated, getting image data.");
                retVal = o.toByteArray();
            } else {
                logger.error("auqireImage: raspistill terminated with an error");
                traceProcessOutput(process);
            }
        } catch (InterruptedException e) {
        }
        logger.debug("auqireImage: done.");
        return retVal;
    }

    public RenderedImage getImage() throws IOException {
        return ImageIO.read(new ByteArrayInputStream(getImageAsByteArray()));
    }

    private void traceProcessOutput(Process process) throws IOException {
        InputStreamReader tempReader = new InputStreamReader(new BufferedInputStream(process.getErrorStream()));
        BufferedReader    reader     = new BufferedReader(tempReader);

        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            logger.trace(line);
        }
    }

    private long copy(InputStream input, OutputStream output) throws IOException {
        logger.debug("copy: start reading data from the input stream ...");
        long count = 0;
        int n = 0;
        byte[] buffer = new byte[4096];
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        logger.debug("copy: done, got bytes = {}",count);
        return count;
    }

}
