package skipbo.client;

import skipbo.server.Protocol;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

import static skipbo.client.SBClient.clientLog;

/**
 * Class for the graphical representation of the game.
 */
public class GameGraphic implements ActionListener {

    private final SBClientListener clientListener;
    private DefaultButtonModel notClickableModel;
    private final DefaultButtonModel defaultButtonModel = new DefaultButtonModel();
    private CardButton button1Pressed = null;
    final Border defaultBorder = UIManager.getBorder("Button.border");
    final Border clickedBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
    private final String playerName;
    JLayeredPane layeredPane;
    ActionListener actionListener = this;
    boolean soundMuted = false;


    // Layout of opponents
    private final int WIDTH_OP1 = 40; //30
    private final int HEIGHT_OP1 = 58; //50

    //Opponents
    JLabel e1;
    JLabel e2;
    JLabel e3;
    //Array of JLabels e1, e2, e3 depending on how many players are playing
    private JLabel[] oppArray;

    private int playerIndex = 0;

    //Own piles
    final CardButton[] hand = new CardButton[5];
    CardButton stock;
    final ArrayList<CardButton>[] discard = new ArrayList[4];

    //Game piles
    final CardButton[] build = new CardButton[4];

    //Opponent discard piles
    final ArrayList<CardButton>[] e1_discard = new ArrayList[4];
    final ArrayList<CardButton>[] e2_discard = new ArrayList[4];
    final ArrayList<CardButton>[] e3_discard = new ArrayList[4];

    //Distances between discard cards on same discard pile
    private final int DISTDISCARD = 30;
    private final int DISTOPPDISCARD = 13;

    //Opponent stock piles
    CardButton e1_stock;
    CardButton e2_stock;
    private CardButton e3_stock;

    //Number of cards left on stock piles
    JLabel numOfStockCards;
    JLabel[] oppNumStockCards = new JLabel[3];

    int initialNumStockCards;

    JLabel yourTurnLabel;

    final CardIcons cardIcons = new CardIcons(WIDTH_OP1, HEIGHT_OP1, 78, 120);

    final Font DEFAULTFONT = UIManager.getDefaults().getFont("Label.font");

