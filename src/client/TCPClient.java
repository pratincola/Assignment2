package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by prateek on 2/16/14.
 */
public class TCPClient {
    private final int socketTimeout = 1000;

    int len, port;
    String hostname, sentence, modifiedSentence;
    Socket clientSocket;
    ServerSocket tcpSocket;

    public TCPClient( int port, String hostname, String message) {
        this.len = len;
        this.port = port;
        this.hostname = hostname;
        this.sentence = message;

        try {
            tcpSocket = new ServerSocket(port);
            tcpSocket.setSoTimeout(socketTimeout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean connectTCP(){
        //BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        DataOutputStream outToServer = null;

        try {
            clientSocket = tcpSocket.accept();
            // Send to Server
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //sentence = inFromUser.readLine();
            outToServer.writeBytes(sentence + '\n');

            // Receive from Server
            modifiedSentence = inFromServer.readLine();
            System.out.println(modifiedSentence);
            clientSocket.close();

        } catch (SocketTimeoutException to) {
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}
