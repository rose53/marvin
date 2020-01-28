package de.rose53.marvin.platform.message;

import de.rose53.marvin.platform.EMessageType;
import de.rose53.marvin.platform.Message;

/**
 *
 * @author rose
 */
public class USMessage extends Message {

    private final int distance;

    public USMessage(String messageId, int distance) {
        super(EMessageType.US,messageId);
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }


    @Override
    protected String getDataString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
