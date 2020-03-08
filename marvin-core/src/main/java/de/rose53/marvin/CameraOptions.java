/*
 * PiCamOptions.java
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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.List;

/**
 * @author rose
 *
 */
abstract public class CameraOptions {

    public enum ISO {
        ISO_100((short)100),
        ISO_200((short)200),
        ISO_400((short)400),
        ISO_800((short)800);

        final short value;

        private ISO(short value) {
            this.value = value;
        }

        public short getValue() {
            return value;
        }
    }

    public enum Rotation {
        ROT_0((short)0),
        ROT_90((short)90),
        ROT_180((short)180),
        ROT_270((short)270);

        final short value;

        private Rotation(short value) {
            this.value = value;
        }

        public short getValue() {
            return value;
        }
    }

    public enum Exposure {
        OFF,
        /**
         * Use automatic exposure mode
         */
        AUTO,
        /**
         * Select setting for night shooting
         */
        NIGHT,
        NIGHTPREVIEW,
        /**
         * Select setting for back lit subject
         */
        BACKLIGHT,
        SPOTLIGHT,
        /**
         * Select setting for sports (fast shutter etc)
         */
        SPORTS,
        /**
         * Select setting optimised for snowy scenery
         */
        SNOW,
        /**
         * Select setting optimised for beach
         */
        BEACH,
        /**
         * Select setting for long exposures
         */
        VERYLONG,
        /**
         * Constrain fps to a fixed value
         */
        FIXEDFPS,
        /**
         * Antishake mode
         */
        ANTISHAKE,
        FIREWORKS
    }

    /**
     * Set Automatic White Balance (AWB) mode
     */
    public enum AWB {
        /**
         * Turn off white balance calculation
         */
        OFF,
        /**
         * Automatic mode (default)
         */
        AUTO,
        /**
         * Sunny mode
         */
        SUN,
        /**
         * Cloudy mode
         */
        CLOUD,
        /**
         * Shaded mode
         */
        SHADE,
        /**
         * Tungsten lighting mode
         */
        TUNGSTEN,
        /**
         * Fluorescent lighting mode
         */
        FLUORESCENT,
        /**
         * Incandescent lighting mode
         */
        INCANDESCENT,
        /**
         * Flash mode
         */
        FLASH,
        /**
         * Horizon mode
         */
        HORIZON
    }

    public enum ImxFx {
        /**
         * No effect (default)
         */
        NONE,
        /**
         * Negate the image
         */
        NEGATIVE,
        /**
         * Solarise the image
         */
        SOLARISE,
        /**
         * Posterise the image
         */
        POSTERIZE,
        /**
         * Whiteboard effect
         */
        WHITEBOARD,
        /**
         * Blackboard effect
         */
        BLACKBOARD,
        /**
         * Sketch style effect
         */
        SKETCH,
        /**
         * Denoise the image
         */
        DENOISE,
        /**
         * Emboss the image
         */
        EMBOSS,
        /**
         * Apply an oil paint style effect
         */
        OILPAINT,
        /**
         * Hatch sketch style
         */
        HATCH,

        GPEN,
        /**
         * A pastel style effect
         */
        PASTEL,
        /**
         * A watercolour style effect
         */
        WATERCOLOUR,
        /**
         * Film grain style effect
         */
        FILM,
        /**
         * Blur the image
         */
        BLUR,
        /**
         * Colour saturate the image
         */
        SATURATION,
        /**
         * Not fully implemented
         */
        COLOURSWAP,
        /**
         * Not fully implemented
         */
        WASHEDOUT,
        /**
         * Not fully implemented
         */
        POSTERISE,
        /**
         * Not fully implemented
         */
        COLOURPOINT,
        /**
         * Not fully implemented
         */
        COLOURBALANCE,
        /**
         * Not fully implemented
         */
        CARTOON
    }

    public enum Metering {
        /**
         * Average the whole frame for metering.
         */
        AVERAGE,
        /**
         * Spot metering
         */
        SPOT,
        /**
         * Assume a backlit image
         */
        BACKLIT,
        /**
         * Matrix metering
         */
        MATRIX
    }

    /**
     * Holds the values for the colour effect <U:V>
     * <br />
     * The supplied U and V parameters (range 0 to 255) are applied to the U and Y
     * channels of the image. For example, --colfx 128:128 should result in a monochrome
     * image.
     *
     */
    public class ColFxParameter {

        public short u;

        public short v;

        public String getParameter() {
            return new StringBuilder().append(u).append(':').append(v).toString();
        }
    }

    // Preview Window

    /**
     * Allows the user to define the size and location on the screen that the preview window
     * will be placed. Note this will be superimposed over the top of any other
     * windows/graphics.
     */
    private Rectangle preview;

    /**
     * Forces the preview window to use the whole screen. Note that the aspect ratio of the
     * incoming image will be retained, so there may be bars on some edges.
     */
    private boolean fullscreen;

    /**
     * Disables the preview window completely. Note that even though the preview is
     * disabled, the camera will still be producing frames, so will be using power.
     */
    private boolean nopreview;

    /**
     * Sets the opacity of the preview windows. 0 = invisible, 255 = fully opaque.
     */
    private short opacity;

    // Camera Control Options

    /**
     * Set the sharpness of the image, 0 is the default (-100 to 100).
     */
    private byte sharpness;

    /**
     * Set the contrast of the image, 0 is the default (-100 to 100)
     */
    private byte contrast;

    /**
     * Set the brightness of the image, 50 is the default. 0 is black, 100 is white.
     */
    private byte brightness;

    /**
     * Set the colour saturation of the image. 0 is the default (-100 to 100).
     */
    private byte saturation;

    /**
     * Sets the ISO to be used for captures. Range is 100 to 800.
     */
    private ISO iso;

    /**
     * Set the EV compensation of the image. Range is -10 to +10, default is 0.
     */
    private byte ev;

    /**
     * Set exposure mode
     */
    private Exposure exposure;

    /**
     * Set Automatic White Balance (AWB) mode
     */
    private AWB awb;

    /**
     * Set an effect to be applied to the image
     */
    private ImxFx imxfx;

    private ColFxParameter colfx;

    /**
     * Specify the metering mode used for the preview and capture
     */
    private Metering metering;

    /**
     * Sets the rotation of the image in viewfinder and resulting image. This can take any
     * value from 0 upwards, but due to hardware constraints only 0, 90, 180 and 270
     * degree rotations are supported.
     */
    private Rotation rotation;

    /**
     * Set horizontal flip
     */
    private boolean hflip;

    /**
     * Set vertical flip
     */
    private boolean vflip;

    /**
     * Allows the specification of the area of the sensor to be used as the source for the
     * preview and capture. This is defined as x,y for the top left corner, and a width and
     * height, all values in normalised coordinates (0.0-1.0). So to set a ROI at half way
     * across and down the sensor, and an width and height of a quarter of the sensor use
     */
    private Rectangle2D.Float roi;

    public void addOptions(List<String> options) {
        if (options == null) {
            throw new IllegalArgumentException("options list must not be null");
        }
    }
}
