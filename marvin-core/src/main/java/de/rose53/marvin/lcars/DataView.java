package de.rose53.marvin.lcars;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.util.HashMap;
import java.util.Map;

import de.rose53.marvin.Distance.Place;

public class DataView extends LcarsComponent {

    private Map<Place, LabeledInfoButton> distansLabeledInfoButtorns = new HashMap<>();
    private LabeledInfoButton             headerLabeledInfoButton = new LabeledInfoButton("HEADING",LabeledInfoButton.BUTTON_COLOR2);

    public DataView() {
        super();

        distansLabeledInfoButtorns.put(Place.FRONT, new LabeledInfoButton("FRONT", LabeledInfoButton.BUTTON_COLOR1));
        distansLabeledInfoButtorns.put(Place.LEFT, new LabeledInfoButton("LEFT", LabeledInfoButton.BUTTON_COLOR1));
        distansLabeledInfoButtorns.put(Place.RIGHT, new LabeledInfoButton("RIGHT", LabeledInfoButton.BUTTON_COLOR1));
        distansLabeledInfoButtorns.put(Place.BACK, new LabeledInfoButton("BACK", LabeledInfoButton.BUTTON_COLOR1));
    }

    public void setDistance(float distance, Place place) {
        distansLabeledInfoButtorns.get(place).setInfo(Integer.toString((int)distance));
    }

    public void setHeading(float heading) {
        headerLabeledInfoButton.setInfo(Integer.toString((int)heading));
    }

    public void setPanTilt(short pan, short tilt) {

    }

    @Override
    public void draw(Graphics2D g2d, Rectangle r) {

        g2d.setPaint(FRAME_COLOR);
        GeneralPath path = new GeneralPath();

        path.moveTo(r.x + r.width, r.y);

        path.lineTo(r.x + FRAME_LARGE_SIZE,r.y);
        path.quadTo(r.x ,r.y,r.x,r.y  + BASE_BUTTON_HEIGHT);
        path.lineTo(r.x,r.y + r.height - BASE_BUTTON_HEIGHT);
        path.quadTo(r.x ,r.y + r.height,r.x + BASE_BUTTON_HEIGHT,r.y  + r.height);
        path.lineTo(r.x + r.width, r.y + r.height);
        path.lineTo(r.x + r.width, r.y + r.height - FRAME_SMALL_SIZE);
        path.lineTo(r.x + FRAME_LARGE_SIZE + BASE_BUTTON_HEIGHT, r.y + r.height - FRAME_SMALL_SIZE);
        path.quadTo(r.x + FRAME_LARGE_SIZE,r.y + r.height - FRAME_SMALL_SIZE,r.x + FRAME_LARGE_SIZE,r.y + r.height - BASE_BUTTON_HEIGHT);
        path.lineTo(r.x + FRAME_LARGE_SIZE,r.y + BASE_BUTTON_HEIGHT + FRAME_SMALL_SIZE);
        path.quadTo(r.x + FRAME_LARGE_SIZE,r.y + FRAME_SMALL_SIZE,r.x + BASE_BUTTON_HEIGHT + FRAME_LARGE_SIZE,r.y + + FRAME_SMALL_SIZE);
        path.lineTo(r.x + r.width,r.y + FRAME_SMALL_SIZE);

        g2d.fill(path);

        Rectangle distanceFrontRect = new Rectangle(r.x + FRAME_LARGE_SIZE + SPACE,r.y + (FRAME_SMALL_SIZE + BUTTON_LABEL_HEIGHT + SPACE),-1,-1);
        Rectangle distanceLeftRect  = new Rectangle(distanceFrontRect.x + SPACE + LabeledInfoButton.getLabeledInfoButtonWidth(),distanceFrontRect.y ,-1,-1);
        Rectangle distanceRightRect = new Rectangle(distanceLeftRect.x + SPACE + LabeledInfoButton.getLabeledInfoButtonWidth(),distanceLeftRect.y ,-1,-1);
        Rectangle distanceBackRect  = new Rectangle(distanceRightRect.x + SPACE + LabeledInfoButton.getLabeledInfoButtonWidth(),distanceRightRect.y ,-1,-1);

        Rectangle headingRect = new Rectangle(r.x + FRAME_LARGE_SIZE + SPACE,distanceFrontRect.y + BUTTON_LABEL_HEIGHT + SPACE,-1,-1);

        distansLabeledInfoButtorns.get(Place.FRONT).draw(g2d,distanceFrontRect);
        distansLabeledInfoButtorns.get(Place.LEFT).draw(g2d,distanceLeftRect);
        distansLabeledInfoButtorns.get(Place.RIGHT).draw(g2d,distanceRightRect);
        distansLabeledInfoButtorns.get(Place.BACK).draw(g2d,distanceBackRect);

        headerLabeledInfoButton.draw(g2d, headingRect);
    }



}
