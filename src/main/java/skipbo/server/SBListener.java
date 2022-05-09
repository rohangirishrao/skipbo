package skipbo.server;

import skipbo.game.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import static skipbo.server.SBServer.servLog;

/**
 * Thread waiting for any action from client.
 */
public class SBListener implements NWPListener {
    SBServer server;
    Socket sock;
    PrintWriter pw;
    BufferedReader br;
    boolean running;
    int id;
    Player player;

    public SBListener(SBServer server, Socket sock, int id) throws IOException {
        this.server = server;
        this.sock = sock;
        try {
            this.pw = new PrintWriter(sock.getOutputStream(), true);
            this.br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        } finally {
        }
        this.running = true;
        this.id = id;
        this.player = null;
    }

    /**
     * Constantly reads input from assigned client. Read input is sliced and then passes it as argument to analyze method.
     */
    public void run() {
        while (running) {
            String[] input = null;
            try {
                input = this.br.readLine().split("§", 3);
            } catch (IOException e) {
                servLog.warn("Error with reading input from client.");
            }

            if (input != null && input[0].length() > 0) this.analyze(input);
        }

    }

    /**
     * First branching out for protocol execution. Triggers required method depending on input protocol command.
     *
     * @param input: Sliced input String from client.
     */
    public void analyze(String[] input) {
        try {
            Protocol protocol = Protocol.valueOf(input[0]);
            switch (protocol) {
                case SETTO:
                    new ProtocolExecutor(input, this).setTo();
                    break;
                case CHNGE:
                    new ProtocolExecutor(input, this).changeTo(false);
                    break;
                case CHATM:
                    new ProtocolExecutor(input, this).chatMessage();
                    break;
                case DISPL:
                    new ProtocolExecutor(input, this).display();
                    break;
                case LGOUT:
                    new ProtocolExecutor(input, this).logout();
                    break;
                case NWGME:
                    new ProtocolExecutor(input, this).newGame();
                    break;
                case PUTTO:
                    new ProtocolExecutor(input, this).putTo();
                    break;
                case CHEAT:
                    new ProtocolExecutor(input, this).cheat();
                    break;
                case PLAYR:
                    new ProtocolExecutor(input, this).playerLeavingGame();
                    break;
            }
        } catch (IllegalArgumentException iae) {
            servLog.warn(input[0] + ": not a command.");
        } catch (NoCommandException nce) {
            if (nce.command != null && nce.option != null) {
                servLog.warn(nce.option + ": not an option for command " + nce.command + ".");
            } else {
                servLog.warn("No valid protocol.");
            }
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public PrintWriter getPW() {
        return this.pw;
    }

    public SBServer getServer() {
        return server;
    }

    /**
     * @return the game lobby of the player belonging to this SBListener.
     */
    public ArrayList<Player> getGameLobby() {
        if (this.player.getGame() != null) return this.player.getGame().players;
        else return null;
    }

    /**
     * Sets running to false, thus getting the SBListener out of the while loop and terminating the thread.
     */
    public void stopRunning() {
        this.running = false;
    }

}




