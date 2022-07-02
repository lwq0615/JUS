package lwq.jdbc.mysql;

import lwq.jdbc.annotation.Column;
import lwq.jdbc.annotation.Pass;
import lwq.utils.ClassUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBC implements Execute {

    private String url;
    private String username;
    private String password;

    private Map config;

    private List<Connection> cons;

    private static HashMap<Class, String> resultMethods = new HashMap<Class, String>();

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
     * 查询一条记录
     * @param sql 查询语句
     * @param rClass 映射类型
     * @return 查询结果
     */
    @Override
    public <R> R query(String sql, Class<R> rClass) throws Exception{
        R res = null;
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            ResultSet result = statement.executeQuery(sql);
            if(result.next()){
                res = rClass.newInstance();
                this.setFieldValue(res,result);
            }
        }catch (Exception e){
            throw e;
        }finally {
            try {
                statement.close();
                this.free(conn);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * 查询多条记录
     * @param sql 查询语句
     * @param rClass 映射类型
     * @return 查询结果
     */
    @Override
    public <E> List<E> queryList(String sql, Class<E> rClass) throws Exception{
        List<E> res = new ArrayList<>();
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while(result.next()) {
                E obj = rClass.newInstance();
                this.setFieldValue(obj, result);
                res.add(obj);
            }
        }catch (Exception e){
            throw e;
        }finally {
            try {
                statement.close();
                this.free(conn);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * 增删改
     * @param sql sql语句
     * @return 操作成功的记录条数
     */
    @Override
    public int execute(String sql) throws Exception{
        int res = 0;
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            res = statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw e;
        }finally {
            try {
                statement.close();
                this.free(conn);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * 插入一条数据并返回自动递增的id
     * @param sql 插入语句
     * @return 插入成功返回自动递增的id，否则返回null
     */
    @Override
    public Integer insertReturnId(String sql) throws Exception{
        Integer res = null;
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            statement.executeUpdate(sql);
            ResultSet result = statement.executeQuery("select LAST_INSERT_ID() id");
            result.next();
            res = result.getInt("id");
        } catch (SQLException e) {
            throw e;
        }finally {
            try {
                statement.close();
                this.free(conn);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * 分页查询
     * @param sql 查询sql
     * @param rClass 映射类型
     * @param current 当前页
     * @param size 每页条数
     * @return Page对象
     */
    @Override
    public <E extends Entity> Page<E> getPage(String sql, Class rClass, int current, int size) {
        if(current < 1){
            current = 1;
        }
        if(size < 0){
            size = 0;
        }
        int selectStart = sql.toLowerCase().indexOf("select");
        int fromStart = sql.toLowerCase().indexOf("from");
        String countSql = sql.substring(0,selectStart+6)+" count(*) count "+sql.substring(fromStart);
        int count = this.queryCount(countSql);
        Page<E> page = new Page<E>(current, size, count);
        if(count == 0){
            return page;
        }
        sql += " limit "+(current-1)*size+","+size;
        List data = null;
        try {
            data = this.queryList(sql,rClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(data != null){
            page.addAll(data);
        }
        return page;
    }

    /**
     * 查询total
     * @param sql 查询语句
     * @return total总条数
     */
    private int queryCount(String sql){
        int res = 0;
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
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
     * 根据数据库查询结果为一个类型的实例赋值，作为一行的数据
     * @param res 类实例
     * @param result 查询结果
     */
    private void setFieldValue(Object res, ResultSet result){
        Field[] fields = ClassUtils.getFieldsToClass(res.getClass(), Entity.class);
        for (Field field : fields) {
            if(field.getAnnotation(Pass.class) != null){
                continue;
            }
            field.setAccessible(true);
            Column column = field.getAnnotation(Column.class);
            String columnName = column==null ? field.getName() : column.value();
            String methodName = this.resultMethods.get(field.getType());
            try{
                Method method = ResultSet.class.getDeclaredMethod(methodName,String.class);
                Object value;
                if(field.getType() == char.class || field.getType() == Character.class){
                    value = ((String) method.invoke(result, columnName)).charAt(0);
                }else{
                    value = method.invoke(result, columnName);
                }
                field.set(res,value);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
