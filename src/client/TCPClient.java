package client;

import logicfactory.LamportMutex;
import utils.Message;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import logicfactory.library;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by prateek on 2/16/14.
 */
public class TCPClient implements Runnable {
    private final int socketTimeout = 1000;
    private final static Logger logger = Logger.getLogger(TCPClient.class.getName());

    
    private library serializedLibrary = null;
    private library updatedLibrary = null;

    private boolean getNextAddress = true;


    LamportMutex lm = LamportMutex.getInstance();


    int len, port;
    String hostname, sentence, modifiedSentence;
    Socket clientSocket;
    private final String whitespaceRegex = "\\s";

    //Constructors
    public TCPClient(int port, String hostname, String message) {
        this.len = len;
        this.port = port;
        this.hostname = hostname;
        this.sentence = message;
    }

    //Used when creating a client to replicate data between servers
    public TCPClient(int port, String hostname, String message, library dataReplica) {
        this.len = len;
        this.port = port;
        this.hostname = hostname;
        this.sentence = message;
        this.serializedLibrary = dataReplica;
    }

    public boolean testConnection(SocketAddress testSocket) {
        Socket dummy = new Socket();

        try {
            dummy.connect(testSocket, socketTimeout);
            dummy.close();
        } catch (SocketTimeoutException to) {
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }

        return false;
    }

    public boolean getStatus() {
        return getNextAddress;
    }

    public library getUpdatedLibrary() {
        return updatedLibrary;
    }

    @Override
    public void run() {
        //BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        DataOutputStream outToServer = null;

        try {
            logger.log(Level.INFO, "Startting 1");
            clientSocket = new Socket(hostname, port);
            logger.log(Level.INFO, "Startting 2");

            // Send to Server variables
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            logger.log(Level.INFO, "Startting 3");
            OutputStream os = clientSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            // Receive from Server variables
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            InputStream is = clientSocket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);

            logger.log(Level.INFO, "Inside TCPClient, msg to server: " + sentence);

            //Writes data out to the TCP Server
            outToServer.writeBytes(sentence + '\n');
            //The library object in TCPClient should be null unless we specifically set it with the library object
            //to be replicated
            if (this.serializedLibrary != null) {
                oos.writeObject(this.serializedLibrary);
            }

            // Receive from Server
            modifiedSentence = inFromServer.readLine();
            logger.log(Level.INFO, "Inside TCPClient, msg from server: " + modifiedSentence);

            String [] message = modifiedSentence.split(whitespaceRegex);
            if(message[0].equalsIgnoreCase("server")){
                // Call lamport
                try{
                StringTokenizer st = new StringTokenizer(modifiedSentence);
                Message receivedMessage = Message.parseMsg(st);
                LamportMutex.handleMsg(receivedMessage, receivedMessage.getSrcId(), receivedMessage.getTag());
                }catch (Exception e){
                    logger.log(Level.SEVERE, String.valueOf(e));
                }
            }else{
                //Write results to output file
                writeOutputFile(modifiedSentence);
            }
            //Close the socket when finished with the transaction
            clientSocket.close();
            getNextAddress = false;

        } catch (SocketTimeoutException to) {
            getNextAddress = true;
        } catch (IOException e) {
            //e.printStackTrace();
            getNextAddress = true;
        }
    }

    private void writeOutputFile(String result) {
        System.out.println("Stub for writing to output file: " + result);
    }
}
