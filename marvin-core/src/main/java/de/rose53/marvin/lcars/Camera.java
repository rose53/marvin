package de.rose53.marvin.lcars;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Camera extends LcarsComponent {

    private BufferedImage image = null;


    public void setImage(BufferedImage image) {
        this.image = image;
    }

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

        Rectangle imageRect = new Rectangle(r.x + FRAME_LARGE_SIZE + FRAME_SMALL_SIZE+ SPACE,r.y + SPACE, r.width - FRAME_LARGE_SIZE - FRAME_SMALL_SIZE - 2 * SPACE,r.height - FRAME_SMALL_SIZE - 2 * SPACE);

        if (image != null) {

            double scaleX = (double)(imageRect.width - 2 * SPACE) / image.getWidth();
            double scaleY = (double)(imageRect.height - 2 * SPACE) / image.getHeight();

            double scale = Math.min(scaleX, scaleY);

            double scaledWidth  = scale * image.getWidth();
            double scaledHeight = scale * image.getHeight();



            AffineTransform at = new AffineTransform();
            at.scale(scale, scale);
            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

            g2d.drawImage(image, scaleOp, (int) (imageRect.x + SPACE + (imageRect.width - scaledWidth) / 2), (int) (imageRect.y + SPACE + (imageRect.height - scaledHeight) / 2));
        }

        g2d.setPaint(Color.decode("#006699"));

        double tick = imageRect.getWidth() % 20;

        int posx = (int) imageRect.getCenterX();
        int i = 0;
        while (i * tick + posx < imageRect.getX() + imageRect.getWidth()) {
            g2d.drawLine((int)(i * tick + posx), (int)imageRect.getY(), (int)(i * tick + posx), (int)(imageRect.getY() + imageRect.getHeight()));
            g2d.drawLine((int)(-i * tick + posx), (int)imageRect.getY(), (int)(-i * tick + posx), (int)(imageRect.getY() + imageRect.getHeight()));
            i++;
        }
        tick = imageRect.getHeight() % 20;
        i = 0;
        int posy = (int) imageRect.getCenterY();
        while (i * tick + posy < imageRect.getY() + imageRect.getHeight()) {
            g2d.drawLine((int)imageRect.getX(), (int)(i * tick + posy), (int)(imageRect.getX() + imageRect.getWidth()),(int)(i * tick + posy));
            g2d.drawLine((int)imageRect.getX(), (int)(-i * tick + posy), (int)(imageRect.getX() + imageRect.getWidth()),(int)(-i * tick + posy));
            i++;
        }

    }

}
