package skipbo.server;

import skipbo.game.Game;
import skipbo.game.Player;

import java.util.ArrayList;

/**
 * Stores all new players in an ArrayList.
 */
public class SBLobby {
    private final ArrayList<Player> playerLobby;
    private final ArrayList<Game> gameList;

    SBLobby() {
        this.playerLobby = new ArrayList<Player>();
        this.gameList = new ArrayList<Game>();
    }

    public ArrayList<Player> getPlayerLobby() {
        return this.playerLobby;
    }

    public ArrayList<Game> getGamesList() {
        return this.gameList;
    }

    Player getPlayer(int index) {
        return playerLobby.get(index);
    }

    /**
     * Returns the player in the Lobby with the name given as argument.
     * If the player does not exists, the method return null.
     */
    Player getPlayerByName(String name) {
        for (Player p : this.playerLobby) {
            if (p.getName().equals(name)) return p;
        }
        return null;
    }

    /**
     * @return the sbL of the player with 'index'.
     */
    SBListener getSBL(int index) {
        return playerLobby.get(index).getSBL();
    }

    /**
     * @return the size of the playerLobby
     */
    int getSize() {
        return playerLobby.size();
    }

    void addPlayer(Player p) {
        playerLobby.add(p);
    }

    void removePlayer(Player p) {
        playerLobby.remove(p);
    }

    void addGame(Game g) {
        gameList.add(g);
    }


    /**
     * Checks if the name is already in use.
     */
    public boolean nameIsTaken(String name) {
        if (playerLobby.isEmpty()) return false;
        return playerLobby.stream().anyMatch(pl -> pl.getName().equals(name));
    }

    /**
     * Checks if name is valid. Only letters and digits are allowed, name can neither
     * be shorter than 2 nor be longer than 13 characters.
     */
    public boolean nameIsValid(String name) {
        if (name.length() > 13 || name.length() < 3) return false;
        for (int i = 0; i < name.length(); i++) {
            if (!Character.isLetterOrDigit(name.charAt(i))) return false;
        }
        return true;
    }

}
