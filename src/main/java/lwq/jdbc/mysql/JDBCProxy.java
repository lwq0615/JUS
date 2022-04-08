package lwq.jdbc.mysql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JDBCProxy implements InvocationHandler {

    private JDBC jdbc;

    public JDBCProxy(JDBC jdbc) {
        this.jdbc = jdbc;
    }

    public Execute getProxy(){
        return (Execute)Proxy.newProxyInstance(this.getClass().getClassLoader(),jdbc.getClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(jdbc.debug()){
            System.out.println("JDBCUtils: "+args[0]);
        }
        return method.invoke(jdbc,args);
    }

}
