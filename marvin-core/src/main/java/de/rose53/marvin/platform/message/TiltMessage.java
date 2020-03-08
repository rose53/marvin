package de.rose53.marvin.platform.message;

import java.util.StringJoiner;

import de.rose53.marvin.platform.EMessageType;
import de.rose53.marvin.platform.Message;

/**
 *
 * @author rose
 */
public class TiltMessage extends Message {

    private final short tilt;

    public TiltMessage(short tilt) {
        super(EMessageType.TILT, "","");
        this.tilt = tilt;
    }

    @Override
    protected String getDataString() {
        StringJoiner sj = new StringJoiner(",");

        sj.add(getMessageHeader())
          .add(Short.toString(tilt));

        return sj.toString();
   }
}
