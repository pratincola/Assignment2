package logicfactory;

import client.TCPClient;
import server.TCPServer;
import server.serverAttribute;
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
    private static int sleepCounter = -1;
    private static long Time2Sleep = 0L;
    private static MessageImplementation mImpl = new MessageImplementation();

    private TCPClient replicateSocket = null;
    private static library serverLibrary = null;

    final Logger logger = Logger.getLogger(businessLogic.class.getName());
    serverAttribute server = serverAttribute.getInstance();
    LamportMutex mutex = LamportMutex.getInstance();

    /**
     * Initializes the library to where nothing is checkedout
     */
    public void library_Init(library myLibrary) {
        for (int book = 1; book < myLibrary.getNumOfBooks(); book++) {
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
                    sleepCounter = Integer.valueOf(command[1]);
                    Time2Sleep = Long.valueOf(command[2]);
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
     * Parse client's request & call appropriate methods
     *
     * @return byte value for true/false depending on if the execution succeeds or not
     * c1 b2 reserve
     */
    public byte[] makeResponse(String msgIN, library l) throws InterruptedException {

        // Sleep on the Kth command.
        if(1 == sleepCounter){
            server_Sleep(Time2Sleep);
        }
        Boolean actionResult = false;
            try {
                String[] terms = msgIN.split(" ");
                if (3 == terms.length) {
                    String clientID = terms[0];
                    String bookID = terms[1];
                    String commandID = terms[2];


                    if (commandID.equalsIgnoreCase("reserve")) {
                        mutex.requestCS();
                        actionResult = reserveBook(clientID, bookID, l);
                        //We only care about "true" condition
                        //If actionResult == false, then the data within the server has not actually changed, and we
                        //therefore do not need to send an update to the rest of the servers
                        if (actionResult == true) {
                            mImpl.broadcastMsg(server.getServerID(),"replicate", "push", l);
                        }
                        mutex.releaseCS();

                    } else if (commandID.equalsIgnoreCase("return")) {
                        mutex.requestCS();
                        //We only care about "true" condition
                        //If actionResult == false, then the data within the server has not actually changed, and we
                        //therefore do not need to send an update to the rest of the servers
                        actionResult = returnBook(clientID, bookID, l);
                        if (actionResult == true) {
                            mImpl.broadcastMsg(server.getServerID(),"replicate", "push", l);
                        }
                        mutex.releaseCS();

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
                sleepCounter--;
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
     * Start TCP server on port
     */
    public void startMyServerInstance(library lib) {

        //Keep a local copy of the server's library within business logic object
        serverLibrary = lib;

        int server_port = Integer.valueOf(server.getServerAddresses().get(server.getServerID()).split(":")[1]);
        TCPServer tcpServer = new TCPServer(server_port, 1024, lib);
        Thread qt = new Thread(tcpServer);
        qt.start();
        logger.log(Level.INFO, "TCP Server started on port: " + server_port);
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

    public library replicateServers() {
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
    }

    //Remove the ':' from the IP Address read in by the configuration file
    //This separates the IP Address from the port number
    public String[] parseIP(String address) {
        String[] tokens = address.split(":");
        return tokens;
    }
}
