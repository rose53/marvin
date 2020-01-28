package de.rose53.marvin.platform.message;

import static de.rose53.marvin.platform.EMessageType.MEC_CURR;

import java.util.StringJoiner;

import de.rose53.marvin.platform.Message;

/**
 *
 * @author rose
 */
public class MECCurrentMessage extends Message {

    private final int fl;
    private final int rl;
    private final int fr;
    private final int rr;

    public MECCurrentMessage(int fl, int rl, int fr, int rr) {
        super(MEC_CURR, "","");
        this.fl = fl;
        this.rl = rl;
        this.fr = fr;
        this.rr = rr;
    }

    @Override
    protected String getDataString() {
        StringJoiner sj = new StringJoiner(",");

        sj.add(getMessageHeader())
          .add(Integer.toString(fl))
          .add(Integer.toString(rl))
          .add(Integer.toString(fr))
          .add(Integer.toString(rr));

        return sj.toString();
    }

    public short[] getCurrent() {
        short[] retVal = new short[4];

        retVal[0] = (short)fl;
        retVal[1] = (short)rl;
        retVal[2] = (short)fr;
        retVal[3] = (short)rr;

        return retVal;
    }
}
