package lwq.jdbc.mysql;

import java.util.List;

public class Page {

    private Integer current;
    private Integer size;
    private Integer total;
    private List data;

    public Page(Integer current, Integer size) {
        this.current = current;
        this.size = size;
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

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", size=" + size +
                ", total=" + total +
                ", data=" + data +
                '}';
    }
}
