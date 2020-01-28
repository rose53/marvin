package de.rose53.marvin.platform.message;

import de.rose53.marvin.platform.EMessageType;
import de.rose53.marvin.platform.Message;

public class CloseMessage extends Message {

    public static final CloseMessage CLOSE = new CloseMessage();

    private CloseMessage() {
        super(EMessageType.CLOSE,"");
    }

    @Override
    protected String getDataString() {
        return getMessageHeader();
    }

}
