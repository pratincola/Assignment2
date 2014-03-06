package utils;

import client.clientAttribute;
import logicfactory.library;
import server.serverAttribute;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by prateek on 3/4/14.
 */
public class fileParser {
    String regex = "\\s";
    String line = null;

    private final static Logger logger = Logger.getLogger(fileParser.class.getName());
    serverAttribute server = serverAttribute.getInstance();
    clientAttribute client = clientAttribute.getInstance();
    library lib = new library();
    /**
     * Parses the file server1.in to get the serverID, #ofServers, ServerAddresses & ServerInstructions
     */
    public void serverInit(String filename) throws IOException {

        //Get server ID for the process
        server.setServerID(Integer.valueOf(filename.split("\\.", 0)[0].replaceAll("server", "")));

        //Get how many servers are in the cluster
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String lineONE = reader.readLine();
        server.setNumOfServersInstances(Integer.valueOf(lineONE.split(regex)[0]));

        //Get how many books in the library
        lib.setNumOfBooks(Integer.valueOf(lineONE.split(regex)[1]));

        //Load all Server addresses for communication
        for (int i = 0; i < server.getNumOfServersInstances(); i++) {
            server.getServerAddresses().put(i, reader.readLine());
        }

        //Load server instructions for later use
        while((line = reader.readLine())!=null) {
            server.getServerInstruction().add(line);
            logger.log(Level.INFO, "serverInstruction " + line);
        }

        logger.info("ServerID " +  server.getServerID());
        logger.log(Level.INFO, "getNumOfServersInstances " + server.getNumOfServersInstances());
        logger.log(Level.INFO, "getNumOfBooks " +  lib.getNumOfBooks());

    }

    /**
     * Parses the file client1.in to get the clientID, #ofServers, ServerAddresses & ClientInstructions
     */
    public void clientInit(String filename) throws IOException {

        //Get server ID for the process
        client.setClientID(Integer.valueOf(filename.split("\\.", 0)[0].replaceAll("client", "")));

        //Get how many servers are in the cluster
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String lineONE = reader.readLine();
        client.setNumOfClientsInstances(Integer.valueOf(lineONE.split(regex)[0]));

        //
        for (int i = 0; i < client.getNumOfClientsInstances(); i++) {
            client.getHostAddresses().add(reader.readLine());
        }

        //Load all Server addresses for communication
        while((line = reader.readLine())!=null) {
            client.getClientInstruction().add(line);
            logger.log(Level.INFO, "serverInstruction" + line);
        }

        logger.info("ClientID " +  client.getClientID());
        logger.log(Level.INFO, "getNumOfClientsInstances " + client.getNumOfClientsInstances());

    }


}
