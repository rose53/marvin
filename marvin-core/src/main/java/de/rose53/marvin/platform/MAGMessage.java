package de.rose53.marvin.platform;

/**
 *
 * @author rose
 */
public class MAGMessage extends Message {

    private final float heading;

    public MAGMessage(String messageId, float heading) {
        super(EMessageType.MAG,messageId);
        this.heading = heading;
    }

    public float getHeading() {
        return heading;
    }

    @Override
    protected String getDataString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
