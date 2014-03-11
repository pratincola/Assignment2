package server;

import logicfactory.library;
import utils.bookValues;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by prateek on 3/10/14.
 */
public class serverProcess {

    private final String whitespaceRegex = "\\s";

    serverAttribute server = serverAttribute.getInstance();
    final Logger logger = Logger.getLogger(serverProcess.class.getName());


    /**
     * Initializes the library to where nothing is checkedout
     */
    public void library_Init(library myLibrary) {
        for (int book = 1; book <= myLibrary.getNumOfBooks(); book++) {
            bookValues b = new bookValues(book, "none");
            myLibrary.getBooks().put("b" + String.valueOf(book), b);
            logger.log(Level.INFO, " " + myLibrary.getBooks().keySet());
        }
    }





    /**
     * The following code checks to see if there are any instructions for the server to
     * sleep. If so, we set the variables appropriately and remove the command from memory
     */
    public void execServerCommands() {
        if (!server.getServerInstruction().isEmpty()) {
            for (int commands = 0; commands < server.getServerInstruction().size(); commands++) {
                String[] command = parseCommands(server.getServerInstruction().get(commands), whitespaceRegex);
                // Check if the sleep command is for my process
                if (command[0].equalsIgnoreCase(server.getCanonicalServerID())) {
                    server.setSleepCounter(Integer.valueOf(command[1]));
                    server.setTime2Sleep(Long.valueOf(command[2]));
                    // We will execute this command eventually, hence no need to keep it in memory
                    server.getServerInstruction().remove(commands);
                    break;
                }
            }
        }
    }



    public String[] parseCommands (String command, String regex) {
        String[] tokens = command.split(regex);
        return tokens;
    }


    /**
     * Start TCP server on port
     */
    public void startMyServerInstance(library lib) {

        String [] token = parseCommands(server.getServerAddresses().get(server.getServerID()), ":");
        int server_port = Integer.valueOf(token[1]);
        TCPServer tcpServer = new TCPServer(server_port, 1024, lib);
        Thread qt = new Thread(tcpServer);
        qt.start();
        logger.log(Level.INFO, "TCP Server started on port: " + server_port);
    }


}
