package jdbc.mysql;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 用于建立与获取与数据库的连接
 */
public class JDBC {

    private String url;
    private String username;
    private String password;

    private Map config;

    private List<Connection> cons;

    protected static HashMap<Class, String> resultMethods = new HashMap<Class, String>();

    static {
        resultMethods.put(byte.class, "getByte");
        resultMethods.put(Byte.class, "getByte");
        resultMethods.put(short.class, "getShort");
        resultMethods.put(Short.class, "getShort");
        resultMethods.put(int.class, "getInt");
        resultMethods.put(Integer.class, "getInt");
        resultMethods.put(long.class, "getLong");
        resultMethods.put(Long.class, "getLong");
        resultMethods.put(float.class, "getFloat");
        resultMethods.put(Float.class, "getFloat");
        resultMethods.put(double.class, "getDouble");
        resultMethods.put(Double.class, "getDouble");
        resultMethods.put(boolean.class, "getBoolean");
        resultMethods.put(Boolean.class, "getBoolean");
        resultMethods.put(char.class, "getString");
        resultMethods.put(Character.class, "getString");
        resultMethods.put(String.class, "getString");
        resultMethods.put(Date.class, "getDate");
    }


    /**
     * 在构造函数内进行配置文件读取和连接池的初始化
     * @param path 配置文件的路径
     */
    public JDBC(String path) {
        try {
            Yaml yaml = new Yaml();
            InputStream in = new FileInputStream(path);
            Map config = yaml.loadAs(in, Map.class);
            Map jdbc = (Map) config.get("jdbc");
            this.config = jdbc;
            this.config(jdbc);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("读取配置文件失败");
        }
    }

    public Map getConfig() {
        return config;
    }

    /**
     * 加载相关的配置和连接池
     * @param jdbc 从配置文件读取的map对象
     */
    private void config(Map jdbc) {
        Map datasource = (Map) jdbc.get("datasource");
        Map config = (Map) jdbc.get("config");
        String driver = (String) datasource.get("driver");
        try {
            Class.forName(driver);
            this.url = String.valueOf(datasource.get("url"));
            this.username = String.valueOf(datasource.get("username"));
            this.password = String.valueOf(datasource.get("password"));
            if(config.get("maxCount") != null){
                this.loadConnection((Integer) config.get("maxCount"));
            }else{
                this.loadConnection(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载数据库连接池
     * @param count 连接数
     */
    private void loadConnection(int count){
        this.cons = new ArrayList<Connection>();
        for (int i = 0; i < count; i++) {
            try {
                cons.add(DriverManager.getConnection(url, username, password));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从连接池内获取一个连接，如果没有闲置的连接，则进入等待
     * @return Connection连接对象
     */
    protected Connection getConnection(){
        synchronized (cons){
            if(cons.size() < 1){
                try {
                    cons.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Connection conn = cons.remove(cons.size()-1);
            try {
                if(conn == null || conn.isClosed()){
                    conn = DriverManager.getConnection(url, username, password);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return conn;
        }
    }

    /**
     * 闲置该连接，使得其他线程可以获取该连接
     * @param conn Connection连接对象
     */
    protected void free(Connection conn){
        synchronized (cons){
            cons.add(conn);
            cons.notify();
        }
    }

}
