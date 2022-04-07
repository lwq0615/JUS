package lwq.jdbc.mysql;

import lwq.jdbc.annotation.Column;
import lwq.jdbc.utils.ArrayUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JDBC {

    private String url;
    private String username;
    private String password;

    private boolean debug = false;

    public List<Connection> cons;


    /**
     * 在构造函数内进行配置文件读取和连接池的初始化
     * @param path 配置文件的路径
     */
    public JDBC(String path) {
        try {
            Yaml yaml = new Yaml();
            InputStream in = new FileInputStream(path);
            Map config = yaml.loadAs(in, Map.class);
            this.config(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载相关的配置和连接池
     * @param config 从配置文件读取的map对象
     */
    private void config(Map config) {
        Map datasource = (Map) config.get("datasource");
        String driver = (String) datasource.get("driver");
        try {
            Class.forName(driver);
            this.url = String.valueOf(datasource.get("url"));
            this.username = String.valueOf(datasource.get("username"));
            this.password = String.valueOf(datasource.get("password"));
            if(datasource.get("maxCount") != null){
                this.loadConnection((Integer) datasource.get("maxCount"));
            }else{
                this.loadConnection(100);
            }
            if(datasource.get("debug") != null){
                this.debug = (boolean) datasource.get("debug");
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
    private Connection getConnection(){
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
    private void free(Connection conn){
        synchronized (cons){
            cons.add(conn);
            cons.notify();
        }
    }

    /**
     * 打印执行的sql语句
     * @param sql
     */
    private void debugSql(String sql){
        if(this.debug){
            System.out.println(sql);
        }
    }

    /**
     * 查询一条记录
     * @param sql 查询语句
     * @param rClass 映射类型
     * @return 查询结果
     */
    public <R> R query(String sql, Class<R> rClass){
        R res = null;
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            this.debugSql(sql);
            ResultSet result = statement.executeQuery(sql);
            if(result.next()){
                res = rClass.newInstance();
                this.setFieldValue(res,result);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                statement.close();
                this.free(conn);
            }catch (Exception e){
                e.printStackTrace();
            }
            return res;
        }
    }

    /**
     * 查询多条记录
     * @param sql 查询语句
     * @param rClass 映射类型
     * @return 查询结果
     */
    public <E> List<E> queryList(String sql, Class<E> rClass){
        List<E> res = null;
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            this.debugSql(sql);
            ResultSet result = statement.executeQuery(sql);
            while(result.next()) {
                if(res == null){
                    res = new ArrayList<>();
                }
                E obj = rClass.newInstance();
                this.setFieldValue(obj, result);
                res.add(obj);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                statement.close();
                this.free(conn);
            }catch (Exception e){
                e.printStackTrace();
            }
            return res;
        }
    }

    /**
     * 增删改
     * @param sql sql语句
     * @return 操作成功的记录条数
     */
    public int execute(String sql){
        int res = 0;
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            this.debugSql(sql);
            res = statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                statement.close();
                this.free(conn);
            }catch (Exception e){
                e.printStackTrace();
            }
            return res;
        }
    }

    /**
     * 插入一条数据并返回自动递增的id
     * @param sql 插入语句
     * @return 插入成功返回自动递增的id，否则返回null
     */
    public Integer insertReturnId(String sql){
        Integer res = null;
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            this.debugSql(sql);
            statement.executeUpdate(sql);
            this.debugSql("select LAST_INSERT_ID() id");
            ResultSet result = statement.executeQuery("select LAST_INSERT_ID() id");
            result.next();
            res = result.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                statement.close();
                this.free(conn);
            }catch (Exception e){
                e.printStackTrace();
            }
            return res;
        }
    }

    /**
     * 查询total
     * @param sql 查询语句
     * @return total总条数
     */
    public int queryCount(String sql){
        int res = 0;
        Connection conn = null;
        Statement statement = null;
        try {
            int selectStart = sql.toLowerCase().indexOf("select");
            int fromStart = sql.toLowerCase().indexOf("from");
            sql = sql.substring(0,selectStart+6)+" count(*) count "+sql.substring(fromStart);
            conn = getConnection();
            statement = conn.createStatement();
            this.debugSql(sql);
            ResultSet result = statement.executeQuery(sql);
            result.next();
            res = result.getInt("count");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                statement.close();
                this.free(conn);
            }catch (Exception e){
                e.printStackTrace();
            }
            return res;
        }
    }


    /**
     * 获取某个类的所有属性（包括父类的属性）
     * @param cls 类的class对象
     * @return Field数组
     */
    protected Field[] getFields(Class cls){
        if(cls == null){
            return null;
        }
        Field[] fields = cls.getDeclaredFields();
        return ArrayUtils.concat(fields,this.getFields(cls.getSuperclass()));
    }

    /**
     * 根据数据库查询结果为一个类型的实例赋值，作为一行的数据
     * @param res 类实例
     * @param result 查询结果
     */
    protected void setFieldValue(Object res, ResultSet result){
        Field[] fields = getFields(res.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            String type;
            if(field.getType() == Integer.class){
                type = "int";
            }else{
                String[] typeStr = field.getType().toString().split("\\.");
                type = typeStr[typeStr.length-1];
            }
            type = type.substring(0,1).toUpperCase()+type.substring(1);
            String methonName = "get" + type;
            try{
                Method method;
                Object value;
                Column column = field.getAnnotation(Column.class);
                String columnName = column==null ? field.getName() : column.value();
                if(field.getType() == char.class){
                    method = result.getClass().getDeclaredMethod("getString",String.class);
                    value = ((String)method.invoke(result, columnName)).charAt(0);
                }else{
                    method = result.getClass().getDeclaredMethod(methonName,String.class);
                    value = method.invoke(result, columnName);
                }
                field.set(res,value);
            }catch (Exception e){
                return;
            }
        }
    }

}
