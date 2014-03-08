package client;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prateek on 3/4/14.
 */
public class clientAttribute {
    int clientID, numOfClientsInstances;
    List<String> hostAddresses = new ArrayList<String>();
    List<String> clientInstruction = new ArrayList<String>();


    private static final clientAttribute singleton = new clientAttribute();

    private clientAttribute() {
    }

    /* Static 'instance' method */
    public static clientAttribute getInstance() {
        return singleton;
    }


    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public int getNumOfClientsInstances() {
        return numOfClientsInstances;
    }

    public void setNumOfClientsInstances(int numOfClientsInstances) {
        this.numOfClientsInstances = numOfClientsInstances;
    }

    public List<String> getHostAddresses() {
        return hostAddresses;
    }

    public void setHostAddresses(List<String> hostAddresses) {
        this.hostAddresses = hostAddresses;
    }

    public List<String> getClientInstruction() {
        return clientInstruction;
    }

    public void setClientInstruction(List<String> clientInstruction) {
        this.clientInstruction = clientInstruction;
    }
}
