package com.thoughtworks.acceptance.someobjects;


public class Employee extends TestPerson {
    private String employeeId;
    private String department;

    public Employee(String firstname, String lastname, PhoneNumber phone, String employeeId, String department) {
        super(firstname, lastname, phone);
        this.employeeId = employeeId;
        this.department = department;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId='" + employeeId + '\'' +
                ", department='" + department + '\'' +
                "} " + super.toString();
    }
}
