package utils;

import java. util .*;

/**
 * Created by prateek on 3/5/14.
 */
public class Message {
    int srcId, destId;
    String tag;
    String msgBuf;
    private final String whitespaceRegex = "\\s";
    private final String whitespace = " ";

    public Message(int s, int t, String msgType, String buf) {
        this.srcId = s;
        destId = t;
        tag = msgType;
        msgBuf = buf;
    }
    public int getSrcId() {
        return srcId;
    }
    public int getDestId() {
        return destId;
    }
    public String getTag() {
        return tag;
    }
    public String getMessage() {
        return msgBuf;
    }
    public Integer getClock() {
        return Integer.valueOf(msgBuf);
    }

    public static Message parseMsg(StringTokenizer st){
        String s = st.nextToken() ;
        String tag = st.nextToken();
        int srcId = Integer.parseInt(st.nextToken());
        int destId = Integer.parseInt(st.nextToken());
        String buf = st.nextToken("#");
        return new Message(srcId, destId, tag, buf);
    }


    public String toString(){
        String s = "server" + whitespace +
                tag + whitespace +
                String.valueOf(srcId) + whitespace +
                String.valueOf(destId) + whitespace +
                msgBuf + "#";
        return s;
    }

}
