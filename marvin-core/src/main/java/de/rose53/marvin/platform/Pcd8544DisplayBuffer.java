/*
 * Pcd8544DisplayBuffer.java
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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rose
 *
 */
final class Pcd8544DisplayBuffer {

    private static final Logger logger = LoggerFactory.getLogger(Pcd8544DisplayBuffer.class);

    public final static int WIDTH = 84;
    public final static int HEIGHT = 48;

    private final byte[] displayBuffer = new byte[WIDTH * HEIGHT / 8];

    private final Point cursor;

    private boolean invert = false;

    /**
     * C'tor
     */
    public Pcd8544DisplayBuffer() {
        this.cursor = new Point();
        clearDisplayBuffer();
    }

    /**
     * Clears the display buffer by filling it with zeros
     */
    public void clearDisplayBuffer() {
        logger.debug("clearDisplayBuffer:");
        fillScreen(false);
    }

    /**
     * Fills the buffer with the given value
     *
     * @param the color to fill
     */
    public void fillScreen(boolean color) {
        logger.debug("fillScreen: filling screen with color = {}", color);
        Arrays.fill(displayBuffer, color? (byte) 0xFF:(byte) 0);
    }

    /**
     *
     * @param invert
     */
    public void invertDisplay(boolean invert) {
        this.invert = invert;
    }

    public byte[] getBuffer() {
        byte[] retVal = Arrays.copyOf(displayBuffer, displayBuffer.length);
        if (invert) {
            for (int i = 0; i < retVal.length; i++) {
                retVal[i] = (byte)~retVal[i];
            }
        }
        return retVal;
    }

    public void setCursor(Point cursor) {
        this.cursor.setLocation(cursor);
    }

    public Point getCursor() {
        return cursor;
    }

    public void setPixel(Point p, boolean value) {
        logger.debug("setPixel: p : {}, value = {}", p, value);
        if (p.x < 0 || p.x >= WIDTH || p.y < 0 || p.y >= HEIGHT) {
            logger.error("setPixel: x or y is out of range");
            return;
        }
        if (value) {
            displayBuffer[p.x + (p.y / 8) * WIDTH] |= 1 << (p.y % 8);
        } else {
            displayBuffer[p.x + (p.y / 8) * WIDTH] &= ~(1 << (p.y % 8));
        }
    }

    public boolean getPixel(Point p) {
        logger.debug("getPixel: p : {}", p);
        if (p.x < 0 || p.x >= WIDTH || p.y < 0 || p.y >= HEIGHT) {
            logger.error("getPixel: x or y is out of range");
            return false;
        }
        boolean retVal = ((displayBuffer[p.x + (p.y / 8) * WIDTH] >> (p.y % 8)) & 0x1) > 0;
        logger.debug("getPixel: value = {}", retVal);
        return retVal;
    }

    private void drawHLine(Point p, int w, boolean color) {
        drawLine(p, new Point(p.x+w-1, p.y), color);
    }

    private void drawVLine(Point p, int h, boolean color) {
        drawLine(p, new Point(p.x, p.y + h - 1), color);
    }

    // Bresenham's algorithm - thx wikpedia
    public void drawLine(final Point p0, final Point p1, boolean color) {
        Point work0 = new Point(p0);
        Point work1 = new Point(p1);
        boolean steep = Math.abs(work1.y - work0.y) > Math.abs(work1.x - work0.x);
        int tmp;
        if (steep) {
            tmp = work0.x;
            work0.x = work0.y;
            work0.y = tmp;

            tmp = work1.x;
            work1.x = work1.y;
            work1.y = tmp;
        }

        if (work0.x > work1.x) {
            tmp = work0.x;
            work0.x = work1.x;
            work1.x = tmp;

            tmp = work0.y;
            work0.y = work1.y;
            work1.y = tmp;
        }

        int dx, dy;
        dx = work1.x - work0.x;
        dy = Math.abs(work1.y - work0.y);

        int err = dx / 2;
        int ystep;

        if (work0.y < work1.y) {
            ystep = 1;
        } else {
            ystep = -1;
        }

        Point p = new Point();
        for (; work0.x <= work1.x; work0.x++) {
            if (steep) {
                p.x = work0.y;
                p.y = work0.x;
            } else {
                p.x = work0.x;
                p.y = work0.y;

            }
            setPixel(p, color);
            err -= dy;
            if (err < 0) {
                work0.y += ystep;
                err += dx;
            }
        }
    }

