package logicfactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by prateek on 3/5/14.
 */
public class library {

    public Hashtable getBooks() {
        return books;
    }

    public void setBooks(Hashtable books) {
        this.books = books;
    }

    public int getNumOfBooks() {
        return numOfBooks;
    }

    public void setNumOfBooks(int numOfBooks) {
        this.numOfBooks = numOfBooks;
    }

    protected Hashtable<String,Integer> books = new Hashtable();
    protected int numOfBooks;

}
