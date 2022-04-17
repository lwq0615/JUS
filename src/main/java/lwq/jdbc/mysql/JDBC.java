package lwq.jdbc.mysql;

import lwq.jdbc.annotation.Column;
import lwq.jdbc.utils.ClassUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JDBC implements Execute {

    private String url;
    private String username;
    private String password;

    private Map config;

    private List<Connection> cons;


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
    public <R> R query(String sql, Class<R> rClass){
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
    @Override
    public <E> List<E> queryList(String sql, Class<E> rClass){
        List<E> res = null;
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
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
    @Override
    public int execute(String sql){
        int res = 0;
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
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
    @Override
    public Integer insertReturnId(String sql){
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
     * 分页查询
     * @param sql 查询sql
     * @param rClass 映射类型
     * @param current 当前页
     * @param size 每页条数
     * @return Page对象
     */
    @Override
    public <E extends Entity> Page<E> getPage(String sql, Class rClass, int current, int size) {
        Page<E> page;
        if(current < 1){
            current = 1;
        }
        if(size < 0){
            size = 0;
        }
        int selectStart = sql.toLowerCase().indexOf("select");
        int fromStart = sql.toLowerCase().indexOf("from");
        String countSql = sql.substring(0,selectStart+6)+" count(*) count "+sql.substring(fromStart);
        page = new Page(current, size, this.queryCount(countSql));
        sql += " limit "+(current-1)*size+","+size;
        List data = this.queryList(sql,rClass);
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
        Field[] fields = ClassUtils.getFields(res.getClass());
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