    public void drawRect(Rectangle r, boolean color) {
        drawHLine(new Point(r.x, r.y), r.width, color);
        drawHLine(new Point(r.x, r.y + r.height - 1), r.width, color);
        drawVLine(new Point(r.x, r.y), r.height, color);
        drawVLine(new Point(r.x + r.width - 1, r.y), r.height, color);
    }

    public void drawRoundRect(Rectangle r, int radius, boolean color) {

        drawHLine(new Point(r.x+radius  , r.y    ), r.width-2*radius, color); // Top
        drawHLine(new Point(r.x+radius  , r.y+r.height-1), r.width-2*radius, color); // Bottom
        drawVLine(new Point(r.x    , r.y+radius)  , r.height-2*radius, color); // Left
        drawVLine(new Point(r.x+r.width-1, r.y+radius)  , r.height-2*radius, color); // Right

        // draw four corners
        drawCircleHelper(r.x+radius    , r.y+radius    , radius, (byte)1, color);
        drawCircleHelper(r.x+r.width-radius-1, r.y+radius    , radius, (byte)2, color);
        drawCircleHelper(r.x+r.width-radius-1, r.y+r.height-radius-1, radius, (byte)4, color);
        drawCircleHelper(r.x+radius    , r.y+r.height-radius-1,radius, (byte)8, color);


    }

    public void fillRoundRect(Rectangle r, int radius, boolean color) {
        fillRect(new Rectangle(r.x + radius, r.y, r.width - 2 * radius, r.height), color);

        // draw four corners
        fillCircleHelper(r.x+r.width-radius-1,  r.y+radius, radius, (byte)1, r.height-2*radius-1, color);
        fillCircleHelper(r.x+radius    ,  r.y+radius, radius, (byte)2, r.height-2*radius-1, color);
    }

    private void drawCircleHelper(int x0, int y0, int r, byte cornername,boolean color) {
        int f = 1 - r;
        int ddF_x = 1;
        int ddF_y = -2 * r;
        int x = 0;
        int y = r;

        while (x < y) {
            if (f >= 0) {
                y--;
                ddF_y += 2;
                f += ddF_y;
            }
            x++;
            ddF_x += 2;
            f += ddF_x;
            if ((cornername & 0x4) != 0) {
                setPixel(new Point(x0 + x, y0 + y), color);
                setPixel(new Point(x0 + y, y0 + x), color);
            }
            if ((cornername & 0x2) != 0) {
                setPixel(new Point(x0 + x, y0 - y), color);
                setPixel(new Point(x0 + y, y0 - x), color);
            }
            if ((cornername & 0x8) != 0) {
                setPixel(new Point(x0 - y, y0 + x), color);
                setPixel(new Point(x0 - x, y0 + y), color);
            }
            if ((cornername & 0x1) != 0) {
                setPixel(new Point(x0 - y, y0 - x), color);
                setPixel(new Point(x0 - x, y0 - y), color);
            }
        }
    }

    private void fillCircleHelper(int x0, int y0, int r, byte cornername, int delta, boolean color) {

        int f = 1 - r;
        int ddF_x = 1;
        int ddF_y = -2 * r;
        int x = 0;
        int y = r;

        while (x < y) {
            if (f >= 0) {
                y--;
                ddF_y += 2;
                f += ddF_y;
            }
            x++;
            ddF_x += 2;
            f += ddF_x;

            if ((cornername & 0x1) != 0) {
                drawVLine(new Point(x0 + x, y0 - y), 2 * y + 1 + delta, color);
                drawVLine(new Point(x0 + y, y0 - x), 2 * x + 1 + delta, color);
            }
            if ((cornername & 0x2) != 0) {
                drawVLine(new Point(x0 - x, y0 - y), 2 * y + 1 + delta, color);
                drawVLine(new Point(x0 - y, y0 - x), 2 * x + 1 + delta, color);
            }
        }
    }

