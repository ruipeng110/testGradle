package priv.crp.util;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.ContextLoader;

import java.io.File;

/**
 * Created by crp on 2017/6/27.
 *
 *
 * 对于2.4.11这个版本的groovy，下面代码可以解释执行单条脚本   2.1.16在run的时候参数不对，不行
 * //localhost:8181/test/testgroovy?orderCode=BZ-XD-9836860342080&script=new File("C:\\Users\\Administrator\\Desktop\\debug.log").beanxxx.getBean("orderServiceImpl").getOrder("BZ-XD-9836860342080")
 //--------------------------------
 CompilerConfiguration conf = new CompilerConfiguration(System.getProperties());
 GroovyShell shell = new GroovyShell(conf);
 final Thread current = Thread.currentThread();
 class DoSetContext implements PrivilegedAction {
 ClassLoader classLoader;

 public DoSetContext(ClassLoader loader) {
 classLoader = loader;
 }

 public Object run() {
 current.setContextClassLoader(classLoader);
 return null;
 }
 }

 AccessController.doPrivileged(new DoSetContext(shell.getClassLoader()));
 GroovyCodeSource groovyCodeSource = new GroovyCodeSource(script, "script_from_command_line", GroovyShell.DEFAULT_CODE_BASE);
 //------------------------------groovy main中抄的代码
 return shell.run(groovyCodeSource, new LinkedList());
 *
 *
 *
 */
public class SpringUtilMethods {

    public static BeanFactory getBeanxxx(File methods){
        try{
            return ContextLoader.getCurrentWebApplicationContext();
        } catch (Exception e){
            return null;
        }
    }

}
