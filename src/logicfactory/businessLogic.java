package logicfactory;

import server.TCPServer;
import server.serverAttribute;
import utils.bookValues;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by prateek on 3/4/14.
 */
public class businessLogic {

    final Logger logger = Logger.getLogger(businessLogic.class.getName());
    serverAttribute server = serverAttribute.getInstance();
    library myLibrary = new library();
    int sleepCounter = -1;
    long Time2Sleep = 0L;

    /**
     * Initializes the library to where nothing is checkedout
     */
    public void library_Init(){
        for(int book = 0; book < myLibrary.getNumOfBooks(); ++book){
            bookValues b = new bookValues(book, "none");
            myLibrary.getBooks().put("b" + String.valueOf(book), b);
            logger.log(Level.INFO, " " + myLibrary.getBooks().keySet());
        }
    }

    /**
     * The following code checks to see if there are any instructions for the server to
     * sleep. If so, we set the variables appropriately and remove the command from memory
     */
    public void execServerCommands(){
        if(!server.getServerInstruction().isEmpty()){
            for(int commands = 0; commands < server.getServerInstruction().size(); commands++){
                String [] command = server.getServerInstruction().get(commands).split(" ");
                // Check if the sleep command is for my process
                if(command[0].equalsIgnoreCase(server.getCanonicalServerID())){
                    sleepCounter = Integer.valueOf(command[1]);
                    Time2Sleep = Long.valueOf(command[2]);
                    // We will execute this command eventually, hence no need to keep it in memory
                    server.getServerInstruction().remove(commands);
                    break;
                }
            }
        }
    }

    /**
     * TODO: A new method which decrements the sleepCounter & initiates a sleep with Time2Sleep time
     * This has to be called upon when a client makes a call to the server. We would decrement the
     * sleepCounter and check to see if its zero. If reaches zero then failover.
     * We will also have to be smart about not calling 'execServerCommands' method till after the failover
     * because otherwise, we would be overwriting the sleep commands. Hence, we should trigger execServerCommands
     * again after the server has recovered from failover.
     */





    /**
     * Parse client's request & call appropriate methods
     * @return byte value for true/false depending on if the execution succeeds or not
     * c1 b2 reserve
     */
    public byte[] makeResponse(String msgIN){
        String [] terms = msgIN.split(" ");
        Boolean actionResult = false;
        if(2 == terms.length ){
            String clientID = terms[0];
            String bookID = terms[1];
            String commandID = terms[2];

            if(commandID.equalsIgnoreCase("reserve")){
                actionResult = reserveBook(clientID, bookID);
            }
            else if(commandID.equalsIgnoreCase("return")){
                actionResult = returnBook(clientID, bookID);
            }
            else {
                logger.log(Level.WARNING, "Invalid Command");
            }
        }

        return String.valueOf(actionResult).getBytes();
    }


    /**
     *
     * @param client
     * @param book
     * @return True if successfully reserved the book; else false
     */
    public Boolean reserveBook(String client, String book){
        // Check if book exists & already taken
        if(myLibrary.getBooks().contains(book)){
            bookValues b =  (bookValues)myLibrary.getBooks().get(book);
            // Reserve the book if not checked-out
            if(!b.getCheckedOut()){
                b.setClientName(client);
                b.setCheckedOut(true);
                logger.log(Level.INFO, "Reserved the book " + book + " for: " + b.getClientName());
                return b.getCheckedOut();
            }
            else {
                logger.log(Level.INFO, "Book is currently with: " + b.getClientName());
            }
        }
        else {
            logger.log(Level.WARNING, "Book is not in the library");
        }
        return false;
    }

    /**
     *
     * @param client
     * @param book
     * @return True if successfully returned the book; else false
     */
    public Boolean returnBook(String client, String book){
        // Check if we know about the book & already taken
        if(myLibrary.getBooks().contains(book)){
            bookValues b =  (bookValues)myLibrary.getBooks().get(book);
            // Return the book if checked-out
            if(b.getCheckedOut()){
                b.setClientName("none");
                b.setCheckedOut(false);
                logger.log(Level.INFO, "Client: " + b.getClientName() + " returned the book " +  book );
                return true;
            }
            else {
                logger.log(Level.INFO, "Book is in the Library & Client is lying" );
            }
        }
        else{
            logger.log(Level.WARNING, "Book is not in the library");
        }
        return false;
    }

    /**
     * broadcasts the message to all the known servers and does not expect an ack in return
     * @param msg
     */
    public void broadcast(String msg){

    }

    /**
     * responds to the broadcast message
     * @param msg
     */
    public void respond2broadcast(String msg){

    }

    /**
     *  Start TCP server on port
     */
    public void startMyServerInstance(){

        int server_port = Integer.valueOf(server.getServerAddresses().get(server.getServerID()).split(":")[1]);
        TCPServer tcpServer = new TCPServer(server_port, 1024);
        Thread qt = new Thread(tcpServer);
        qt.start();
        logger.log(Level.INFO, "TCP Server started on port: " + server_port);
    }

    /**
     * @param sleepTime
     * @throws InterruptedException
     */
    public void server_Sleep(long sleepTime) throws InterruptedException {
        Thread.sleep(sleepTime);
    }





}
