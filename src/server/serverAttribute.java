package server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prateek on 3/4/14.
 */
public class serverAttribute {
    int serverID, numOfServersInstances;
    List<String> serverAddresses= new ArrayList<String>();
    List<String> serverInstruction = new ArrayList<String>();


    private static final serverAttribute singleton = new serverAttribute();
    private serverAttribute(){ }
    /* Static 'instance' method */
    public static serverAttribute getInstance( ) {
        return singleton;
    }


    public int getServerID() {
        return serverID;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public String getCanonicalServerID() {
        return "s" + String.valueOf(serverID);
    }

    public int getNumOfServersInstances() {
        return numOfServersInstances;
    }

    public void setNumOfServersInstances(int numOfServersInstances) {
        this.numOfServersInstances = numOfServersInstances;
    }

    public List<String> getServerAddresses() {
        return serverAddresses;
    }

    public void setServerAddresses(List<String> serverAddresses) {
        this.serverAddresses = serverAddresses;
    }

    public List<String> getServerInstruction() {
        return serverInstruction;
    }

    public void setServerInstruction(List<String> serverInstruction) {
        this.serverInstruction = serverInstruction;
    }

}
