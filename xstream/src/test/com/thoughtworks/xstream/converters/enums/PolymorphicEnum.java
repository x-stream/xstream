package com.thoughtworks.xstream.converters.enums;

enum PolymorphicEnum {
    A() {
        String fruit() {
            return "apple";
        }
    },
    B() {
        String fruit() {
            return "banana";
        }
    };

    abstract String fruit();
}
