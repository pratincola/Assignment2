package client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Steve Kim on 3/6/14.
 * <p/>
 * This is the main logic for the client processes
 * We assume that the client configuration file has already been read in by the clientAttribute object
 * The client process will parse the commands and send messages to the servers for processing
 */
public class clientProcess {
    private TCPClient tcpClient;

    private List<String> possibleAddresses;
    private List<String> commands;

    private final String whitespaceRegex = "\\s";
    private final String colonRegex = ":";

    private int commandSize = 0;
    private int commandCounter = 0;
    private int addressCounter = 0;

//    private static PrintWriter fileOutput = null;
    private static File file;
    private int clientID = clientAttribute.getInstance().getClientID();


    //Client is a singleton. This guarantees only one instance of client per process
    clientAttribute client = clientAttribute.getInstance();
    final Logger logger = Logger.getLogger(clientProcess.class.getName());

    //Constructor
    public clientProcess() {
        try{
            this.possibleAddresses = client.getHostAddresses();
            this.commands = client.getClientInstruction();
            //fileOutput = new PrintWriter("c"+Integer.toString(clientID)+".out", "UTF-8");

            //create output file for this client
            file = new File ("c"+Integer.toString(clientID)+".out");
            if (!file.exists()) {
                file.createNewFile();
            }

            commandSize = commands.size();
        } catch (FileNotFoundException notFound) {
            notFound.printStackTrace();
        } catch (UnsupportedEncodingException unsupported) {
            unsupported.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Construct the TCPClient object
    public void setTcpClient(String hostname, String portNumber, String instruction) {
        int port = Integer.parseInt(portNumber);
        tcpClient = new TCPClient(port, hostname, instruction);
    }

    //Remove white spaces so we can see what type of command it is
    //3 tokens means we are doing a reserve or a return
    //2 tokens means we are sleeping for a time defined by token[1]
    public String[] parseCommands(String command) {
        String[] tokens = command.split(whitespaceRegex);
        return tokens;
    }

    //Remove the ':' from the IP Address read in by the configuration file
    //This separates the IP Address from the port number
    public String[] parseIP(String address) {
        String[] tokens = address.split(colonRegex);
        return tokens;
    }

    /* Return value
     *      -1 : We have finished executing all commands given for this client
     *       0 : This is a reserve/return command
     *      >0 : Returns the amount of time in ms to sleep
     */
    public int processInstruction() {
        //We have finished parsing all the commands. This process can now exit
        if (commandCounter >= commandSize) {
            return -1;
        }

        //Delimited string for the command to be executed
        String[] instruction = parseCommands(commands.get(commandCounter));

        //If there are only 2 tokens, then we know the client needs to sleep
        if (instruction.length == 2) {
            commandCounter++;
            return (Integer.valueOf(instruction[1]));
        }

        //We don't care about handling reserve/return just yet
        return 0;
    }

    public void mainClient() throws InterruptedException {
        boolean status = true;

        while (status == true) {
            int instructionCode = processInstruction();

            //We are done with all the instructions for this client.
            //Exit out of the process
            if (instructionCode == -1)
                status = false;
                //We will be sending a message to a server to reserve/return a book
            else if (instructionCode == 0) {
                //boolean socketTimedOut = true;

                do {
                    String[] socketProperties = parseIP(possibleAddresses.get(addressCounter));
                    setTcpClient(socketProperties[0], socketProperties[1], commands.get(commandCounter));
                    //We can assume that at least 1 server will always be up.
                    //Not adding any error handling for now
                    addressCounter++;

                    tcpClient.run();
                } while (tcpClient.getStatus() == true);

                //Reset the host address counter so it will start from the first server every time
                addressCounter = 0;
                //Increment the command counter so we will process the next command on the next iteration
                commandCounter++;

                //Should have returned a true/false now, go ahead and write result to output file
                tcpClient.writeOutputFile(tcpClient.modifiedSentence, file);
            }
            //Put this process to sleep for defined amount of time (ms)
            else if (instructionCode > 0) {
                logger.log(Level.INFO, "Entering client sleepmode!!!");
                Thread.sleep(instructionCode);
                logger.log(Level.INFO, "!!!!Exiting client sleepmode");
            }
        }
    }
}
