package de.rose53.marvin.lcars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

public class Header extends LcarsComponent {

    private final static Color HEADER_COLOR  = Color.decode("#FF9933");

    private final String name;
    private final String address;

    public Header(String name, String address) {
        super();
        this.name    = name;
        this.address = address;
    }



    @Override
    public void draw(Graphics2D g2d, Rectangle r) {

        int radius = r.height / 2;

        g2d.setPaint(HEADER_COLOR);
        GeneralPath path = new GeneralPath();

        path.append(new Arc2D.Double(r.getX(), SPACE, r.height, r.height, 90, 180, Arc2D.OPEN), true);
        path.moveTo(r.x + radius, SPACE);
        path.lineTo(r.x + radius + r.height, SPACE);
        path.lineTo(r.x + radius + r.height, SPACE + r.height);
        path.lineTo(r.x + radius, SPACE + r.height);

        g2d.fill(path);


        path = new GeneralPath();

        path.moveTo(r.x+ radius + r.height + SPACE, SPACE);
        path.lineTo(r.width - radius - SPACE, SPACE);
        path.append(new Arc2D.Double(r.width - r.height - SPACE, SPACE, r.height, r.height, 90, -180, Arc2D.OPEN), true);
        path.lineTo(r.x + radius + r.height + SPACE, SPACE + r.height);

        g2d.fill(path);

        g2d.setFont(lcars24);
        g2d.setColor(BACKGROUND_COLOR);
        int width = g2d.getFontMetrics().stringWidth(name);

        g2d.fillRect(r.width - radius - 2 * SPACE - r.height- width,SPACE,width + 2 * SPACE,r.height);

        g2d.setColor(TEXT_COLOR);
        g2d.drawString(name, r.width - radius - SPACE - r.height - width, SPACE + r.height + (r.height - g2d.getFontMetrics().getHeight()) / 2);

        g2d.setFont(lcars18);
        g2d.setColor(BACKGROUND_COLOR);
        g2d.drawString(address, r.x + radius + r.height + 2 * SPACE, SPACE + r.height - g2d.getFontMetrics().getHeight()/4);
    }

}
