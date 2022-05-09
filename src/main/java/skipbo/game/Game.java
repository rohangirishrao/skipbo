package skipbo.game;

import skipbo.server.Protocol;
import skipbo.server.ProtocolExecutor;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import static skipbo.server.SBServer.servLog;

/**
 * Class for the implementation of the game Skip Bo, with all hand operation methods
 * and other essential in game methods.
 */

public class Game implements Runnable {

    public ArrayList<Player> players;
    public int turnCounter = 0;
    public double score = -1.0;
    Pile piles;
    int sizeOfStockPile;
    int playersTurn = 0;
    private Player winner;
    private boolean gameRunning;
//    boolean cheat;

    /**
     * Constructor for Object Game, where the main Game and Game rules
     * will be implemented.
     */
    public Game(ArrayList<Player> players, int sizeOfStockPile) {

        this.players = players;
        this.piles = new Pile();
        this.sizeOfStockPile = sizeOfStockPile;

    }

    /**
     * Returns the drawPile of the main Game
     */
    public ArrayList<Card> getDrawPile() {
        return this.piles.drawPile;
    }

    public int getPlayersTurn() {
        return this.playersTurn;
    }

    public Pile getPiles() {
        return this.piles;
    }

    public boolean gameIsRunning() {
        return this.gameRunning;
    }

