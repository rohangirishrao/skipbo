package skipbo.server;

/**
 * Interface giving framework for a NetWork Protocol Listener.
 */
public interface NWPListener extends Runnable {

    void run();

    void analyze(String[] input);

    void stopRunning();
}
