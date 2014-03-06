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
     */
    public static void main(String [] args) throws IOException {
        fileParser fp = new fileParser();
        businessLogic bl =


                new businessLogic();

        final Logger logger = Logger.getLogger(LibraryService.class.getName());
        logger.setLevel(Level.INFO);


        if (args[0].equals("server")) {
            fp.serverInit(args[1]);
            // Initialize the books upon startup
            bl.library_Init();

        }
        else if (args[0].equals("client")) {
            fp.clientInit(args[1]);

        }
        else {
            throw new IllegalArgumentException("Not a valid argument, Shutting Down: " + args);
        }

    }

}

