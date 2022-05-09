package skipbo.client;

import skipbo.server.NoCommandException;
import skipbo.server.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static skipbo.client.SBClient.clientLog;

/**
 * Thread waiting for any input from server and executes the input on client
 */
public class SBServerListener implements Runnable {
    Socket socket;
    BufferedReader br;
    ChatGraphic chatGraphic;
    Boolean isLoggedIn = true;
    String input;

    SBServerListener(Socket socket, ChatGraphic chatGraphic) throws IOException {
        this.socket = socket;
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.chatGraphic = chatGraphic;
        this.input = null;
    }

    /**
     * Constantly reads input from server. Forwards input to executeCommand method which executes the input on client
     */
    @Override
    public void run() {

        while (isLoggedIn) {
            try {
                input = br.readLine();
                clientLog.debug(input);
                executeCommand(input);
            } catch (IOException e) {
                clientLog.warn("Error with reading input from server");
            } catch (NoCommandException e) {
                clientLog.warn("Error with network protocol command (NoCommand)");
            } catch (IllegalArgumentException e) {
                clientLog.warn("Error with network protocol command (IllegalArgument)");
            }
        }
    }

    /**
     * Executes commands coming from the server according to network protocol
     *
     * @param commandLine Network protocol string from server
     * @throws NoCommandException If commandLine string doesn't match network protocol
     */
    void executeCommand(String commandLine) throws NoCommandException {
        String[] command = commandLine.split("§", 3);
        Protocol protocol = Protocol.valueOf(command[0]);

        switch (protocol) {
            case CHATM:
                chatGraphic.printChatMessage(command[2]);
                break;
            case CHNGE:
            case SETTO:
                chatGraphic.changeOwnName(command[2]);
                break;
            case PUTTO:
                putTo(command);
                break;
            case LGOUT:
                logOut();
                break;
            case PRINT:
                chatGraphic.printInfoMessage(command[2]);
                break;
            case NWGME:
                newGame(command);
                break;
            case CHECK:
                check(command);
                break;
            case PLAYR:
                player(command);
                break;
            case DISPL:
                chatGraphic.setHighScore(command[2].split("§"));
                break;
            case CHEAT:
                chatGraphic.getGameGraphic().cheatJoker(command[2]);
                break;
            case ENDGM:
                endGame(command);
                break;
            default:
                throw new NoCommandException();
        }
    }


    void putTo(String[] command) {
        clientLog.debug("got into putTo with command " + command[1]);
        String[] argument = command[2].split("§");
        if (command[1].equals("Response")) {
            if (argument[0].equals("H")) {
                if (argument[2].equals("B")) {
                    chatGraphic.getGameGraphic().handToBuild(Integer.parseInt(argument[1]),
                            Integer.parseInt(argument[3]), argument[4], "", -1);
                } else { //2nd pile must be Discard
                    chatGraphic.getGameGraphic().handToDiscard(Integer.parseInt(argument[1]),
                            Integer.parseInt(argument[3]), argument[4], "", -1);
                }
            } else if (argument[0].equals("D")) { //pile must be Discard
                chatGraphic.getGameGraphic().discardToBuild(Integer.parseInt(argument[1]),
                        Integer.parseInt(argument[3]), argument[4]);
            }
        } else if (command[1].equals("StockResponse")) {
            chatGraphic.getGameGraphic().stockToBuild(Integer.parseInt(argument[3]), argument[4], argument[5],
                    Integer.parseInt(argument[6]), "", -1);
        } else { //must be Update
            if (argument.length == 3) { //Discard to build
                clientLog.debug("updated discard to build");
                chatGraphic.getGameGraphic().discardToBuild(Integer.parseInt(argument[0]), Integer.parseInt(argument[1]),
                        argument[2]);
            } else if (argument[0].equals("D")) { //Hand to discard
                clientLog.debug("updated hand to discard");
                chatGraphic.getGameGraphic().handToDiscard(Integer.parseInt(argument[1]), Integer.parseInt(argument[2]),
                        argument[3], argument[4], Integer.parseInt(argument[5]));
            } else if (argument[0].equals("B")) { //Hand to build
                clientLog.debug("updated hand to build");
                chatGraphic.getGameGraphic().handToBuild(Integer.parseInt(argument[1]), Integer.parseInt(argument[2]),
                        argument[3], argument[4], Integer.parseInt(argument[5]));
            } else { //Must be stock to build
                clientLog.debug("updated stock to build");
                chatGraphic.getGameGraphic().stockToBuild(Integer.parseInt(argument[1]), argument[2], argument[3],
                        Integer.parseInt(argument[4]), argument[5], Integer.parseInt(argument[6]));
            }
        }
    }

    /**
     * Handels the NWGME command
     * @param command Input from server split into command, option and argument
     */
    void newGame(String[] command) {
        if (command[1].equals("Names")) {
            chatGraphic.setGameGraphic(false);
            String[] names = command[2].split("§",2);
            chatGraphic.getGameGraphic().setOpponentNames(names[1].split("§"));
            chatGraphic.getGameGraphic().setStockSize(Integer.parseInt(names[0]));
        } else if (command[1].equals("Cards")) {
            String[] cards = command[2].split("§");
            chatGraphic.getGameGraphic().setInitialCards(cards);
        }
    }

    /**
     * Handels the ENDGM command
     * @param command Input from server split into command, option and argument
     */
    private void endGame(String[] command) {
        if (command.length < 3) { //No winner
            chatGraphic.endGame(null, false, null);
        } else { //winner
            String[] s = command[2].split("§", 2);
            chatGraphic.endGame(s[0], false, s[1]);
        }
    }

    /**
     * Handels the CHECK command
     * @param command Input from server split into command, option and argument
     */
    private void check(String[] command) {
        clientLog.debug("got into check method");
        if (command[1].equalsIgnoreCase("HandCards")) {
            String[] cards = command[2].split("§");
            String[] colours = new String[cards.length / 2];
            int[] numbers = new int[colours.length];
            for (int i = 0, j = 0; i < colours.length; i++) {
                colours[i] = cards[j++];
                numbers[i] = Integer.parseInt(cards[j++]);
            }
            chatGraphic.getGameGraphic().updateHandCards(colours, numbers);
        }
    }

    /**
     * Handels the PLAYR command
     * @param command Input from server split into command, option and argument
     */
    private void player(String[] command) {
        if (command[1].equalsIgnoreCase("List")) {
            String[] n = command[2].split(", ");
            if (!n[0].isEmpty()) {
                chatGraphic.setPlayers(n);
            }
        } else if (command[1].equalsIgnoreCase("Joined")) {
            chatGraphic.addPlayer(command[2]);
        } else if (command[1].equalsIgnoreCase("Left")) {
            chatGraphic.removePlayer(command[2]);
        } else if (command[1].equalsIgnoreCase("Change")) {
            String[] n = command[2].split("§");
            chatGraphic.changePlayerName(n[0], n[1]);
        } else if (command[1].equalsIgnoreCase("LeaveGame")) {
            chatGraphic.getGameGraphic().removePlayer(command[2]);
        } else {
            clientLog.error("PLAYR command sent by server differs from commands checked by client");
        }
    }

    /**
     * Terminates SBServerListener thread and sends status message to client
     */
    void logOut() {
        isLoggedIn = false;
        try {
            br.close();
            socket.close();
            //printMessage("Logout successful");
        } catch (IOException e) {
            clientLog.warn("Error with closing BufferedReader or Socket");
        }
    }

    public String getInput() { return this.input; }
}