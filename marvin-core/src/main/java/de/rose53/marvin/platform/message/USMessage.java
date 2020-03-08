package de.rose53.marvin.platform.message;

import de.rose53.marvin.Distance.Place;
import de.rose53.marvin.platform.EMessageType;
import de.rose53.marvin.platform.Message;
import de.rose53.marvin.utils.StringUtils;

/**
 *
 * @author rose
 */
public class USMessage extends Message {

    private final float distance;

    public USMessage(String messageId, float distance) {
        super(EMessageType.US,messageId);
        this.distance = distance;
    }

    public Place getPlace() {
        if (StringUtils.isEmpty(getMessageId())) {
            return null;
        }
        return Place.fromString(getMessageId());
    }

    public float getDistance() {
        return distance;
    }


    @Override
    protected String getDataString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
