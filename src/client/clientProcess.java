package client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

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

    //Client is a singleton. This guarantees only one instance of client per process
    clientAttribute client = clientAttribute.getInstance();

    //Constructor
    public clientProcess() {
        this.possibleAddresses = client.getHostAddresses();
        this.commands = client.getClientInstruction();
        commandCounter = possibleAddresses.size();
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
        if (commandCounter > commandSize) {
            return -1;
        }

        //Delimited string for the command to be executed
        String[] instruction = parseCommands(commands.get(commandCounter));

        //If there are only 2 tokens, then we know the client needs to sleep
        if (instruction.length == 2) {
            commandCounter++;
            return (Integer.getInteger(instruction[1]));
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
                boolean socketTimedOut = true;
                while (socketTimedOut == true) {
                    String[] socketProperties = parseIP(possibleAddresses.get(addressCounter));
                    setTcpClient(socketProperties[0], socketProperties[1], commands.get(commandCounter));
                    //We can assume that at least 1 server will always be up.
                    //Not adding any error handling for now
                    addressCounter++;

                    //Create a dummy socket to test if the server is alive
                    SocketAddress testSocket = new InetSocketAddress(socketProperties[0], Integer.parseInt(socketProperties[1]));
                    //Tests to see if server is up and running
                    //Returns true if server times out
                    socketTimedOut = tcpClient.testConnection(testSocket);
                }
                //Reset the host address counter so it will start from the first server every time
                addressCounter = 0;
                //Increment the command counter so we will process the next command on the next iteration
                commandCounter++;

                //We will need to implement Lamport's Mutex algorithm on the TCPClient side
                tcpClient.run();
            }
            //Put this process to sleep for defined amount of time (ms)
            else if (instructionCode > 0) {
                Thread.sleep(instructionCode);
            }
        }
    }
}
