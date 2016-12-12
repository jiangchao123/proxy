import com.version_2.HelloInvocationHandler;
import com.version_2.InvocationHandler;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by jiangchao08 on 16/12/12.
 */
public class ProxyVersion_2 implements Serializable {

    private static final long serialVersionUID = 1L;

    public static Object newProxyInstance(Class<?> interfaces, InvocationHandler h) throws Exception {
        Method[] methods = interfaces.getMethods();
        StringBuilder sb = new StringBuilder(1024);

        sb.append("import java.lang.reflect.Method;\n\n");
        sb.append("public class $Proxy1 implements " +  interfaces.getSimpleName() + "\n");
        sb.append("{\n");
        sb.append("\tcom.version_2.InvocationHandler h;\n\n");
        sb.append("\tpublic $Proxy1(com.version_2.InvocationHandler h)\n");
        sb.append("\t{\n");
        sb.append("\t\tthis.h = h;\n");
        sb.append("\t}\n\n");

        for (Method m : methods)
        {
            sb.append("\tpublic " + m.getReturnType() + " " + m.getName() + "()\n");
            sb.append("\t{\n");
            sb.append("\t\ttry\n");
            sb.append("\t\t{\n");
            sb.append("\t\t\tMethod md = " + interfaces.getName() + ".class.getMethod(\"" + m.getName() + "\");\n");
            sb.append("\t\t\th.invoke(this, md);\n");
            sb.append("\t\t}\n");
            sb.append("\t\tcatch (Exception e)\n");
            sb.append("\t\t{\n");
            sb.append("\t\t\te.printStackTrace();\n");
            sb.append("\t\t}\n");
            sb.append("\t}\n");
        }
        sb.append("}");

        System.out.println("==============generate .java=====");

        /**  生成一段java代码 **/
        String fileDir = System.getProperty("user.dir");
        String fileName = fileDir + "/src/$Proxy1.java";
        File javaFile = new File(fileName);
        Writer writer = new FileWriter(javaFile);
        writer.write(sb.toString());
        writer.close();

        System.out.println("==============generate .class=====");

        /** 动态编译这段Java代码,生成.class文件 */
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager sjfm = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> iter = sjfm.getJavaFileObjects(fileName);
        JavaCompiler.CompilationTask ct = compiler.getTask(null, sjfm, null, null, null, iter);
        ct.call();
        sjfm.close();

        System.out.println("==============load class=====");

        /** 将生成的.class文件载入内存，默认的ClassLoader只能载入CLASSPATH下的.class文件 **/
        URL[] urls = new URL[]{(new URL("file:/" + System.getProperty("user.dir") + "/src"))};
        URLClassLoader ul = new URLClassLoader(urls);
        Class<?> c = Class.forName("$Proxy1", false, ul);

        /** 利用反射将c实例化出来 */
        Constructor<?> constructor = c.getConstructor(InvocationHandler.class);
        Object obj = constructor.newInstance(h);

        return obj;
    }

    public static void main(String args[]) throws Exception {
        long start = System.currentTimeMillis();
        HelloWorld helloWorldImp = new HelloWorldImpl();
        InvocationHandler invocationHandler = new HelloInvocationHandler(helloWorldImp);
        HelloWorld helloWorld = (HelloWorld)ProxyVersion_2.newProxyInstance(HelloWorld.class, invocationHandler);
        System.out.println("动态生成代理耗时：" + (System.currentTimeMillis() - start) + "ms");
        helloWorld.print();
        System.out.println();
    }
}
