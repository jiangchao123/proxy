package com.version_2;

import java.lang.reflect.Method;

/**
 * Created by jiangchao08 on 16/12/12.
 */
public class HelloInvocationHandler implements InvocationHandler {

    private Object object;

    public HelloInvocationHandler(Object object) {
        this.object = object;
    }

    @Override
    public void invoke(Object proxy, Method method) throws Exception{
        System.out.println("Before Hello World!");
        try {
            method.invoke(object, new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("After Hello World!");
    }
}