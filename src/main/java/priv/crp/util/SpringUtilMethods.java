package priv.crp.util;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.ContextLoader;

import java.io.File;

/**
 * Created by crp on 2017/6/27.
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
