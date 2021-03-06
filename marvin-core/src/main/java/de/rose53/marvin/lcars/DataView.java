package de.rose53.marvin.lcars;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import de.rose53.marvin.Distance.Place;
import de.rose53.marvin.utils.LiPoStatus;

public class DataView extends LcarsComponent {

    private Map<Place, LabeledInfoButton> distansLabeledInfoButtorns = new HashMap<>();
    private LabeledInfoButton             headingLabeledInfoButton = new LabeledInfoButton("HEADING",LabeledInfoButton.BUTTON_COLOR2);
    private LabeledInfoButton             panLabeledInfoButton = new LabeledInfoButton("PAN",LabeledInfoButton.BUTTON_COLOR1);
    private LabeledInfoButton             tiltLabeledInfoButton = new LabeledInfoButton("TILT",LabeledInfoButton.BUTTON_COLOR1);
    private LabeledInfoButton             cell1LabeledInfoButton = new LabeledInfoButton("CELL1",LabeledInfoButton.BUTTON_COLOR2);
    private LabeledInfoButton             cell2LabeledInfoButton = new LabeledInfoButton("CELL2",LabeledInfoButton.BUTTON_COLOR2);
    private LabeledInfoButton             batteryLabeledInfoButton = new LabeledInfoButton("CHARGE",LabeledInfoButton.BUTTON_COLOR2);

    DecimalFormat f = new DecimalFormat("#0.00");

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
        headingLabeledInfoButton.setInfo(Integer.toString((int)heading));
    }

    public void setPanTilt(short pan, short tilt) {
        panLabeledInfoButton.setInfo(Short.toString(pan));
        tiltLabeledInfoButton.setInfo(Short.toString(tilt));
    }

    public void setLiPoStatus(LiPoStatus liPoStatus) {
        cell1LabeledInfoButton.setInfo(f.format(liPoStatus.getVoltageCell1()));
        cell2LabeledInfoButton.setInfo(f.format(liPoStatus.getVoltageCell2()));
        batteryLabeledInfoButton.setInfo(Integer.toString(liPoStatus.getStateOfCharge()));
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

        Rectangle panRect  = new Rectangle(r.x + FRAME_LARGE_SIZE + SPACE,headingRect.y + BUTTON_LABEL_HEIGHT + SPACE,-1,-1);
        Rectangle tiltRect = new Rectangle(panRect.x + SPACE + LabeledInfoButton.getLabeledInfoButtonWidth(),panRect.y ,-1,-1);

        Rectangle cell1Rect = new Rectangle(r.x + FRAME_LARGE_SIZE + SPACE,panRect.y + BUTTON_LABEL_HEIGHT + SPACE,-1,-1);
        Rectangle cell2Rect  = new Rectangle(cell1Rect.x + SPACE + LabeledInfoButton.getLabeledInfoButtonWidth(),cell1Rect.y ,-1,-1);
        Rectangle batteryRect = new Rectangle(cell2Rect.x + SPACE + LabeledInfoButton.getLabeledInfoButtonWidth(),cell2Rect.y ,-1,-1);

        distansLabeledInfoButtorns.get(Place.FRONT).draw(g2d,distanceFrontRect);
        distansLabeledInfoButtorns.get(Place.LEFT).draw(g2d,distanceLeftRect);
        distansLabeledInfoButtorns.get(Place.RIGHT).draw(g2d,distanceRightRect);
        distansLabeledInfoButtorns.get(Place.BACK).draw(g2d,distanceBackRect);

        headingLabeledInfoButton.draw(g2d, headingRect);
        panLabeledInfoButton.draw(g2d, panRect);
        tiltLabeledInfoButton.draw(g2d, tiltRect);

        cell1LabeledInfoButton.draw(g2d,cell1Rect);
        cell2LabeledInfoButton.draw(g2d,cell2Rect);
        batteryLabeledInfoButton.draw(g2d,batteryRect);
    }
}
