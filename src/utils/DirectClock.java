package utils;

/**
 * Created by prateek on 3/8/14.
 */
public class DirectClock {

    public int [] clock;
    int myId;

    // Need to fix this!!******************
    int numProc ;

    public DirectClock(int numProc, int id ) {
        myId = id ;
        clock = new int [numProc + 1]; // Since all our serverIDs start with 1 and not 0, however, the loops start at 0
        for (int i = 0; i <= numProc ; i++){
            clock[i] = 0;
        }
            clock [myId] = 1;

    }

    public int getValue(int i ) {
        return clock [i] ;
    }

    public void tick () {
        clock [myId]++;
    }

    public void sendAction () {
    // sentvalue = clock [myld];
        tick ();
    }

    public void receiveAction (int sender , int sentvalue) {
        clock [ sender ] = Math.max(clock[sender], sentvalue );
        clock [myId] = Math.max(clock[myId], sentvalue) + 1;
    }

}