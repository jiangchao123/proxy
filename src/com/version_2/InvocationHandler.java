package com.version_2;

import java.lang.reflect.Method;

/**
 * Created by jiangchao08 on 16/12/12.
 */
public interface InvocationHandler {
    void invoke(Object proxy, Method method) throws Exception;
}
