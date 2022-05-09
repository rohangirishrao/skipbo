package skipbo.client;

import skipbo.server.Protocol;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

import static skipbo.client.SBClient.clientLog;
import static skipbo.server.Protocol.*;

/**
 * GUI for Skip-Bo lobby
 */
public class ChatGraphic extends JFrame implements KeyListener, ActionListener {

    private final SBClientListener clientListener;
    private Container contentPane;
    private JTextPane chat;
    private JTextArea inputMes;
    private JScrollPane chatScrollPane;
    private JButton manualB;
    private JButton changeNameB;
    private JButton readyB;
    private JButton startB;
    private JButton infoB;
    private JButton gamesB;
    private JButton pause;
    private JButton soundB;
    private JButton whosOnB;
    private JButton leaveB;
    private JButton tutorialB;
    private JTextPane highScore;
    private GameGraphic gameGraphic = null;
    private String playerName = "";
    private DefaultComboBoxModel<String> playerComboModel;
    private ArrayList<String> playerArray = new ArrayList<>();
    MusicPlayer backgroundMusic;
    private boolean unmuteIsBlocked = false;
    boolean soundMuted = false;

    static final Color DARKGREEN = new Color(0x0AB222);

    private final int X_FRAME = 400;
    private final int Y_FRAME = 20;
    private final int WIDTH_FRAME = 420;
    private final int HEIGHT_FRAME = 830;

    private boolean isTesting = false;

    /**
     * Constructor for ChatGraphic without player name. Lets client choose their name.
     *
     * @param clientListener A SBClientListener
     */
    ChatGraphic(SBClientListener clientListener) {
        this.clientListener = clientListener;
        setFrame();
        setName();
        printInfoMessage("Connection successful");
        printCommandList();
        playMusic();
        clientListener.setChatGraphic(this);
    }

    /**
     * Only for testing purposes.
     */
    ChatGraphic(SBClientListener clientListener, boolean testing) {
        isTesting = testing;
        if (testing) {
            soundMuted = true;
        }
        this.clientListener = clientListener;
        setFrame();
        if(!testing) setName();
        printInfoMessage("Connection successful");
        printCommandList();
        clientListener.setChatGraphic(this);
    }

    /**
     * Constructor for ChatGraphic with player name
     *
     * @param clientListener A SBClientListener
     * @param name           The player name that was initially chosen when starting the program
     */
    ChatGraphic(SBClientListener clientListener, String name, boolean testing) {
        isTesting = testing;
        this.clientListener = clientListener;
        setFrame();
        clientListener.pw.println("SETTO§Nickname§" + name);
        printInfoMessage("Connection successful");
        printCommandList();
        if (!testing) {
            playMusic();
        } else {
            soundMuted = true;
        }
        clientListener.setChatGraphic(this);
    }

    /**
     * Creates the chat window with buttons
     */
    void setFrame() {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            clientLog.warn("Error with setting LookAndFeel");
        }

        setTitle("Skip-Bros CHAT");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(X_FRAME, Y_FRAME, WIDTH_FRAME, HEIGHT_FRAME);

        contentPane = getContentPane();
        contentPane.setBackground(Color.orange);
        contentPane.setLayout(null);