    public void fillRect(Rectangle r, boolean color) {
        for (int i= r.x; i< r.x + r.width; i++) {
            drawVLine(new Point(i, r.y), r.height, color);
        }
    }

    public void drawCircle(Point center, int radius, boolean color) {
        int f = 1 - radius;
        int ddF_x = 1;
        int ddF_y = -2 * radius;
        int x = 0;
        int y = radius;

        setPixel(new Point(center.x, center.y + radius), color);
        setPixel(new Point(center.x, center.y - radius), color);
        setPixel(new Point(center.x + radius, center.y), color);
        setPixel(new Point(center.x - radius, center.y), color);

        while (x < y) {
            if (f >= 0) {
                y--;
                ddF_y += 2;
                f += ddF_y;
            }
            x++;
            ddF_x += 2;
            f += ddF_x;

            setPixel(new Point(center.x + x, center.y + y), color);
            setPixel(new Point(center.x - x, center.y + y), color);
            setPixel(new Point(center.x + x, center.y - y), color);
            setPixel(new Point(center.x - x, center.y - y), color);
            setPixel(new Point(center.x + y, center.y + x), color);
            setPixel(new Point(center.x - y, center.y + x), color);
            setPixel(new Point(center.x + y, center.y - x), color);
            setPixel(new Point(center.x - y, center.y - x), color);
        }
    }

    public void fillCircle(Point center, int radius, boolean color) {
        drawVLine(new Point(center.x, center.y - radius), 2 * radius + 1, color);
        fillCircleHelper(center.x, center.y, radius, (byte)3, 0, color);
    }

    public void drawTriangle(Point p0, Point p1, Point p2, boolean color) {
        drawLine(p0, p1, color);
        drawLine(p1, p2, color);
        drawLine(p2, p0, color);
    }

    public void fillTriangle(Point p0, Point p1, Point p2, boolean color) {

        int x0 = p0.x;
        int y0 = p0.y;
        int x1 = p1.x;
        int y1 = p1.y;
        int x2 = p2.x;
        int y2 = p2.y;

        int a, b, y, last;

        int tmp;
        // Sort coordinates by Y order (y2 >= y1 >= y0)
        if (y0 > y1) {
            tmp = y0;
            y0 = y1;
            y0 = tmp;

            tmp = x0;
            x0 = x1;
            x0 = tmp;
        }
        if (y1 > y2) {
            tmp = y2;
            y2 = y1;
            y1 = tmp;

            tmp = x2;
            x2 = x1;
            x1 = tmp;
        }

        if (y0 > y1) {
            tmp = y0;
            y0 = y1;
            y1 = tmp;

            tmp = x0;
            x0 = x1;
            x1 = tmp;
        }

      if (y0 == y2) { // Handle awkward all-on-same-line case as its own thing
        a = b = x0;
        if (x1 < a) {
            a = x1;
        } else if(x1 > b) {
            b = x1;
        }

        if(x2 < a) {
            a = x2;
        } else if(x2 > b) {
            b = x2;
        }
        drawHLine(new Point(a, y0), b-a+1, color);
        return;
      }

      int
        dx01 = x1 - x0,
        dy01 = y1 - y0,
        dx02 = x2 - x0,
        dy02 = y2 - y0,
        dx12 = x2 - x1,
        dy12 = y2 - y1,
        sa   = 0,
        sb   = 0;

      // For upper part of triangle, find scanline crossings for segments
      // 0-1 and 0-2.  If y1=y2 (flat-bottomed triangle), the scanline y1
      // is included here (and second loop will be skipped, avoiding a /0
      // error there), otherwise scanline y1 is skipped here and handled
      // in the second loop...which also avoids a /0 error here if y0=y1
      // (flat-topped triangle).
      if(y1 == y2) last = y1;   // Include y1 scanline
      else         last = y1-1; // Skip it

      for(y=y0; y<=last; y++) {
        a   = x0 + sa / dy01;
        b   = x0 + sb / dy02;
        sa += dx01;
        sb += dx02;
        /* longhand:
        a = x0 + (x1 - x0) * (y - y0) / (y1 - y0);
        b = x0 + (x2 - x0) * (y - y0) / (y2 - y0);
        */
        if(a > b) {
            tmp = a;
            a = b;
            b = tmp;
        }
        drawHLine(new Point(a, y), b-a+1, color);
      }

      // For lower part of triangle, find scanline crossings for segments
      // 0-2 and 1-2.  This loop is skipped if y1=y2.
      sa = dx12 * (y - y1);
      sb = dx02 * (y - y0);
      for(; y<=y2; y++) {
        a   = x1 + sa / dy12;
        b   = x0 + sb / dy02;
        sa += dx12;
        sb += dx02;
        /* longhand:
        a = x1 + (x2 - x1) * (y - y1) / (y2 - y1);
        b = x0 + (x2 - x0) * (y - y0) / (y2 - y0);
        */
        if(a > b) {
            tmp = a;
            a = b;
            b = tmp;
        }
        drawHLine(new Point(a, y), b-a+1, color);
      }
    }

