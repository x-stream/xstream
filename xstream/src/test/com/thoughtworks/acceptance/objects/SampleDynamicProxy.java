package com.thoughtworks.acceptance.objects;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SampleDynamicProxy implements InvocationHandler {

    private String aField = "hello";

    public static interface InterfaceOne {
        String doSomething();
    }

    public static interface InterfaceTwo {
        String doSomething();
    }

    public static Object newInstance() {
        return Proxy.newProxyInstance(InterfaceOne.class.getClassLoader(),
                new Class[]{InterfaceOne.class, InterfaceTwo.class},
                new SampleDynamicProxy());
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("equals")) {
            return equals(args[0]) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            return aField;
        }
    }

    public boolean equals(Object obj) {
        return equalsInterfaceOne(obj) && equalsInterfaceTwo(obj);
    }

    private boolean equalsInterfaceOne(Object o) {
        if (o instanceof InterfaceOne) {
            InterfaceOne interfaceOne = (InterfaceOne) o;
            return aField.equals(interfaceOne.doSomething());
        } else {
            return false;
        }
    }

    private boolean equalsInterfaceTwo(Object o) {
        if (o instanceof InterfaceTwo) {
            InterfaceTwo interfaceTwo = (InterfaceTwo) o;
            return aField.equals(interfaceTwo.doSomething());
        } else {
            return false;
        }
    }

}
