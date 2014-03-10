package utils;

/**
 * Created by prateek on 3/8/14.
 */
public interface Lock {
    public void requestCS (); //may block
    public void releaseCS ();
}