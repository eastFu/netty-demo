package per.east.netty.http.xml;

import java.util.List;

/**
 *  http + xml pojo 类 Customer
 */
public class Customer {

    private long customerNumber;

    /* personal name */
    private String firstName;

    /* familly name */
    private String lastName;

    /* middle name(s) ,if any */
    private List<String> middleNames;


    public long getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(long customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(List<String> middleNames) {
        this.middleNames = middleNames;
    }
}
