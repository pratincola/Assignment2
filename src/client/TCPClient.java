package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

/**
 * Created by prateek on 2/16/14.
 */
public class TCPClient implements Runnable {
    private final int socketTimeout = 1000;

    int len, port;
    String hostname, sentence, modifiedSentence;
    Socket clientSocket;

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
            outToServer.writeBytes(sentence + '\n');

            // Receive from Server
            modifiedSentence = inFromServer.readLine();
            System.out.println(modifiedSentence);

            //Write results to output file
            writeOutputFile(modifiedSentence);

            //Close the socket when finished with the transaction
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeOutputFile(String result) {
        System.out.println("Stub for writing to output file");
    }
}
