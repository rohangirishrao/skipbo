package skipbo.game;

public enum Status {
    WAITING,
    /**
     * When player is waiting in the lobby to get into a game
     **/

    READY,
    /**
     * When player is ready to play the game
     **/

    INGAME      /** When player is in the process of playing a game **/
}
