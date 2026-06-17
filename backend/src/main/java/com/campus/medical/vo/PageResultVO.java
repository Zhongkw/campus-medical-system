package com.campus.medical.vo;

import lombok.Data;
import java.util.List;

/**
 * 通用分页结果封装
 */
@Data
public class PageResultVO<T> {
    /**
     * 数据列表
     */
    private List<T> records;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 总页数
     */
    private Long pages;
    
    /**
     * 当前页码
     */
    private Long pageNum;
    
    /**
     * 每页记录数
     */
    private Long pageSize;
    
    public PageResultVO() {}
    
    public PageResultVO(List<T> records, Long total, Long pages, Long pageNum, Long pageSize) {
        this.records = records;
        this.total = total;
        this.pages = pages;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
