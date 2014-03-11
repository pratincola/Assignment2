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

    protected Hashtable books = new Hashtable<String, bookValues>();

    private int numOfBooks;

    @Override
    public String toString() {
        String hashRepresentation = "";
        bookValues b;
        for (Object book : books.keySet()) {
            b = (bookValues)books.get(book);
            hashRepresentation = hashRepresentation + (String)book + ','
                    + b.getBookNum() + ',' +  b.getClientName()
                    + ',' + b.getCheckedOut() + ';';
        }
        return hashRepresentation;
    }

    public void libraryUpdate(String s){
        String [] chunks = s.split(";");
        for(String k: chunks){
            String key = k.split(",")[0];
            String id = k.split(",")[1];
            String client = k.split(",")[2];
            String checkedOut = k.split(",")[3];
            bookValues b = new bookValues(Integer.parseInt(id), client, Boolean.valueOf(checkedOut));
            books.remove(key);
            books.put(key, b);
        }
    }
}
