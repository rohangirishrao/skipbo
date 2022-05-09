package skipboTest.serverTest;

import org.junit.Before;
import org.junit.Test;
import skipbo.game.Status;
import skipbo.server.*;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static skipbo.server.SBServer.servLog;

/**
 * Tests the execution of the ProtocolExecutor class. Therefore, it will test cases for most protocol commands but
 * will not, however, test what happens if the input from the client is not a network protocol command to begin with –
 * this is taken care of in the SBListener class.
 * The methods putTo(), check() and gameEnding() are not tested here since they are part of the game component and
 * cannot be tested in this frame. Apart from that, auxiliary methods like informLogin(), sendAll() and broadcast()
 * are not tested either since they are sufficiently tested over the various other methods.
 */
public class ProtocolExecutorTest {

    Main server;
    Main client0;
    Main client1;
    Main client2;
    Main client3;
    static int port = 63000;

    /**
     * This initialization method starts up a server and four clients connected to that server.
     */
    @Before
    public void initialize() {
        String portString = ++port + "";
        server = new Main(); Main.main(new String[]{"server", portString});
        client0 = new Main(); client1 = new Main(); client2 = new Main(); client3 = new Main();
        try {
            Main.main(new String[]{"testClient", "localhost:"+portString, "Janni"});
            Main.main(new String[]{"testClient", "localhost:"+portString, "Manuela"});
            Main.main(new String[]{"testClient", "localhost:"+portString, "Rohan"});
            Main.main(new String[]{"testClient", "localhost:"+portString, "Guillaume"});
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the implementation of setTo with option 'Nickname'. Details about what is tested are found
     * next to the assertEquals methods.
     */
    @Test
    public void testSetTo() {
        Main client4 = new Main(); Main client5 = new Main(); Main client6 = new Main();
        Main client7 = new Main(); Main client8 = new Main(); Main client9 = new Main();
        Main.main(new String[]{"testClient", "localhost:"+ port});
        Main.main(new String[]{"testClient", "localhost:"+ port});
        Main.main(new String[]{"testClient", "localhost:"+ port});
        Main.main(new String[]{"testClient", "localhost:"+ port});
        Main.main(new String[]{"testClient", "localhost:"+ port});
        Main.main(new String[]{"testClient", "localhost:"+ port});
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ProtocolExecutor pe4 = new ProtocolExecutor(new String[]{"SETTO", "Nickname"}, Main.server.getSblList().get(4));
        ProtocolExecutor pe5 = new ProtocolExecutor(new String[]{"SETTO", "Nickname", "Ma&rc"}, Main.server.getSblList().get(5));
        ProtocolExecutor pe6 = new ProtocolExecutor(new String[]{"SETTO", "Nickname", "John"}, Main.server.getSblList().get(6));
        ProtocolExecutor pe7 = new ProtocolExecutor(new String[]{"SETTO", "Nickname", "John"}, Main.server.getSblList().get(7));
        ProtocolExecutor pe8 = new ProtocolExecutor(new String[]{"SETTO", "Nickname", "Al"}, Main.server.getSblList().get(8));
        ProtocolExecutor pe9 = new ProtocolExecutor(new String[]{"SETTO", "Nickname", "WaaayTooLongName"},
                                                                            Main.server.getSblList().get(9));
        try {
            pe4.setTo();
            pe5.setTo();
            pe6.setTo();
            pe7.setTo();
            pe8.setTo();
            pe9.setTo();
            sleep(1000);
        } catch (NoCommandException e) {
            System.out.println("Error with testing framework");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("SBPlayer", pe4.getSBL().getPlayer().getName()); // Test: setTo command with null argument
        assertEquals("SBPlayer1", pe5.getSBL().getPlayer().getName());
        // Test: setTo invalid name + default name is already taken
        assertEquals("John", pe6.getSBL().getPlayer().getName()); // Test: regular name setting
        assertEquals("John1", pe7.getSBL().getPlayer().getName()); // Test: setTo name already in use
        assertEquals("SBPlayer2", pe8.getSBL().getPlayer().getName()); // Test: name too short
        assertEquals("SBPlayer3", pe9.getSBL().getPlayer().getName()); // Test: name too long
        servLog.debug("END of testSetToNickname, size of sbListenerList: " + Main.server.getSblList().size());
    }

    /**
     * Tests the case where the option given to the SETTO command is not valid.
     */
    @Test(expected = NoCommandException.class)
    public void testSetToException1() throws NoCommandException {
        new ProtocolExecutor(new String[]{"SETTO", "NotAnOption"}, Main.server.getSblList().get(0)).setTo();
    }

    /**
     * Tests the case where the option given to the SETTO command equals null.
     */
    @Test(expected = NoCommandException.class)
    public void testSetToException2() throws NoCommandException {
        new ProtocolExecutor(new String[]{"SETTO"}, Main.server.getSblList().get(0)).setTo();
    }

    /**
     * Tests the implementation of changeTo with option 'Nickname'. Details about what is tested are
     * found next to the assertEquals methods.
     */
    @Test
    public void testChangeToName() {
        ProtocolExecutor pe0 = new ProtocolExecutor(new String[]{"CHNGE", "Nickname", "Guillaume"}, Main.server.getSblList().get(0));
        ProtocolExecutor pe1 = new ProtocolExecutor(new String[]{"CHNGE", "Nickname", "Ma&rc"}, Main.server.getSblList().get(1));
        ProtocolExecutor pe2 = new ProtocolExecutor(new String[]{"CHNGE", "Nickname", "Marc"}, Main.server.getSblList().get(2));
        ProtocolExecutor pe3 = new ProtocolExecutor(new String[]{"CHNGE", "Nickname", "Guillaume"}, Main.server.getSblList().get(3));
        try {
           pe0.changeTo(false);
           pe1.changeTo(false);
           pe2.changeTo(false);
           pe3.changeTo(false);
           sleep(1000);
        } catch(NoCommandException nce) {
            System.out.println("Error with testing framework.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("Guillaume1", pe0.getSBL().getPlayer().getName()); // Test: changeTo name already in use
        assertEquals("Manuela", pe1.getSBL().getPlayer().getName()); // Test: changeTo invalid name
        assertEquals("Marc", pe2.getSBL().getPlayer().getName()); // Test: regular name changing
        assertEquals("Guillaume", pe3.getSBL().getPlayer().getName());
        // Test: changeTo own name (should not change anything)

        pe0.getSBL().getPlayer().changeStatus(Status.INGAME);
        pe0.setInput(new String[]{"CHNGE", "Nickname", "Whatever"});
        assertEquals("Guillaume1", pe0.getSBL().getPlayer().getName());
        // Test: changTo while ingame (not allowed)
        pe0.getSBL().getPlayer().changeStatus(Status.WAITING);
        servLog.debug("END of testChangeToName, size of sbListenerList: " + Main.server.getSblList().size());
    }

    /**
     * Tests the implementation of changeTo with option 'Status'. Details about what is tested are
     * found next to the assertEquals methods.
     */
    @Test
    public void testChangeToStatus() {
        ProtocolExecutor pe0 = new ProtocolExecutor(new String[]{"CHNGE", "Status", "READY"}, Main.server.getSblList().get(0));
        ProtocolExecutor pe1 = new ProtocolExecutor(new String[]{"CHNGE", "Status", "WAITING"}, Main.server.getSblList().get(1));
        ProtocolExecutor pe2 = new ProtocolExecutor(new String[]{"CHNGE", "Status", "INGAME"}, Main.server.getSblList().get(2));
        try {
            pe0.changeTo(false);
            pe1.changeTo(false);
            pe2.changeTo(false);
            sleep(200);
        } catch(NoCommandException nce) {
            System.out.println("Error with testing framework.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(Status.READY, pe0.getSBL().getPlayer().getStatus()); // Test: changeTo READY
        assertEquals(Status.WAITING, pe1.getSBL().getPlayer().getStatus());
        // Test: changeTo own status (should not change anything)
        assertEquals(Status.WAITING, pe2.getSBL().getPlayer().getStatus());
        // Test: trying to changeTo INGAME (not allowed, game does this, not the client)

        pe0.setInput(new String[]{"CHNGE", "Status", "WAITING"});
        try {
            pe0.changeTo(false);
            sleep(400);
        } catch(NoCommandException nce) {
            System.out.println("Error with testing framework.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(Status.WAITING, pe0.getSBL().getPlayer().getStatus()); // Test: change back to WAITING
        servLog.debug("END of testChangeToStatus, size of sbListenerList: " + Main.server.getSblList().size());
    }

    /**
     * Tests the case where the option given to the CHNGE command is not valid.
     */
    @Test(expected = NoCommandException.class)
    public void testChangeToException1() throws NoCommandException {
        new ProtocolExecutor(new String[]{"CHNGE", "NotAnOption"}, Main.server.getSblList().get(0)).changeTo(false);
    }

    /**
     * Tests the case where the option given to the CHNGE command equals null.
     */
    @Test(expected = NoCommandException.class)
    public void testChangeToException2() throws NoCommandException {
        new ProtocolExecutor(new String[]{"CHNGE"}, Main.server.getSblList().get(0)).changeTo(false);
    }

    /**
     * Tests the implementation of newGame. Details about what is tested are
     * found next to the assertEquals methods.
     */
    @Test
    public void testNewGame() {
        ProtocolExecutor pe0 = new ProtocolExecutor(new String[]{"CHNGE", "Status", "READY"}, Main.server.getSblList().get(0));
        ProtocolExecutor pe1 = new ProtocolExecutor(new String[]{"NWGME", "New", "2§30"}, Main.server.getSblList().get(1));
        ProtocolExecutor pe2 = new ProtocolExecutor(new String[]{"NWGME", "New", "2§30"}, Main.server.getSblList().get(2));
        try {
            pe0.changeTo(false);
            sleep(200);
            pe1.newGame();
            sleep(200);
            pe2.newGame();
            sleep(400);
        } catch(NoCommandException nce) {
            servLog.debug("Error with testing framework");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, Main.server.getLobby().getGamesList().size());
        // Test: First game was started, second one did not (not enough people ready)
        assertEquals("Manuela\nJanni", Main.server.getLobby().getGamesList().get(0).getPlayerList());
        // Test: Client 0 (Guillaume1) and 1 (Manuela) – the only one that was ready and the one that started the game,
        // even though the client was not ready – are the ones in the game, client 1 comes first since it started game
        assertEquals(30, Main.server.getLobby().getGamesList().get(0).players.get(0).getStockPile().size());
        // Test: Stockpile of the game is 30 cards high
        assertEquals("Rohan, Guillaume",
                Main.server.getPlayerNotIngameList());
        // Test: Client 2 (Marc) and 3 (Guillaume) remain in Lobby
    }

    /**
     * Tests the case where the option given to the NWGME command is not valid.
     */
    @Test(expected = NoCommandException.class)
    public void testNewGameException1() throws NoCommandException {
        new ProtocolExecutor(new String[]{"NWGME", "NotAnOption"}, Main.server.getSblList().get(0)).changeTo(false);
    }

    /**
     * Tests the case where the option given to the NWGME command equals null.
     */
    @Test(expected = NoCommandException.class)
    public void testNewGameException2() throws NoCommandException {
        new ProtocolExecutor(new String[]{"NWGME"}, Main.server.getSblList().get(0)).changeTo(false);
    }

    /**
     * Tests the implementation of chatMessage with all three options 'Global', 'Broadcast' and 'Private'.
     * Details about what is tested are found next to the assertEquals methods.
     */
    @Test
    public void testChatMessage() {
        ProtocolExecutor pe0 = new ProtocolExecutor(new String[]{"CHNGE", "Status", "READY"}, Main.server.getSblList().get(0));
        ProtocolExecutor pe1 = new ProtocolExecutor(new String[]{"NWGME", "New", "2§30"}, Main.server.getSblList().get(1));
        try {
            pe0.changeTo(false);
            sleep(200);
            pe1.newGame();
            sleep(1500);
        } catch(NoCommandException nce) {
            servLog.debug("Error with testing framework");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pe0.setInput(new String[]{"CHATM", "Global", "Global ingame."});
        try {
            pe0.chatMessage();
            sleep(400);
        } catch (NoCommandException e) {
            servLog.debug("Error with testing framework.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("CHATM§Global§You: Global ingame.", Main.clientList.get(0).getServerListener().getInput());
        // Test: client sending message receives it
        assertEquals("CHATM§Global§Janni: Global ingame.", Main.clientList.get(1).getServerListener().getInput());
        // Test: other client in the game receives message
        assertEquals("PRINT§Terminal§Janni is READY.", Main.clientList.get(2).getServerListener().getInput());
        // Test: players not ingame don't receive the message

        ProtocolExecutor pe2 = new ProtocolExecutor(new String[]{"CHATM", "Global", "Global not ingame."},
                Main.server.getSblList().get(2));
        try {
            pe2.chatMessage();
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NoCommandException e) {
            servLog.debug("Error with testing framework.");
        }
        assertEquals("CHATM§Global§You: Global not ingame.", Main.clientList.get(2).getServerListener().getInput());
        // Test: client sending message receives it
        assertEquals("CHATM§Global§Rohan: Global not ingame.", Main.clientList.get(3).getServerListener().getInput());
        // Test: other client not ingame receives message
        assertEquals("CHATM§Global§You: Global ingame.", Main.clientList.get(0).getServerListener().getInput());
        // Test: ingame players don't receive the message (still has old input in 'input')

        pe2.setInput(new String[]{"CHATM", "Broadcast", "Broadcast."});
        try {
            pe2.chatMessage();
            sleep(200);
        } catch (NoCommandException e) {
            servLog.debug("Error with testing framework.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("CHATM§Broadcast§[BC] Rohan: Broadcast.", Main.clientList.get(0).getServerListener().getInput());
        // Test: ingame player receive broadcast
        assertEquals("CHATM§Broadcast§[BC] You: Broadcast.", Main.clientList.get(2).getServerListener().getInput());
        // Test: player sending message receives it itself
        assertEquals("CHATM§Broadcast§[BC] Rohan: Broadcast.", Main.clientList.get(3).getServerListener().getInput());
        // Test: not ingame players receive broadcast

        pe2.setInput(new String[]{"CHATM", "Private", "Janni§Private message."});
        try {
            pe2.chatMessage();
            sleep(200);
        } catch (NoCommandException e) {
            servLog.debug("Error with testing framework.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("CHATM§Private§[from Rohan]: Private message.", Main.clientList.get(0).getServerListener().getInput());
        // Test: client receives private message
        assertEquals("CHATM§Private§[to Janni]: Private message.", Main.clientList.get(2).getServerListener().getInput());
        // Test: player sending message receives it itself
        assertEquals("CHATM§Broadcast§[BC] Rohan: Broadcast.", Main.clientList.get(3).getServerListener().getInput());
        // Test: other players don't get message (still has old input in 'input')

        pe2.setInput(new String[]{"CHATM", "Private", "Rohan§Private message."});
        try {
            pe2.chatMessage();
            sleep(200);
        } catch (NoCommandException e) {
            servLog.debug("Error with testing framework.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("PRINT§Terminal§You private messaged yourself, duh...", Main.clientList.get(2).getServerListener().getInput());
        // Test: client is notified when sending private message to himself
    }

    /**
     * Tests the case where the option given to the CHATM command is not valid.
     */
    @Test(expected = NoCommandException.class)
    public void testChatMessageException1() throws NoCommandException {
        new ProtocolExecutor(new String[]{"CHATM", "NotAnOption"}, Main.server.getSblList().get(0)).changeTo(false);
    }

    /**
     * Tests the case where the option given to the CHATM command equals null.
     */
    @Test(expected = NoCommandException.class)
    public void testChatMessageException2() throws NoCommandException {
        new ProtocolExecutor(new String[]{"CHATM"}, Main.server.getSblList().get(0)).changeTo(false);
    }

    /**
     * Tests the implementation of logout.
     */
    @Test
    public void testLogout() {
        ProtocolExecutor pe0 = new ProtocolExecutor(new String[]{"CHNGE", "Status", "READY"}, Main.server.getSblList().get(0));
        ProtocolExecutor pe1 = new ProtocolExecutor(new String[]{"NWGME", "New", "2§30"}, Main.server.getSblList().get(1));
        try {
            pe0.changeTo(false);
            sleep(200);
            pe1.newGame();
            sleep(400);
        } catch(NoCommandException nce) {
            servLog.debug("Error with testing framework");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pe0.setInput(new String[]{"LGOUT"});
        try {
            pe0.logout();
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(3, Main.server.getLobby().getPlayerLobby().size());
        // Test: Player got removed from main lobby
        assertEquals(1, Main.server.getLobby().getGamesList().get(0).players.size());
        // Test: Player was removed from game lobby
        assertEquals(false, Main.server.getLobby().getGamesList().get(0).gameIsRunning());
        // Test: Server recognised game to have only one player left and terminated it
    }
}
