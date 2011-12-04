package com.myeye.gameserver.core.domain;

import com.google.common.base.Preconditions;

public enum MessageType {

    UNKNOWN((short) (0)),
    INITIALIZE_GAME((short) (8)),
    ACTION((short) (16)),
    RESUME_GAME((short) (32)),
    PAUSE_GAME((short) (64)),
    TERMINATE_GAME((short) (128));

    public final Short code;

    private MessageType(Short code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + this.code + ")";
    }

    /**
     * @param code
     *            is the code to be transmitted.
     * @return The type of message.
     */
    public static MessageType fromCode(Short code) {
        Preconditions.checkArgument(Preconditions.checkNotNull(code) > 0, "The code must be a positive number.");
        for (MessageType type : MessageType.values()) {
            if (type.code.shortValue() == code.shortValue()) {
                return type;
            }
        }
        // never reached for value inputs.
        return UNKNOWN;
    }
}
