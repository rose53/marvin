package de.rose53.marvin.platform.message;

import de.rose53.marvin.platform.EMessageType;
import de.rose53.marvin.platform.Message;

public class GetMessage extends Message {

    public GetMessage(EMessageType messageType) {
        this(messageType, "");
    }

    public GetMessage(EMessageType messageType, String messageId) {
        super(messageType, messageId,Long.toHexString(System.currentTimeMillis()));
    }

    @Override
    protected String getDataString() {
        return getMessageHeader();
    }

}
