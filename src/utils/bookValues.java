package utils;

/**
 * Created by prateek on 3/6/14.
 */
public class bookValues {

    private Integer bookNum;
    private String clientName;
    private Boolean checkedOut;

    public bookValues(final Integer bookNum, final String clientName) {
        this.bookNum = bookNum;
        this.clientName = clientName;
        this.checkedOut = false;
    }

    public bookValues(final Integer bookNum, final String clientName, final boolean checkedOut) {
        this.bookNum = bookNum;
        this.clientName = clientName;
        this.checkedOut = checkedOut;
    }

    public Integer getBookNum() {
        return bookNum;
    }

    public void setBookNum(Integer bookNum) {
        this.bookNum = bookNum;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Boolean getCheckedOut() {
        return checkedOut;
    }

    public void setCheckedOut(Boolean checkedOut) {
        this.checkedOut = checkedOut;
    }
}
