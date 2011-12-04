package com.myeye.gameserver.core.domain;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Objects;

/**
 * The incoming message pojo for the class.
 * 
 * @author Marcello de Sales (marcello.desales@gmail.com)
 *
 * http://pragprog.com/articles/tell-dont-ask
 */
@Immutable
public class GameMessage {

    /**
     * The version of the incoming message.
     */
    public final MessageVersion version;
    /**
     * The message type
     */
    public final MessageType type;
    /**
     * The user ID in the message.
     */
    public final Integer userId;
    /**
     * The payload of the incoming message.
     */
    public final String payload;

    private GameMessage(MessageVersion version, MessageType messageType, Integer userId, String payload) {
        this.version = version;
        this.type = messageType;
        this.userId = userId;
        this.payload = payload;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GameMessage)) {
            return false;
        }
        GameMessage that = (GameMessage)obj;
        return Objects.equal(this.version, that.version) 
                && Objects.equal(this.type, that.type)
                && Objects.equal(this.userId, that.userId)
                && Objects.equal(this.payload, that.payload);
    }

    @Override
    public String toString() {
       return Objects.toStringHelper(this).add("Message Version", this.version).add("Message Type", this.type).
            add("UserID", this.userId).add("Payload", payload).toString();
    }

    public static GameMessage makeNew(MessageVersion version, MessageType messageType, Integer userId, String payload) {
        return new GameMessage(version, messageType, userId, payload);
    }
}
