package com.jus.jdbc.mysql;

import com.jus.jdbc.annotation.*;
import com.jus.jdbc.mysql.exception.NoTableException;
import com.jus.jdbc.mysql.exception.NoValueException;
import com.jus.utils.ArrayUtils;
import com.jus.utils.ClassUtils;
import com.jus.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * 通过实体类获取查询sql和参数列表
 */
public class Entity {


    /**
     * 获取预编译对象的参数
     * @param entity   携带参数的实体类对象
     * @param required 是否判断非空属性
     */
    public static List<Object> getParams(Object entity, boolean required) {
        ArrayList<Object> params = new ArrayList();
        Field[] fields = ClassUtils.getFields(entity.getClass());
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            if (field.getAnnotation(Pass.class) != null) {
                continue;
            }
            Object value = Entity.getValueByGetter(entity, field);
            if (required && value == null && field.getAnnotation(Required.class) != null) {
                throw new NoValueException(entity.getClass().getName(), field.getName());
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
        ArrayList<Object> params = new ArrayList<>();
        ArrayList<Object> wheres = new ArrayList<>();
        Field[] fields = ClassUtils.getFields(entity.getClass());
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            if (field.getAnnotation(Pass.class) != null) {
                continue;
            }
            Object value = Entity.getValueByGetter(entity, field);
            if (value == null && field.getAnnotation(Required.class) != null) {
                throw new NoValueException(entity.getClass().getName(), field.getName());
            }
            if (value != null) {
                if (field.getAnnotation(Id.class) != null) wheres.add(value);
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
    public static String selectSql(Object entity) throws NoTableException {
        StringBuilder sql = new StringBuilder("select * from ");
        if (entity.getClass().getAnnotation(Table.class) == null) {
            throw new NoTableException();
        }
        String tableName = entity.getClass().getAnnotation(Table.class).value();
        Field[] fields = ClassUtils.getFields(entity.getClass());
        sql.append(tableName);
        List<String> wheres = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getAnnotation(Pass.class) != null) {
                continue;
            }
            field.setAccessible(true);
            if (Entity.getValueByGetter(entity, field) == null) {
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            String columnName = column == null ? field.getName() : column.value();
            wheres.add(columnName + " = ?");
        }
        if (wheres.size() > 0) {
            sql.append(" where ").append(ArrayUtils.join(wheres, " and "));
        }
        return sql.toString();
    }

    /**
     * 给定一个与表格映射的实体类实例，根据该实例自动生成新增语句
     * @return 新增sql语句
     */
    public static String insertSql(Object entity) throws NoTableException {
        StringBuilder sql = new StringBuilder("insert into ");
        if (entity.getClass().getAnnotation(Table.class) == null) {
            throw new NoTableException();
        }
        String tableName = entity.getClass().getAnnotation(Table.class).value();
        Field[] fields = ClassUtils.getFields(entity.getClass());
        List<String> columns = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        for (Field field : fields) {
            if (field.getAnnotation(Pass.class) != null) {
                continue;
            }
            field.setAccessible(true);
            if (Entity.getValueByGetter(entity, field) == null) {
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
        sql.append(tableName).append(colStr).append(" values ").append(valStr);
        return sql.toString();
    }

    /**
     * 给定一个与表格映射的实体类实例，根据该实例自动生成更新语句
     * @return 更新sql语句
     */
    public static String updateSql(Object entity) throws NoTableException {
        StringBuilder sql = new StringBuilder("update ");
        List<String> wheres = new ArrayList<String>();
        if (entity.getClass().getAnnotation(Table.class) == null) {
            throw new NoTableException();
        }
        String tableName = entity.getClass().getAnnotation(Table.class).value();
        Field[] fields = ClassUtils.getFields(entity.getClass());
        List<String> values = new ArrayList<String>();
        for (Field field : fields) {
            if (field.getAnnotation(Pass.class) != null) {
                continue;
            }
            field.setAccessible(true);
            if (Entity.getValueByGetter(entity, field) == null) {
                continue;
            } else {
                Column column = field.getAnnotation(Column.class);
                String columnName = column == null ? field.getName() : column.value();
                Id id = field.getAnnotation(Id.class);
                if (id == null) {
                    values.add(columnName + " = ?");
                } else {
                    wheres.add(columnName + " = ?");
                }
            }
        }
        String valStr = ArrayUtils.join(values, ", ");
        sql.append(tableName).append(" set ").append(valStr);
        if (wheres.size() > 0) {
            sql.append(" where ").append(ArrayUtils.join(wheres, " and "));
        }
        return sql.toString();
    }

    /**
     * 给定一个与表格映射的实体类实例，根据该实例自动生成删除语句
     * @return 删除sql语句
     */
    public static String deleteSql(Object entity) throws NoTableException {
        StringBuilder sql = new StringBuilder("delete from ");
        if (entity.getClass().getAnnotation(Table.class) == null) {
            throw new NoTableException();
        }
        String tableName = entity.getClass().getAnnotation(Table.class).value();
        Field[] fields = ClassUtils.getFields(entity.getClass());
        sql.append(tableName);
        List<String> wheres = new ArrayList<String>();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getAnnotation(Pass.class) != null) {
                continue;
            }
            field.setAccessible(true);
            if (Entity.getValueByGetter(entity, field) == null) {
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            String columnName = column == null ? field.getName() : column.value();
            wheres.add(columnName + " = ?");
        }
        if (wheres.size() > 0) {
            sql.append(" where ").append(ArrayUtils.join(wheres, " and "));
        }
        return sql.toString();
    }


    /**
     * 通过get方法获取值
     * 没有对应get方法时直接获取值
     * @param entity 对象实例
     * @param field  要获取值的属性
     * @return 通过get方法获取的属性的值
     */
    public static Object getValueByGetter(Object entity, Field field) {
        Object value = null;
        try {
            Method method = entity.getClass().getMethod("get" + StringUtils.firstUp(field.getName()));
            value = method.invoke(entity);
        } catch (NoSuchMethodException e) {
            value = field.get(entity);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            return value;
        }
    }


}
