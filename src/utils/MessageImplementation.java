package utils;

import client.TCPClient;
import com.sun.tools.javac.util.Pair;
import logicfactory.LamportMutex;
import logicfactory.businessLogic;
import logicfactory.library;
import server.serverAttribute;

import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by prateek on 3/9/14.
 */
public class MessageImplementation {

    private final String whitespaceRegex = "\\s";
    private final String whitespace = " ";

    final Logger logger = Logger.getLogger(MessageImplementation.class.getName());
    serverAttribute server = serverAttribute.getInstance();
    businessLogic bl = new businessLogic();
    LamportMutex lm = LamportMutex.getInstance();

    public void broadcastMsg(int src, String MsgType , String Msg){
        for(Map.Entry<Integer,String> entry: server.getServerAddresses().entrySet()){
            // Send message to everyone but me
            if(entry.getKey() != src){
                Pair<String,Integer> idPair  = server.getAddressForServer(entry.getKey());
                sendMsg(idPair.snd,idPair.fst, entry.getKey() ,src, MsgType, Msg);

            }
        }
    }

    //Broadcast for sending library to all other servers
    public void broadcastMsg(int src, String MsgType , String Msg, library updatedLibrary){
        for(Map.Entry<Integer,String> entry: server.getServerAddresses().entrySet()){
            // Send message to everyone but me
            if(entry.getKey() != src){
                Pair<String,Integer> idPair  = server.getAddressForServer(entry.getKey());
                sendMsg(idPair.snd,idPair.fst, entry.getKey() ,src, MsgType, Msg, updatedLibrary);

            }
        }
    }

    //Send message for replicating data between servers
    public void sendMsg( int destPort , String destIP, int destServerID, int srcServerID, String tag, String msg, library updatedLibrary){
        // Compose message
        Message m = new Message(srcServerID, destServerID, tag, msg );
        TCPClient tcpClient = new TCPClient(destPort,destIP, m.toString(), updatedLibrary);
        Thread tC = new Thread(tcpClient);
        tC.start();
    }


    public void sendMsg( int destPort , String destIP, int destServerID, int srcServerID, String tag, String msg){
        // Compose message
        Message m = new Message(srcServerID, destServerID, tag, msg );
        TCPClient tcpClient = new TCPClient(destPort,destIP, m.toString());
//        tcpClient.run();
        Thread tC = new Thread(tcpClient);
        tC.start();
        logger.log(Level.INFO, "Thread is: " +  String.valueOf(tcpClient.getStatus()));
    }

    public String sendAck( int destServerID, int srcServerID, String tag, String msg){
        // Compose message
        Message m = new Message(srcServerID, destServerID, tag, msg );
        Pair<String, Integer> idPair =  server.getAddressForServer(destServerID);
        return m.toString();
//        TCPClient tcpClient = new TCPClient(idPair.snd,idPair.fst, m.toString());
//        Thread tC = new Thread(tcpClient);
//        tC.start();
    }

    // Have to distinguish the message from client vs. from another server
    public byte [] receiveMsg (String tcpMessage, library lib) throws InterruptedException {
        byte[] res = "false".getBytes();

    try{
        String [] message = tcpMessage.split(whitespaceRegex);
        //If a server sends out a replicate command, we want it to go straight to business logic
        //We assume that replicate commands have already requested a mutex
        if(message[0].equalsIgnoreCase("server") && !message[1].equalsIgnoreCase("replicate")){
            // call mutex
            String ackMsg;
            StringTokenizer st = new StringTokenizer(tcpMessage);
            Message receivedMessage = Message.parseMsg(st);
            ackMsg = lm.handleMsg(receivedMessage, receivedMessage.getSrcId(), receivedMessage.getTag() );
            res = ackMsg.getBytes();
        }

        else{
//            lm.requestCS();
            res =  bl.makeResponse(tcpMessage, lib);
//            lm.releaseCS();
        }
    }catch (NullPointerException e){
        logger.log(Level.INFO, String.valueOf(e));
    }
        return res;
    }

    // useless, need to remove; but okay for now.
    public synchronized void myWait() {
        try {
            wait(1000);
        } catch (InterruptedException e) {System.err.println(e);
        }
    }


}
