package server;

import logicfactory.businessLogic;
import logicfactory.library;
import utils.MessageImplementation;

import java.io.*;
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
    private library sendLibrary = null;

    businessLogic bl = new businessLogic();
    MessageImplementation ml = new MessageImplementation();

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
        /*try {
            // Inbound
            connectionSocket = welcomeSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        while (true) {

            try {
                // Inbound
                connectionSocket = welcomeSocket.accept();

                //Receive buffers
                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                InputStream is = connectionSocket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);

                clientRequest = inFromClient.readLine();
                if (clientRequest.contains("push")) {
                    myLib = (library)ois.readObject();
                }

                // Business Logic

                byte[] b = ml.receiveMsg(clientRequest,myLib);
//                        bl.makeResponse(clientRequest, myLib); // singleton call
                clientResponse = new String(b, "UTF-8");

                // Outbound
//                PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream(), true);
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                OutputStream os = connectionSocket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);

                // outToClient.write(clientResponse);
                outToClient.writeBytes(clientResponse);
                outToClient.flush();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException notFound) {
                notFound.printStackTrace();
            }

        }
    }

}
