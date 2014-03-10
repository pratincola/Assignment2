package server;

import com.sun.tools.javac.util.Pair;

import java.util.*;

/**
 * Created by prateek on 3/4/14.
 */
public class serverAttribute {
    int serverID, numOfServersInstances;
    Map<Integer, String> serverAddresses = new LinkedHashMap<Integer, String>();
    List<String> serverInstruction = new ArrayList<String>();

    private final String colonRegex = ":";


    private static final serverAttribute singleton = new serverAttribute();

    private serverAttribute() {
    }

    /* Static 'instance' method */
    public static serverAttribute getInstance() {
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

    public Map<Integer, String> getServerAddresses() {
        return serverAddresses;
    }

    public void setServerAddresses(Map<Integer, String> serverAddresses) {
        this.serverAddresses = serverAddresses;
    }

    public List<String> getServerInstruction() {
        return serverInstruction;
    }

    public void setServerInstruction(List<String> serverInstruction) {
        this.serverInstruction = serverInstruction;
    }

    public Pair<String, Integer> getAddressForServer(int i){
        String val = serverAddresses.get(i);
        String[] tokens = val.split(colonRegex);
        return Pair.of(tokens[0], Integer.valueOf(tokens[1]));

    }

}
