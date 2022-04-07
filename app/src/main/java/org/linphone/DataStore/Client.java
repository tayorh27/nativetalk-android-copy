package org.linphone.DataStore;

public class Client {

    private String firstName, lastName, email,city,taxId,phoneNumber,address,state,country,postalCode;

    public Client(String firstName, String lastName, String email, String city, String taxId, String phoneNumber, String address, String state, String country, String postalCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.city = city;
        this.taxId = taxId;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
//    public Client(String firstName, String lastName, String email, String city, String taxId, String phoneNumber) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.email = email;
//        this.city = city;
//        this.taxId = taxId;
//        this.phoneNumber = phoneNumber;
//    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getTaxId() {
        return taxId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
