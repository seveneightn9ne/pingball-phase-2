package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import common.Constants;

/**
 * This Runnable waits for input from System.in and adds it to the PingballServer's queue.
 *
 *
 * Thread safety argument:
 * * Immutable data is shared by a thread safe queue.
 *
 * Rep Invariant:
 * * none.
 */
public class CommandLineInterface implements Runnable {

    private BlockingQueue<String> cliQueue;

    /**
     * Create a new CommandLineInterface
     *
     * @param queue the queue on which to place string commands received from System.in
     */
    public CommandLineInterface(BlockingQueue<String> queue) {
        this.cliQueue = queue;

        checkRep();
    }

    /**
     * Read command line input from System.in and pass to the main server thread via a queue
     */
    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                cliQueue.add(line);
            }
        } catch (IOException e) {
            if (Constants.DEBUG) System.err.println(e.getMessage());
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                if (Constants.DEBUG) System.err.println(e.getMessage());
            }
        }

    }

    /**
     * Rep invariant: none
     *
     * this is a do-nothing method
     */
    private void checkRep() {
        return;
    }

}