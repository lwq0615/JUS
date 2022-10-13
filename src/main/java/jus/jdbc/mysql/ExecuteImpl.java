package jus.jdbc.mysql;

import jus.jdbc.annotation.Column;
import jus.jdbc.annotation.Pass;
import jus.utils.ClassUtils;
import jus.utils.NumberUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 获取并执行sql
 */
public class ExecuteImpl extends JDBC implements Execute {


    public ExecuteImpl(String path) {
        super(path);
    }

    /**
     * 加载预编译参数
     */
    private void loadPreStaParams(PreparedStatement preparedStatement, List params) throws SQLException {
        if(Objects.isNull(params)) return;
        for (int i = 0; i < params.size(); i++) {
            if(NumberUtils.isFloat(params.get(i))) preparedStatement.setFloat(i+1, (float)params.get(i));
            if(NumberUtils.isDouble(params.get(i))) preparedStatement.setDouble(i+1, (double)params.get(i));
            if(NumberUtils.isNumber(params.get(i))) preparedStatement.setInt(i+1, (int)params.get(i));
            else preparedStatement.setString(i+1, (String)params.get(i));
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
            }catch (Exception e){
                e.printStackTrace();
            }
            this.free(conn);
        }
        return res;
    }


    /**
     * 预编译查询
     * @param params 预编译查询参数列表
     */
    @Override
    public <R> R query(String sql, List params, Class<R> rClass) throws Exception {
        R res = null;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = getConnection();
            preparedStatement = conn.prepareStatement(sql);
            this.loadPreStaParams(preparedStatement, params);
            ResultSet result = preparedStatement.executeQuery();
            if(result.next()){
                res = rClass.newInstance();
                this.setFieldValue(res,result);
            }
        }catch (Exception e){
            throw e;
        }finally {
            try {
                preparedStatement.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            this.free(conn);
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
        List<E> data = new ArrayList<>();
        Page page = PageLimit.getPage();
        int total = 0;
        if(page != null){
            total = this.queryTotal(sql);
            sql += " limit "+(page.getCurrent()-1)*page.getSize()+", "+page.getSize();
        }
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while(result.next()) {
                E obj = rClass.newInstance();
                this.setFieldValue(obj, result);
                data.add(obj);
            }
        }catch (Exception e){
            throw e;
        }finally {
            try {
                statement.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            this.free(conn);
        }
        if(page != null){
            PageLimit.setPageInfo(data, page.getCurrent(), page.getSize(), total);
        }
        return data;
    }


    /**
     * 预编译列表查询
     */
    @Override
    public <E> List<E> queryList(String sql, List params, Class<E> rClass) throws Exception {
        List<E> data = new ArrayList<>();
        Page page = PageLimit.getPage();
        int total = 0;
        if(page != null){
            total = this.queryTotal(sql);
            sql += " limit "+(page.getCurrent()-1)*page.getSize()+", "+page.getSize();
        }
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = getConnection();
            preparedStatement = conn.prepareStatement(sql);
            this.loadPreStaParams(preparedStatement, params);
            ResultSet result = preparedStatement.executeQuery();
            while(result.next()) {
                E obj = rClass.newInstance();
                this.setFieldValue(obj, result);
                data.add(obj);
            }
        }catch (Exception e){
            throw e;
        }finally {
            try {
                preparedStatement.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            this.free(conn);
        }
        if(page != null){
            PageLimit.setPageInfo(data, page.getCurrent(), page.getSize(), total);
        }
        return data;
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
            }catch (Exception e){
                e.printStackTrace();
            }
            this.free(conn);
        }
        return res;
    }

    /**
     * 预编译增删改
     */
    @Override
    public int execute(String sql, List params) throws Exception {
        int res = 0;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = getConnection();
            preparedStatement = conn.prepareStatement(sql);
            this.loadPreStaParams(preparedStatement, params);
            res = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }finally {
            try {
                preparedStatement.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            this.free(conn);
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
            }catch (Exception e){
                e.printStackTrace();
            }
            this.free(conn);
        }
        return res;
    }

    /**
     * 预编译返回自增ID
     */
    @Override
    public Integer insertReturnId(String sql, List params) throws Exception{
        Integer res = null;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = getConnection();
            preparedStatement = conn.prepareStatement(sql);
            this.loadPreStaParams(preparedStatement, params);
            preparedStatement.executeUpdate();
            ResultSet result = preparedStatement.executeQuery("select LAST_INSERT_ID() id");
            result.next();
            res = result.getInt("id");
        } catch (SQLException e) {
            throw e;
        }finally {
            try {
                preparedStatement.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            this.free(conn);
        }
        return res;
    }


    /**
     * 查询total
     * @param sql 查询语句
     * @return total总条数
     */
    private int queryTotal(String sql){
        int selectStart = sql.toLowerCase().indexOf("select");
        int fromStart = sql.toLowerCase().indexOf("from");
        String totalSql = sql.substring(0,selectStart+6)+" count(*) count "+sql.substring(fromStart);
        int res = 0;
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            ResultSet result = statement.executeQuery(totalSql);
            result.next();
            res = result.getInt("count");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                statement.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            this.free(conn);
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
            Column column = field.getAnnotation(Column.class);
            String columnName = column==null ? field.getName() : column.value();
            String methodName = this.resultMethods.get(field.getType());
            try{
                Method method = ResultSet.class.getDeclaredMethod(methodName, String.class);
                Object value = null;
                if(field.getType() == char.class || field.getType() == Character.class){
                    try {
                        value = ((String) method.invoke(result, columnName)).charAt(0);
                    }catch (Exception e){}
                }else{
                    try {
                        value = method.invoke(result, columnName);
                    }catch (Exception e){}
                }
                field.set(res,value);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
