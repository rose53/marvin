package de.rose53.marvin.lcars;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;

import de.rose53.marvin.Display;

public abstract class LcarsComponent {


    public final static Color BACKGROUND_COLOR = Color.BLACK;
    public final static Color TEXT_COLOR       = Color.decode("#3366CC");
    public final static Color FRAME_COLOR      = Color.decode("#CC99CC");


    public final static int FRAME_INSET = 7;
    public final static int FRAME_LARGE_SIZE = 35;
    public final static int FRAME_SMALL_SIZE = 8;
    public final static int FRAME_THIN_SIZE = 4;

    public final static int SPACE       = 3;
    public final static int HEADER_SIZE = 24;

    public final static int BASE_BUTTON_HEIGHT  = 34;
    public final static int BASE_BUTTON_WIDTH   = 70;

    public final static int BUTTON_RADIUS       = BASE_BUTTON_HEIGHT / 2;
    public final static int BUTTON_LABEL_WIDTH  = BASE_BUTTON_WIDTH / 2;
    public final static int BUTTON_LABEL_HEIGHT = BUTTON_RADIUS;
    public final static int BUTTON_INFO_WIDTH   = 25;

    public static Font lcarsFont;
    public static Font lcars12;
    public static Font lcars14;
    public static Font lcars18;
    public static Font lcars24;

    static {
        try {
            lcarsFont = Font.createFont(Font.TRUETYPE_FONT, Display.class.getResourceAsStream("/lcarsgtj3.ttf"));
            lcars12 = lcarsFont.deriveFont(12f);
            lcars14 = lcarsFont.deriveFont(14f);
            lcars18 = lcarsFont.deriveFont(18f);
            lcars24 = lcarsFont.deriveFont(24f);
        } catch (FontFormatException | IOException e) {
        }
    }

    public abstract void draw(Graphics2D g2d, Rectangle r);
}
