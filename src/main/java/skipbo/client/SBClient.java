package skipbo.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

import static javax.swing.SwingUtilities.invokeLater;

/**
 * A Skip-Bo client.
 */
public class SBClient {

    public static Logger clientLog = LogManager.getLogger(SBClient.class);
    private String[] args;
    private SBClientListener clientListener;
    private SBServerListener serverListener;
    private Socket sock;

    /**
     * Establishes a connection to the Skip-Bo server via SBClientListener thread and SBServerListener thread and opens
     * the GUI
     * @param args: command-line arguments given by Main class: {@literal client <hostAddress>:<port> [<username>] }
     */
    public SBClient(String[] args) {

        this.args = args;

        try {
            String[] ipAndPort = args[1].split(":");
            String ip = ipAndPort[0];
            int port = Integer.parseInt(ipAndPort[1]);

            clientLog.info("Connecting to port " + port + "…");
            this.sock = new Socket(ip, port);

            //Start SBClientListener Thread
            this.clientListener = new SBClientListener(sock);

            invokeLater(this::createGUI);

        } catch (IOException e) {
            clientLog.fatal("Error while connecting to server.");
        }
    }

    /**
     * Creates the GUI and starts the SBServerListener thread. Client logs out correctly and shuts down if an error
     * occurs.
     */
    private void createGUI() {
        //GUI
        ChatGraphic frame;
        if (args.length == 3) {
            if(args[0].equalsIgnoreCase("client")) {
                frame = new ChatGraphic(clientListener, args[2], false);
            } else {
                frame = new ChatGraphic(clientListener, args[2], true);
            }
        } else {
            if(args[0].equalsIgnoreCase("client")) {
                frame = new ChatGraphic(clientListener);
            } else {
                frame = new ChatGraphic(clientListener, true);
            }
        }
        frame.addWindowListener(new WindowHandler(clientListener));

        if (args[0].equalsIgnoreCase("client")) {
            try {
                frame.setVisible(true);
            } catch (Exception e) {
                clientLog.fatal("Caught Exception from setVisible. Shutting down client now.");
                try {
                    frame.getClientListener().forward("/quit");
                } catch (NotACommandException ex) {
                    clientLog.error("Error with shutting down client correctly.");
                }
                System.exit(-1);
            }
        }

        //Start SBServerListener Thread
        try {
            SBServerListener serverListener = new SBServerListener(sock, frame);
            this.serverListener = serverListener;
            Thread sListener = new Thread(serverListener);
            sListener.start();
        } catch (IOException e) {
            clientLog.fatal("Caught IOException from creating SBServerListener. Shutting down client now.");
            try {
                frame.getClientListener().forward("/quit");
            } catch (NotACommandException ex) {
                clientLog.error("Error with shutting down client correctly.");
            }
            System.exit(-1);
        }

    }

    public SBServerListener getServerListener() { return this.serverListener; }


}