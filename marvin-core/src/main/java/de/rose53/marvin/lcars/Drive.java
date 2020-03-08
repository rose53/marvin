package de.rose53.marvin.lcars;

import static de.rose53.marvin.Motor.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import de.rose53.marvin.Motor;
import de.rose53.marvin.ReadMecanumMotorInfo;

public class Drive extends LcarsComponent {

    private static BufferedImage marvinSchematic;

    static {
        try {
            marvinSchematic = ImageIO.read(Drive.class.getResourceAsStream("/marvin_schematic.png"));
        } catch (IOException e) {
        }
    }

    private Map<Motor,ReadMecanumMotorInfo> mecanumMotorInfo = new HashMap<>();

    public Drive() {
    }

    public void setMotorInformation(Map<Motor,ReadMecanumMotorInfo> mecanumMotorInfo) {
        if (mecanumMotorInfo != null) {
            this.mecanumMotorInfo.clear();
            this.mecanumMotorInfo.putAll(mecanumMotorInfo);
        }
    }

    private void drawGridMotor(Graphics2D g2d, Motor motor, Rectangle r) {

        int centerX  = r.x + r.width / 2;
        int tickSize = r.width / 8;

        ReadMecanumMotorInfo motorInfo = mecanumMotorInfo.get(motor);
        if (motorInfo != null) {
            g2d.setPaint(Color.decode("#FF9933"));
            int speedRechtWidth = 4 * tickSize * motorInfo.getSpeed() / 100;
            int speedRechtX;
            if (motorInfo.isDirection()) {
                speedRechtX = centerX - speedRechtWidth;
            } else {
                speedRechtX = centerX;
            }
            g2d.fillRect(speedRechtX, r.y + 4,speedRechtWidth, r.height - 8);
        }


        g2d.setPaint(Color.decode("#006699"));

        g2d.drawLine(r.x, r.y + r.height / 2, r.x + r.width, r.y + r.height / 2);
        g2d.drawLine(centerX, r.y, centerX, r.y + r.height);
        for (int i = 1; i <= 4; i++) {
            g2d.drawLine(centerX + i * tickSize, r.y + 5, centerX + i * tickSize, r.y + 15);
            g2d.drawLine(centerX - i * tickSize, r.y + 5, centerX - i * tickSize, r.y + 15);
        }
    }

    @Override
    public void draw(Graphics2D g2d, Rectangle r) {

        g2d.setPaint(FRAME_COLOR);
        GeneralPath path = new GeneralPath();

        path.moveTo(r.x + r.width, r.y);

        path.lineTo(r.x + r.width,r.y + r.height - (FRAME_SMALL_SIZE + BASE_BUTTON_HEIGHT));
        path.quadTo(r.x + r.width,r.y + r.height,r.x + r.width - (FRAME_SMALL_SIZE + BASE_BUTTON_HEIGHT),r.y + r.height);
        path.lineTo(r.x,r.y + r.height);
        path.lineTo(r.x, r.y + r.height - FRAME_SMALL_SIZE);
        path.lineTo(r.x  + r.width - FRAME_LARGE_SIZE - BASE_BUTTON_HEIGHT, r.y + r.height - FRAME_SMALL_SIZE);
        path.quadTo(r.x + r.width - FRAME_LARGE_SIZE,r.y + r.height - FRAME_SMALL_SIZE,r.x + r.width - FRAME_LARGE_SIZE,r.y + r.height - BASE_BUTTON_HEIGHT);
        path.lineTo(r.x + r.width - FRAME_LARGE_SIZE,r.y);

        g2d.fill(path);

        if (marvinSchematic != null) {

            Point marvinSchmaticOrigin        = new Point(r.x + (r.width - FRAME_LARGE_SIZE - marvinSchematic.getWidth()) / 2, r.y + (r.height - FRAME_SMALL_SIZE - marvinSchematic.getHeight())/2);
            Dimension marvinSchmaticDimension = new Dimension(marvinSchematic.getWidth(), marvinSchematic.getHeight());

            g2d.drawImage(marvinSchematic, null, marvinSchmaticOrigin.x, marvinSchmaticOrigin.y);

            Rectangle leftFrontMotorInfoRect = new Rectangle(r.x , marvinSchmaticOrigin.y + 5, marvinSchmaticOrigin.x - r.x - SPACE, 20);
            Rectangle leftBackMotorInfoRect  = new Rectangle(r.x , marvinSchmaticOrigin.y + marvinSchmaticDimension.height - 25, marvinSchmaticOrigin.x - r.x - SPACE, leftFrontMotorInfoRect.height);
            Rectangle rightFrontMotorInfoRect = new Rectangle(marvinSchmaticOrigin.x + marvinSchmaticDimension.width, marvinSchmaticOrigin.y + 5, leftFrontMotorInfoRect.width, leftFrontMotorInfoRect.height);
            Rectangle rightBackMotorInfoRect = new Rectangle(marvinSchmaticOrigin.x + marvinSchmaticDimension.width, leftBackMotorInfoRect.y, leftFrontMotorInfoRect.width, leftFrontMotorInfoRect.height);

            drawGridMotor(g2d,FRONT_LEFT,leftFrontMotorInfoRect);
            drawGridMotor(g2d,REAR_LEFT,leftBackMotorInfoRect);
            drawGridMotor(g2d,FRONT_RIGHT,rightFrontMotorInfoRect);
            drawGridMotor(g2d,REAR_RIGHT,rightBackMotorInfoRect);
        }



    }

}
