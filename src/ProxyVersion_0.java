import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by jiangchao08 on 16/12/12.
 */
public class ProxyVersion_0 implements Serializable {

    private static final long serialVersionUID = 1L;

    public static Object newProxyInstance() throws Exception{
        String src = "public class StaticProxy implements HelloWorld {\n" +
                "\n" +
                "    private HelloWorld helloWorld;\n" +
                "\n" +
                "    public StaticProxy(HelloWorld helloWorld) {\n" +
                "        this.helloWorld = helloWorld;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void print() {\n" +
                "        System.out.println(\"Before Hello World!\");\n" +
                "        helloWorld.print();\n" +
                "        System.out.println(\"After Hello World!\");\n" +
                "    }\n" +
                "}";
        /**  生成一段java代码 **/
        String fileDir = System.getProperty("user.dir");
        String fileName = fileDir + "/src/StaticProxy.java";
        File javaFile = new File(fileName);
        Writer writer = new FileWriter(javaFile);
        writer.write(src);
        writer.close();

        /** 动态编译这段java代码，生成.class文件 **/
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager sjfm = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> iter = sjfm.getJavaFileObjects(fileName);
        JavaCompiler.CompilationTask ct = compiler.getTask(null, sjfm, null, null, null, iter);
        ct.call();
        sjfm.close();

        /** 将生成的.class文件载入内存，默认的ClassLoader只能载入CLASSPATH下的.class文件 **/
        URL[] urls = new URL[]{(new URL("file:/" + System.getProperty("user.dir") + "/src"))};
        URLClassLoader ul = new URLClassLoader(urls);
        Class<?> c = ul.loadClass("StaticProxy");

        /** 利用反射将c实例化出来  **/
        Constructor<?> constructor = c.getConstructor(HelloWorld.class);
        HelloWorld helloWorldImpl = new HelloWorldImpl();
        HelloWorld helloWorld = (HelloWorld) constructor.newInstance(helloWorldImpl);

        /** 使用完毕删除生成的代理.java文件和.class文件，这样就看不到动态生成的内容了**/
        File classFile = new File(fileDir + "/src/StaticProxy.class");
        //javaFile.delete();
        //classFile.delete();

        return helloWorld;
    }

    public static void main(String[] args) throws Exception
    {
        long start = System.currentTimeMillis();
        HelloWorld helloWorld = (HelloWorld)ProxyVersion_0.newProxyInstance();
        System.out.println("动态生成代理耗时：" + (System.currentTimeMillis() - start) + "ms");
        helloWorld.print();
        System.out.println();
    }
}
