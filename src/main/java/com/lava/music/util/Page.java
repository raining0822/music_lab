package com.lava.music.util;

import java.util.List;

/**
 * Created by mac on 2017/8/23.
 */
public class Page<T> {

    private Integer pageNo;
    private Integer pageSize;
    private Integer totalCount;
    private Integer totalPageCount;
    private Integer next;
    private Integer pre;
    private List<T> list;


    private Boolean hasNext;
    private Boolean hasPre;
    private Integer start;
    private Integer end;



    public Page(Integer pageNo, Integer pageSize, Integer totalCount) {
        this.totalPageCount = (totalCount % pageSize == 0) ? totalCount / pageSize : totalCount / pageSize + 1;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
    }

    public Integer getPageNo() {
        if(this.pageNo > this.totalPageCount)  this.pageNo = totalPageCount;
        if(this.pageNo < 1) this.pageNo = 1;
        return pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public Integer getTotalPageCount() {
        if(totalPageCount < 1) totalPageCount = 1;
        return totalPageCount;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Integer getNext() {
        this.next = this.pageNo + 1;
        if(next > totalPageCount) return totalPageCount;
        return next;
    }

    public Integer getPre() {
        this.pre = this.pageNo - 1;
        if(this.pre < 1) return 1;
        return pre;
    }

    public Boolean getHasNext() {
        if(this.pageNo >= totalPageCount)return false;
        return true;
    }

    public Boolean getHasPre() {
        if(this.pageNo <= 1)return false;
        return true;
    }

    public Integer getStart() {
        if(this.pageNo <= 5){
            this.start = 1;
        }else{
            this.start = this.pageNo - 5;
        }
        return start;
    }

    public Integer getEnd() {
        if(totalPageCount < 1) totalPageCount = 1;
        end = this.pageNo + 5;
        if(end > totalPageCount)end = totalPageCount;
        return end;
    }
}
