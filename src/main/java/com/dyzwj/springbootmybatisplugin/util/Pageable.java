package com.dyzwj.springbootmybatisplugin.util;

import java.util.List;

/**
 * @author 作者 : ZhengWenjie
 * @version 创建时间：2020/12/1 15:53
 * 类说明
 */
public class Pageable {

    private int page = 0;

    private int size = 10;

    private int total;

    private int totalPage;

    private List<?> contene;


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<?> getContene() {
        return contene;
    }

    public void setContene(List<?> contene) {
        this.contene = contene;
    }
}
