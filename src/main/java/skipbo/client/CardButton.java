package skipbo.client;

import javax.swing.*;
import java.util.ArrayList;

public class CardButton extends JButton {

    static final int HAND = 0;
    static final int DISCARD = 1;
    static final int STOCK = 2;
    static final int BUILD = 3;

    private int type = -1;

    private final ArrayList<String> colors;
    private final ArrayList<Integer> numbers;

    CardButton(int type) {
        super();
        colors = new ArrayList<>();
        numbers = new ArrayList<>();
        this.type = type;
    }

    CardButton() {
        super();
        colors = new ArrayList<>();
        numbers = new ArrayList<>();
    }

    void addCard(String color, int number) {
        colors.add(color);
        numbers.add(number);
    }

    int getType() {
        return type;
    }

    /**
     * @return Number of the top Card. If there is no Card it returns 0.
     */
    int getTopNumber() {
        if (numbers.size() > 0) {
            return numbers.get(numbers.size() - 1);
        } else {
            return 0;
        }
    }


    String removeColour() {
        return colors.remove(0);
    }

    int removeNumber() {
        return numbers.remove(0);
    }

    void resetCards() {
        colors.clear();
        numbers.clear();
    }


}