    /**
     * Gets a String containing all the players in the game (one per line).
     */
    public String getPlayerList() {
        StringBuilder playerString = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            playerString.append(players.get(i).getName());
            if (!(i == players.size() - 1)) playerString.append("\n");
        }
        return playerString.toString();
    }

    /**
     * Gets a String of a game with its players and status (all in one line). If forHighScore is true,
     * the date and time are put in front of the game and the state of the game (which would always be
     * "FINISHED") is dropped.
     */
    public String toString(boolean forHighScore) {
        StringBuilder gToString = new StringBuilder();

        if (forHighScore) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime now = LocalDateTime.now();
            gToString.append(dtf.format(now)).append("; ");
        }

        for (int i = 0; i < players.size(); i++) {
            gToString.append(players.get(i).getName());
            if (!(i == players.size() - 1)) gToString.append(", ");
        }
        if(!forHighScore) {
            if (gameRunning) {
                gToString.append("; RUNNING.");
            } else {
                if (winner == null) {
                    gToString.append("; TERMINATED.");
                } else {
                    gToString.append("; FINISHED.");
                }
            }
        } else {
            gToString.append("; WINNER: ").append(this.winner.getName()).append(", ");
            gToString.append("SCORE: ").append(String.format("%.2f", score));
        }

        return gToString.toString();
    }

    /**
     * This run method creates all card Decks and hands out random cards
     * from the main deck to all players in the game, and starts the first player's turn.
     * Game and Player Objects have an Object of type Pile, which contain
     * all the different pile-types which are specifically needed.
     */
    public void run() {
        servLog.debug("Game thread starting.");
        gameRunning = true;

        this.piles.gamePiles();   // Game gets complete set of cards
        Random r = new Random(getDrawPile().size());
        Collections.shuffle(piles.drawPile, r);

        for (Player tempPlayer : players) {     // Players getting their cards
            tempPlayer.getSBL().getPW().println(Protocol.PRINT + "§Terminal§Game is starting...");
            Random random = new Random();

            for (int j = 0; j < 5; j++) {    // Draw hand-cards for each player (Actual hand card loop)
                Card c = getDrawPile().get(random.nextInt(getDrawPile().size()));
                tempPlayer.getHandCards().add(c);
                piles.drawPile.remove(c);
            }

//            tempPlayer.getSBL().getPW().println("PRINT§Terminal§Your Hand cards are: " + piles.handCardPrint(tempPlayer));

            for (int j = 0; j < sizeOfStockPile; j++) {    // Draw Stock-Pile cards for each player (REAL METHOD)
                Card c = getDrawPile().get(random.nextInt(getDrawPile().size()));
                piles.drawPile.remove(c);
                tempPlayer.getStockPile().add(c);
            }
//            Card topCard = tempPlayer.getStockPile().get(tempPlayer.getStockPile().size() - 1);
//            tempPlayer.getSBL().getPW().println("PRINT§Terminal§Your Stock card is: " + topCard.number);
        }

        for (Player p : players) {
            String cards = piles.getAllCardsForProtocol(p, p.getGame());
            servLog.debug("NWGME command with cards: " + cards);
            p.getSBL().getPW().println(Protocol.NWGME + "§Cards§" + cards);
        }

        startFirstTurn(playersTurn);
        servLog.debug("Game thread finished.");
    }

    /**
     * Starts first turn
     * @param playersTurn (who's turn it is)
     */

    void startFirstTurn(int playersTurn) {
        servLog.debug("Entered first turn.");
        Player ply = players.get(playersTurn);
//        new ProtocolExecutor().sendAll(Protocol.PRINT + "§Terminal§DP Size: " + piles.drawPile.size(), ply.getSBL());
        Player player = players.get(playersTurn);
//        player.getSBL().getPW().println(Protocol.PRINT +
//                "§Terminal§It's your first turn! Your first set of hand cards are shown now!");
        turnCounter++;
    }

    /**
     * Method to be executed at the start of each player's turn
     * and to fill their hand cards.
     */

    private void startTurn(int playersTurn) {
        int joker =0;
        Player ply = players.get(playersTurn);
        for(Card card: piles.drawPile){
            if(card.number == 13){
                joker++;
            }
        }
//        new ProtocolExecutor().sendAll(Protocol.PRINT+"§Terminal§Number of jokers: " + joker, ply.getSBL());
        servLog.debug("Entered startTurn: it's " + ply.getName() + "'s turn.");
        checkDrawPile();
        fillHandCards(ply);
        turnCounter++;

//       ply.getSBL().getPW().println(Protocol.PRINT + "§Terminal§It's your turn!");

       /*new ProtocolExecutor().sendAllExceptOne(Protocol.PRINT + "§Terminal§It's " + ply.getName()
               + "'s turn!", ply.getSBL());
        */
    }

    /**
     * Method playToMiddle processes which player, which Card index they wish to play
     * and which buildPile they wish to play to and carries out the command if valid.
     * Furthermore, removes the specified card from their hand cards.
     *
     * @param currentPlayer  (The player that's playing right now)
     * @param handCardIndex  (The index of the hand card, from 0-4)
     * @param buildDeckIndex (The index of the build pile the player wishes to play to, from 0-3)
     */

    public boolean playToMiddle(Player currentPlayer, int handCardIndex, int buildDeckIndex) {

        if (handCardIndex < 0 || handCardIndex >= currentPlayer.getHandCards().size()) {
            currentPlayer.getSBL().getPW().println(Protocol.PRINT + "§Terminal§Hand Card Index is invalid!");
            servLog.debug("Invalid index");
            return false;
        }
        if (buildDeckIndex < 0 || buildDeckIndex > 3) {       //if bp index is a false index that cannot be true
            currentPlayer.getSBL().getPW().println(Protocol.PRINT + "§Terminal§Build Deck Index is invalid!");
            servLog.debug("Invalid build deck index");
            return false;
        }

        servLog.debug("Entered playToMiddle.");
        Card card = currentPlayer.getHandCards().get(handCardIndex);   // returns card at specified index in the hand card arraylist

        ArrayList<ArrayList<Card>> buildPiles = piles.buildPiles;
        ArrayList<Card> specBuildPile = buildPiles.get(buildDeckIndex);
        Card stockCard = currentPlayer.getStockPile().get(currentPlayer.getStockPile().size() - 1);

        if (specBuildPile.isEmpty()) {      //if build pile is empty
            if (1 == card.number) {
                specBuildPile.add(card);
                currentPlayer.getHandCards().remove(card);

                checkBuildPile(card, specBuildPile, currentPlayer);
                /*currentPlayer.getSBL().getPW().println("PRINT§Terminal§Your hand cards are now: "
                        + piles.handCardPrint(currentPlayer));
                currentPlayer.getSBL().getPW().println("PRINT§Terminal§Your stock card is: " +
                        stockCard.number);*/
                return true;
            } else if (card.col == Color.cyan) {
                card.number = 1;
                specBuildPile.add(card);
                currentPlayer.getHandCards().remove(card);

//                currentPlayer.getSBL().getPW().println("PRINT§Terminal§Your hand cards are now: "
//                        + piles.handCardPrint(currentPlayer));

                checkBuildPile(card, specBuildPile, currentPlayer);
                return true;
            } else {
                currentPlayer.getSBL().getPW().println(Protocol.PRINT + "§Terminal§This move is invalid! " +
                        "To play to an empty pile, the card number has to be 1.");
                return false;
            }
        } else {        // if Build Pile isn't empty
            Card topCard = specBuildPile.get(specBuildPile.size() - 1);
            if (topCard.number == (card.number - 1)) {
                specBuildPile.add(card);
                currentPlayer.getHandCards().remove(card);

                checkBuildPile(card, specBuildPile, currentPlayer);  //check if buildPile is full and print build pile

            /*currentPlayer.getSBL().getPW().println("PRINT§Terminal§Your hands cards are now: "
                    + piles.handCardPrint(currentPlayer));
            currentPlayer.getSBL().getPW().println("PRINT§Terminal§Your stock card is: " +
                    stockCard.number);*/

                return true;
            } else if (card.col == Color.cyan) {      //if Joker card
                card.number = topCard.number + 1;
                specBuildPile.add(card);
                currentPlayer.getHandCards().remove(card);

                checkBuildPile(card, specBuildPile, currentPlayer); //check if buildPile is full and should be emptied.
               /*currentPlayer.getSBL().getPW().println("PRINT§Terminal§Your hands cards are now: "
                       + piles.handCardPrint(currentPlayer));
                currentPlayer.getSBL().getPW().println("PRINT§Terminal§Your stock card is: " + stockCard.number);*/

                return true;
            } else {    // not Joker card and not a number higher than the top card on build pile
                currentPlayer.getSBL().getPW().println(Protocol.PRINT + "§Terminal§Invalid move! The card you play " +
                        "to build deck has to be one number higher than the card on the build deck.");
                return false;
            }
        }
    }

    /**
     * This method plays a hand card into a discard pile of the player's choice
     * Parameter handCardIndex to know which hand card should be selected to be played
     * Parameter currentPlayer to know whose turn it is. Furthermore, removes the specified card
     * from Player's hand.
     * Parameter discardPileIndex to know which Discard pile to play to.
     *
     * @param currentPlayer    (The player that's playing right now)
     * @param handCardIndex    (The index of the hand card, from 0-4)
     * @param discardPileIndex (The index of the discard pile that the player wishes to play to.)
     */

    public boolean playToDiscard(Player currentPlayer, int handCardIndex, int discardPileIndex) {

        servLog.debug("Entered playToDiscard.");

        if (handCardIndex < 0 || handCardIndex >= currentPlayer.getHandCards().size()) {
            currentPlayer.getSBL().getPW().println(Protocol.PRINT + "§Terminal§Hand Card Index is invalid!");
            servLog.debug("Invalid index");
            return false;
        }
        if (discardPileIndex < 0 || discardPileIndex > 3) {
            currentPlayer.getSBL().getPW().println(Protocol.PRINT + "§Terminal§Discard deck index is invalid!");
            servLog.debug("Invalid discard deck index");
            return false;
        }

        ArrayList<ArrayList<Card>> discardPiles = currentPlayer.getDiscardPiles();
        ArrayList<Card> specDiscard = discardPiles.get(discardPileIndex);

        Card card = currentPlayer.getHandCards().get(handCardIndex);

        specDiscard.add(card);
        currentPlayer.getHandCards().remove(card);
        endTurn();

        return true;

    }

    /**
     * This method is to play the top card from the Stock Pile to a build pile
     * of the player's choosing.
     *
     * @param currentPlayer  (The player that's playing right now)
     * @param buildPileIndex (The index of the pile that the player wishes to play to)
     */
    public Card playFromStockToMiddle(Player currentPlayer, int buildPileIndex) {

        if (buildPileIndex < 0 || buildPileIndex > 3) {      //if bp index is a false index that cannot be true
            currentPlayer.getSBL().getPW().println(Protocol.PRINT + "§Terminal§Build Deck Index is invalid!");
            servLog.debug("Invalid build deck index");
            return null;
        }
        servLog.debug("Entered playFromStockToMiddle.");
        ArrayList<Card> stockPile = currentPlayer.getStockPile();
        Card stockCard = currentPlayer.getStockPile().get(stockPile.size() - 1);

        ArrayList<ArrayList<Card>> buildPiles = piles.buildPiles;
        ArrayList<Card> specBuildPile = buildPiles.get(buildPileIndex);

        if (specBuildPile.isEmpty()) {
            if (stockCard.number == 1) {        // if card number is 1, add to new pile
                specBuildPile.add(stockCard);
                currentPlayer.getStockPile().remove(stockCard);
                if (currentPlayer.getStockPile().size() == 0) {  //if stock pile is empty
                    return new Card(-1, Color.cyan);
                }
                checkBuildPile(stockCard, specBuildPile, currentPlayer);
//               currentPlayer.getSBL().getPW().println("PRINT§Terminal§Your hand cards are now: "
//                       + piles.handCardPrint(currentPlayer));

                if (currentPlayer.getStockPile().size() == 0) {  //if stock pile is empty
                    return new Card(-1, Color.cyan);
                }
                return stockPile.get(stockPile.size() - 1);
            } else if (stockCard.col == Color.cyan) { //if stock card is Joker card
                stockCard.number = 1;
                specBuildPile.add(stockCard);
                currentPlayer.getStockPile().remove(stockCard);
                if (currentPlayer.getStockPile().size() == 0) {  //if stock pile is empty
                    return new Card(-1, Color.cyan);
                }

                checkBuildPile(stockCard, specBuildPile, currentPlayer);
//                currentPlayer.getSBL().getPW().println("PRINT§Terminal§Your hand cards are now: "
//                        + piles.handCardPrint(currentPlayer));
//                currentPlayer.getSBL().getPW().println("PRINT§Terminal§Your stock card is: " +
//                        currentPlayer.getStockPile().get(currentPlayer.getStockPile().size() - 1).number);

                return stockPile.get(stockPile.size() - 1);
            } else {      //if invalid move
                currentPlayer.getSBL().getPW().println(Protocol.PRINT + "§Terminal§This move is invalid! " +
                        "To play to an empty pile, the card number has to be 1.");
                return null;
            }
        } else {         //case build pile isn't empty
            Card topCard = specBuildPile.get(specBuildPile.size() - 1);
            if (topCard.number == (stockCard.number - 1)) {
                specBuildPile.add(stockCard);
                currentPlayer.getStockPile().remove(stockCard);
                if (currentPlayer.getStockPile().size() == 0) {  //if stock pile is empty
                    return new Card(-1, Color.cyan);
                }
                checkBuildPile(stockCard, specBuildPile, currentPlayer);

                /*currentPlayer.getSBL().getPW().println("PRINT§Terminal§Your hands cards are now: "
                        + piles.handCardPrint(currentPlayer));

                currentPlayer.getSBL().getPW().println("PRINT§Terminal§Your stock card is: " +
                        currentPlayer.getStockPile().get(currentPlayer.getStockPile().size() - 1).number);*/

                return stockPile.get(stockPile.size() - 1);
            } else if (stockCard.col == Color.cyan) {      // if Skip Bo card
                stockCard.number = topCard.number + 1;
                specBuildPile.add(stockCard);
                currentPlayer.getStockPile().remove(stockCard);
                if (currentPlayer.getStockPile().size() == 0) {  //if stock pile is empty
                    return new Card(-1, Color.cyan);
                }

                checkBuildPile(stockCard, specBuildPile, currentPlayer);
//                currentPlayer.getSBL().getPW().println("PRINT§Terminal§Your hand cards are now: "
//                        + piles.handCardPrint(currentPlayer));

                return stockPile.get(stockPile.size() - 1);
            } else {         // If card number isn't 1 and isn't a Skip Bo card
                currentPlayer.getSBL().getPW().println(Protocol.PRINT + "§Terminal§This move is invalid! " +
                        "Card number has to be one higher than top card on build pile.");
                return null;
            }
        }
    }


    /**
     * Method to play a card from the discard pile to the build pile
     * with index of build and discard piles to choose what card to play.
     * Also checks validity of the move and replaces card at the
     *
     * @param currentPlayer    (The player that's playing right now)
     * @param discardPileIndex (The index of the hand card, from 0-4)
     * @param buildPileIndex   (The index of the pile that the player wishes to play to)
     */

    public boolean playFromDiscardToMiddle(Player currentPlayer, int discardPileIndex, int buildPileIndex) {
        servLog.debug("Entered playFromDiscardToMiddle.");
        ArrayList<Card> discardPile = currentPlayer.getDiscardPiles().get(discardPileIndex);

        if (buildPileIndex < 0 || buildPileIndex > 3) {       //if bp index is a false index that cannot be true
            currentPlayer.getSBL().getPW().println(Protocol.PRINT + "§Terminal§Build Deck Index is invalid!");
            servLog.debug("Invalid build deck index");
            return false;
        }
        if (discardPileIndex < 0 || discardPileIndex > 3) {   //If Dp index is false
            currentPlayer.getSBL().getPW().println(Protocol.PRINT + "§Terminal§Discard deck index is invalid!");
            servLog.debug("Invalid discard deck index");
            return false;
        }

        if (discardPile.isEmpty()) {      //if dp is empty and player tries to play from it
            currentPlayer.getSBL().getPW().println(Protocol.PRINT
                    + "§Terminal§That discard pile is empty! Choose another.");
            servLog.debug("Played from an empty discard deck");
            return false;
        }
        ArrayList<Card> specBuildPile = piles.buildPiles.get(buildPileIndex);

        Card card = discardPile.get(discardPile.size() - 1);

        if (specBuildPile.isEmpty()) {
            if (card.number == 1) {                 // if card number is 1, new pile
                specBuildPile.add(card);
                discardPile.remove(card);

                checkBuildPile(card, specBuildPile, currentPlayer);

                return true;
            } else if (card.col == Color.cyan) {       //if Joker card, then make it 1 and add
                card.number = 1;
                specBuildPile.add(card);
                discardPile.remove(card);

                checkBuildPile(card, specBuildPile, currentPlayer);

                return true;
            } else {            //invalid move
                currentPlayer.getSBL().getPW().println(Protocol.PRINT + "§Terminal§This move is invalid! " +
                        "To play to an empty pile, the card number has to be 1.");
                return false;
            }
        } else {
            // DONE: joker case added
            Card topCard = specBuildPile.get(specBuildPile.size() - 1);
            if (topCard.number == (card.number - 1)) {      // checks if move is valid, if number is correct
                specBuildPile.add(card);
                discardPile.remove(card);

                checkBuildPile(card, specBuildPile, currentPlayer);

                return true;
            } else if (card.col == Color.cyan) {       //if Joker card
                card.number = (topCard.number + 1);
                specBuildPile.add(card);
                discardPile.remove(card);

                checkBuildPile(card, specBuildPile, currentPlayer);

                return true;
            } else {          //invalid move
                currentPlayer.getSBL().getPW().println(Protocol.PRINT + "§Terminal§This move is invalid! " +
                        "Card number has to be one higher than top card on build pile.");
                return false;
            }
        }
    }

    /**
     * Method to fill the hand cards up to 5 of the player who's turn it is to play
     * Adds cards from top of draw pile.
     *
     * @param player (Player whose hand you wish to fill)
     */
    public void fillHandCards(Player player) {
        servLog.debug("Entered fillHandCards.");
        ArrayList<Card> drawPile = piles.drawPile;
        int toFill = 5 - player.getHandCards().size();
        if (toFill != 0) {
            for (int i = 0; i < toFill; i++) {
                Card drawCard = drawPile.get(drawPile.size() - 1);
                player.getHandCards().add(drawCard);
                drawPile.remove(drawCard);
            }
        }
        new ProtocolExecutor(null, player.getSBL()).check("HandCards");
    }

    /**
     * Checks if draw pile is empty, if yes, adds cards from the empty pile into the draw pile.
     */
    public void checkDrawPile() {
        if (this.getDrawPile().size() <= 5) {
            Collections.shuffle(piles.getEmptyPile());  //shuffles the empty pile
            getDrawPile().addAll(piles.getEmptyPile()); //adds empty pile cards to the draw pile
        }
    }

    /**
     * Checks if player's hand cards are empty. If yes, fills the player's hand again.
     *
     * @param player The player that's currently playing
     */
    public void checkHandCards(Player player) {
        if (player.getHandCards().isEmpty()) {
            fillHandCards(player);
        }
    }

    /**
     * Checks if build pile top card is 12, if yes, removes cards from that build pile and puts it into and empty pile
     * and prints. If not, prints normally.
     *
     * @param card      card that's played
     * @param buildPile build pile that's played to
     * @param player    Player whose turn it is
     */

    public void checkBuildPile(Card card, ArrayList<Card> buildPile, Player player) {
        String[] buildPiles = piles.buildPilesPrint();
        if (card.number == 12) {
//            for (String str : buildPiles) {
//                new ProtocolExecutor().sendAll("PRINT§Terminal§" + str, player.getSBL());
//            }
            piles.emptyPile.addAll(buildPile);

            for (Iterator<Card> bp = buildPile.iterator(); bp.hasNext(); ) { //Iterator to remove all cards from current BP
                bp.next();
                bp.remove();
            }
        }
    }

    /**
     * Method to display all Discard piles
     */

    void displayDiscard(Player player) {
        String[] discardPiles = piles.discardPilesPrint(player);
        for (String s : discardPiles) {
            player.getSBL().getPW().println(Protocol.PRINT + "§Terminal§ " + s);
        }
    }

    /**
     * Method to add a player's cards to the empty pile that has left the game
     *
     * @param player Player that's left.
     */

    public boolean[] playerLeaving(Player player) {
        boolean[] changeTurn = new boolean[]{false, false};
        if (players.get(playersTurn).equals(player)) {
            changeTurn[0] = true;
        } else if (players.indexOf(player) < playersTurn) {
            changeTurn[1] = true;
        }

        ArrayList<Card> handCards = player.getHandCards();
        piles.emptyPile.addAll(handCards); // adds to empty pile
        player.clearHandCards();      //removes cards from the player's hand cards

        for (ArrayList<Card> dPile : player.getDiscardPiles()) {
            piles.emptyPile.addAll(dPile);
            dPile.clear();      //removes cards from the player's discard pile
        }

        ArrayList<Card> stockPile = player.getStockPile();
        piles.emptyPile.addAll(stockPile);  //adds to empty pile
        stockPile.clear();      //removes cards from the player's stock pile

        return changeTurn;
    }

    /**
     * Method to be run at the end of a player's turn, which
     * then changes turn from one player to the next.
     */
    public void endTurn() {
        servLog.debug("Entered endTurn.");
        if (!(playersTurn == players.size() - 1)) {     //if not the last player in the array, go up by one
            playersTurn++;
        } else {
            playersTurn = 0;        //otherwise start over from first player
            turnCounter++;
        }
        startTurn(playersTurn);     //starts next turn
    }

    /**
     * Ends the current turn after the player who's turn it was left the game.
     */
    public void endTurnAfterLeaving(boolean[] changeTurn) {
        servLog.debug("Entered endTurnAfterLeaving.");
        if (changeTurn[0]) {     //if not the last player in the array, go up by one
            playersTurn = 0;        //otherwise start over from first player
            turnCounter++;
            startTurn(playersTurn);
        } else if (changeTurn[1]){
            playersTurn--;
        }
    }

    /**
     * A method to run the End Game network protocol, and let the
     * player know that some player has won the game!
     *
     * @param winner (The player that has won the game)
     */
    public void endGame(Player winner) {

        servLog.info("Game ending.");
        gameRunning = false;
        this.winner = winner;

        for(int i = 0; i < 4; i++){
            ArrayList<Card> bp = piles.buildPiles.get(i);
            bp.clear();
        }

        score = (double) turnCounter / sizeOfStockPile;
        score = Math.round(score*100) / 100.0;
        if (winner != null) {
            new ProtocolExecutor().sendAll(Protocol.ENDGM + "§Winner§" + winner.getName() + "§" +
                    this.score, winner.getSBL());
        }
        new ProtocolExecutor(new String[]{""}, winner.getSBL()).gameEnding(this);
    }


    /**
     * Ends game after someone used the cheat "Win". The score is set to 100.
     * @param winner The player that wins
     */
    public void endGameCheat(Player winner) {
        servLog.info("Game ending.");
        gameRunning = false;
        this.winner = winner;

        for(int i = 0; i < 4; i++){
            ArrayList<Card> bp = piles.buildPiles.get(i);
            bp.clear();
        }

        score = 100.00;
        if (winner != null) {
            new ProtocolExecutor().sendAll(Protocol.ENDGM + "§Winner§" + winner.getName() + "§" +
                    this.score, winner.getSBL());
        }
        new ProtocolExecutor(new String[]{""}, winner.getSBL()).gameEnding(this);
    }


    /**
     * Method for the cheat code "Joker" - adds 3 joker cards on top of stock.
     */
    public void cheat(Player player) {
        ArrayList<ArrayList<Card>> dPile = player.getDiscardPiles();
        for(ArrayList<Card> specDiscard: dPile){
            specDiscard.add(new Card(13,Color.cyan));
        }
        cheatPunishment(player);
    }

    /**
     * Punishes a player for using the cheat code "Joker".
     * @param player Player that used the cheat
     */
    private void cheatPunishment(Player player) {
        for (int i = 0; i < 5;  i++){
            Card top = this.getDrawPile().get(getDrawPile().size()-1);
            player.getStockPile().add(0,top);
            getDrawPile().remove(top);
        }
    }

    /**
     * Method for when a game gets interrupted because people left. Clears all game cards.
     */
    public void terminateGame() {
        this.gameRunning = false;
        Player pLeft = this.players.get(0);

        pLeft.getSBL().getPW().println(Protocol.ENDGM + "§Terminated");
        pLeft.changeGame(null);
        pLeft.changeStatus(Status.WAITING);

        for(Player player: players){    //Clear all player cards
            player.clearStockPile();
            player.clearDiscardPiles();
            player.clearHandCards();
        }

        for(int i = 0; i < 4; i++){     //Clears all build piles
            ArrayList<Card> bp = piles.buildPiles.get(i);
            bp.clear();
        }
    }
}
