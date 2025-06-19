package org.nginx.auth.dto.vo;

import java.util.Collection;
import java.util.List;

/**
 * @author dongpo.li
 * @date 2024/12/30 16:15
 */
public class BasicPaginationVO<T> {

    private Collection<T> data;
    private long page;
    private long size;
    private long pages;
    private long total;
    private long prev;
    private long next;

    public int pageNumStart() {
        return page > 4 ? (int) (page - 3) : 1;
    }

    public int pageNumEnd() {
        return page + 3 < pages ? (int) (page + 3) : (int) pages;
    }

    public Collection<T> getData() {
        return data;
    }

    public void setData(Collection<T> data) {
        this.data = data;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPrev() {
        return prev;
    }

    public void setPrev(long prev) {
        this.prev = prev;
    }

    public long getNext() {
        return next;
    }

    public void setNext(long next) {
        this.next = next;
    }
}
