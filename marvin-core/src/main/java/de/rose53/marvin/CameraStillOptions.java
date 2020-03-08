/*
 * PiCamStillOptions.java
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

import java.util.List;

/**
 * @author rose
 *
 */
public class CameraStillOptions extends CameraOptions {

    /**
     * Set image width <size>
     */
    private int width;

    /**
     * Set image height <size>
     */
    private int height;

    /**
     * Time before takes picture and shuts down.
     * <br />
     * The program will run for this length of time, then take the capture (if output is
     * specified). If not specified, this is set to 5 seconds.
     */
    private int timeout;

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }



    /**
     * @param width the width to set
     */
    public CameraStillOptions setWidth(int width) {
        this.width = width;
        return this;
    }



    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }



    /**
     * @param height the height to set
     */
    public CameraStillOptions setHeight(int height) {
        this.height = height;
        return this;
    }



    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }



    /**
     * @param timeout the timeout to set
     */
    public CameraStillOptions setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }



    @Override
    public void addOptions(List<String> options) {
        if (options == null) {
            throw new IllegalArgumentException("options list must not be null");
        }
        super.addOptions(options);

        options.add("-w");
        options.add(Integer.toString(width));
        options.add("-h");
        options.add(Integer.toString(height));
        options.add("-t");
        options.add(Integer.toString(timeout));
    }
}
