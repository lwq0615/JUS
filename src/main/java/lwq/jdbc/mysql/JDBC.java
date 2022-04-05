package lwq.jdbc.mysql;

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

    private Page page;


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

    private void config(Map config){
        Map datasource = (Map)config.get("datasource");
        String driver = (String) datasource.get("driver");
        try {
            Class.forName(driver);
        }catch (Exception e){
            e.printStackTrace();
        }
        this.url = String.valueOf(datasource.get("url"));
        this.username = String.valueOf(datasource.get("username"));
        this.password = String.valueOf(datasource.get("password"));
    }

    private Connection getConnection(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return conn;
        }
    }

    /**
     * 设置分页信息，设置后getPage方法将自动分页
     * @param current 当前页
     * @param size 每页条数
     */
    public void setPage(Integer current, Integer size){
        this.page = new Page(current,size);
    }

    /**
     * 分页查询
     * @param sql 查询语句
     * @param rClass 返回数据类型
     * @return Page分页数据
     */
    protected Page getPage(String sql, Class rClass){
        String countSql = sql.substring(0,sql.indexOf("select")+6)+" count(*) count "+sql.substring(sql.indexOf("from"));
        try{
            Connection conn = this.getConnection();
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(countSql);
            result.next();
            this.page.setTotal(result.getInt("count"));
            Integer current = this.page.getCurrent();
            Integer size = this.page.getSize();
            if(current != null && size != null){
                sql += " limit "+(current-1)*size+","+size;
            }
            this.page.setData(this.queryList(sql,rClass));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return this.page;
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
                conn.close();
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
    public List queryList(String sql, Class rClass){
        List res = null;
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while(result.next()) {
                Object obj = rClass.newInstance();
                this.setFieldValue(obj, result);
                if(res == null){
                    res = new ArrayList();
                }
                res.add(obj);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                statement.close();
                conn.close();
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
            res = statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }finally {
            try {
                statement.close();
                conn.close();
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
            statement.executeUpdate(sql);
            ResultSet result = statement.executeQuery("select LAST_INSERT_ID() id");
            result.next();
            res = result.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }finally {
            try {
                statement.close();
                conn.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            return res;
        }
    }


    protected Field[] getFields(Class cls){
        if(cls == null){
            return null;
        }
        Field[] fields = cls.getDeclaredFields();
        return ArrayUtils.concat(fields,this.getFields(cls.getSuperclass()));
    }

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
                if(field.getType() == char.class){
                    method = result.getClass().getDeclaredMethod("getString",String.class);
                    value = ((String)method.invoke(result, field.getName())).charAt(0);
                }else{
                    method = result.getClass().getDeclaredMethod(methonName,String.class);
                    value = method.invoke(result, field.getName());
                }
                field.set(res,value);
            }catch (Exception e){
                return;
            }
        }
    }

}
