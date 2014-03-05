package logicfactory;

import server.serverAttribute;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by prateek on 3/4/14.
 */
public class businessLogic {

    final Logger logger = Logger.getLogger(businessLogic.class.getName());
    serverAttribute server = serverAttribute.getInstance();
    library myLibrary = new library();
    int checkOutflag = 0 ;
    int sleepCounter = -1;
    long Time2Sleep = 0L;

    public void library_Init(){
        for(int books = 0; books < myLibrary.getNumOfBooks(); books++){
            myLibrary.getBooks().put("b" + String.valueOf(books), checkOutflag);
            logger.log(Level.INFO, " " + myLibrary.getBooks().keySet() );
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




    public void server_Sleep(long sleepTime) throws InterruptedException {
        Thread.sleep(sleepTime);
    }





}
