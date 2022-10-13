package jus.jdbc.mysql;

import jus.jdbc.annotation.Column;
import jus.jdbc.annotation.Id;
import jus.jdbc.annotation.Pass;
import jus.jdbc.annotation.Table;
import jus.utils.ArrayUtils;
import jus.utils.ClassUtils;
import jus.utils.NumberUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 通过实体类获取查询sql和参数列表
 */
public class Entity {


    private final static String NO_TABLE_MESSAGE = "Before using entity as parameters, " +
            "you need to configure @Table(tableName) annotation of the entity.";


    /**
     * 获取预编译对象的参数
     */
    public static List<Object> getParams(Object entity) {
        ArrayList<Object> params = new ArrayList();
        Field[] fields = ClassUtils.getFields(entity.getClass());
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if(field.getAnnotation(Pass.class) != null){
                continue;
            }
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(entity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (value != null) {
                params.add(value);
            }
        }
        return params;
    }


    /**
     * 更新语句的预编译参数由于set与where的位置关系，需要将@id放在参数列表最后
     */
    public static List<Object> getUpdateParams(Object entity) {
        ArrayList<Object> params = new ArrayList();
        ArrayList<Object> wheres = new ArrayList();
        Field[] fields = ClassUtils.getFields(entity.getClass());
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if(field.getAnnotation(Pass.class) != null){
                continue;
            }
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(entity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (value != null) {
                if(field.getAnnotation(Id.class) != null) wheres.add(value);
                else params.add(value);
            }
        }
        params.addAll(wheres);
        return params;
    }



    /**
     * 给定一个与表格映射的实体类实例，根据该实例自动生成查询语句
     * @return 查询sql语句
     */
    public static String selectSql(Object entity) {
        String sql = null;
        try {
            if (entity.getClass().getAnnotation(Table.class) == null) {
                throw new Exception(NO_TABLE_MESSAGE);
            }
            String tableName = entity.getClass().getAnnotation(Table.class).value();
            Field[] fields = ClassUtils.getFields(entity.getClass());
            sql = "select * from " + tableName;
            List<String> wheres = new ArrayList<String>();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if(field.getAnnotation(Pass.class) != null){
                    continue;
                }
                field.setAccessible(true);
                if(field.get(entity) == null){
                    continue;
                }
                Column column = field.getAnnotation(Column.class);
                String columnName = column == null ? field.getName() : column.value();
                wheres.add(columnName + " = ?");
            }
            if (wheres.size() > 0) {
                sql += " where " + ArrayUtils.join(wheres, " and ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return sql;
        }
    }

    /**
     * 给定一个与表格映射的实体类实例，根据该实例自动生成新增语句
     * @return 新增sql语句
     */
    public static String insertSql(Object entity) {
        String sql = null;
        try {
            if (entity.getClass().getAnnotation(Table.class) == null) {
                throw new Exception(NO_TABLE_MESSAGE);
            }
            String tableName = entity.getClass().getAnnotation(Table.class).value();
            Field[] fields = ClassUtils.getFields(entity.getClass());
            List<String> columns = new ArrayList<String>();
            List<String> values = new ArrayList<String>();
            for (Field field : fields) {
                if(field.getAnnotation(Pass.class) != null){
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value == null) {
                    continue;
                } else {
                    Column column = field.getAnnotation(Column.class);
                    String columnName = column == null ? field.getName() : column.value();
                    columns.add(columnName);
                    values.add("?");

                }
            }
            String colStr = "(" + ArrayUtils.join(columns, ", ") + ")";
            String valStr = "(" + ArrayUtils.join(values, ", ") + ")";
            sql = "insert into " + tableName + colStr + " values " + valStr;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return sql;
        }
    }

    /**
     * 给定一个与表格映射的实体类实例，根据该实例自动生成更新语句
     * @return 更新sql语句
     */
    public static String updateSql(Object entity) {
        String sql = null;
        List<String> wheres = new ArrayList<String>();
        try {
            if (entity.getClass().getAnnotation(Table.class) == null) {
                throw new Exception(NO_TABLE_MESSAGE);
            }
            String tableName = entity.getClass().getAnnotation(Table.class).value();
            Field[] fields = ClassUtils.getFields(entity.getClass());
            List<String> values = new ArrayList<String>();
            for (Field field : fields) {
                if(field.getAnnotation(Pass.class) != null){
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value == null) {
                    continue;
                } else {
                    Column column = field.getAnnotation(Column.class);
                    String columnName = column == null ? field.getName() : column.value();
                    Id id = field.getAnnotation(Id.class);
                    String val = value.toString();
                    if (id == null) {
                        values.add(columnName + " = ?");
                    } else {
                        wheres.add(columnName + " = ?");
                    }
                }
            }
            String valStr = ArrayUtils.join(values, ", ");
            sql = "update " + tableName + " set " + valStr;
            if (wheres.size() > 0) {
                sql += " where " + ArrayUtils.join(wheres, " and ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return sql;
        }
    }

    /**
     * 给定一个与表格映射的实体类实例，根据该实例自动生成删除语句
     * @return 删除sql语句
     */
    public static String deleteSql(Object entity) {
        String sql = null;
        try {
            if (entity.getClass().getAnnotation(Table.class) == null) {
                throw new Exception("");
            }
            String tableName = entity.getClass().getAnnotation(Table.class).value();
            Field[] fields = ClassUtils.getFields(entity.getClass());
            sql = "delete from " + tableName;
            List<String> wheres = new ArrayList<String>();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if(field.getAnnotation(Pass.class) != null){
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value == null) {
                    continue;
                }
                Column column = field.getAnnotation(Column.class);
                String columnName = column == null ? field.getName() : column.value();
                wheres.add(columnName + " = ?");
            }
            if (wheres.size() > 0) {
                sql += " where " + ArrayUtils.join(wheres, " and ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return sql;
        }
    }
}