    public void drawChar(Point p, char c, int size) {

        byte[] character = Pcd8544Font.getBytes(c);
        if (character == null) {
            logger.debug("drawChar: character >{}< is not defined in the font",c);
            return;
        }

        if (    (p.x >= WIDTH)                // Clip right
             || (p.y >= HEIGHT)               // Clip bottom
             || ((p.x + (Pcd8544Font.CHAR_WIDTH + 1) * size - 1) < 0)    // Clip left
             || ((p.y + 8 * size - 1) < 0)) { // Clip top
            logger.debug("drawChar: character was clipped");
            return;
        }

        for (int i = 0; i < (Pcd8544Font.CHAR_WIDTH + 1); i++ ) {
            int line;
            if (i >= Pcd8544Font.CHAR_WIDTH) {
                // small space behind the character
                line = 0x0;
            } else {
                line = Pcd8544Font.getLine(c, i);
            }
            for (int j = 0; j <8; j++) {
                if (size == 1) {// default size
                    setPixel(new Point(p.x+i, p.y+j), (line & 0x1) != 0);
                } else {  // big size
                    fillRect(new Rectangle(p.x+(i*size), p.y+(j*size), size, size), (line & 0x1) != 0);
                }
                line >>= 1;
            }
        }
    }

    /**
     * Draws th given <code>image</code> at the specified location
     *
     * @param p
     * @param image the image to draw
     */
    public void drawImage(Point p, BufferedImage image) {

        if (p == null) {
            logger.debug("drawImage: no point given where to set origin, returning");
            return;
        }
        if (image == null) {
            logger.debug("drawImage: no image given to draw, returning");
            return;
        }

        int color;
        float grey;
        Point actPoint = new Point();
        for (int i = 0, w = image.getWidth(); i < w; i++) {
            for (int j = 0, h = image.getHeight(); j < h; j++) {

                actPoint.x = p.x + i;
                actPoint.y = p.y + j;
                color = image.getRGB(i,j) & 0x00FFFFFF;

                grey = 0.2126f * ((color >> 16) & 0xFF) + 0.7152f  * ((color >> 8) & 0xFF) + 0.0722f * ((color >> 0) & 0xFF);

                setPixel(actPoint, grey < 127);
            }
        }

    }


    public void drawImage(Point p, byte[] bitmap, int w, int h, boolean color) {

        int byteWidth = (w + 7) / 8;

        Point actPoint = new Point();
        for(int j=0; j<h; j++) {
            for(int i=0; i<w; i++ ) {
                if ((bitmap[j * byteWidth + i / 8] & (128 >> (i & 7))) > 0) {
                    actPoint.x = p.x + i;
                    actPoint.y = p.y + j;
                    setPixel(actPoint, color);
                }
            }
        }
    }

    /**
     * Write the display buffer as an ASCII image.
     *
     * @return the display buffer as an ASCII image
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (getPixel(new Point(x,y))) {
                    builder.append('*');
                } else {
                    builder.append('o');
                }
            }
            builder.append('\n');
        }
        return builder.toString();
    }


}
