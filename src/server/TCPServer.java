package server;

import logicfactory.businessLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by prateek on 3/6/14.
 */
public class TCPServer implements Runnable {
    int port, cap;
    byte [] Ibuf;
    ServerSocket welcomeSocket;
    String clientRequest;
    String clientResponse;

    businessLogic bl ;

    public TCPServer(int serverPort, int bufflen){
        this.port = serverPort;
        Ibuf = new byte [bufflen] ;

        try {
            welcomeSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while(true)
        {
            Socket connectionSocket = null;
            try {
                // Inbound
                connectionSocket = welcomeSocket.accept();
                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                clientRequest = inFromClient.readLine();

                // Business Logic
                byte [] b = bl.makeResponse(clientRequest); // singleton call
                clientResponse = new String(b, "UTF-8");

                // Outbound
                PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream(), true);
//                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                outToClient.write(clientResponse);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