    GameGraphic() {
        clientListener = null;
        playerName = null;
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1550, 870);
        setButtonModel();
    }

    GameGraphic(SBClientListener clientListener, String name, JTextPane chat) {
        this.clientListener = clientListener;
        playerName = name;
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1550, 870);
        setButtonModel();
        appendDecks();
    }

    /**
     * Creates all necessary components for the game.
     */
    void appendDecks() {
        /*
        Cards of the Player and build piles
        */

        // Layout Manager

        final int WIDTH_HAND = 78;
        final int HEIGHT_HAND = 120;
        final int x_HAND = 550;  // change here
        final int y_HAND = 620;  // change here
        final int x_HAND_DISTANCE = 88;
        final int y_HAND_LABEL = y_HAND-20;

        // Layout of discard piles
        final int WIDTH_DISCARD = 100;
        final int HEIGHT_DISCARD = 145;
        final int x_DISCARD = 550;  // change here
        final int y_DISCARD = 330;  // change here
        final int x_DISCARD_DISTANCE = 120;
        final int y_DISCARD_LABEL = y_DISCARD-20;

        // Layout of stock pile
        final int WIDTH_STOCK = 100;
        final int HEIGHT_STOCK = 145;
        final int x_STOCK = 400;    // change here
        final int y_STOCK = 330;    // change here
        final int y_STOCK_LABEL = y_STOCK-20;

        // Layout of build piles
        final int WIDTH_BUILD = 100;
        final int HEIGHT_BUILD = 145;
        final int x_BUILD = 550;  // change here
        final int y_BUILD = 120;  // change here
        final int x_BUILD_DISTANCE = 120;
        final int y_BUILD_LABEL = y_BUILD-20;

        // Layout of draw pile
        final int WIDTH_DRAW = 100;
        final int HEIGHT_DRAW = 145;
        final int x_DRAW = 400;    // change here
        final int y_DRAW = 120;    // change here
        final int y_DRAW_LABEL = y_DRAW-20;

        // Layout of opponents
        final int WIDTH_OP1 = 40; //30
        final int HEIGHT_OP1 = 58; //50
        final int x_OP1 = 1050;  // change here
        final int y_OP1 = 120;  // change here
        final int y_OP2 = y_OP1+140; //120  // change here
        final int y_OP3 = y_OP2+140;  // change here
        final int x_OP1_DISTANCE = 45; //35
        
        
        JLabel dp = new JLabel("Your Discard Piles");
        dp.setBounds(x_DISCARD, y_DISCARD_LABEL, 120, 15);

        JLabel hp = new JLabel("Your hand cards");
        hp.setBounds(x_HAND, y_HAND_LABEL, 120, 15);

        JLabel sp = new JLabel("Your stock pile");
        sp.setBounds(x_STOCK, y_STOCK_LABEL, 120, 15);

        JLabel bp = new JLabel("Build piles");
        bp.setBounds(x_BUILD, y_BUILD_LABEL, 120, 15);

        JLabel dpg = new JLabel("Draw pile");
        dpg.setBounds(x_DRAW, y_DRAW_LABEL, 120, 15);

        layeredPane.add(dpg);
        layeredPane.add(dp);
        layeredPane.add(hp);
        layeredPane.add(sp);
        layeredPane.add(bp);

        // Discard and build Piles
        for (int i = 0, j = 1; i < discard.length; i++, j++) {
            build[i] = new CardButton(CardButton.BUILD);
            build[i].setBounds(x_BUILD + i * x_BUILD_DISTANCE, y_BUILD, WIDTH_BUILD, HEIGHT_BUILD);
            layeredPane.add(build[i]);
            build[i].setName(" B " + j);

            discard[i] = new ArrayList<>();
            CardButton b = new CardButton(CardButton.DISCARD);
            b.setBounds(x_DISCARD + i * x_DISCARD_DISTANCE, y_DISCARD, WIDTH_DISCARD, HEIGHT_DISCARD);
            b.addActionListener(actionListener);
            b.setName(" D " + j);
            layeredPane.add(b);
            discard[i].add(b);
        }

        // hand piles
        for (int i = 0; i < hand.length; ) {
            hand[i] = new CardButton(CardButton.HAND);
            layeredPane.add(hand[i]);
            hand[i].addActionListener(actionListener);
            hand[i].setName(" H " + ++i);
        }
        hand[0].setBounds(x_HAND, y_HAND, WIDTH_HAND, HEIGHT_HAND);
        hand[1].setBounds(x_HAND + x_HAND_DISTANCE, y_HAND, WIDTH_HAND, HEIGHT_HAND);
        hand[2].setBounds(x_HAND + 2* x_HAND_DISTANCE, y_HAND, WIDTH_HAND, HEIGHT_HAND);
        hand[3].setBounds(x_HAND + 3* x_HAND_DISTANCE, y_HAND, WIDTH_HAND, HEIGHT_HAND);
        hand[4].setBounds(x_HAND + 4* x_HAND_DISTANCE, y_HAND, WIDTH_HAND, HEIGHT_HAND);


        //stock pile
        stock = new CardButton(CardButton.STOCK);
        stock.setBounds(x_STOCK, y_STOCK, WIDTH_STOCK, HEIGHT_STOCK);
        layeredPane.add(stock);
        stock.setName(" S 1");
        stock.addActionListener(actionListener);


        /*
        Cards of the game (draw pile)
        */

        // draw pile
        JButton draw = new JButton();
        draw.setBounds(x_DRAW, y_DRAW, WIDTH_DRAW, HEIGHT_DRAW);
        layeredPane.add(draw);
        ImageIcon back = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("Back.png")));
        draw.setIcon(back);
        setNotClickable(draw);


        /*
        display cards of enemies
        */

        //Labels of enemies
        e1 = new JLabel("Opponent 1");
        e1.setBounds(x_OP1, y_OP1 -24, 250, 25); //y: -20, width: 120, height 15
        layeredPane.add(e1);
        e1.setVisible(false);

        e2 = new JLabel("Opponent 2");
        e2.setBounds(x_OP1, y_OP2 -24, 250, 25);
        layeredPane.add(e2);
        e2.setVisible(false);

        e3 = new JLabel("Opponent 3");
        e3.setBounds(x_OP1, y_OP3 -24, 250, 25);
        layeredPane.add(e3);
        e3.setVisible(false);

        //Stock piles of enemies
        e1_stock = new CardButton();
        e1_stock.setBounds(x_OP1, y_OP1, WIDTH_OP1, HEIGHT_OP1);
        layeredPane.add(e1_stock);
        setNotClickable(e1_stock);
        e1_stock.setVisible(false);

        e2_stock = new CardButton();
        e2_stock.setBounds(x_OP1, y_OP2, WIDTH_OP1, HEIGHT_OP1);
        layeredPane.add(e2_stock);
        setNotClickable(e2_stock);
        e2_stock.setVisible(false);

        e3_stock = new CardButton();
        e3_stock.setBounds(x_OP1, y_OP3, WIDTH_OP1, HEIGHT_OP1);
        layeredPane.add(e3_stock);
        setNotClickable(e3_stock);
        e3_stock.setVisible(false);

        //Discard piles of enemies
        for (int i = 0; i < e1_discard.length; i++) {
            e1_discard[i] = new ArrayList<>();
            e2_discard[i] = new ArrayList<>();
            e3_discard[i] = new ArrayList<>();
            CardButton b1 = new CardButton();
            CardButton b2 = new CardButton();
            CardButton b3 = new CardButton();
            b1.setBounds(x_OP1 + i * x_OP1_DISTANCE + 55, y_OP1, WIDTH_OP1, HEIGHT_OP1);
            b2.setBounds(x_OP1 + i * x_OP1_DISTANCE + 55, y_OP2, WIDTH_OP1, HEIGHT_OP1);
            b3.setBounds(x_OP1 + i * x_OP1_DISTANCE + 55, y_OP3, WIDTH_OP1, HEIGHT_OP1);
            setNotClickable(b1);
            setNotClickable(b2);
            setNotClickable(b3);
            layeredPane.add(b1);
            layeredPane.add(b2);
            layeredPane.add(b3);
            e1_discard[i].add(b1);
            e2_discard[i].add(b2);
            e3_discard[i].add(b3);
            b1.setVisible(false);
            b2.setVisible(false);
            b3.setVisible(false);
        }

        //Displays number of own stock cards left. Name of label corresponds to the number of stock cards left.
        //Further adjustments are done in method setOpponentNames
        numOfStockCards = new JLabel();
        numOfStockCards.setBounds(x_STOCK, y_STOCK + HEIGHT_STOCK +5, 100, 15);
        layeredPane.add(numOfStockCards);

        //creates JLabels for number of stock cards of opponents and sets their bounds. Further adjustments are done in
        //method setOpponentNames
        for (int i = 0; i < oppNumStockCards.length; i++) {
            oppNumStockCards[i] = new JLabel();
            oppNumStockCards[i].setBounds(x_OP1 +(WIDTH_OP1/2)-7, y_OP1 +HEIGHT_OP1+4+i*140, 35, 15);
            oppNumStockCards[i].setVisible(false);
        }

        //creates the label shown when it's your turn
        yourTurnLabel = new JLabel("It's your turn!");
        yourTurnLabel.setBounds(657, 10, 600,100);
        yourTurnLabel.setFont(new Font(DEFAULTFONT.getName(), Font.BOLD, 35));
        yourTurnLabel.setForeground(ChatGraphic.DARKGREEN);
        layeredPane.add(yourTurnLabel);
        yourTurnLabel.setVisible(false);

    }

    /**
     * Sets the opponents names and puts them in an array in the correct order (order of turns). Adds labels of number
     * of stock cards for opponents to layeredPane.
     *
     * @param names Array with names of opponents and own player name.
     */
    void setOpponentNames(String[] names) {
        oppArray = new JLabel[names.length];
        int i = 0;
        if (names[i].equals(playerName)) {
            i++;
            yourTurnLabel.setVisible(true); //if your own name is the first one -> it's your turn
            if (!soundMuted) {
                playTurnSound();
            }
        } else {
            e1.setForeground(ChatGraphic.DARKGREEN);
            e1.setFont(new Font(DEFAULTFONT.getName(), Font.BOLD, DEFAULTFONT.getSize()+5));
        }
        setOpponentVisible(0);
        e1.setText(names[i]);
        oppArray[i] = e1;
        layeredPane.add(oppNumStockCards[0]);
        oppNumStockCards[0].setName(names[i]);
        i++;
        if (names.length > 2) {
            if (names[i].equals(playerName)) {
                i++;
            }
            setOpponentVisible(1);
            e2.setText(names[i]);
            oppArray[i] = e2;
            layeredPane.add(oppNumStockCards[1]);
            oppNumStockCards[1].setName(names[i]);
            i++;
            if (names.length > 3) {
                if (names[i].equals(playerName)) {
                    i++;
                }
                setOpponentVisible(2);
                e3.setText(names[i]);
                oppArray[i] = e3;
                layeredPane.add(oppNumStockCards[2]);
                oppNumStockCards[2].setName(names[i]);
            }
        }
    }

    /**
     * Sets own hand cards and stock card and sets the number of stock cards.
     *
     * @param colAndNum Array of colours and numbers of hand cards (5x alternating) and colour and number of stock card.
     */
    void setInitialCards(String[] colAndNum) {

        for (int i = 0, j = 0; i < hand.length; i++) {
            hand[i].setIcon(cardIcons.getIcon(colAndNum[j], Integer.parseInt(colAndNum[j + 1]), CardIcons.MEDIUM));
            hand[i].addCard(colAndNum[j++], Integer.parseInt(colAndNum[j++]));
        }
        for (int i = 0, j = 10; i < (colAndNum.length - 10) / 3; i++) {
            CardButton stockCard = getEnemyButton(colAndNum[j]);
            j++;
            if (stockCard == null) {
                stock.setIcon(cardIcons.getIcon(colAndNum[j], Integer.parseInt(colAndNum[j + 1]), CardIcons.LARGE));
                stock.addCard(colAndNum[j++], Integer.parseInt(colAndNum[j++]));
            } else {
                stockCard.setIcon(cardIcons.getIcon(colAndNum[j], Integer.parseInt(colAndNum[j + 1]), CardIcons.SMALL));
                stockCard.addCard(colAndNum[j++], Integer.parseInt(colAndNum[j++]));
            }
        }

        //Displays number of own stock cards left. Name of label corresponds to the number of stock cards left.
        numOfStockCards.setText(initialNumStockCards + " cards left");
        numOfStockCards.setName(String.valueOf(initialNumStockCards));

        //creates JLabels for number of stock cards of opponents and sets their bounds. Further adjustments are done in
        //method setOpponentNames
        for (JLabel oppNumStockCard : oppNumStockCards) {
            oppNumStockCard.setText(String.valueOf(initialNumStockCards));
        }
    }

    /**
     * Makes a button not clickable without greying it out (like setEnable(false) would do)
     * @param button Button to make not clickable
     */
    private void setNotClickable(JButton button) {
        button.setFocusPainted(false);
        button.setModel(notClickableModel);
    }

    /**
     * Creates the ButtonModel for buttons that can't be pressed but are not greyed out
     */
    void setButtonModel() {
        notClickableModel = new DefaultButtonModel() {
            public boolean isArmed() {
                return false;
            }
            public boolean isPressed() {
                return false;
            }
            public boolean isRollover() { return false;}
        };
    }

    /**
     * Makes all components belonging to an opponent visible
     * @param oppIndex Index of the opponent
     */
    void setOpponentVisible(int oppIndex) {
        if (oppIndex == 0) {
            e1.setVisible(true);
            e1_stock.setVisible(true);
            oppNumStockCards[0].setVisible(true);
            for (int i = 0; i < 4; i++) {
                e1_discard[i].get(0).setVisible(true);
            }
            return;
        }
        if (oppIndex == 1) {
            e2.setVisible(true);
            e2_stock.setVisible(true);
            oppNumStockCards[1].setVisible(true);
            for (int i = 0; i < 4; i++) {
                e2_discard[i].get(0).setVisible(true);
            }
            return;
        }
        if (oppIndex == 2) {
            e3.setVisible(true);
            e3_stock.setVisible(true);
            oppNumStockCards[2].setVisible(true);
            for (int i = 0; i < 4; i++) {
                e3_discard[i].get(0).setVisible(true);
            }
        }
    }

    /**
     * Plays a hand card to a discard pile
     *
     * @param i      Index of hand card
     * @param j      Index of discard pile
     * @param name   Player name of player who makes the move
     * @param colour Colour of hand card being moved
     * @param number Number of hand card being moved
     */
    void handToDiscard(int i, int j, String name, String colour, int number) {
        if (!soundMuted) {
            playCardSound();
        }
        ArrayList<CardButton> al;
        CardButton newDisCard;

        if (name.equals(playerName)) {
            CardButton handCard = hand[i - 1];
            String col = handCard.removeColour();
            int num = handCard.removeNumber();
            handCard.setIcon(null);
            clientListener.pw.println(Protocol.PUTTO + "§Update§D§" + i + "§" + j + "§" + name + "§" +
                    col + "§" + num);

            al = discard[j - 1];
            CardButton oldDisCard = al.get(al.size() - 1);
            newDisCard = new CardButton(CardButton.DISCARD);
            newDisCard.addActionListener(actionListener);
            oldDisCard.removeActionListener(actionListener);
            newDisCard.setName(" D " + j);
            setBoundsOfDiscard(newDisCard, al, DISTDISCARD);
            newDisCard.setIcon(cardIcons.getIcon(col, num, CardIcons.LARGE));
            newDisCard.addCard(col, num);
            layeredPane.add(newDisCard, Integer.valueOf(al.size()));
            al.add(newDisCard);

            //Paint name of player whose turn it is, is green
            playerIndex = (playerIndex + 1) % oppArray.length;
            oppArray[playerIndex].setForeground(ChatGraphic.DARKGREEN);
            oppArray[playerIndex].setFont(new Font(DEFAULTFONT.getName(), Font.BOLD, DEFAULTFONT.getSize()+5));

            yourTurnLabel.setVisible(false);

        } else {
            al = getEnemyArray(name, j);
            newDisCard = new CardButton();
            setNotClickable(newDisCard);
            setBoundsOfDiscard(newDisCard, al, DISTOPPDISCARD);
            newDisCard.setIcon(cardIcons.getIcon(colour, number, CardIcons.SMALL));
            newDisCard.addCard(colour, number);
            layeredPane.add(newDisCard, Integer.valueOf(al.size()));
            al.add(newDisCard);

            //Paint names of opponents in the right colors
            oppArray[playerIndex].setForeground(Color.BLACK);
            oppArray[playerIndex].setFont(DEFAULTFONT);
            playerIndex = (playerIndex + 1) % oppArray.length;
            if (oppArray[playerIndex] != null) {
                oppArray[playerIndex].setForeground(ChatGraphic.DARKGREEN);
                oppArray[playerIndex].setFont(new Font(DEFAULTFONT.getName(), Font.BOLD, DEFAULTFONT.getSize()+5));
            } else { //it's this players turn
                yourTurnLabel.setVisible(true);
                if (!soundMuted) {
                    playTurnSound();
                }
            }
        }
    }

    /**
     * Sets the location of all cards on a discard pile when a card is added or removed.
     * @param newDisCard The card that is added on the discard pile. If null, the top card will be removed
     * @param al An ArrayList with discard buttons
     * @param distance Distance between two cards of the same discard pile (Y-Direction)
     */
    private void setBoundsOfDiscard(CardButton newDisCard, ArrayList<CardButton> al, int distance) {
        if (newDisCard != null) { //Card will be added
            if (al.size() >= 6) {
                for (int i = al.size() - 4; i < al.size(); i++) {
                    al.get(i).setLocation(al.get(0).getX(), al.get(i).getY() - distance);
                }
            }
            if (al.size() == 1) {
                distance = 0;
            }
            newDisCard.setBounds(al.get(0).getX(), al.get(al.size() - 1).getY() + distance,
                    al.get(0).getWidth(), al.get(0).getHeight());
        } else { //Card will be removed
            if (al.size() >= 6) {
                for (int i = al.size()-4; i < al.size(); i++) {
                    al.get(i).setLocation(al.get(0).getX(), al.get(i).getY() + distance);
                }
            }
        }
    }

    /**
     * Plays a hand card to a build pile
     *
     * @param i      Index of hand card
     * @param j      Index of build pile
     * @param name   Player name of player who makes the move
     * @param colour Colour of hand card being moved
     * @param number Number of hand card being moved
     */
    void handToBuild(int i, int j, String name, String colour, int number) {
        if (!soundMuted) {
            playCardSound();
        }
        if (name.equals(playerName)) {
            CardButton handCard = hand[i - 1];
            CardButton buildCard = build[j - 1];
            String col = handCard.removeColour();
            number = handCard.removeNumber();
            if (number == 13) {
                number = buildCard.getTopNumber() + 1;
            }
            buildCard.addCard(col, number);
            buildCard.setIcon(cardIcons.getIcon(col, number, CardIcons.LARGE));
            handCard.setIcon(null);
            clientListener.pw.println(Protocol.PUTTO + "§Update§B§" + i + "§" + j + "§" + name + "§" +
                    col + "§" + number);
        } else {
            clientLog.debug("set build for everyone");
            CardButton buildCard = build[j - 1];
            buildCard.addCard(colour, number);
            buildCard.setIcon(cardIcons.getIcon(colour, number, CardIcons.LARGE));
        }
        if (number == 12) {
            resetBuildPile(j - 1);
        }
    }

    /**
     * Plays a stock card to a build pile
     *
     * @param j       Index of build pile
     * @param name    Player name of player who makes the move
     * @param colour1 Colour of the new stock card
     * @param number1 Number of the new stock card
     * @param colour2 Colour of the stock card being moved to build pile
     * @param number2 Number of the stock card being moved to build pile
     */
    void stockToBuild(int j, String name, String colour1, int number1, String colour2, int number2) {
        if (!soundMuted) {
            playCardSound();
        }
        //clientLog.debug("(GameGraphic) entered stock to build method");.
        if (name.equals(playerName)) {
            CardButton buildCard = build[j - 1];
            String col = stock.removeColour();
            number2 = stock.removeNumber();
            if (number2 == 13) {
                number2 = buildCard.getTopNumber() + 1;
            }
            buildCard.addCard(col, number2);
            buildCard.setIcon(cardIcons.getIcon(col, number2, CardIcons.LARGE));
            stock.setIcon(cardIcons.getIcon(colour1, number1, CardIcons.LARGE));
            stock.addCard(colour1, number1);

            //Update label with number of stock cards left
            numOfStockCards.setName(String.valueOf(Integer.parseInt(numOfStockCards.getName())-1));
            if (numOfStockCards.getName().equals("1")) {
                numOfStockCards.setText(1 + " card left");
            } else {
                numOfStockCards.setText(Integer.parseInt(numOfStockCards.getName()) + " cards left");
            }

            clientListener.pw.println(Protocol.PUTTO + "§Update§S§" + j + "§" + name + "§" +
                    colour1 + "§" + number1 + "§" + col + "§" + number2);
        } else {
            clientLog.debug("is updating build & stock from enemy");
            CardButton stockCard = getEnemyButton(name);
            CardButton buildCard = build[j - 1];
            buildCard.addCard(colour2, number2);
            buildCard.setIcon(cardIcons.getIcon(colour2, number2, CardIcons.LARGE));
            stockCard.addCard(colour1, number1);
            stockCard.setIcon(cardIcons.getIcon(colour1, number1, CardIcons.SMALL));

            //Update label with number of stock cards left
            JLabel l = getNumOfStockCardsLabel(name);
            assert l != null;
            l.setText(String.valueOf(Integer.parseInt(l.getText())-1));
        }
        if (number2 == 12) {
            resetBuildPile(j - 1);
        }
    }


    /**
     * Plays a card from a discard pile to a build pile
     *
     * @param i    Index of discard pile
     * @param j    Index of build pile
     * @param name Player name of player who made the move
     */
    void discardToBuild(int i, int j, String name) {
        if (!soundMuted) {
            playCardSound();
        }
        CardButton buildCard = build[j - 1];
        CardButton oldDisCard;
        ArrayList<CardButton> al;
        String col;
        int num;
        if (name.equals(playerName)) {
            clientListener.pw.println(Protocol.PUTTO + "§Update§" + i + "§" + j + "§" + name);

            al = discard[i - 1];
            oldDisCard = al.remove(al.size() - 1);
            layeredPane.remove(oldDisCard);
            col = oldDisCard.removeColour();
            num = oldDisCard.removeNumber();
            if (num == 13) {
                num = buildCard.getTopNumber() + 1;
            }
            buildCard.addCard(col, num);
            buildCard.setIcon(cardIcons.getIcon(col, num, CardIcons.LARGE));
            al.get(al.size() - 1).addActionListener(actionListener);
            setBoundsOfDiscard(null, al, DISTDISCARD);
        } else {
            al = getEnemyArray(name, i);
            oldDisCard = al.remove(al.size() - 1);
            layeredPane.remove(oldDisCard);
            col = oldDisCard.removeColour();
            num = oldDisCard.removeNumber();
            if (num == 13) {
                num = buildCard.getTopNumber() + 1;
            }
            buildCard.addCard(col, num);
            buildCard.setIcon(cardIcons.getIcon(col, num, CardIcons.LARGE));
            setBoundsOfDiscard(null, al, DISTOPPDISCARD);
        }
        layeredPane.repaint();
        if (num == 12) {
            resetBuildPile(j - 1);
        }
    }

    /**
     * Refreshes player's hand cards to the correct ones.
     * @param colours Colors of player's cards
     * @param numbers Numbers of player's cards
     */

    void updateHandCards(String[] colours, int[] numbers) {
        for (int i = 0; i < colours.length; i++) {
            hand[i].setIcon(cardIcons.getIcon(colours[i], numbers[i], CardIcons.MEDIUM));
            hand[i].resetCards();
            hand[i].addCard(colours[i], numbers[i]);
        }
        for (int i = 4; i >= colours.length; i--) {
            hand[i].resetCards();
            hand[i].setIcon(null);
        }
    }

    /**
     * Removes all cards from a build pile
     *
     * @param i Index of build pile
     */
    void resetBuildPile(int i) {
        build[i].resetCards();
        build[i].setIcon(null);
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if (button1Pressed == null) {
            button1Pressed = (CardButton) actionEvent.getSource();
            if (button1Pressed.getIcon() == null) {
                button1Pressed = null;
                return;
            }
            button1Pressed.setBorder(clickedBorder);
            changeButtonStates(button1Pressed, false);
        } else if (button1Pressed == actionEvent.getSource()) {
            button1Pressed.setBorder(defaultBorder);
            changeButtonStates(button1Pressed, true);
            button1Pressed = null;
        } else {
            JButton button2Pressed = (JButton) actionEvent.getSource();
            String input = "/play" + button1Pressed.getName() + button2Pressed.getName();
            try {
                clientListener.forward(input);
            } catch (IndexOutOfBoundsException | NotACommandException e) {
                clientLog.warn("Error with /play command");
            }
            button1Pressed.setBorder(defaultBorder);
            changeButtonStates(button1Pressed, true);
            button1Pressed = null;
        }
    }

    /**
     * Method to return discard pile array of enemy
     * @param name  name of enemy
     * @param index index of array
     * @return ArrayList discard pile
     */
    private ArrayList<CardButton> getEnemyArray(String name, int index) {
        ArrayList<CardButton> array = null;
        if (e1.getText().equals(name)) {
            array = e1_discard[index - 1];
        } else if (e2.getText().equals(name)) {
            array = e2_discard[index - 1];
        } else if (e3.getText().equals(name)) {
            array = e3_discard[index - 1];
        }
        return array;
    }

    //returns stock pile button of enemy
    private CardButton getEnemyButton(String name) {
        CardButton button = null;
        if (e1.getText().equals(name)) {
            button = e1_stock;
        } else if (e2.getText().equals(name)) {
            button = e2_stock;
        } else if (e3.getText().equals(name)) {
            button = e3_stock;
        }
        return button;
    }

    private JLabel getNumOfStockCardsLabel(String name) {
        for (JLabel oppNumStockCard : oppNumStockCards) {
            if (oppNumStockCard.getName().equals(name)) {
                return oppNumStockCard;
            }
        }
        return null;
    }


    /**
     * Enables or disables buttons of hand cards, stock cards and build cards
     *
     * @param button A button which is not getting disabled
     * @param b      If true, hand cards, stock cards and discard piles are enabled while the build cards are disabled. If
     *               false it is the other way round.
     */
    void changeButtonStates(CardButton button, boolean b) {
        if (button.getType() == CardButton.HAND) {
            for (CardButton cardButton : hand) {
                if (button != cardButton) {
                    cardButton.setEnabled(b);
                }
            }
            stock.setEnabled(b);
            for (CardButton cardButton : build) {
                if (b) {
                    cardButton.removeActionListener(actionListener);
                } else {
                    cardButton.addActionListener(actionListener);
                }
            }
        } else if (button.getType() == CardButton.DISCARD) {
            for (int i = 0; i < discard.length; i++) {
                for (CardButton cardButton : discard[i]) {
                    if (cardButton != button) {
                        cardButton.setEnabled(b);
                    }
                }
                if (b) {
                        build[i].removeActionListener(actionListener);
                } else {
                    build[i].addActionListener(actionListener);
                }
            }
            for (CardButton cardButton : hand) {
                cardButton.setEnabled(b);
            }
            stock.setEnabled(b);
        } else { //button is stock button
            for (CardButton cardButton : hand) {
                cardButton.setEnabled(b);
            }
            for (int i = 0; i < build.length; i++) {
                if (b) {
                    build[i].removeActionListener(actionListener);
                } else {
                    build[i].addActionListener(actionListener);
                }
                for (CardButton cardButton : discard[i]) {
                    cardButton.setEnabled(b);
                }
            }
        }
    }

    /**
     * Executes the joker-cheat. Adds a joker to every discard pile and adds 5 stock cards.
     */
    void cheatJoker(String name) {
        ArrayList<CardButton> al;
        CardButton newDisCard;
        if (name.equals(playerName)) {
            numOfStockCards.setName(String.valueOf(Integer.parseInt(numOfStockCards.getName())+5));
            numOfStockCards.setText(Integer.parseInt(numOfStockCards.getName()) + " cards left");
            for (int i = 1; i < 5; i++) {
                al = discard[i-1];
                CardButton oldDisCard = al.get(al.size() - 1);
                newDisCard = new CardButton(CardButton.DISCARD);
                newDisCard.addActionListener(actionListener);
                oldDisCard.removeActionListener(actionListener);
                newDisCard.setName(" D " + i);
                setBoundsOfDiscard(newDisCard, al, DISTDISCARD);
                newDisCard.setIcon(cardIcons.getIcon("S", 13, CardIcons.LARGE));
                newDisCard.addCard("S", 13);
                layeredPane.add(newDisCard, Integer.valueOf(al.size()));
                al.add(newDisCard);
            }

        } else {
            JLabel l = getNumOfStockCardsLabel(name);
            assert l != null;
            l.setText(String.valueOf(Integer.parseInt(l.getText())+5));
            for (int i = 1; i < 5; i++) {
                al = getEnemyArray(name, i);
                newDisCard = new CardButton();
                setNotClickable(newDisCard);
                setBoundsOfDiscard(newDisCard, al, DISTOPPDISCARD);
                newDisCard.setIcon(cardIcons.getIcon("S", 13, CardIcons.SMALL));
                newDisCard.addCard("S", 13);
                layeredPane.add(newDisCard, Integer.valueOf(al.size()));
                al.add(newDisCard);
            }
        }
    }

    /**
     * Removes a player that quits the game
     * @param name Player that quit the game
     */
    void removePlayer(String name) {
        clientLog.debug("Got into removePlayer method in GameGraphic");

        //disable discard piles
        for (int i = 1; i < 5; i++) {
            ArrayList<CardButton> al =  getEnemyArray(name, i);
            for (CardButton cardButton : al) {
                layeredPane.remove(cardButton);
                //cardButton.setEnabled(false);
            }
        }

        layeredPane.remove(getEnemyButton(name));
        layeredPane.remove(Objects.requireNonNull(getNumOfStockCardsLabel(name)));

        int indexOfPlayerLeft = -1;
        //remove from oppArray
        JLabel[] newOppArray = new JLabel[oppArray.length-1];
        for (int i = 0, j = 0; i < oppArray.length; i++) {
            if (oppArray[i] != null) {
                if (!oppArray[i].getText().equals(name)) {
                    newOppArray[j] = oppArray[i];
                } else {
                    indexOfPlayerLeft = i;
                    layeredPane.remove(oppArray[i]);
                    //oppArray[i].setForeground(Color.GRAY);
                    j--;
                }
            }
            j++;
        }

        //Paint names of opponents in the right colors
        if (oppArray[playerIndex] != null && oppArray[playerIndex].getText().equals(name)) {
            oppArray[playerIndex].setFont(DEFAULTFONT);
            playerIndex = playerIndex % newOppArray.length;
            if (newOppArray[playerIndex] != null) {
                newOppArray[playerIndex].setForeground(ChatGraphic.DARKGREEN);
                newOppArray[playerIndex].setFont(new Font(DEFAULTFONT.getName(), Font.BOLD, DEFAULTFONT.getSize()+5));
            } else { //it's this players turn
                yourTurnLabel.setVisible(true);
            }
        }

        if (indexOfPlayerLeft < playerIndex) {
            playerIndex--;
        }
        layeredPane.repaint();
        oppArray = newOppArray;
    }

    void setStockSize(int stockSize) {
        initialNumStockCards = stockSize;
    }

    ArrayList<String> getOpponentNames() {
        ArrayList<String> al = new ArrayList<>();
        for (JLabel jLabel : oppArray) {
            if (jLabel != null) {
                al.add(jLabel.getText());
            }
        }
        return al;
    }

    /**
     * Method to play the card playing sound byte.
     */
    void playCardSound(){
        MusicPlayer cardSound = new MusicPlayer();
        if(cardSound.loadFile("src/main/resources/cardsound.mp3")){
            cardSound.play();
        }
    }

    void playTurnSound() {
        MusicPlayer turnSound = new MusicPlayer();
        if (turnSound.loadFile("src/main/resources/turnsound.mp3")) {
            turnSound.play();
        }
    }

    void setSoundMuted(boolean soundMuted) {
        this.soundMuted = soundMuted;
    }

    JLayeredPane getGameComponent() {
        return layeredPane;
    }

}
