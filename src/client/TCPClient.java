package client;

import logicfactory.LamportMutex;
import utils.Message;

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
    private boolean getNextAddress = true;
    private static PrintWriter fileOutput = null;

    private static FileWriter fw;
    private static BufferedWriter bw;

//    BufferedWriter b ;

    LamportMutex lm = LamportMutex.getInstance();

    int len, port;
    String hostname, sentence, modifiedSentence;
    Socket clientSocket;
    private final String whitespaceRegex = "\\s";

    public TCPClient(int port, String hostname, String message) {
        this.len = len;
        this.port = port;
        this.hostname = hostname;
        this.sentence = message;
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

    @Override
    public void run() {
        //BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        DataOutputStream outToServer = null;

        try {

            clientSocket = new Socket(hostname, port);

            // Send to Server
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //sentence = inFromUser.readLine();
            logger.log(Level.INFO, "Inside TCPClient, msg to server: " + sentence);
            outToServer.writeBytes(sentence + '\n');

            // Receive from Server
            modifiedSentence = inFromServer.readLine();
            logger.log(Level.INFO, "Inside TCPClient, msg from server: " + modifiedSentence);

            String [] message = modifiedSentence.split(whitespaceRegex);
            if(message[0].equalsIgnoreCase("server")){
                // Call lamport
                try{
                StringTokenizer st = new StringTokenizer(modifiedSentence);
                Message receivedMessage = Message.parseMsg(st);
//                LamportMutex.handleMsg(receivedMessage, receivedMessage.getSrcId(), receivedMessage.getTag());
                lm.handleMsg(receivedMessage, receivedMessage.getSrcId(), receivedMessage.getTag());
                }catch (Exception e){
                    logger.log(Level.SEVERE, String.valueOf(e));
                }
            }
//            else if((message[0].equalsIgnoreCase("true")) || (message[0].equalsIgnoreCase("false"))) {
//                //Write results to output file
//                this.writeOutputFile(modifiedSentence);
//            }
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

    public void writeOutputFile(String result, File file) {
        System.out.println("Stub for writing to output file: " + result);
        String[] commandTokens = sentence.split(" ");


        String outputString = commandTokens[0] + " " + commandTokens[1];
        try {
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
//            if (fileOutput == null) {
//                fileOutput = new PrintWriter("c"+Integer.toString(clientID)+".out", "UTF-8");
////                b = new BufferedWriter(fileOutput);
//            }
            if (result.equals("false")) {
                bw.write("fail " + outputString + "\n");
                bw.close();
            }
            else if (result.equals("true")) {
                bw.write(outputString + "\n");
                bw.close();
//                b.write(outputString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
