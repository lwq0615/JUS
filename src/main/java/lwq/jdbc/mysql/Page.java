package lwq.jdbc.mysql;

import java.util.ArrayList;

public class Page<E> extends ArrayList<E> {

    private Integer current;
    private Integer size;
    private Integer total;

    public Page(Integer current, Integer size, Integer total) {
        this.current = current;
        this.size = size;
        this.total = total;
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

    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", size=" + size +
                ", total=" + total +
                ", data=" + super.toString() +
                '}';
    }
}
