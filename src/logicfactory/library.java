package logicfactory;

import utils.bookValues;

import java.util.Hashtable;

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

    protected Hashtable books = new Hashtable<String,bookValues>();

    private int numOfBooks;

}
