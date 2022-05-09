package skipbo.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static skipbo.client.SBClient.clientLog;
import static skipbo.server.Protocol.*;

/**
 * Handles user input by forwarding correct network protocol command to the Server
 */
class SBClientListener {
    Socket sock;
    PrintWriter pw;
    ChatGraphic chatGraphic;

    boolean usedJokerCheat = false;

    /**
     * Creates a Skip-Bo client listener
     *
     * @param sock A client socket
     * @throws IOException: If an I/O error occurs
     */
    SBClientListener(Socket sock) throws IOException {
        this.sock = sock;
        pw = new PrintWriter(sock.getOutputStream(), true);
    }

    /**
     * Forwards user input to server according to network protocol
     *
     * @param input Input from client
     * @throws NotACommandException If the input doesn't match any command
     */
    void forward(String input) throws NotACommandException {

        if (input.isEmpty()) {
            return;
        }
        String protocolString;

        //It's a chat message
        if (!(input.startsWith("/"))) {
            protocolString = CHATM + "§Global§" + input;
            pw.println(protocolString);
            return;
        }

        //It's not a chat message
        String[] command = input.split(" ", 2);

        switch (command[0].toLowerCase()) {
            case "/change":
                protocolString = getChangeString(input);
                break;
            case "/msg":
                protocolString = getPrivateMessageString(input);
                break;
            case "/broadcast":
                protocolString = getBroadcastString(input);
                break;
            case "/new":
                protocolString = getNewString(input);
                break;
            case "/play":
                protocolString = getPlayString(input);
                break;
            case "/list":
                protocolString = getListString(input);
                break;
            case "/cheat":
                protocolString = getCheatString(command);
                break;
            case "/quit":
                pw.println(LGOUT + "");
                //logOut();
                return;
            default:
                throw new NotACommandException("Please enter a valid command");
        }
        pw.println(protocolString);
    }

    /**
     * Builds network protocol string for the "change" command
     *
     * @param input Input from client
     * @return The network protocol string for the "change" command
     * @throws NotACommandException If the input doesn't match any command
     */
    String getChangeString(String input) throws NotACommandException {

        String[] line = input.split(" ", 3);

        if (line.length < 3) throw new NotACommandException("Please add an argument to your command");

        String option = line[1];
        String argument = line[2];

        if (option.equalsIgnoreCase("name")) {
            return CHNGE + "§Nickname§" + argument;

        } else if (option.equalsIgnoreCase("status")) {
            if (argument.equalsIgnoreCase("ready") || argument.equalsIgnoreCase("waiting")) {
                return CHNGE + "§Status§" + argument.toUpperCase();
            }
        }
        throw new NotACommandException("Please enter a valid command");
    }

    /**
     * Builds network protocol string for the "msg" command
     *
     * @param input Input from client
     * @return The network protocol string for the "msg" command
     * @throws NotACommandException If the input doesn't match any command
     */
    String getPrivateMessageString(String input) throws NotACommandException {
        String[] line = input.split(" ", 3);
        if (line.length < 3) {
            throw new NotACommandException("Please add a name and a message to your command");
        }
        String name = line[1];
        String message = line[2];
        return CHATM + "§Private§" + name + "§" + message;
    }

    /**
     * Builds network protocol string for the "broadcast" command
     *
     * @param input Input from client
     * @return The network protocol string for the "broadcast" command
     * @throws NotACommandException If the input doesn't match any command
     */
    String getBroadcastString(String input) throws NotACommandException {
        String[] line = input.split(" ", 2);
        if (line.length < 2) {
            throw new NotACommandException("Please add a message");
        }
        String message = line[1];
        return CHATM + "§Broadcast§" + message;
    }

    /**
     * Builds network protocol string for the "new" command
     *
     * @param input Input from client
     * @return The network protocol string for the "new" command
     * @throws NotACommandException If the input doesn't match any command
     */
    String getNewString(String input) throws NotACommandException {
        String[] line = input.split(" ", 2);
        if (line.length < 2) {
            throw new NotACommandException("Please enter a valid command");
        }
        if (line[1].equalsIgnoreCase("game")) {
            return NWGME + "§New";
        }
        throw new NotACommandException("Please enter a valid command");
    }

    /**
     * Builds network protocol string for the "play" command
     *
     * @param input Input from client
     * @return The network protocol string for the "play" command
     * @throws NotACommandException If the input doesn't match any command
     */
    String getPlayString(String input) throws NotACommandException {
        String[] line = input.split(" ", 5);
        if (line.length < 5) {
            throw new NotACommandException("Usage: /play [PlaceFrom] [n] [PlaceTo] [n]");
        }

        String placeFrom = line[1];
        String fromNumber = line[2];
        String placeTo = line[3];
        String toNumber = line[4];

        clientLog.debug("Command: " + PUTTO + "§Card§" + placeFrom + "§" + fromNumber + "§" + placeTo + "§" + toNumber);
        return PUTTO + "§Card§" + placeFrom + "§" + fromNumber + "§" + placeTo + "§" + toNumber;
    }

    /**
     * Builds network protocol string for the "list" command
     *
     * @param input Input from client
     * @return The network protocol string for the "list" command
     * @throws NotACommandException If the input doesn't match any command
     */
    String getListString(String input) throws NotACommandException {
        String[] line = input.split(" ", 2);
        if (line.length < 2) {
            throw new NotACommandException("Please add an option to your command");
        }
        String option = line[1].toLowerCase();
        if (!(option.equals("games") || option.equals("players"))) {
            throw new NotACommandException("Please add a valid option to your command");
        }
        return DISPL + "§" + option;
    }

    /**
     * Builds network protocol string for the "cheat" command and makes sure that the client can only cheat once
     * @param command Split input from client
     * @return The netwrok protocol string for the "cheat" command
     * @throws NotACommandException If the input doesn't match any command or the client cheated already
     */
    String getCheatString(String[] command) throws NotACommandException {
        if (chatGraphic.getGameGraphic() == null) {
            throw new NotACommandException("You can only use cheats in a game");
        }
        if (command.length < 2) {
            throw new NotACommandException("Please specify the cheat that you wan't to use");
        }
        if (command[1].equalsIgnoreCase("joker")) {
            if (usedJokerCheat) {
                throw new NotACommandException("You can only cheat once!");
            }
            usedJokerCheat = true;
            chatGraphic.getGameGraphic().cheatJoker(chatGraphic.getPlayerName());
            return CHEAT + "§Joker";
        } else if (command[1].equalsIgnoreCase("win")) {
            return CHEAT + "§Win";
        }
        throw new NotACommandException("Valid cheats: /cheat Joker || /cheat Win");
    }

    /**
     * Terminates SBClientListener thread
     */
    void logOut() {
        pw.close();
        try {
            sock.close();
        } catch (IOException e) {
            clientLog.warn("Issue with closing the socket");
        }
        clientLog.debug("logged out");
    }

    void setChatGraphic(ChatGraphic chatGraphic) {
        this.chatGraphic = chatGraphic;
    }

}