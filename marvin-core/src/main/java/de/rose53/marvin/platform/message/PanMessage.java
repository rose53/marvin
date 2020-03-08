package de.rose53.marvin.platform.message;

import java.util.StringJoiner;

import de.rose53.marvin.platform.EMessageType;
import de.rose53.marvin.platform.Message;

/**
 *
 * @author rose
 */
public class PanMessage extends Message {

    private final short pan;

    public PanMessage(short pan) {
        super(EMessageType.PAN, "","");
        this.pan = pan;
    }

    @Override
    protected String getDataString() {
        StringJoiner sj = new StringJoiner(",");

        sj.add(getMessageHeader())
          .add(Short.toString(pan));

        return sj.toString();
   }
}
