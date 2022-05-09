package skipbo.game;

import java.awt.*;

public class Card {

    public int number;
    public Color col;

    /**
     * Constructor for a normal Card object
     *
     * @param num The number on the card
     * @param col The color of the card
     */

    public Card(int num, Color col) { // Constructor for a normal card with (color and number)
        number = num;
        this.col = col;
    }

    /**
     * Constructor for a Skip Bo card (joker)
     *
     * @param col (Special color cyan for Skip Bo cards)
     */
    Card(Color col) { // Constructor for the special card (Skip Bo)
        this.number = 13;
        this.col = col;
    }

    public String getColString() {
        String c = "";
        if (this.col.equals(Color.green)) c = "G"; // will be green on GUI
        if (this.col.equals(Color.orange)) c = "B"; // will be blue on GUI
        if (this.col.equals(Color.red)) c = "R"; // will be red on GUI
        if (this.col.equals(Color.cyan)) c = "S"; // special card
        // servLog.debug("getColString returning: " + c);
        return c;
    }


}
