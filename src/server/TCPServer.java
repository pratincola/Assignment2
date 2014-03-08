package server;

import logicfactory.businessLogic;
import logicfactory.library;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

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

    businessLogic bl = new businessLogic();

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
        try {
            // Inbound
            connectionSocket = welcomeSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {

            try {
                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                clientRequest = inFromClient.readLine();

                // Business Logic
                byte[] b = bl.makeResponse(clientRequest, myLib); // singleton call
                clientResponse = new String(b, "UTF-8");

                // Outbound
//                PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream(), true);
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                // outToClient.write(clientResponse);
                outToClient.writeBytes(clientResponse);
                outToClient.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
