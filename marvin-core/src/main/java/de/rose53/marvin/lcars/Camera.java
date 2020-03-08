package de.rose53.marvin.lcars;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

public class Camera extends LcarsComponent {

    @Override
    public void draw(Graphics2D g2d, Rectangle r) {

        g2d.setPaint(FRAME_COLOR);
        GeneralPath path = new GeneralPath();

        path.moveTo(r.x, r.y);
        path.lineTo(r.x,r.y + r.height - (FRAME_SMALL_SIZE + BASE_BUTTON_HEIGHT));
        path.quadTo(r.x ,r.y + r.height,r.x + FRAME_LARGE_SIZE,r.y + r.height);
        path.lineTo(r.x +r.width,r.y + r.height);
        path.lineTo(r.x +r.width,r.y + r.height - FRAME_SMALL_SIZE);
        path.lineTo(r.x  + FRAME_LARGE_SIZE + BASE_BUTTON_HEIGHT, r.y + r.height - FRAME_SMALL_SIZE);
        path.quadTo(r.x + FRAME_LARGE_SIZE,r.y + r.height - FRAME_SMALL_SIZE,
                    r.x + FRAME_LARGE_SIZE,r.y + r.height - BASE_BUTTON_HEIGHT);
        path.lineTo(r.x + FRAME_LARGE_SIZE,r.y);

        g2d.fill(path);
    }

}
