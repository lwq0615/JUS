package jus.jdbc.mysql;

import java.util.ArrayList;
import java.util.List;

public class Page<E> {

    private Integer current;
    private Integer size;
    private Integer total;
    private List<E> data = new ArrayList<>();

    public Page(List data){
        this.data = data;
        Page pageInfo = PageLimit.getPageInfo(data);
        if(pageInfo != null){
            this.size = pageInfo.getSize();
            this.current = pageInfo.getCurrent();
            this.total = pageInfo.getTotal();
        }
    }


    public Page(Integer current, Integer size) {
        this.current = current;
        this.size = size;
    }

    public Page(Integer current, Integer size, Integer total, List data) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.data = data;
    }

    public Integer getCurrent() {
        return current;
    }

    public Integer getSize() {
        return size;
    }

    public Integer getTotal() {
        return total;
    }

    public List<E> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", size=" + size +
                ", total=" + total +
                ", data=" + data.toString() +
                '}';
    }
}
