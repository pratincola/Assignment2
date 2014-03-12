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

    public void sendMsg( int destPort , String destIP, int destServerID, int srcServerID, String tag, String msg){
        // Compose message
        Message m = new Message(srcServerID, destServerID, tag, msg );
        TCPClient tcpClient = new TCPClient(destPort,destIP, m.toString());
//        tcpClient.run();
        Thread tC = new Thread(tcpClient);
        tC.start();
        try {
            tC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(tcpClient.getStatus() == true){
            //Faking message from the faulted server.
            Message ms = new Message(destServerID, srcServerID, "ack", String.valueOf( Integer.valueOf(msg) + 1));
            LamportMutex.handleMsg(ms, destServerID, "ack");
            logger.log(Level.INFO, ms.toString());
        }

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
        if(message[0].equalsIgnoreCase("server")){
            // call mutex
            String ackMsg;
            StringTokenizer st = new StringTokenizer(tcpMessage);
            Message receivedMessage = Message.parseMsg(st);
            if(receivedMessage.getTag().equalsIgnoreCase("replicate")){
                //replicate
                bl.updateLib(receivedMessage.getMessage(), lib);
                res = "Successful".getBytes();
            }else{
                ackMsg = lm.handleMsg(receivedMessage, receivedMessage.getSrcId(), receivedMessage.getTag() );
                res = ackMsg.getBytes();
            }
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