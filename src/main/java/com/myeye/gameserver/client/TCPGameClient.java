package com.myeye.gameserver.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.Random;
import java.util.Scanner;

import com.google.common.base.Preconditions;
import com.myeye.gameserver.core.domain.GameMessage;
import com.myeye.gameserver.core.domain.MessageType;
import com.myeye.gameserver.core.domain.MessageVersion;
import com.myeye.gameserver.core.service.GameMessageService;
import com.myeye.gameserver.server.TCPGameServer;

/**
 * This is the Game client.
 * 
 * @author Marcello de Sales (marcello.desales@gmail.com)
 *
 */
public class TCPGameClient {

    /**
     * Default version of the game.
     */
    private MessageVersion defaultVersion = MessageVersion.ONE;
    /**
     * The current message type sent to the server.
     */
    private MessageType currentType;
    /**
     * The user identification for the client.
     */
    private final Integer userId;

    /**
     * Creates a new client with the given user ID.
     * @param userId is the user identification.
     * @throws UnknownHostException if the host can't be found.
     * @throws IOException
     */
    public TCPGameClient(Integer userId) throws UnknownHostException, IOException {
        this.userId = userId;
    }

    /**
     * Sets the version of the game.
     * @param version is the version selected.
     */
    public void setVersion(MessageVersion version) {
        this.defaultVersion = version;
    }

    /**
     * Makes a new {@link GameMessage} object with the given type and payload.
     * @param type is the type of the message.
     * @param payload is the payload content.
     * @return a new instance of {@link GameMessage}.
     */
    public GameMessage makeMessage(MessageType type, String payload) {
        switch(type) {
        case INITIALIZE_GAME:
        case PAUSE_GAME:
        case RESUME_GAME:
        case TERMINATE_GAME:
            payload = type.name() + " at " + new Date();
            break;
        }
        return GameMessageService.create(this.defaultVersion, type, this.userId, payload);
    }

    /**
     * Creates messages with the default value of the payload with the type name
     * and current time.
     * @param type is the type of message.
     * @return a new instance of {@link GameMessage}.
     */
    public GameMessage makeMessage(MessageType type) {
        Preconditions.checkArgument(Preconditions.checkNotNull(type) != MessageType.ACTION, "No payload for ACTION?");
        return makeMessage(type, "");
    }

    /**
     * @param type is the type of message.
     * @return The state machine for the given message.
     */
    public EnumSet<MessageType> getPossibleMessages(MessageType type) {
        if (type == null) {
            return EnumSet.of(MessageType.INITIALIZE_GAME);
        }
        EnumSet<MessageType> possibleMessages = EnumSet.noneOf(MessageType.class);
        switch(type) {
        case INITIALIZE_GAME:
        case ACTION:
        case RESUME_GAME:
            possibleMessages = EnumSet.of(MessageType.ACTION, MessageType.PAUSE_GAME, MessageType.TERMINATE_GAME);
            break;

        case PAUSE_GAME:
            possibleMessages = EnumSet.of(MessageType.RESUME_GAME, MessageType.TERMINATE_GAME);
            break;

        default:
            // no changes here as the default is none.
            break;
        }
        return possibleMessages;
    }

    /**
     * Sends the message to the server.
     * @param message is an instance of the game message.
     * @throws IOException if any communication problem occurs.
     */
    public void sendMessage(GameMessage message) throws IOException {

        String serverHost = TCPGameServer.getCurrentEnvironmentNetworkIp();
        int port = TCPGameServer.SERVER_PORT;
        Socket clientSocket = new Socket(serverHost, port);
        OutputStream outputStream = clientSocket.getOutputStream();

        byte[] binaryMessage = GameMessageService.marshall(message);

        System.out.println("BINARY: " + Arrays.toString(binaryMessage));

        // send the binary message
        outputStream.write(binaryMessage);
        outputStream.close();
        clientSocket.close();

        this.currentType = message.type;
    }

    public static void main(String[] args) throws IOException {
        Integer userId = new Random().nextInt(Integer.MAX_VALUE);
        TCPGameClient client = new TCPGameClient(userId);

        final String serverHost = TCPGameServer.getCurrentEnvironmentNetworkIp();
        int port = TCPGameServer.SERVER_PORT;

        System.out.println("MYEYE Game Client running, will connect to " + serverHost + ":" + port);
        System.out.println("Developed by Marcello de Sales (marcello.desales@gmail.com)");

        clientEngine: while(true) {
            Scanner input = new Scanner(System.in);
            System.out.println();
            System.out.println("##### Game Session Information ####");
            System.out.println("Your User ID is '" + userId + "'");
            System.out.println();
            System.out.println("### Game versions ###");
            for(MessageVersion v : MessageVersion.values()) {
                System.out.println(v);
            }
            System.out.print("-> What version would you like to play? ");
            Byte versionRequest = (byte)input.nextInt();
            client.setVersion(MessageVersion.fromCode(versionRequest));

            while (client.currentType != MessageType.TERMINATE_GAME) {
                EnumSet<MessageType> possibleMsgs = client.getPossibleMessages(client.currentType);
                if (possibleMsgs.contains(MessageType.INITIALIZE_GAME)) {
                    GameMessage message = client.makeMessage(MessageType.INITIALIZE_GAME);
                    System.out.println("");
                    System.out.println("##### Sending Message #####");
                    System.out.println("Sending " + message);
                    client.sendMessage(message);
                    System.out.println("Sent!");
                    System.out.println("");
                    continue;
                }
                System.out.println("");
                System.out.println("##### Game Options ####");
                for(MessageType t : possibleMsgs) {
                    System.out.println(t);
                }
                System.out.print("-> What do you want to do? ");
                Short typeRequestCode = (short)input.nextInt();
                MessageType typeRequest = MessageType.fromCode(typeRequestCode);

                if (typeRequest == MessageType.UNKNOWN) {
                    System.out.println("");
                    System.out.println("#### Message type unknown ###");
                    System.out.println("Choose from the available options");
                    System.out.println("");
                    continue;
                }
                GameMessage message = null;
                switch(typeRequest) {
                case PAUSE_GAME:
                case RESUME_GAME:
                case TERMINATE_GAME:
                    message = client.makeMessage(typeRequest);
                    break;

                case ACTION:
                    System.out.println("-> What would you like to say? ");
                    input = new Scanner(System.in);
                    String payload = input.nextLine();
                    message = client.makeMessage(typeRequest, payload);
                    break;
                }
                System.out.println("");
                System.out.println("##### Sending Message #####");
                System.out.println("Sending " + message);
                client.sendMessage(message);
                System.out.println("Sent!");
                System.out.println("");
            }
            System.out.println("");
            System.out.println("Game terminated! Good bye!");
            break clientEngine;
        }
    }
}