        // Logo on top
        ImageIcon logoI = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("logo.png")));
        Image image = logoI.getImage().getScaledInstance(210, 160, Image.SCALE_DEFAULT);
        ImageIcon scaledIcon = new ImageIcon(image);
        JTextPane logoJ = new JTextPane();
        logoJ.setBorder(null);
        logoJ.setEditable(false);
        logoJ.setBounds(100, 30, scaledIcon.getIconWidth(), scaledIcon.getIconHeight());
        logoJ.setPreferredSize(new Dimension(scaledIcon.getIconWidth(), scaledIcon.getIconHeight()));
        logoJ.insertIcon(scaledIcon);
        contentPane.add(logoJ);

        // Layout Manager for menu buttons
        final int WIDTH_MENU_B = 120;
        final int HEIGHT_MENU_B = 22;
        final int X_MENU_B_R1 = 80;
        final int X_MENU_B_R2 = 210;
        final int Y_MENU_B = 195;
        final int Y_DISTANCE_MENU_B = 30;

        manualB = new JButton("Manual");
        manualB.setBounds(X_MENU_B_R1, Y_MENU_B, WIDTH_MENU_B, HEIGHT_MENU_B);
        contentPane.add(manualB);
        manualB.addActionListener(this);

        changeNameB = new JButton("Change name");
        changeNameB.setBounds(X_MENU_B_R2, Y_MENU_B, WIDTH_MENU_B, HEIGHT_MENU_B);
        contentPane.add(changeNameB);
        changeNameB.addActionListener(this);

        infoB = new JButton("Info");
        infoB.setBounds(X_MENU_B_R1, Y_MENU_B+ Y_DISTANCE_MENU_B, WIDTH_MENU_B, HEIGHT_MENU_B);
        contentPane.add(infoB);
        infoB.addActionListener(this);

        gamesB = new JButton("Games list");
        gamesB.setBounds(X_MENU_B_R2, Y_MENU_B+ Y_DISTANCE_MENU_B, WIDTH_MENU_B, HEIGHT_MENU_B);
        contentPane.add(gamesB);
        gamesB.addActionListener(this);

        whosOnB = new JButton("Player list");
        whosOnB.setBounds(X_MENU_B_R1, Y_MENU_B+ 2*Y_DISTANCE_MENU_B, WIDTH_MENU_B, HEIGHT_MENU_B);
        contentPane.add(whosOnB);
        whosOnB.addActionListener(this);

        leaveB = new JButton("Leave game");
        leaveB.setBounds(X_MENU_B_R2, Y_MENU_B+ 2*Y_DISTANCE_MENU_B, WIDTH_MENU_B, HEIGHT_MENU_B);
        contentPane.add(leaveB);
        leaveB.addActionListener(this);
        leaveB.setEnabled(false);

        Color gameButtonColor = new Color (153,255,153);

        readyB = new JButton("Ready");
        readyB.setBackground(gameButtonColor);
        readyB.setBounds(X_MENU_B_R1, Y_MENU_B+ 3*Y_DISTANCE_MENU_B+8, WIDTH_MENU_B, HEIGHT_MENU_B);
        contentPane.add(readyB);
        readyB.addActionListener(this);

        startB = new JButton("Start Game");
        startB.setBackground(gameButtonColor);
        startB.setBounds(X_MENU_B_R2, Y_MENU_B+ 3*Y_DISTANCE_MENU_B+8, WIDTH_MENU_B, HEIGHT_MENU_B);
        contentPane.add(startB);
        startB.addActionListener(this);

        tutorialB = new JButton("Tutorial");
        tutorialB.setBounds(5, 5, 120, 22);
        contentPane.add(tutorialB);
        tutorialB.addActionListener(this);

        pause = new JButton();
        pause.setName("Mute");
        pause.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("not_muted.png"))));
        pause.setBounds(X_MENU_B_R2+96, Y_MENU_B+ 4*Y_DISTANCE_MENU_B+8, 24, 24);
        contentPane.add(pause);
        pause.addActionListener(this);

        soundB = new JButton();
        soundB.setName("on");
        soundB.setBounds(X_MENU_B_R2+68, Y_MENU_B+ 4*Y_DISTANCE_MENU_B+8, 24, 24); //x:72
        soundB.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("fx_on.png"))));
        contentPane.add(soundB);
        soundB.addActionListener(this);


        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(80, 330, 250, 340);
        contentPane.add(tabbedPane);

        //Output textfield
        chat = new JTextPane();
        chat.setBounds(80, 330, 250, 340);
        chat.setEditable(false);
        chat.setEditorKit(new WrapEditorKit());

        chatScrollPane = new JScrollPane(chat);
        chatScrollPane.setBounds(80, 330, 250, 340);
        chatScrollPane.setVisible(true);
        chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.setName("Chat");
        tabbedPane.add(chatScrollPane);

        DefaultCaret caret = (DefaultCaret) chat.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);


        // Dropdown menu for global or private chat
        JLabel privateChatL = new JLabel("Chat with:");
        add(privateChatL);
        privateChatL.setBounds(80,675,90,20);
        JComboBox<String> listChat = new JComboBox<>();
        playerComboModel = (DefaultComboBoxModel<String>) listChat.getModel();
        playerComboModel.addAll(Arrays.asList("lobby", "broadcast"));
        listChat.setSelectedIndex(0);
        listChat.setVisible(true);
        listChat.setBounds(150,675,180,20);
        add(listChat);


        //Input textfield
        inputMes = new JTextArea();
        inputMes.setBounds(80, 700, 250, 60);
        inputMes.setEditable(true);
        inputMes.setColumns(3);
        inputMes.setLineWrap(true);
        inputMes.setWrapStyleWord(true);
        inputMes.addKeyListener(this);
        contentPane.add(inputMes);

        JScrollPane inputScrollPane = new JScrollPane(inputMes);
        inputScrollPane.setBounds(80, 700, 250, 60);
        inputScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(inputScrollPane);

        highScore = new JTextPane();
        highScore.setBounds(80, 330, 250, 340);
        highScore.setEditable(false);

        JScrollPane scoreScrollPane = new JScrollPane(highScore);
        scoreScrollPane.setBounds(80, 330, 250, 340);
        scoreScrollPane.setVisible(true);
        scoreScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scoreScrollPane.setName("High score");
        tabbedPane.add(scoreScrollPane);

        DefaultCaret scoreCaret = (DefaultCaret) highScore.getCaret();
        scoreCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

    }


    /**
     * Sends Information about valid commands to the client
     */
    void printCommandList() {
        String infoCreators = "This game was developed by Manuela, Janni, Guillaume and Rohan for cs-108 in 2020!";
        printInfoMessage(infoCreators);
    }

    /**
     * Displays an information to the client
     *
     * @param message An information message
     */
    void printInfoMessage(String message) {
        appendToChat("\n[Info] ", DARKGREEN);
        appendToChat(message, Color.BLACK);
    }

    /**
     * Displays an error message to the client
     *
     * @param message An error message
     */
    void printErrorMessage(String message) {
        appendToChat("\n[Error] ", Color.RED);
        appendToChat(message, Color.BLACK);
    }

    /**
     * Displays a chat message in the chat
     *
     * @param message A chat message
     */
    void printChatMessage(String message) {
        appendToChat("\n" + message, Color.BLACK);
    }

    /**
     * Creates the game GUI
     */
    void setGameGraphic(boolean isTutorial) {
        if (isTutorial) {
            gameGraphic = new Tutorial(this);
            leaveB.setEnabled(false);
        } else {
            gameGraphic = new GameGraphic(clientListener, playerName, chat);
            tutorialB.setEnabled(false);
            leaveB.setEnabled(true);
            changeNameB.setEnabled(false);
        }
        gameGraphic.setSoundMuted(soundMuted);
        contentPane.add(gameGraphic.getGameComponent());
        setTitle("Skip-Bros GAME");
        setBounds(100, 100, 1150, 800);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        startB.setEnabled(false);
        readyB.setEnabled(false);
        readyB.setText("Ready");
    }

    /**
     * Ends the game. Removes the game graphic from the frame and displays the winners name. Sends player back to main lobby.
     * @param name Name of the winner of the game.
     */
    void endGame(String name, boolean isTutorial, String score) {
        String message = null;
        if (!isTutorial) {
            if (name != null) {
                message = "The winner is: " + name + "! Score: " + score;
            } else {
                message = "Game ended because everyone else left. There is no winner.";
            }
        } else if (name != null) {
            message = "You won because you played all your stock cards! You are now ready to play a real game.";
        }

        if (message != null && !isTesting) {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("logo.png")));

            int iconWidth = icon.getIconWidth();
            int iconHeight = icon.getIconHeight();

            JPanel endGamePanel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            JLabel messageLabel = new JLabel(message);
            JLabel iconLabel = new JLabel(icon);
            c.insets = new Insets(15, 0, 0, 0);
            c.gridx = 0;
            c.gridy = 0;
            endGamePanel.add(messageLabel, c);
            c.gridy = 1;
            endGamePanel.add(iconLabel, c);

            JOptionPane optionPane = new JOptionPane(endGamePanel, JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.DEFAULT_OPTION);
            JDialog dialog = optionPane.createDialog(contentPane, "Game is finished");
            dialog.setSize( iconWidth+270, iconHeight+140);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
        contentPane.remove(gameGraphic.getGameComponent());
        setBounds(X_FRAME, Y_FRAME, WIDTH_FRAME, HEIGHT_FRAME);
        setTitle("Skip-Bros CHAT");
        startB.setEnabled(true);
        readyB.setEnabled(true);
        leaveB.setEnabled(false);
        changeNameB.setEnabled(true);
        tutorialB.setText("Tutorial");
        tutorialB.setEnabled(true);
        gameGraphic = null;
    }


    /**
     * Lets client set their name
     */
    void setName() {
        String message = "Please enter your name\n\nName can only contain letters or digits\n" +
                "and must have between 3 and 13 characters";
        String title = "Skip-Bo";
        String nameSuggestion = System.getProperty("user.name");

        String name = (String) JOptionPane.showInputDialog(contentPane, message, title, JOptionPane.QUESTION_MESSAGE,
                null, null, nameSuggestion);

        if (name == null) {
            name = "";
        }
        clientListener.pw.println(SETTO + "§Nickname§" + name);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        if (keyEvent.getKeyChar() == KeyEvent.VK_ENTER) {
            String input = inputMes.getText().replaceAll("\n", " ");
            if (input.isEmpty()) {
                return;
            }
            inputMes.replaceRange("", 0, input.length());
            input = input.substring(0, input.length() - 1);
            if (!input.startsWith("/")) {
                String s = (String) playerComboModel.getSelectedItem();
                assert s != null;
                if (s.equals("broadcast")) {
                    input = "/broadcast " + input;
                } else if (!s.equals("lobby")) {
                    input = "/msg " + s + " " + input;
                }
            }
            try {
                clientListener.forward(input);
            } catch (IndexOutOfBoundsException | NotACommandException e) {
                printErrorMessage(e.getMessage());
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }


    //Handles button events
    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        //Button sounds
        if (!soundMuted) {
            playButtonSound();
        }

        JButton buttonPressed = (JButton) actionEvent.getSource();
        if (buttonPressed == readyB) {
            try {
                if (readyB.getText().equals("Ready")) {
                    clientListener.forward("/change status ready");
                    readyB.setText("Waiting");
                } else {
                    clientListener.forward("/change status waiting");
                    readyB.setText("Ready");
                }

            } catch (NotACommandException e) {
                clientLog.warn("Error with /change status command");
            }

        } else if (buttonPressed == startB) {

            JComboBox<Integer> playerBox = new JComboBox<>(new Integer[]{2, 3, 4});
            JComboBox<Integer> stockBox = new JComboBox<>(new Integer[]{5, 10, 20, 30});

            JPanel startGamePanel = new JPanel(new GridLayout(2, 2, 10, 10));
            startGamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            startGamePanel.add(new JLabel("Number of players:", JLabel.LEADING));
            startGamePanel.add(playerBox);
            startGamePanel.add(new JLabel("Number of stock cards:", JLabel.LEADING));
            startGamePanel.add(stockBox);

            int option = JOptionPane.showConfirmDialog(contentPane, startGamePanel, "Please select to start a game",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (!soundMuted) {
                playButtonSound();
            }
            if (option == JOptionPane.OK_OPTION) {
                clientListener.pw.println(NWGME + "§New§" + playerBox.getSelectedItem() + "§" + stockBox.getSelectedItem());
            }

        } else if (buttonPressed == pause) {
            if (unmuteIsBlocked) {
                return;
            }
            if (pause.getName().equals("Mute")) {
                backgroundMusic.mute();
                pause.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("muted.png"))));
                pause.setName("Unmute");
            } else {
                unmuteIsBlocked = true;
                backgroundMusic.mute();
                pause.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("not_muted.png"))));
                pause.setName("Mute");
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        unmuteIsBlocked = false;
                    }
                };
                timer.schedule(task, 100);
            }
        } else if (buttonPressed == soundB) {
            soundMuted = !soundMuted;
            if (gameGraphic != null) {
                gameGraphic.setSoundMuted(soundMuted);
            }
            if (soundMuted) {
                soundB.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("fx_off.png"))));
            } else {
                soundB.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("fx_on.png"))));
            }
        } else if (buttonPressed == manualB) {
            if (Desktop.isDesktopSupported()) {
                try {
                    File manual = new File("src/main/resources/Instruction_manual.pdf");
                    Desktop.getDesktop().open(manual);
                } catch (NullPointerException | IOException npe) {
                    clientLog.warn("Cannot open manual PDF");
                    printInfoMessage("Could not open PDF file");
                }
            }

        } else if (buttonPressed == infoB) {
            printCommandList();


        } else if (buttonPressed == gamesB) {

            try {
                clientListener.forward("/list games");
            } catch (NotACommandException e) {
                clientLog.warn("Error with /list command");
            }

        } else if (buttonPressed == whosOnB) {
            try {
                clientListener.forward("/list players");
            } catch (NotACommandException e) {
                clientLog.warn("Error with /list command");
            }

        } else if (buttonPressed == leaveB) {
            clientListener.pw.println(PLAYR + "§LeaveGame");
            contentPane.remove(gameGraphic.getGameComponent());
            setBounds(X_FRAME, Y_FRAME, WIDTH_FRAME, HEIGHT_FRAME);
            setTitle("Skip-Bros CHAT");
            startB.setEnabled(true);
            readyB.setEnabled(true);
            leaveB.setEnabled(false);
            changeNameB.setEnabled(true);
            gameGraphic = null;

        } else if (buttonPressed == changeNameB) {
            String name = (String) JOptionPane.showInputDialog(contentPane, "Enter your new name:",
                    "Change your name", JOptionPane.PLAIN_MESSAGE, null, null, playerName);
            if (!soundMuted) {
                playButtonSound();
            }
            if (name != null) {
                clientListener.pw.println(Protocol.CHNGE + "§Nickname§" + name);
            }
        } else if (buttonPressed == tutorialB) {
            if (tutorialB.getText().equals("Tutorial")) {
                if (readyB.getText().equals("Waiting")) {
                    clientListener.pw.println(CHNGE + "§Status§WAITING");
                }
                tutorialB.setText("Leave tutorial");
                setGameGraphic(true);
            } else {
                tutorialB.setText("Tutorial");
                endGame(null, true, null);
            }
        }
    }

    /**
     * Prints a string to the chat.
     * @param s     The string to be appended to the chat.
     * @param color The color in which the string is displayed.
     */
    private void appendToChat(String s, Color color) {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        attributeSet.addAttribute(StyleConstants.ColorConstants.Foreground, color);
        Document doc = chat.getDocument();
        try {
            doc.insertString(doc.getLength(), s, attributeSet);
            doc = chat.getDocument();
            chat.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            clientLog.warn("Error with appending text to chat");
        }

    }

    /**
     * Method to display High Score in following format:
     * Format:  "2020-04-26 17:47\nGuillaume1, Guillaume\nWINNER: Guillaume, SCORE: 8.67\n\n"
     * @param scores String array with the scores
     */
    void setHighScore(String[] scores) {

        String scoreString = "Skip-Bro High scores Top 10\n\n";
        String winnerString = " WINNER: ";
        for (String score : scores) {
            StringBuilder buffer = new StringBuilder(score);
            buffer.insert(16, '\n');
            int i = buffer.indexOf(winnerString)-1;
            buffer.insert(i, '\n');
            buffer.append("\n\n");
            scoreString = scoreString + buffer.toString();
        }
        highScore.setText(scoreString.replaceAll("; ", ""));
    }


    void updateNamesInComboBox(String selectedOption) {
        playerArray.sort(String.CASE_INSENSITIVE_ORDER);
        playerComboModel.removeAllElements();
        playerComboModel.addAll(Arrays.asList("lobby", "broadcast"));
        playerComboModel.addAll(playerArray);
        playerComboModel.setSelectedItem(selectedOption);
    }

    void changeOwnName(String name) {
        playerName = name;
    }

    /**
     * Updates the player array and the names in the combo box when a player changes its name.
     * @param oldName The old name of the player that want's to change its name.
     * @param newName New name that the player want's to change its name to.
     */
    void changePlayerName(String oldName, String newName) {
        playerArray.remove(oldName);
        playerArray.add(newName);
        if (playerComboModel.getSelectedItem().equals(oldName)) {
            updateNamesInComboBox(newName);
        } else {
            updateNamesInComboBox((String) playerComboModel.getSelectedItem());
        }
    }

    void addPlayer(String name) {
        playerArray.add(name);
        updateNamesInComboBox((String) playerComboModel.getSelectedItem());
    }

    void removePlayer(String name) {
        playerArray.remove(name);
        if (playerComboModel.getSelectedItem().equals(name)) {
            printInfoMessage("Your chat partner left. You are now chatting in the lobby chat.");
            updateNamesInComboBox("lobby");
        } else {
            updateNamesInComboBox((String) playerComboModel.getSelectedItem());
        }
        if (gameGraphic != null && !(gameGraphic instanceof Tutorial)) {
            if (gameGraphic.getOpponentNames().contains(name)) {
                gameGraphic.removePlayer(name);
            }
        }

    }

    /**
     * Starts playing the background music and loops it.
     */
    void playMusic() {
        backgroundMusic = new MusicPlayer();
        if(backgroundMusic.loadFile("src/main/resources/background.mp3")) {
            backgroundMusic.play();
            backgroundMusic.loop();
            clientLog.info(backgroundMusic.isPlaying());
        }
    }

    void playButtonSound() {
        MusicPlayer buttonSound = new MusicPlayer();
        if (buttonSound.loadFile("src/main/resources/buttonclick2.mp3")) {
            buttonSound.play();
        }
    }

    /**
     * Puts all clients already connected to the server into the player array when this client connects to the server.
     * @param names Array of all names that are connected to the server except for own name.
     */
    void setPlayers(String[] names) {
        playerArray.addAll(Arrays.asList(names));
        updateNamesInComboBox("lobby");
    }

    GameGraphic getGameGraphic() {
        return gameGraphic;
    }

    SBClientListener getClientListener() {
        return clientListener;
    }

    String getPlayerName() {
        return playerName;
    }
}
