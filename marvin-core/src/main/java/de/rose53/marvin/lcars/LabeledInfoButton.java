package de.rose53.marvin.lcars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

public class LabeledInfoButton extends LcarsComponent {

    public final static Color BUTTON_COLOR1 = Color.decode("#FFCC66");
    public final static Color BUTTON_COLOR2 = Color.decode("#3366CC");

    private String label;
    private String info;
    private Color  color;

    public LabeledInfoButton() {
        super();
    }

    public LabeledInfoButton(Color color) {
        super();
        this.color = color;
    }

    public LabeledInfoButton(String label, Color color) {
        super();
        this.label = label;
        this.color = color;
    }


    public LabeledInfoButton(String label, String info, Color color) {
        super();
        this.label = label;
        this.info = info;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void draw(Graphics2D g2d, Rectangle r) {

        int infoPosX = r.x + BUTTON_RADIUS / 2 + BUTTON_LABEL_WIDTH;

        g2d.setPaint(color);

        GeneralPath path = new GeneralPath();

        path.append(new Arc2D.Double(r.x, r.y, BUTTON_RADIUS, BUTTON_RADIUS, 90, 180, Arc2D.OPEN), true);
        g2d.fill(path);
        path = new GeneralPath();

        path.moveTo(r.x + BUTTON_RADIUS / 2 , r.y);
        path.lineTo(infoPosX, r.y);
        path.lineTo(infoPosX, r.y + BUTTON_LABEL_HEIGHT);
        path.lineTo(r.x + BUTTON_RADIUS / 2 , r.y + BUTTON_LABEL_HEIGHT);

        g2d.fill(path);

        if (label != null) {

            g2d.setFont(lcars18);
            g2d.setColor(BACKGROUND_COLOR);

            int stringWidth = g2d.getFontMetrics().stringWidth(label);

            g2d.drawString(label, infoPosX - stringWidth - SPACE, r.y + BUTTON_LABEL_HEIGHT - 2);
        }


        if (info != null) {

            g2d.setFont(lcars18);
            g2d.setColor(color);

            int stringWidth = g2d.getFontMetrics().stringWidth(info);

            g2d.drawString(info, infoPosX + BUTTON_INFO_WIDTH - stringWidth, r.y + BUTTON_LABEL_HEIGHT - 2);
        }
        g2d.setPaint(color);
        path = new GeneralPath();

        path.moveTo(infoPosX + BUTTON_INFO_WIDTH  , r.y);
        path.lineTo(infoPosX + BUTTON_INFO_WIDTH  + SPACE, r.y);
        path.lineTo(infoPosX + BUTTON_INFO_WIDTH  + SPACE, r.y + BUTTON_LABEL_HEIGHT);
        path.lineTo(infoPosX + BUTTON_INFO_WIDTH , r.y + BUTTON_LABEL_HEIGHT);

        g2d.fill(path);

        path = new GeneralPath();

        path.append(new Arc2D.Double(infoPosX + BUTTON_INFO_WIDTH + SPACE - BUTTON_RADIUS / 2 - 1, r.y, BUTTON_RADIUS, BUTTON_RADIUS, 90, -180, Arc2D.OPEN), true);
        g2d.fill(path);
    }

    public static int getLabeledInfoButtonWidth() {
        return BUTTON_RADIUS / 2 + BUTTON_LABEL_WIDTH + BUTTON_INFO_WIDTH + SPACE + BUTTON_RADIUS / 2;
    }
}
