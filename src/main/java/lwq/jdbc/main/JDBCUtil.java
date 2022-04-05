package lwq.jdbc.main;

import lwq.jdbc.annotation.Column;
import lwq.jdbc.annotation.Id;
import lwq.jdbc.annotation.Table;
import lwq.jdbc.utils.ArrayUtils;
import lwq.jdbc.utils.NumberUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JDBCUtil extends JDBC {

    private final String NO_TABLE_MESSAGE = "Before using entity as parameters, " +
            "you need to configure @Table(tableName) annotation of the entity.";

    public JDBCUtil(String path) {
        super(path);
    }


    /**
     * 查询数据库中的一条记录4
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为查询参数
     * @return 返回查询结果，查询不到返回null
     */
    public <R> R query(Object obj){
        String sql = this.getSelectSql(obj);
        Class cls = obj.getClass();
        return (R)this.query(sql, cls);
    }

    /**
     * 查询数据库中的多条记录
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为查询参数
     * @return 返回查询结果，查询不到返回null
     */
    public List queryList(Object obj){
        String sql = this.getSelectSql(obj);
        Class cls = obj.getClass();
        return this.queryList(sql,cls);
    }

    /**
     * 插入一条数据并返回自动递增的id
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为新增参数
     * @return 插入成功返回自动递增的id，否则返回null
     */
    public Integer insertReturnId(Object obj){
        String sql = this.getInsertSql(obj);
        return this.insertReturnId(sql);
    }

    /**
     * 更新数据
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为更新参数，
     *            配置了@Id的属性将作为查询参数，不参与更新
     * @return 更新成功返回1，否则返回0
     */
    public int update(Object obj){
        String sql = this.getUpdateSql(obj);
        return this.execute(sql);
    }

    /**
     * 新增数据
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为新增参数
     * @return 新增成功返回1，否则返回0
     */
    public int insert(Object obj){
        String sql = this.getInsertSql(obj);
        return this.execute(sql);
    }

    /**
     * 删除数据
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为查询参数
     * @return 删除成功返回1，否则返回0
     */
    public int delete(Object obj){
        String sql = this.getDeleteSql(obj);
        return this.execute(sql);
    }


    /**
     * 给定一个与表格映射的实体类实例，根据该实例自动生成查询语句
     * @param obj 与数据库表格映射的实体类实例
     * @return 查询sql语句
     */
    public String getSelectSql(Object obj){
        String sql = null;
        try{
            if(obj.getClass().getAnnotation(Table.class) == null){
                throw new Exception(NO_TABLE_MESSAGE);
            }
            String tableName = obj.getClass().getAnnotation(Table.class).value();
            Field[] fields = getFields(obj.getClass());
            sql = "select * from "+tableName;
            List<String> wheres = new ArrayList<String>();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                Object value = field.get(obj);
                if(value == null){
                    continue;
                }
                Column column = field.getAnnotation(Column.class);
                String columnName = column==null ? field.getName() : column.value();
                if(NumberUtils.isNumber(value)){
                    wheres.add(columnName+" = "+value);
                }else{
                    wheres.add(columnName+" = '"+value+"'");
                }
            }
            if(wheres.size() > 0){
                sql += " where "+ ArrayUtils.join(wheres," and ");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return sql;
        }
    }

    /**
     * 给定一个与表格映射的实体类实例，根据该实例自动生成新增语句
     * @param obj 与数据库表格映射的实体类实例
     * @return 新增sql语句
     */
    public String getInsertSql(Object obj){
        String sql = null;
        try{
            if(obj.getClass().getAnnotation(Table.class) == null){
                throw new Exception(NO_TABLE_MESSAGE);
            }
            String tableName = obj.getClass().getAnnotation(Table.class).value();
            Field[] fields = getFields(obj.getClass());
            List<String> columns = new ArrayList<String>();
            List<String> values = new ArrayList<String>();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                if(value == null){
                    continue;
                }else{
                    Column column = field.getAnnotation(Column.class);
                    String columnName = column==null ? field.getName() : column.value();
                    columns.add(columnName);
                    if(NumberUtils.isNumber(value)) {
                        values.add(value.toString());
                    }else{
                        values.add("'"+value.toString()+"'");
                    }
                }
            }
            String colStr = "(" + ArrayUtils.join(columns,",") + ")";
            String valStr = "(" + ArrayUtils.join(values,",") + ")";
            sql = "insert into "+tableName+colStr+" values "+valStr;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return sql;
        }
    }

    /**
     * 给定一个与表格映射的实体类实例，根据该实例自动生成更新语句
     * @param obj 与数据库表格映射的实体类实例
     * @return 更新sql语句
     */
    public String getUpdateSql(Object obj){
        String sql = null;
        List<String> wheres = new ArrayList<String>();
        try{
            if(obj.getClass().getAnnotation(Table.class) == null){
                throw new Exception(NO_TABLE_MESSAGE);
            }
            String tableName = obj.getClass().getAnnotation(Table.class).value();
            Field[] fields = getFields(obj.getClass());
            List<String> values = new ArrayList<String>();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                if(value == null){
                    continue;
                }else{
                    Column column = field.getAnnotation(Column.class);
                    String columnName = column==null ? field.getName() : column.value();
                    Id id = field.getAnnotation(Id.class);
                    String val = value.toString();
                    if(id == null){
                        if(NumberUtils.isNumber(value)) {
                            values.add(columnName+"="+val);
                        }else{
                            values.add(columnName+"='"+val+"'");
                        }
                    }else{
                        if(NumberUtils.isNumber(value)) {
                            wheres.add(columnName+"="+val);
                        }else{
                            wheres.add(columnName+"='"+val+"'");
                        }
                    }
                }
            }
            String valStr = ArrayUtils.join(values,",");
            sql = "update "+tableName+" set "+valStr;
            if(wheres.size() > 0){
                sql += " where "+ArrayUtils.join(wheres," and ");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return sql;
        }
    }

    /**
     * 给定一个与表格映射的实体类实例，根据该实例自动生成删除语句
     * @param obj 与数据库表格映射的实体类实例
     * @return 删除sql语句
     */
    public String getDeleteSql(Object obj){
        String sql = null;
        try{
            if(obj.getClass().getAnnotation(Table.class) == null){
                throw new Exception("");
            }
            String tableName = obj.getClass().getAnnotation(Table.class).value();
            Field[] fields = getFields(obj.getClass());
            sql = "delete from "+tableName;
            List<String> wheres = new ArrayList<String>();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                Object value = field.get(obj);
                if(value == null){
                    continue;
                }
                Column column = field.getAnnotation(Column.class);
                String columnName = column==null ? field.getName() : column.value();
                if(NumberUtils.isNumber(value)){
                    wheres.add(columnName+" = "+value);
                }else{
                    wheres.add(columnName+" = '"+value+"'");
                }
            }
            if(wheres.size() > 0){
                sql += " where "+ArrayUtils.join(wheres," and ");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return sql;
        }
    }

}
