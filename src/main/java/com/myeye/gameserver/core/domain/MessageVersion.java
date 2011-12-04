package com.myeye.gameserver.core.domain;

public enum MessageVersion {

    ONE((byte)1),
    TWO((byte)2);

    public final Byte code;

    private MessageVersion(Byte code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + this.code + ")";
    }

    public static MessageVersion fromCode(Byte code) {
        for (MessageVersion v : MessageVersion.values()) {
            if (v.code.byteValue() == code.byteValue()) {
                return v;
            }
        }
        return ONE;
    }
}
