package per.east.netty.http.xml;

/**
 * http + xml pojo 类 Address
 */
public class Address {

    /* first line of street information(required) */
    private String street1;

    /* second line of street information(optional) */
    private String street2;

    private String city;

    /* state abbreviation (required for the u.s and canada, optional otherwise) */
    private String state;

    /* postal code (required for the u.s and canada , optional otherwise) */
    private String postCode;

    /* country name (required for the u.s and canada , optional otherwise)  */
    private String country;

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
