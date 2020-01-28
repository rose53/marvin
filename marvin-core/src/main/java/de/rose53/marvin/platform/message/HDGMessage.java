package de.rose53.marvin.platform.message;

import java.util.StringJoiner;

import de.rose53.marvin.platform.EMessageType;
import de.rose53.marvin.platform.Message;

/**
 *
 * @author rose
 */
public class HDGMessage extends Message {

    private final float heading;

    public HDGMessage(String messageId, float heading) {
        super(EMessageType.HDG,messageId);
        this.heading = heading;
    }

    public float getHeading() {
        return heading;
    }

    @Override
    protected String getDataString() {
        StringJoiner sj = new StringJoiner(",");

        sj.add(getMessageHeader())
          .add(Float.toString(getHeading()));

        return sj.toString();
    }

}
