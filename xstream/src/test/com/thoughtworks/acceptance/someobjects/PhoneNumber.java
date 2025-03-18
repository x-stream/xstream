package com.thoughtworks.acceptance.someobjects;

public class PhoneNumber {
    private int code;
    private String number;

    public PhoneNumber(int code, String number) {
        this.code = code;
        this.number = number;
    }

    @Override
    public String toString() {
        return "PhoneNumber{" +
                "code=" + code +
                ", number='" + number + '\'' +
                '}';
    }
}
