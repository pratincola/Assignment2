package client;

import logicfactory.library;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

/**
 * Created by prateek on 2/16/14.
 */
public class TCPClient implements Runnable {
    private final int socketTimeout = 1000;
<<<<<<< HEAD
    private library serializedLibrary = null;
    private library updatedLibrary = null;
=======
    private boolean getNextAddress = true;
>>>>>>> refs/heads/master

    int len, port;
    String hostname, sentence, modifiedSentence;
    Socket clientSocket;

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

    @Override
    public void run() {
        //BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        DataOutputStream outToServer = null;

        try {
            clientSocket = new Socket(hostname, port);

            // Send to Server variables
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            OutputStream os = clientSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            // Receive from Server variables
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            InputStream is = clientSocket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);

            //Writes data out to the TCP Server
            outToServer.writeBytes(sentence + '\n');
            if (this.serializedLibrary != null) {
                oos.writeObject(this.serializedLibrary);
            }

            // Receive from Server
            modifiedSentence = inFromServer.readLine();
            System.out.println(modifiedSentence);
            if (modifiedSentence.equals("replicate")) {
                updatedLibrary = (library)ois.readObject(ois);
            }

            //Write results to output file
            writeOutputFile(modifiedSentence);

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
        System.out.println("Stub for writing to output file");
    }
}
