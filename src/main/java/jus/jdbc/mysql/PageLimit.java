package jus.jdbc.mysql;

import java.util.HashMap;
import java.util.List;

public class PageLimit {

    private static ThreadLocal<Page> limit = new ThreadLocal<>();

    private static ThreadLocal<HashMap<List, Page>> pageInfo = new ThreadLocal<>();

    /**
     * 设置分页查询参数
     * @param current 当前页码
     * @param size 每页条数
     */
    public static void setLimit(int current, int size) {
        if(current < 1){
            current = 1;
        }
        if(size < 1){
            size = 1;
        }
        limit.set(new Page(current, size));
    }

    public static Page getPage(){
        Page page = limit.get();
        limit.remove();
        return page;
    }


    public static void setPageInfo(List data, int current, int size, int total){
        if(pageInfo.get() == null){
            pageInfo.set(new HashMap<>());
        }
        pageInfo.get().put(data, new Page(current, size, total, data));
    }


    public static Page getPageInfo(List data){
        if(pageInfo.get() == null){
            return null;
        }
        return pageInfo.get().get(data);
    }

}
