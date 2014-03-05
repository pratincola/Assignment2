package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by prateek on 2/16/14.
 */
public class TCPClient implements Runnable{
    int len, port;
    String hostname, sentence, modifiedSentence;
    Socket clientSocket;

    public TCPClient( int port, String hostname) {
        this.len = len;
        this.port = port;
        this.hostname = hostname;


        try {
            clientSocket = new Socket(hostname, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        DataOutputStream outToServer = null;
        try {
            // Send to Server
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            sentence = inFromUser.readLine();
            outToServer.writeBytes(sentence + '\n');

            // Receive from Server
            modifiedSentence = inFromServer.readLine();
            System.out.println(modifiedSentence);
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
