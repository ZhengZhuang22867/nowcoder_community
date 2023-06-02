package com.nowcoder.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 封装分页相关的信息
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Page {
    // 当前的页码
    private int current = 1;
    // 最多显示的上限
    private int limit = 10;
    // 数据总数（用于计算总页数）
    private int rows;
    // 查询路径（用于复用分页链接）
    private String path;

    public int getCurrent() {
        return current;
    }

    public int getLimit() {
        return limit;
    }

    public int getRows() {
        return rows;
    }

    public String getPath() {
        return path;
    }

    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100){
            this.limit = limit;
        }
    }

    public void setRows(int rows) {
        if(rows >= 0){
            this.rows = rows;
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行（获取的是当前页下显示的第一行数据是数据库中的第几行）
     * @return
     */
    public int getOffset(){
        return (current-1) * limit;
    }

    /**
     * 获取总的页数（用于在页面下方的跳转栏中显示最大页数）
     * @return
     */
    public int getTotal(){
        int ans = 0;
        if(rows % limit != 0){
            ans = rows / limit + 1;
        }else{
            ans = rows / limit;
        }
        return ans;
    }

    /**
     * 当页数很多的时候，跳转栏会隐藏部分页数
     * 获取不隐藏页面的起始页码
     * @return
     */
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 当页数很多的时候，跳转会隐藏中间的页数
     * 获取不隐藏页面的终止页码
     * @return
     */
    public int getTo(){
        int to = current + 2;
        return to > getTotal() ? getTotal() : to;
    }


}
