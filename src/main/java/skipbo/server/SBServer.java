package skipbo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import skipbo.game.Game;
import skipbo.game.Player;
import skipbo.game.Status;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Server for Skip-Bo: manages lobby, chat, starts game. This server accepts players while starting
 * a listener for every new player and is the highest instance of the program.
 */
public class SBServer implements Runnable {
    int playerID = 0;
    int playerCount = 0;

    public SBLobby serverLobby = new SBLobby(); // Should this maybe be non-static?
    public ArrayList<SBListener> sbListenerList = new ArrayList<>(); // not needed in program itself, just for testing

    public static Logger servLog = LogManager.getLogger(SBServer.class);

    int port;

    public SBServer(int port) {
        this.port = port;
    }

    public SBLobby getLobby() { return serverLobby;}

    public ArrayList<SBListener> getSblList() { return sbListenerList; }

    /**
     * This method opens a ServerSocket and then waits for clients to connect. If there is no Highscores.txt file on
     * the computer where the server is run yet, it makes a new one.
     */
    public void run() {

        File highscores = new File("skipBoLogs/Highscores.txt");
        if (!highscores.exists()) {
            try {
                if (highscores.createNewFile()) {
                    PrintWriter pw = new PrintWriter(new FileOutputStream(highscores), true);
                    pw.println("Skip-Bro Highscores (score = turns to win / stockpile size; lower scores are better)");
                }
            } catch (IOException ioe) {
                 ioe.printStackTrace();
            }
        }

        ServerSocket sbServerSocket = null;

        try {
            sbServerSocket = new ServerSocket(port);
            servLog.info("Server waiting for port " + port + ".");
        } catch(IOException ioe) {
            servLog.fatal("Issue with opening Serversocket. Try with another port.");
        }

        while(true) {
            try {
                login(sbServerSocket);
            } catch (IOException e) {
                servLog.fatal("Issue with login.");
            }
        }

    }

    /**
     * Accepts a new socket and starts a SBListener thread.
     */
    private void login(ServerSocket serverSo) throws IOException {
        try {
            Socket sock = serverSo.accept();
            playerCount++;

            SBListener sbListen = new SBListener(this, sock, ++playerID);
            Thread sbListenT = new Thread(sbListen); sbListenT.start();
            sbListenerList.add(sbListen);
        } finally {}
    }

    /**
     * @return a String containing all players currently connected.
     */
    public String getWholePlayerList() {
        StringBuilder allNames = new StringBuilder();
        for(Player p : serverLobby.getPlayerLobby()) {
            allNames.append(p.getName() + " (" + p.getStatus().toString() + "), ");
        }
        if(allNames.length() > 0) allNames.delete(allNames.length()-2, allNames.length());
        return allNames.toString();
    }

    /**
     * @return a String containing all players currently connected except Player 'except'.
     */
    public String getWholePlayerList(Player except) {
        StringBuilder allNames = new StringBuilder();
        for(Player p : serverLobby.getPlayerLobby()) {
            if(!p.equals(except)) {
                allNames.append(p.getName() + ", ");
            }
        }
        if(allNames.length() > 0) allNames.delete(allNames.length()-2, allNames.length());
        return allNames.toString();
    }

    /**
     * @return a String containing all players currently in the main lobby.
     */
    public String getPlayerNotIngameList() {
        StringBuilder allNames = new StringBuilder();
        for(Player p : serverLobby.getPlayerLobby()) {
            if(!p.getStatus().equals(Status.INGAME))
            allNames.append(p.getName() + ", ");
        }
        if(allNames.length() > 0) {
            allNames = allNames.replace(allNames.length()-2, allNames.length(), "");
        }
        return allNames.toString();
    }

    /**
     * @return a String with all games, running and finished.
     */
    public synchronized String[] getGamesListString() {
        String[] allGames = new String[serverLobby.getGamesList().size()];
        int counter = 0;
        for(Game g : serverLobby.getGamesList()) {
            if(g.gameIsRunning()) {
                allGames[counter++] = counter + ": " + g.toString(false);
            }
        }
        for(Game g : serverLobby.getGamesList()) {
            if(!g.gameIsRunning()) {
                allGames[counter++] = counter + ": " + g.toString(false);
            }
        }
        return allGames;
    }

}

