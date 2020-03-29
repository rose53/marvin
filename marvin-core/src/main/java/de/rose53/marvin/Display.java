/*
* Display.java
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
package de.rose53.marvin;

import static de.rose53.marvin.lcars.LcarsComponent.*;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.image.BufferedImage;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.swing.JFrame;

import de.rose53.marvin.utils.LiPoStatus;
import org.slf4j.Logger;
import org.tw.pi.framebuffer.FrameBuffer;

import de.rose53.marvin.Distance.Place;
import de.rose53.marvin.Hardware.hw;
import de.rose53.marvin.lcars.Header;
import de.rose53.marvin.lcars.Camera;
import de.rose53.marvin.lcars.DataView;
import de.rose53.marvin.lcars.Drive;

/**
 * @author rose
 *
 */
@ApplicationScoped
public class Display {


    @Inject
    Logger logger;

    private FrameBuffer   frameBuffer;
    private BufferedImage frameBufferImage;
    private Graphics2D    frameBufferG2d;

    private Header   header;
    private Drive    drive;
    private Camera   camera;
    private DataView data;

    private BufferedImage doubleBuffer;

    @PostConstruct
    public void init() {

        InetAddress adress = null;
        try {
            adress = getFirstNonLoopbackAddress(true, false);
        } catch (SocketException e) {
            logger.error("init: ",e);
        }

        header = new Header("MARVIN",adress != null?adress.getHostAddress():"");
        drive = new Drive();
        camera = new Camera();
        data = new DataView();

        if (new HardwareInstance().hardware == hw.INTEL) {
            frameBuffer = new FrameBuffer("dummy_480x320",false);

            JFrame f = new JFrame("Frame Buffer Test");
            f.setSize(640, 400);
            f.setLocation(300,200);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.getContentPane().add(BorderLayout.CENTER, frameBuffer.getScreenPanel());
            f.setVisible(true);

            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                   redraw();
                }
            }, 1000, 1000);

        } else {
            frameBuffer = new FrameBuffer("/dev/fb1",false);
        }
        frameBufferImage = frameBuffer.getScreen();
        frameBufferG2d   = frameBufferImage.createGraphics();

        doubleBuffer = new BufferedImage(frameBufferImage.getWidth(), frameBufferImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    @PreDestroy
    public void destroy() {
        if (frameBuffer != null) {
            frameBuffer.close();
        }
    }

    private void redraw() {

        int windowWidth = frameBufferImage.getWidth();
        int windowHeight = frameBufferImage.getHeight();


        Graphics2D g2d = doubleBuffer.createGraphics();

        Rectangle headerRect  = new Rectangle(FRAME_INSET,SPACE,windowWidth - FRAME_INSET, HEADER_SIZE);
        Rectangle driveRect   = new Rectangle(FRAME_INSET, HEADER_SIZE + 2 * SPACE, (int)(0.5 * windowWidth), 150);
        Rectangle cameraRect  = new Rectangle(driveRect.x + driveRect.width + SPACE, HEADER_SIZE + 2 * SPACE, windowWidth - driveRect.width - SPACE - 2 * FRAME_INSET, driveRect.height);

        Rectangle dataRect    = new Rectangle(FRAME_INSET,driveRect.y + driveRect.height + SPACE,windowWidth - 2 * FRAME_INSET,windowHeight - driveRect.y - driveRect.height - 2 * SPACE);

        // RenderingHints.VALUE_ANTIALIAS_ON must before rotate !
        // Rotated font drawing behaves strange without that....
        Map<Key,Object> hints = new HashMap<>();

        hints.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setRenderingHints(new RenderingHints(hints));

        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, windowWidth, windowHeight);

        header.draw(g2d,headerRect);
        drive.draw(g2d,driveRect);
        camera.draw(g2d,cameraRect);
        data.draw(g2d,dataRect);


        this.frameBufferG2d.drawImage(doubleBuffer, null, 0, 0);
        frameBuffer.repaint();
    }

    /**
     * After successful startup, this method gets called
     */
    public void welcome() {
        redraw();
    }

    /**
     * Displays the distance. The value is in [cm]
     * @param distance the distance
     * @param place the place of the sensor
     */
    public void distance(float distance, Place place) {
        data.setDistance(distance, place);
        redraw();
    }

    /**
     * Displays the heading. The value is in [°]
     * @param heading the heading
     */
    public void heading(float heading) {
        data.setHeading(heading);
        redraw();
    }

    /**
     * Displays the pan/tilt angle. The value is in [°]
     * @param pan the pan value
     * @param tilt the tilt value
     */
    public void panTilt(short pan, short tilt) {
        data.setPanTilt(pan,tilt);
        redraw();
    }

    /**
     * Displays the {@linkplain LiPoStatus}
     * @param liPoStatus the {@linkplain LiPoStatus}
     */
    public void liPoStatus(LiPoStatus liPoStatus) {
        data.setLiPoStatus(liPoStatus);
        redraw();
    }

    /**
     * Displays the given image
     * @param image the iamge to display
     */
    public void image(BufferedImage image) {
        camera.setImage(image);
        redraw();
    }

    /**
     * Displays the motor information for each motor.
     * @param mecanumMotorInfo the {@linkplain ReadMecanumMotorInfo motor information} for each {@linkplain Motor}
     */
    public void motorInformation(Map<Motor,ReadMecanumMotorInfo> mecanumMotorInfo) {
        drive.setMotorInformation(mecanumMotorInfo);
        redraw();
    }

    private InetAddress getFirstNonLoopbackAddress(boolean preferIpv4, boolean preferIPv6) throws SocketException {
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface i = en.nextElement();
            for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements();) {
                InetAddress addr = en2.nextElement();
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
