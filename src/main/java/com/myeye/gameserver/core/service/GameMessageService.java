package com.myeye.gameserver.core.service;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.ByteBuffer;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.myeye.gameserver.core.domain.GameMessage;
import com.myeye.gameserver.core.domain.MessageType;
import com.myeye.gameserver.core.domain.MessageVersion;

public enum GameMessageService {

    ;

    /**
     * @param outgoingMessage is the message to be sent.
     * @return the mashalled state of the service in the following format.
     * [Version (1 byte), Message Type (2 byte integer), User ID (4 byte integer), Payload (ASCII string length)]
     */
    public static byte[] marshall(GameMessage outgoingMessage) {
        byte[] payloadBytes = outgoingMessage.payload.getBytes();

        ByteBuffer bb = ByteBuffer.allocate(1 + 2 + 4 + payloadBytes.length);
        bb.put(outgoingMessage.version.code); // 1 byte
        bb.putShort(outgoingMessage.type.code); // 2 bytes
        bb.putInt(outgoingMessage.userId); //4 bytes
        bb.put(payloadBytes); // payloadBytes.length bytes

        return bb.array();
    }

    /**
     * @param marshalledMessage is the serialized incoming message with the following
     * data: [Version (1 byte), Message Type (2 byte integer), User ID (4 byte integer), 
     * Payload (variable length ASCII string)]
     */
    public static GameMessage unmarshall(byte[] marshalledMessage) {
        Preconditions.checkArgument(marshalledMessage != null, "The message must be provided.");
        Preconditions.checkArgument(marshalledMessage.length > 8, "The message must contain at least the header.");

        ByteBuffer reader = ByteBuffer.wrap(marshalledMessage);
        MessageVersion version = MessageVersion.fromCode(reader.get());
        MessageType messageType = MessageType.fromCode(reader.getShort());
        Integer userId = reader.getInt();
        String payload = new String(reader.array(), reader.position(), reader.remaining());

        return create(version, messageType, userId, payload);
    }

    /**
     * Creates a GameMessage based on the given types.
     * @param version the version of the game.
     * @param messageType the message type.
     * @param userId the user identification.
     * @param payload the payload to be sent.
     * @return an instance of the immutable GameMessage to be transmitted.
     */
    public static GameMessage create(MessageVersion version, MessageType messageType, Integer userId, String payload) {
        Preconditions.checkNotNull(version, "The version number must be provided.");
        Preconditions.checkNotNull(messageType, "The message type number must be provided.");

        Preconditions.checkNotNull(userId, "The user ID must be provided.");
        Preconditions.checkArgument(userId > 0, "The user ID must be greater than 0.");

        Preconditions.checkNotNull(payload, "The payload must be provided.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(payload), "The payload must be not empty.");

        return GameMessage.makeNew(version, messageType, userId, payload);
    }

    /**
     * The method to log the incoming message to the standard out.
     * @param incomingMessage the message to be logged.
     * @param out is the output stream to output the message.
     */
    public static void log(GameMessage gameMesssage, PrintStream out) {
        System.out.println("Logging message from " + Thread.currentThread().getName());
        out.println("");
        out.println(gameMesssage);
        out.println("");
    }
}
