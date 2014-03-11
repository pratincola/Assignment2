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


    private TCPClient replicateSocket = null;
    private static library serverLibrary = null;

    private static MessageImplementation mImpl = new MessageImplementation();
    final Logger logger = Logger.getLogger(businessLogic.class.getName());
    serverAttribute server = serverAttribute.getInstance();

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
        if(1 == server.getSleepCounter()){
            logger.log(Level.INFO, "Entering SLEEP MODE !!!!!!!!!");
            server_Sleep(server.getTime2Sleep());
        }
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
                        //We only care about "true" condition
                        //If actionResult == false, then the data within the server has not actually changed, and we
                        //therefore do not need to send an update to the rest of the servers
                        if (actionResult == true) {
                            mImpl.broadcastMsg(server.getServerID(),"replicate", "push", l);
                        }
                        LamportMutex.getInstance().releaseCS();

                    } else if (commandID.equalsIgnoreCase("return")) {
                        LamportMutex.getInstance().requestCS();

                        //We only care about "true" condition
                        //If actionResult == false, then the data within the server has not actually changed, and we
                        //therefore do not need to send an update to the rest of the servers
                        actionResult = returnBook(clientID, bookID, l);
                        if (actionResult == true) {
                            mImpl.broadcastMsg(server.getServerID(),"replicate", "push", l);
                        }
                        LamportMutex.getInstance().releaseCS();

                    } else if (commandID.equalsIgnoreCase("replicate")) {
                        //We assume we have already requested a mutex if calling for a server replicate
                        serverLibrary = l;
                    }
                    else {
                        logger.log(Level.WARNING, "Invalid Command");
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                server.decSleepCounter();
                return String.valueOf(actionResult).getBytes();
            }


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

    /**
     * @param sleepTime
     * @throws InterruptedException
     */
    public void server_Sleep(long sleepTime) throws InterruptedException {
        logger.log(Level.INFO, "Entering sleep mode for " + sleepTime);
        Thread.sleep(sleepTime);
    }


    public library getlibrary(library l) {
        return l;
    }

    /*public library replicateServers() {
        int serverKey = 1;
        Map<Integer, String> serverList = server.getServerAddresses();

        do {
            //Parse list of available server IP addresses
            if (serverKey != server.getServerID()) {
                String[] targetServer = parseIP(serverList.get(serverKey));
                serverKey++;
                replicateSocket = new TCPClient (Integer.parseInt(targetServer[1]), targetServer[0], "request replicate");
                replicateSocket.run();
            }
        } while (replicateSocket.getStatus() == true);
        //Go until one of them is successfull
        return replicateSocket.getUpdatedLibrary();
    }*/

    //Remove the ':' from the IP Address read in by the configuration file
    //This separates the IP Address from the port number
    public String[] parseIP(String address) {
        String[] tokens = address.split(":");
        return tokens;
    }
}
