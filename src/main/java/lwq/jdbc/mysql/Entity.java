package lwq.jdbc.mysql;

import lwq.jdbc.annotation.Column;
import lwq.jdbc.annotation.Id;
import lwq.jdbc.annotation.Table;
import lwq.jdbc.utils.ArrayUtils;
import lwq.jdbc.utils.ClassUtils;
import lwq.jdbc.utils.NumberUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Entity {



    private final String NO_TABLE_MESSAGE = "Before using entity as parameters, " +
            "you need to configure @Table(tableName) annotation of the entity.";


    /**
     * 给定一个与表格映射的实体类实例，根据该实例自动生成查询语句
     * @return 查询sql语句
     */
    public String getSelectSql(){
        String sql = null;
        try{
            if(this.getClass().getAnnotation(Table.class) == null){
                throw new Exception(NO_TABLE_MESSAGE);
            }
            String tableName = this.getClass().getAnnotation(Table.class).value();
            Field[] fields = ClassUtils.getFieldsToClass(this.getClass(),Entity.class);
            sql = "select * from "+tableName;
            List<String> wheres = new ArrayList<String>();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                Object value = field.get(this);
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
     * @return 新增sql语句
     */
    public String getInsertSql(){
        String sql = null;
        try{
            if(this.getClass().getAnnotation(Table.class) == null){
                throw new Exception(NO_TABLE_MESSAGE);
            }
            String tableName = this.getClass().getAnnotation(Table.class).value();
            Field[] fields = ClassUtils.getFieldsToClass(this.getClass(),Entity.class);
            List<String> columns = new ArrayList<String>();
            List<String> values = new ArrayList<String>();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(this);
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
     * @return 更新sql语句
     */
    public String getUpdateSql(){
        String sql = null;
        List<String> wheres = new ArrayList<String>();
        try{
            if(this.getClass().getAnnotation(Table.class) == null){
                throw new Exception(NO_TABLE_MESSAGE);
            }
            String tableName = this.getClass().getAnnotation(Table.class).value();
            Field[] fields = ClassUtils.getFieldsToClass(this.getClass(),Entity.class);
            List<String> values = new ArrayList<String>();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(this);
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
     * @return 删除sql语句
     */
    public String getDeleteSql(){
        String sql = null;
        try{
            if(this.getClass().getAnnotation(Table.class) == null){
                throw new Exception("");
            }
            String tableName = this.getClass().getAnnotation(Table.class).value();
            Field[] fields = ClassUtils.getFieldsToClass(this.getClass(),Entity.class);
            sql = "delete from "+tableName;
            List<String> wheres = new ArrayList<String>();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                Object value = field.get(this);
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
