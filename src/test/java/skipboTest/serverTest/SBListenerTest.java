package skipboTest.serverTest;

import org.junit.Before;
import org.junit.Test;
import skipbo.server.NWPListener;
import skipbo.server.Protocol;

import java.io.*;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static skipbo.server.SBServer.servLog;

public class SBListenerTest {

    public class testingSBL implements NWPListener {

        BufferedReader br;
        String[] analyzedInput;
        boolean analyzed = false;
        String trigger;
        boolean running;

        public testingSBL(PipedInputStream pis) {
            servLog.debug("Constructing testingSBL.");
            this.br = new BufferedReader(new InputStreamReader(pis));
            this.running = true;
            Thread testingSBLT = new Thread(this);
            testingSBLT.start();
        }

        @Override
        public void run() {
            while (running) {
                servLog.debug("Start of testingSBL while loop.");
                String[] input = null;
                try {
                    input = this.br.readLine().split("§", 3);
                    servLog.debug("testingSBL input[0] = " + input[0]);
                    analyzed = false;
                } catch (IOException e) {
                    servLog.warn("Error with reading input.");
                }

                if (input != null && input[0].length() > 0) this.analyze(input);
                servLog.debug("End of testingSBL while loop.");
            }
        }

        @Override
        public void analyze(String[] input) {
            servLog.debug("Got into analyze method.");
            analyzed = true;
            analyzedInput = input;
            try {
                Protocol protocol = Protocol.valueOf(input[0]);
                switch (protocol) {
                    case SETTO:
                        trigger = "setTo()";
                        break;
                    case CHNGE:
                        trigger = "changeTo()";
                        break;
                    case CHATM:
                        trigger = "chatMessage()";
                        break;
                    case LGOUT:
                        trigger = "logout()";
                        break;
                    case NWGME:
                        trigger = "newGame()";
                        break;
                    case PUTTO:
                        trigger = "putTo()";
                        break;
                    case DISPL:
                        trigger = "display()";
                        break;
                    case PLAYR:
                        trigger = "playerLeavingGame()";
                        break;
                }
            } catch (IllegalArgumentException iae) {
                trigger = input[0] + ": not a command.";
            }
        }

        @Override
        public void stopRunning() {
            this.running = false;
        }
    }

    PipedOutputStream pos;
    PipedInputStream pis;
    PrintWriter pw;
    testingSBL tSBL;

    @Before
    public void initialize() {
        pos = new PipedOutputStream();
        pis = new PipedInputStream();
        pw = new PrintWriter(pos, true);

        try {
            pis.connect(pos);
        } catch (IOException e) {
            servLog.error("Problem with connecting pis and pos.");
        }

        tSBL = new testingSBL(pis);
    }

    /**
     * Tests what happens in the case of a normal command.
     */
    @Test
    public void normalCommand() {
        try {
            pw.println("SETTO§Nickname§Marc");
            sleep(100);
            assertEquals("setTo()", tSBL.trigger);
            pw.println("CHNGE§Status§READY");
            sleep(100);
            assertEquals("changeTo()", tSBL.trigger);
            pw.println("CHATM§Global§Hi");
            sleep(100);
            assertEquals("chatMessage()", tSBL.trigger);
            pw.println("NWGME§New§2§20");
            sleep(100);
            assertEquals("newGame()", tSBL.trigger);
            pw.println("DISPL§players");
            sleep(100);
            assertEquals("display()", tSBL.trigger);
        } catch (InterruptedException ie) {
            servLog.error("Sleep time interrupted.");
        }
    }

    /**
     * Tests case where input[0] is not a valid command. This should throw an IllegalArgumentException
     * in the analyze method when trying to convert input[0] to a Protocol object.
     */
    @Test
    public void notACommand() {
        try {
            pw.println("SETO§Nickname§Marc");
            sleep(100);
            assertEquals("SETO: not a command.", tSBL.trigger);
            pw.println("CHANGE§Status§READY");
            sleep(100);
            assertEquals("CHANGE: not a command.", tSBL.trigger);
            pw.println("chatm§Global§Hi");
            sleep(100);
            assertEquals("chatm: not a command.", tSBL.trigger);
            pw.println("GUTZI§Schoggi§20");
            sleep(100);
            assertEquals("GUTZI: not a command.", tSBL.trigger);
            pw.println("SET");
            sleep(100);
            assertEquals("SET: not a command.", tSBL.trigger);
        } catch (InterruptedException ie) {
            servLog.error("Sleep time interrupted.");
        }
    }

