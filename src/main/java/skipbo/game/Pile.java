package skipbo.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Pile {

    public ArrayList<Card> drawPile;        // 1 draw pile
    public ArrayList<Card> stockPile;
    public ArrayList<Card> handCards;
    public ArrayList<ArrayList<Card>> buildPiles;   // ArrayList for the 4 build decks in the middle
    public ArrayList<ArrayList<Card>> discardPiles;
    public ArrayList<Card> emptyPile;   //pile for the reshuffle set of cards
    public Player player;
    int id;

    /**
     * The Pile-constructor is overloaded because we have different
     * types of piles for the Game and Player.
     * The Pile-constructor for the Player needs the Player-ID, this
     * assures that we can identify the different piles.
     */
    public Pile() {   // Pile without id (for Game)
        this.drawPile = new ArrayList<Card>();
        this.buildPiles = new ArrayList<ArrayList<Card>>();
        this.emptyPile = new ArrayList<Card>(0);
    }

    public Pile(int id) {   // Pile with id (for Player)
        this.id = id;
        this.discardPiles = new ArrayList<ArrayList<Card>>();

        for (int i = 0; i < 4; i++) {
            ArrayList<Card> deck = new ArrayList<Card>();
            discardPiles.add(deck);
        }

        this.stockPile = new ArrayList<Card>();
        this.handCards = new ArrayList<Card>();
    }

    /**
     * {@literal
     * The method gamePiles() creates all cards of a full set and
     * puts them in a "ArrayList<Card>-pile" (named: drawPile).
     * It also creates an "ArrayList<ArrayList<Cards>>-set" (named: buildPiles),
     * which contains four empty "ArrayList<Card>-piles".
     * Every card has a number and a colour.
     * Booth piles are elements of a Pile-Object.}
     */

    public void gamePiles() {    // All Cards are created, and the build-pile is added

        int colourCount = 0;

        ArrayList<Color> colours = new ArrayList<Color>();     // Save five different colours
        //colours.add(Color.yellow);      // Color index Nr. 0
        colours.add(Color.orange);      // Color index Nr. 0
        colours.add(Color.green);       // Color index Nr. 1
        colours.add(Color.red);         // Color index Nr. 2
        colours.add(Color.cyan);        // Color index Nr. 3

        for (int j = 0; j < 18; j++) {        // Add Normal Cards (144 pcs.)
            for (int i = 0; i < 12; i++) {
                Card card = new Card(i + 1, colours.get(colourCount));
                this.drawPile.add(card);
            }
            colourCount++;
            if (colourCount == 3) colourCount = 0;
        }

        for (int l = 0; l < 27; l++) {       // Add Special Cards (18 pcs.)
            Card card = new Card(colours.get(3));
            this.drawPile.add(card);
        }

        Collections.shuffle(drawPile);  //shuffles all draw pile cards after creating and adding them

        for (int i = 0; i < 4; i++) {        // Add four empty card piles (buildPiles)
            ArrayList<Card> deck = new ArrayList<Card>();
            this.buildPiles.add(deck);
        }
    }


    /**
     * Method to print the player's hand cards onto the Chat window.
     *
     * @param player (The player whose hand cards you wish to print)
     * @return A string with the player's hand cards
     */

    public String handCardPrint(Player player) {
        ArrayList<Card> handCards = player.getHandCards();
        int len = handCards.size();
        int[] a = new int[len];
        for (int i = 0; i < len; i++) {
            a[i] = handCards.get(i).number;
        }
        return Arrays.toString(a);
    }

    /**
     * @return A String containing all hand cards and the top stock card of a player, formatted for network protocol.
     */
    public String getAllCardsForProtocol(Player player, Game game) {
        StringBuilder cards = new StringBuilder();
        for (Card c : player.getHandCards()) {
            cards.append(c.getColString()).append("§").append(c.number).append("§");
        }
        for (Player p : game.players) {
            Card stockTopCard = p.getStockPile().get(p.getStockPile().size() - 1);
            cards.append(p.getName()).append("§").append(stockTopCard.getColString()).append("§").append(stockTopCard.number).append("§");
        }

        return cards.toString();
    }

    public String getHandCardsForProtocol(Player player) {
        StringBuilder cards = new StringBuilder();
        for (Card c : player.getHandCards()) {
            cards.append(c.getColString()).append("§").append(c.number).append("§");
        }

        return cards.toString();
    }

    /**
     * Returns all build piles printed to console.
     *
     * @return String with piles
     */

    public String[] buildPilesPrint() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            ArrayList<Card> specBuildPile = buildPiles.get(i);
            if (specBuildPile.isEmpty()) {
                str.append("Build pile ").append(i + 1).append(" is: [ ]").append("\n");
            } else {
                str.append("Build pile ").append(i + 1).append(" is: [").append(specBuildPile.get(specBuildPile.size() - 1).number).append("]").append("\n");
            }
        }
        return str.toString().split("\n");
    }

    /**
     * Method to print a player's discard piles onto the Chat window
     *
     * @param player (The player whose discard piles you wish to print)
     * @return A string with the player's hand cards
     */

    public String[] discardPilesPrint(Player player) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            ArrayList<Card> specDiscardPile = player.getDiscardPiles().get(i);
            int[] printArray = new int[specDiscardPile.size()];
            for (int j = 0; j < specDiscardPile.size(); j++) {
                printArray[j] = specDiscardPile.get(j).number;
            }
            int t = i + 1;
            str.append("Discard Pile ").append(t).append(" of ").append(player.getName()).append(" is: ")
                    .append(Arrays.toString(printArray)).append("\n");
        }
        return str.toString().split("\n");
    }

    /** returns emptyPile in a String   **/
    public String emptyPilePrint(){
        assert emptyPile != null;
        int len = emptyPile.size();
        int[] a = new int[len];
        for (int i = 0; i < len; i++) {
            a[i] = emptyPile.get(i).number;
        }
        return Arrays.toString(a);
    }

    /** returns emptyPile   **/
    public ArrayList<Card> getEmptyPile(){
        return emptyPile;
    }
}

