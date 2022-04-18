package lwq.jdbc.mysql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;


/**
 * 动态代理Execute接口
 */
public class JDBCProxy implements InvocationHandler {

    private JDBC jdbc;
    private AspectHandler aspectHandler;
    private boolean debug;

    public JDBCProxy(JDBC jdbc) {
        this.jdbc = jdbc;
        Map jdbcConfig = jdbc.getConfig();
        Map config = (Map) jdbcConfig.get("config");
        if(config.get("debug") != null){
            this.debug = (boolean) config.get("debug");
        }
        if(config.get("aspectHandler") != null){
            try {
                Class ahClass = Class.forName((String) config.get("aspectHandler"));
                this.aspectHandler = (AspectHandler) ahClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Execute getProxy(){
        return (Execute)Proxy.newProxyInstance(this.getClass().getClassLoader(),jdbc.getClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(aspectHandler != null){
            aspectHandler.before((String) args[0]);
        }
        if(debug){
            System.out.println("JDBCUtils: "+args[0]);
        }
        Object res;
        try {
            res = method.invoke(jdbc,args);
        } catch (Exception e) {
            if(aspectHandler != null){
                aspectHandler.error((String) args[0],(InvocationTargetException)e);
            }
            throw e;
        }
        if(aspectHandler != null){
            aspectHandler.after((String) args[0]);
        }
        return res;
    }

}
