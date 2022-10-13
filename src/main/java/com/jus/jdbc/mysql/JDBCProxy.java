package com.jus.jdbc.mysql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;


/**
 * 动态代理Execute接口
 */
public class JDBCProxy implements InvocationHandler {

    private ExecuteImpl executeImpl;
    private AspectHandler aspectHandler;
    private boolean debug;

    public JDBCProxy(ExecuteImpl executeImpl) {
        this.executeImpl = executeImpl;
        Map jdbcConfig = executeImpl.getConfig();
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
        return (Execute)Proxy.newProxyInstance(this.getClass().getClassLoader(),executeImpl.getClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(aspectHandler != null){
            aspectHandler.before(args);
        }
        if(debug){
            System.out.println("JDBCUtils: "+args[0]);
            if(args.length > 1 && args[1] instanceof List){
                System.out.println("params: "+args[1]);
            }
        }
        Object res = null;
        try {
            res = method.invoke(executeImpl,args);
        }catch (Exception e) {
            if(aspectHandler != null){
                if(aspectHandler.error((String) args[0], (InvocationTargetException) e)) throw e;
            }else{
                throw e;
            }
        }
        if(aspectHandler != null){
            aspectHandler.after(args);
        }
        return res;
    }

}
