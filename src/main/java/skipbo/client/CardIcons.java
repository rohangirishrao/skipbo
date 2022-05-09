package skipbo.client;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

class CardIcons {

    private final ImageIcon[][] icons;
    static final int LARGE = 0;
    static final int MEDIUM = 1;
    static final int SMALL = 2;

    CardIcons(int widthSmall, int heightSmall, int widthMedium, int heightMedium) {

        ImageIcon[] rL = new ImageIcon[12];
        ImageIcon[] gL = new ImageIcon[12];
        ImageIcon[] bL = new ImageIcon[12];

        ImageIcon[] rM = new ImageIcon[12];
        ImageIcon[] gM = new ImageIcon[12];
        ImageIcon[] bM = new ImageIcon[12];

        ImageIcon[] rS = new ImageIcon[12];
        ImageIcon[] gS = new ImageIcon[12];
        ImageIcon[] bS = new ImageIcon[12];

        ImageIcon[] skipbo = new ImageIcon[15];

        Image image;
        for (int i = 1; i <= 12; i++) {
            rL[i - 1] = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("R" + i + ".png")));
            image = rL[i - 1].getImage().getScaledInstance(widthMedium, heightMedium, Image.SCALE_SMOOTH);
            rM[i - 1] = new ImageIcon(image);
            image = rL[i - 1].getImage().getScaledInstance(widthSmall, heightSmall, Image.SCALE_SMOOTH);
            rS[i - 1] = new ImageIcon(image);

            gL[i - 1] = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("G" + i + ".png")));
            image = gL[i - 1].getImage().getScaledInstance(widthMedium, heightMedium, Image.SCALE_SMOOTH);
            gM[i - 1] = new ImageIcon(image);
            image = gL[i - 1].getImage().getScaledInstance(widthSmall, heightSmall, Image.SCALE_SMOOTH);
            gS[i - 1] = new ImageIcon(image);

            bL[i - 1] = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("B" + i + ".png")));
            image = bL[i - 1].getImage().getScaledInstance(widthMedium, heightMedium, Image.SCALE_SMOOTH);
            bM[i - 1] = new ImageIcon(image);
            image = bL[i - 1].getImage().getScaledInstance(widthSmall, heightSmall, Image.SCALE_SMOOTH);
            bS[i - 1] = new ImageIcon(image);

            skipbo[i - 1] = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("S" + i + ".png")));

        }

        skipbo[12] = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("skipbo.png")));
        image = skipbo[12].getImage().getScaledInstance(widthMedium, heightMedium, Image.SCALE_SMOOTH);
        skipbo[13] = new ImageIcon(image);
        image = skipbo[12].getImage().getScaledInstance(widthSmall, heightSmall, Image.SCALE_SMOOTH);
        skipbo[14] = new ImageIcon(image);

        icons = new ImageIcon[][]{rL, rM, rS, gL, gM, gS, bL, bM, bS, skipbo};

    }

    ImageIcon getIcon(String color, int number, int size) {

        if (color == null || number == -1) {
            return null;
        }

        int i = 0;

        switch (color) {
            case "R":
                i = 0;
                break;
            case "G":
                i = 3;
                break;
            case "B":
                i = 6;
                break;
        }

        if (size == MEDIUM) {
            i++;
        } else if (size == SMALL) {
            i = i + 2;
        }

        if (color.equals("S")) {
            number += i;
            i = 9;
        }

        return icons[i][number - 1];
    }
}
