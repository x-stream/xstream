package com.thoughtworks.acceptance.someobjects;


public class TestPerson {
    private String firstname;
    private String lastname;
    private PhoneNumber phone;

    public TestPerson(String firstname, String lastname, PhoneNumber phone) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "TestPerson{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", phone=" + phone +
                '}';
    }
}
