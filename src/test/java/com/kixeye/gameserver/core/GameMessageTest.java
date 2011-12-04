package com.kixeye.gameserver.core;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.Test;

import com.myeye.gameserver.core.domain.GameMessage;
import com.myeye.gameserver.core.domain.MessageType;
import com.myeye.gameserver.core.domain.MessageVersion;
import com.myeye.gameserver.core.service.GameMessageService;

public class GameMessageTest {

    protected Logger LOGGER = Logger.getLogger(getClass().getName());

    @Test
    public void testGameMessageCreation() {
        MessageVersion version = MessageVersion.ONE;
        MessageType msgType = MessageType.RESUME_GAME;
        Integer userId = Integer.MAX_VALUE - 5;
        String payloadValue = "This is the game payload";

        GameMessage outGoingMsg = GameMessageService.create(version, msgType, userId, payloadValue);
        LOGGER.info(outGoingMsg.toString());

        assertEquals("The version is incorrect", version, outGoingMsg.version);
        assertEquals("The message type is incorrect", msgType, outGoingMsg.type);
        assertEquals("The user ID is incorrect", userId, outGoingMsg.userId);
        assertEquals("The payload is incorrect", payloadValue, outGoingMsg.payload);
    }

    @Test
    public void testGameMessageMarshallUnmarshall() {
        MessageVersion version = MessageVersion.TWO;
        MessageType msgType = MessageType.PAUSE_GAME;
        Integer userId = Integer.MAX_VALUE - 234;
        String payloadValue = "Super game will be paused for now...";

        GameMessage outgoingMsg = GameMessageService.create(version, msgType, userId, payloadValue);
        LOGGER.info("OUT: " + outgoingMsg.toString());

        byte[] binaryMessage = GameMessageService.marshall(outgoingMsg);
        LOGGER.info("BINARY: " + Arrays.toString(binaryMessage));

        GameMessage incomingMsg = GameMessageService.unmarshall(binaryMessage);
        LOGGER.info("IN: " + incomingMsg.toString());

        assertEquals("The outgoing version is incorrect", version, outgoingMsg.version);
        assertEquals("The incoming version is incorrect", version, incomingMsg.version);
        assertEquals("The outgoing message type is incorrect", msgType, outgoingMsg.type);
        assertEquals("The incoming message type is incorrect", msgType, incomingMsg.type);
        assertEquals("The outgoing user ID is incorrect", userId, outgoingMsg.userId);
        assertEquals("The incoming user ID is incorrect", userId, incomingMsg.userId);
        assertEquals("The outgoing payload is incorrect", payloadValue, outgoingMsg.payload);
        assertEquals("The incoming payload is incorrect", payloadValue, incomingMsg.payload);
    }
}
