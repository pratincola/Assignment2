package logicfactory;

import client.TCPClient;
import server.TCPServer;
import server.serverAttribute;
import sun.print.resources.serviceui_zh_TW;
import utils.MessageImplementation;
import utils.bookValues;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by prateek on 3/4/14.
 */
public class businessLogic {

    private final String whitespaceRegex = "\\s";

    final Logger logger = Logger.getLogger(businessLogic.class.getName());
    serverAttribute server = serverAttribute.getInstance();
    static MessageImplementation mi_bl = new MessageImplementation();


    public String[] parseCommands (String command, String regex) {
        String[] tokens = command.split(regex);
        return tokens;
    }

    /**
     * Parse client's request & call appropriate methods
     *
     * @return byte value for true/false depending on if the execution succeeds or not
     * c1 b2 reserve
     */
    public byte[] makeResponse(String msgIN, library l) throws InterruptedException {

        // Sleep on the Kth command.

        Boolean actionResult = false;
            try {
                String[] terms = msgIN.split(" ");
                if (3 == terms.length) {
                    String clientID = terms[0];
                    String bookID = terms[1];
                    String commandID = terms[2];

                    if (commandID.equalsIgnoreCase("reserve")) {
                        LamportMutex.getInstance().requestCS();
                        actionResult = reserveBook(clientID, bookID, l);
                        replicateLib(l);
                        server.decSleepCounter();
                        LamportMutex.getInstance().releaseCS();
                    } else if (commandID.equalsIgnoreCase("return")) {
                        LamportMutex.getInstance().requestCS();
                        actionResult = returnBook(clientID, bookID, l);
                        replicateLib(l);
                        server.decSleepCounter();
                        LamportMutex.getInstance().releaseCS();
                    } else {
                        logger.log(Level.WARNING, "Invalid Command");
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
//                server.decSleepCounter();
                return String.valueOf(actionResult).getBytes();
            }


    }

    public boolean replicateLib(library lib){
        String libraryString = lib.toString();
        logger.log(Level.INFO, libraryString);
        mi_bl.broadcastMsg(server.getServerID(), "replicate",libraryString );
        return true;
    }

    public void updateLib(String s, library lib){
        lib.libraryUpdate(s);
    }



    /**
     * @param client
     * @param book
     * @return True if successfully reserved the book; else false
     */
    public Boolean reserveBook(String client, String book, library myLibrary) {
        // Check if book exists & already taken
        if (myLibrary.getBooks().containsKey(book)) {
            bookValues b = (bookValues) myLibrary.getBooks().get(book);
            // Reserve the book if not checked-out
            if (!b.getCheckedOut()) {
                b.setClientName(client);
                b.setCheckedOut(true);
                logger.log(Level.INFO, "Reserved the book " + book + " for: " + b.getClientName());
                return b.getCheckedOut();
            } else {
                logger.log(Level.INFO, "Book is currently with: " + b.getClientName());
            }
        } else {
            logger.log(Level.WARNING, "Book is not in the library");
        }
        return false;
    }

    /**
     * @param client
     * @param book
     * @return True if successfully returned the book; else false
     */
    public Boolean returnBook(String client, String book, library myLibrary) {
        // Check if we know about the book & already taken
        if (myLibrary.getBooks().containsKey(book)) {
            bookValues b = (bookValues) myLibrary.getBooks().get(book);
            // Return the book if checked-out
            logger.log(Level.INFO, client);
            if (b.getCheckedOut() && client.equalsIgnoreCase(b.getClientName())) {
                b.setClientName("none");
                b.setCheckedOut(false);
                logger.log(Level.INFO, "Client: " + client + " returned the book " + book);
                return true;
            } else {
                logger.log(Level.INFO, "Book is in the Library & Client is lying");
            }
        } else {
            logger.log(Level.WARNING, "Book is not in the library");
        }
        return false;
    }

    /**
     * broadcasts the message to all the known servers and does not expect an ack in return
     *
     * @param msg
     */
    public void broadcast(String msg) {




    }

    /**
     * responds to the broadcast message
     *
     * @param msg
     */
    public void respond2broadcast(String msg) {

    }

    public library getlibrary(library l) {
        return l;
    }


}
