import client.clientProcess;
import logicfactory.LamportMutex;
import logicfactory.businessLogic;
import logicfactory.library;
import server.serverAttribute;
import server.serverProcess;
import utils.fileParser;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by prateek on 3/4/14.
 */
public class LibraryService {

    /**
     * To run the server program, pass in "server server1.in" program parameters.
     * To run the client program, pass in "client client1.in" program parameters.
     * BLAH!!!!
     */
    public static void main(String[] args) throws IOException {
        fileParser fp = new fileParser();
        serverProcess sp = new serverProcess();
        clientProcess client;
        LamportMutex lm = LamportMutex.getInstance();
        serverAttribute s = serverAttribute.getInstance();
        library libraryInstance;

        final Logger logger = Logger.getLogger(LibraryService.class.getName());
        logger.setLevel(Level.INFO);


        if (args[0].equals("server")) {
            libraryInstance = fp.serverFileParser(args[1]);
            // Read commands to execute
            sp.execServerCommands();
            // Initialize the books upon startup
            sp.library_Init(libraryInstance);

            // Initialize the server to keep track of other servers
            lm.LamportMutex_Init(s.getServerID(),s.getNumOfServersInstances());

            sp.startMyServerInstance(libraryInstance);

        } else if (args[0].equals("client")) {
            fp.clientFileParser(args[1]);
            try {
                client = new clientProcess();
                client.mainClient();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            throw new IllegalArgumentException("Not a valid argument, Shutting Down: " + args);
        }

    }

}

