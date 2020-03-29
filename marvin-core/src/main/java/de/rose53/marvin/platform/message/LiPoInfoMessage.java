package de.rose53.marvin.platform.message;

import de.rose53.marvin.platform.Message;
import de.rose53.marvin.utils.LiPoStatus;

import java.util.StringJoiner;

import static de.rose53.marvin.platform.EMessageType.LIPO_INFO;

/**
 *
 * @author rose
 */
public class LiPoInfoMessage extends Message {

    private final float voltageCell1;
    private final float voltageCell2;

    public LiPoInfoMessage(String messageId,float voltageCell1, float voltageCell2) {
        super(LIPO_INFO, messageId);
        this.voltageCell1 = voltageCell1;
        this.voltageCell2 = voltageCell2;
    }
    @Override
    protected String getDataString() {
        StringJoiner sj = new StringJoiner(",");

        sj.add(getMessageHeader())
          .add(Float.toString(voltageCell1))
          .add(Float.toString(voltageCell2));

        return sj.toString();
    }

    public LiPoStatus getLiPoStatus() {
        return new LiPoStatus(voltageCell1, voltageCell2);
    }
}
