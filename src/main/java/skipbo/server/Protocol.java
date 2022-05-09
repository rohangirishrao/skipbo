package skipbo.server;

/**
 * Enum containing network protocol commands.
 * The layout of a full network protocol command is: COMMAND§Option§Argument1§Argument2...
 * The command is always an all-capital 5-letter code.
 */
public enum Protocol {
    /**
     * Prints a message given as argument to a location given as option.
     */
    PRINT,
    /**
     * Sets a parameter given as option to the value given as argument.
     */
    SETTO,
    /**
     * Changes an already existing parameter given as option to the value given as argument.
     */
    CHNGE,
    /**
     * For server: sends all clients the received chat message; For client: prints chat message (options: Global, Private).
     */
    CHATM,
    /**
     * Displays information on chat window.
     */
    DISPL,
    /**
     * Tells the client that a player joined or left the server or changed its name.
     */
    PLAYR,
    /**
     * For server: client wants to log ut; For Client: confirmation that logout was successful.
     */
    LGOUT,
    /**
     * Starts new game with the first 4 players which have the status 'READY'
     */
    NWGME,
    /**
     * Plays a card with index i from Pile fr to Pile to (i, fr and to given as arguments).
     */
    PUTTO,
    /**
     * Gives the client information about the game state (e.g. what its handcards are in that moment).
     */
    CHECK,
    /**
     * Tells receiver that a cheat code has been activated.
     */
    CHEAT,
    /**
     * Tells the client that the game is finished and who the winner is
     */
    ENDGM

}