    /**
     * Tests case where input[1] is not a valid option or null. This should not be a problem
     * since the option is only processed by the ProtocolExecutor, not the SBListener.
     */
    @Test
    public void notAnOption() {
        try {
            pw.println("SETTO§Name§Marc");
            sleep(100);
            assertEquals("setTo()", tSBL.trigger);
            pw.println("CHATM§.-*#§Hi");
            sleep(100);
            assertEquals("chatMessage()", tSBL.trigger);
            pw.println("NWGME§2§20");
            sleep(100);
            assertEquals("newGame()", tSBL.trigger);
            pw.println("LGOUT§Status§READY");
            sleep(100);
            assertEquals("logout()", tSBL.trigger);
            pw.println("DISPL");
            sleep(100);
            assertEquals("display()", tSBL.trigger);
        } catch (InterruptedException ie) {
            servLog.error("Sleep time interrupted.");
        }
    }

    /**
     * Tests case where the arguments are not fitting the command (list is too long / short, invalid arguments).
     * This should not be a problem since the arguments are not handled by the SBListener.
     */
    @Test
    public void notFittingArguments() {
        try {
            pw.println("SETTO§Nickname§Ma-rc");
            sleep(100);
            assertEquals("setTo()", tSBL.trigger);
            pw.println("CHNGE§Status");
            sleep(100);
            assertEquals("changeTo()", tSBL.trigger);
            pw.println("NWGME§New§ThisNotAnArgument§2§20");
            sleep(100);
            assertEquals("newGame()", tSBL.trigger);
            pw.println("PUTTO§Card§Hi");
            sleep(100);
            assertEquals("putTo()", tSBL.trigger);
            pw.println("DISPL§players§All");
            sleep(100);
            assertEquals("display()", tSBL.trigger);
        } catch (InterruptedException ie) {
            servLog.error("Sleep time interrupted.");
        }
    }

    /**
     * Tests case where option and command are switched. This should give the same result as an invalid command.
     */
    @Test
    public void comAndOpSwitch() {
        try {
            pw.println("Nickname§SETTO§Marc");
            sleep(100);
            assertEquals("Nickname: not a command.", tSBL.trigger);
            pw.println("Status§CHNGE§READY");
            sleep(100);
            assertEquals("Status: not a command.", tSBL.trigger);
            pw.println("Global§CHATM§Hi");
            sleep(100);
            assertEquals("Global: not a command.", tSBL.trigger);
            pw.println("New§NWGME§2§20");
            sleep(100);
            assertEquals("New: not a command.", tSBL.trigger);
            pw.println("players§DISPL");
            sleep(100);
            assertEquals("players: not a command.", tSBL.trigger);
        } catch (InterruptedException ie) {
            servLog.error("Sleep time interrupted.");
        }
    }

    /**
     * Tests case where the input is an empty String.
     */
    @Test
    public void emptyInput() {
        try {
            pw.println("");
            sleep(100);
            assertEquals(false, tSBL.analyzed);
            assertEquals(null, tSBL.trigger);
        } catch (InterruptedException ie) {
            servLog.error("Sleep time interrupted.");
        }
    }

    /**
     * Tests if the input is split the right way, meaning with a maximum of 3 resulting parts, no empty Strings in
     * input, but no information lost.
     */
    @Test
    public void rightSplit() {
        try {
            pw.println("CHATM§Global§Hi§this§is§a§chat§message.");
            sleep(100);
            assertEquals("Hi§this§is§a§chat§message.", tSBL.analyzedInput[2]);
        } catch (InterruptedException ie) {
            servLog.error("Sleep time interrupted.");
        }
    }

    @Test
    public void stopSBL() {
        tSBL.stopRunning();
        pw.println("SETTO§Nickname§Marc");
        try {
            sleep(100);
        } catch (InterruptedException e) {
            servLog.error("Sleep time interrupted.");
        }
        assertEquals(false, tSBL.running);
        assertEquals(null, tSBL.trigger);
    }

}


