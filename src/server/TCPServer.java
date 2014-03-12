package server;

//import com.sun.jdi.event.ThreadDeathEvent;
//import logicfactory.businessLogic;
import logicfactory.library;
import utils.MessageImplementation;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by prateek on 3/6/14.
 */
public class TCPServer implements Runnable {
    int port, cap;
    byte[] Ibuf;
    ServerSocket welcomeSocket;
    String clientRequest;
    String clientResponse;
    library myLib;

    MessageImplementation ml = new MessageImplementation();
    private final static Logger logger = Logger.getLogger(TCPServer.class.getName());
    serverAttribute server = serverAttribute.getInstance();

    public TCPServer(int serverPort, int bufflen, library lib) {
        this.port = serverPort;
        this.myLib = lib;
        Ibuf = new byte[bufflen];

        try {
            welcomeSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Socket connectionSocket = null;
        while (true) {

            try {
                // Inbound
                logger.log(Level.INFO, "Connecting to: Server " + Integer.toString(server.getServerID()));
                connectionSocket = welcomeSocket.accept();
                logger.log(Level.INFO, "CONNECTED!!!!!");
                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                clientRequest = inFromClient.readLine();
                logger.log(Level.INFO, "Into the TCPServer: " + clientRequest);

                // Business Logic
                if(clientRequest!= null){
                    byte[] b = ml.receiveMsg(clientRequest,myLib);
//                        bl.makeResponse(clientRequest, myLib); // singleton call
                    clientResponse = new String(b, "UTF-8");

                    logger.log(Level.INFO, "Out of TCPServer: "+ clientResponse);
                    // Outbound
//                    PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream(), true);
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
//                    outToClient.write(clientResponse);
                    outToClient.writeBytes(clientResponse + '\n');
                    outToClient.flush();

                    if(0 == server.getSleepCounter()){
                        logger.log(Level.INFO, "Entering SLEEP MODE !!!!!!!!!");
//                        Thread dummy = new Thread();
//                        dummy.sleep(server.getTime2Sleep());
//                        dummy.join();
                          Thread.sleep(server.getTime2Sleep());

                        logger.log(Level.INFO, "!!!!!!Exiting SLEEP MODE");
                    }
                }
                else{
                    clientRequest = "false";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
