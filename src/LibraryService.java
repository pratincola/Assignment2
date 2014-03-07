import client.clientProcess;
import logicfactory.businessLogic;
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
    public static void main(String [] args) throws IOException {
        fileParser fp = new fileParser();
        businessLogic bl = new businessLogic();
        clientProcess client = new clientProcess();

        final Logger logger = Logger.getLogger(LibraryService.class.getName());
        logger.setLevel(Level.INFO);


        if (args[0].equals("server")) {
            fp.serverFileParser(args[1]);
            // Initialize the books upon startup
            bl.library_Init();
            bl.startMyServerInstance();

        }
        else if (args[0].equals("client")) {
            fp.clientFileParser(args[1]);
            try {
                client.mainClient();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        else {
            throw new IllegalArgumentException("Not a valid argument, Shutting Down: " + args);
        }

    }

}

