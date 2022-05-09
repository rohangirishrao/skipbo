package skipbo.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Tutorial extends GameGraphic implements ActionListener {

    ChatGraphic chatGraphic;
    int moveNumber = 1;
    boolean allowMove = false;
    JTextArea instruction = new JTextArea();
    CardButton chosenBuildButton;
    ArrayList<CardButton> chosenDiscardPile;

    Font arrowFont = new Font(DEFAULTFONT.getName(), Font.BOLD, 35);

    JLabel downArrow = new JLabel("\u2B07");
    JLabel upArrow = new JLabel("\u2B06");


    private int delay = 2000; //2000

    Tutorial(ChatGraphic chatGraphic) {
        super();
        this.chatGraphic = chatGraphic;
        actionListener = this;
        setArrowFonts();
        appendDecks();
        adjustGameGraphic();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                moveOne();
            }
        };
        timer.schedule(task,1000);
    }

    private void adjustGameGraphic() {
        yourTurnLabel.setBounds(657, 0, 600,100);
        layeredPane.add(instruction, Integer.valueOf(-1));
        e1.setText("Bob");
        e2.setText("Alice");
        setOpponentVisible(0);
        setOpponentVisible(1);
        yourTurnLabel.setVisible(true);
        initialNumStockCards = 2;
        numOfStockCards.setText(initialNumStockCards + " cards left");
        oppNumStockCards[0].setText(String.valueOf(initialNumStockCards));
        oppNumStockCards[1].setText(String.valueOf(initialNumStockCards));
        layeredPane.add(oppNumStockCards[0]);
        layeredPane.add(oppNumStockCards[1]);

        hand[0].setIcon(cardIcons.getIcon("R",10, CardIcons.MEDIUM));
        hand[1].setIcon(cardIcons.getIcon("B",7, CardIcons.MEDIUM));
        hand[2].setIcon(cardIcons.getIcon("G", 10, CardIcons.MEDIUM));
        hand[3].setIcon(cardIcons.getIcon("B", 1, CardIcons.MEDIUM));
        hand[4].setIcon(cardIcons.getIcon("R",12, CardIcons.MEDIUM));
        stock.setIcon(cardIcons.getIcon("R",2, CardIcons.LARGE));

        e1_stock.setIcon(cardIcons.getIcon("B",8, CardIcons.SMALL));
        e2_stock.setIcon(cardIcons.getIcon("G",4, CardIcons.SMALL));

        instruction.setBackground(Color.orange);
        instruction.setFont(new Font(DEFAULTFONT.getName(), Font.BOLD, DEFAULTFONT.getSize()+3));
        instruction.setForeground(ChatGraphic.DARKGREEN);
        instruction.setEditable(false);

    }

    /**
     * Introduces the player to the game. Asks to click on hand card 2.
     */
    private void moveOne() {
        try {
            playTurnSound();
            upArrow.setBounds(400, 495, 50, 50);
            layeredPane.add(upArrow);
            instruction.setBounds(440, 500, 300, 100);
            instruction.setText("Your goal is to play all of\nyour stock cards on the build piles.");
            Thread.sleep(4500); //5000
            layeredPane.remove(upArrow);
            layeredPane.repaint();
            downArrow.setBounds(831, 575, 50, 50);
            layeredPane.add(downArrow, Integer.valueOf(-1));
            instruction.setBounds(861, 580, 300, 100);
            instruction.setText("Click on your hand card to grab it.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        allowMove = true;
    }

    /**
     *Asks player to click on a build pile.
     */
    private void moveTwo() {
        moveNumber = 2;
        layeredPane.remove(downArrow);
        layeredPane.repaint();
        downArrow.setBounds(680, 75, 50, 50);
        layeredPane.add(downArrow);
        instruction.setBounds(710, 80, 400, 100);
        instruction.setText("Click on any build pile to play the one.");
        allowMove = true;
    }

    /**
     *Puts the one on the build pile and asks player to click on stock pile.
     */
    private void moveThree() {
        moveNumber = 3;
        if (!soundMuted) {
            playCardSound();
        }
        chosenBuildButton.setIcon(cardIcons.getIcon("B", 1, CardIcons.LARGE));
        hand[3].setIcon(hand[4].getIcon());
        hand[4].setIcon(null);
        layeredPane.remove(downArrow);
        layeredPane.repaint();
        upArrow.setBounds(400, 495, 50, 50);
        layeredPane.add(upArrow);
        instruction.setBounds(440, 500, 400, 100);
        instruction.setText("Now you can play your first stock card\non the build pile.");
        allowMove = true;
    }

    /**
     * Asks the player to click on the build pile
     */
    private void moveFour() {
        moveNumber = 4;
        layeredPane.remove(upArrow);
        layeredPane.repaint();
        instruction.setText("Put the stock card on the build pile.");
        if (chosenBuildButton == build[0]) {
            downArrow.setBounds(chosenBuildButton.getX()+65,75,50,50);
            instruction.setBounds(chosenBuildButton.getX()+95,80,400,100);
        } else {
            downArrow.setBounds(chosenBuildButton.getX()+20,75,50,50);
            instruction.setBounds(chosenBuildButton.getX()+50,80,400,100);
        }
        layeredPane.add(downArrow);
        allowMove = true;
    }

    /**
     * Puts the stock card on the build pile and asks player to finish their turn.
     */
    private void moveFive() {
        moveNumber = 5;
        if (!soundMuted) {
            playCardSound();
        }
        chosenBuildButton.setIcon(stock.getIcon());
        stock.setIcon(cardIcons.getIcon("B", 6, CardIcons.LARGE));
        numOfStockCards.setText("1 card left");
        downArrow.setBounds(831, 575, 50, 50);
        instruction.setBounds(865, 580, 400, 100);
        instruction.setText("Play the 12 on a discard pile to end your turn.\nIn a normal game you could play any hand card.");
        allowMove = true;
    }

    /**
     * Asks the player to click on a discard pile
     */
    private void moveSix() {
        moveNumber = 6;
        layeredPane.remove(downArrow);
        layeredPane.repaint();
        upArrow.setBounds(700, 475, 50, 50);
        layeredPane.add(upArrow);
        instruction.setText("Play your hand card on any discard pile.");
        instruction.setBounds(730, 500, 400, 100);
        allowMove = true;
    }

    /**
     * Puts the hand card on the discard pile, makes opponents turns and asks player to play the joker.
     */
    private void moveSeven() {
        moveNumber = 7;
        if (!soundMuted) {
            playCardSound();
        }
        layeredPane.remove(upArrow);
        instruction.setText("");
        layeredPane.repaint();
        hand[3].setIcon(null);
        chosenDiscardPile.get(0).setIcon(cardIcons.getIcon("R",12, CardIcons.LARGE));
        yourTurnLabel.setText("Please wait, it's Bob's turn.");
        yourTurnLabel.setFont(new Font(DEFAULTFONT.getName(), Font.BOLD, 25));
        e1.setForeground(ChatGraphic.DARKGREEN);
        e1.setFont(new Font(DEFAULTFONT.getName(), Font.BOLD, DEFAULTFONT.getSize()+5));
        TimerTask opponentTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    chosenBuildButton.setIcon(cardIcons.getIcon("B", 3, CardIcons.LARGE));
                    Thread.sleep(delay);
                    e1_discard[3].get(0).setIcon(cardIcons.getIcon("R", 9, CardIcons.SMALL));
                    yourTurnLabel.setText("Please wait, it's Alice's turn.");
                    e1.setFont(DEFAULTFONT);
                    e1.setForeground(Color.BLACK);
                    e2.setFont(new Font(DEFAULTFONT.getName(), Font.BOLD, DEFAULTFONT.getSize()+5));
                    e2.setForeground(ChatGraphic.DARKGREEN);
                    Thread.sleep(delay);
                    chosenBuildButton.setIcon(cardIcons.getIcon("G",4, CardIcons.LARGE));
                    e2_stock.setIcon(cardIcons.getIcon("R", 11, CardIcons.SMALL));
                    oppNumStockCards[1].setText("1");
                    Thread.sleep(delay);
                    if (build[1] == chosenBuildButton) {
                        build[3].setIcon(cardIcons.getIcon("B", 1, CardIcons.LARGE));
                    } else {
                        build[1].setIcon(cardIcons.getIcon("B", 1, CardIcons.LARGE));
                    }
                    Thread.sleep(delay);
                    e2_discard[1].get(0).setIcon(cardIcons.getIcon("B", 3, CardIcons.SMALL));
                    e2.setFont(DEFAULTFONT);
                    e2.setForeground(Color.BLACK);

                    hand[3].setIcon(cardIcons.getIcon("G", 3, CardIcons.MEDIUM));
                    hand[4].setIcon(cardIcons.getIcon("S", 13, CardIcons.MEDIUM));

                    yourTurnLabel.setText("It's your turn!");
                    playTurnSound();
                    yourTurnLabel.setFont(new Font(DEFAULTFONT.getName(), Font.BOLD, 35));
                    downArrow.setBounds(919, 575, 50, 50);
                    layeredPane.add(downArrow);
                    instruction.setBounds(953, 580, 400, 100);
                    instruction.setText("Play the joker on the build pile.");
                    allowMove = true;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(opponentTask, delay);

    }

    /**
     * Asks player to put the joker on the build pile.
     */
    private void moveEight() {
        moveNumber = 8;
        instruction.setText("Put the joker on the build pile.");
        if (chosenBuildButton == build[0]) {
            downArrow.setBounds(chosenBuildButton.getX()+65,75,50,50);
            instruction.setBounds(chosenBuildButton.getX()+95,80,400,100);
        } else {
            downArrow.setBounds(chosenBuildButton.getX()+20,75,50,50);
            instruction.setBounds(chosenBuildButton.getX()+50,80,400,100);
        }
        allowMove = true;
    }

    /**
     * Asks player to play the last stock card.
     */
    private void moveNine() {
        moveNumber = 9;
        if (!soundMuted) {
            playCardSound();
        }
        layeredPane.remove(downArrow);
        layeredPane.repaint();
        chosenBuildButton.setIcon(cardIcons.getIcon("S", 5, CardIcons.LARGE));
        hand[4].setIcon(null);
        upArrow.setBounds(400, 495, 50, 50);
        layeredPane.add(upArrow);
        instruction.setBounds(440, 500, 400, 100);
        instruction.setText("Play your last stock card on the build pile\nto win the game.");
        allowMove = true;
    }

    /**
     * Asks player to put the stock card on the build pile.
     */
    private void moveTen() {
        moveNumber = 10;
        layeredPane.remove(upArrow);
        layeredPane.repaint();
        instruction.setText("Put the stock card on the build pile.");
        if (chosenBuildButton == build[0]) {
            downArrow.setBounds(chosenBuildButton.getX()+65,75,50,50);
            instruction.setBounds(chosenBuildButton.getX()+95,80,400,100);
        } else {
            downArrow.setBounds(chosenBuildButton.getX()+20,75,50,50);
            instruction.setBounds(chosenBuildButton.getX()+50,80,400,100);
        }
        layeredPane.add(downArrow);
        allowMove = true;
    }

    /**
     *Ends the tutorial.
     */
    private void endTutorial() {
        if (!soundMuted) {
            playCardSound();
        }
        chosenBuildButton.setIcon(cardIcons.getIcon("B", 6, CardIcons.LARGE));
        stock.setIcon(null);
        numOfStockCards.setText("0 cards left");
        layeredPane.remove(instruction);
        layeredPane.remove(downArrow);
        layeredPane.repaint();
        chatGraphic.endGame("you", true, null);
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (!allowMove) {
            return;
        }
        CardButton buttonPressed = (CardButton) actionEvent.getSource();
        if (moveNumber == 1 && buttonPressed != hand[3]) {
            return;
        } else if (moveNumber == 1) {
            allowMove = false;
            buttonPressed.setBorder(clickedBorder);
            changeButtonStates(buttonPressed, false);
            moveTwo();
        }
        if (moveNumber == 2 && !isBuildButton(buttonPressed)) {
            return;
        } else if (moveNumber == 2) {
            allowMove = false;
            chosenBuildButton = buttonPressed;
            hand[3].setBorder(defaultBorder);
            changeButtonStates(hand[3], true);
            moveThree();
        }
        if (moveNumber == 3 && buttonPressed != stock) {
            return;
        } else if (moveNumber == 3) {
            allowMove = false;
            buttonPressed.setBorder(clickedBorder);
            changeButtonStates(buttonPressed, false);
            moveFour();
        }
        if (moveNumber == 4 && buttonPressed != chosenBuildButton) {
            return;
        } else if (moveNumber == 4) {
            allowMove = false;
            stock.setBorder(defaultBorder);
            changeButtonStates(stock, true);
            moveFive();
        }
        if (moveNumber == 5 && buttonPressed != hand[3]) {
            return;
        } else if (moveNumber == 5) {
            allowMove = false;
            buttonPressed.setBorder(clickedBorder);
            changeButtonStates(buttonPressed, false);
            moveSix();
        }
        if (moveNumber == 6 && getDiscardPile(buttonPressed) == null) {
            return;
        } else if (moveNumber == 6) {
            allowMove = false;
            chosenDiscardPile = getDiscardPile(buttonPressed);
            hand[3].setBorder(defaultBorder);
            changeButtonStates(hand[3], true);
            moveSeven();
        }
        if (moveNumber == 7 && buttonPressed != hand[4]) {
            return;
        } else if (moveNumber == 7) {
            allowMove = false;
            buttonPressed.setBorder(clickedBorder);
            changeButtonStates(buttonPressed, false);
            moveEight();
        }
        if (moveNumber == 8 && buttonPressed != chosenBuildButton) {
            return;
        } else if (moveNumber == 8) {
            allowMove = false;
            hand[4].setBorder(defaultBorder);
            changeButtonStates(hand[4], true);
            moveNine();
        }
        if (moveNumber == 9 && buttonPressed !=stock) {
            return;
        } else if (moveNumber == 9) {
            allowMove = false;
            stock.setBorder(clickedBorder);
            changeButtonStates(stock, false);
            moveTen();
        }
        if (moveNumber == 10 && buttonPressed == chosenBuildButton) {
            allowMove = false;
            stock.setBorder(defaultBorder);
            changeButtonStates(stock, true);
            endTutorial();
        }
    }

    private void setArrowFonts() {
        upArrow.setFont(arrowFont);
        downArrow.setFont(arrowFont);

        upArrow.setForeground(ChatGraphic.DARKGREEN);
        downArrow.setForeground(ChatGraphic.DARKGREEN);

    }

    private boolean isBuildButton(CardButton button) {
        for (CardButton cardButton : build) {
            if (button == cardButton) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<CardButton> getDiscardPile(CardButton button) {
        for (ArrayList<CardButton> cardButtons : discard) {
            if (cardButtons.contains(button)) {
                return cardButtons;
            }
        }
        return null;
    }
}
