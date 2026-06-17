package com.campus.medical.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 分页处理工具类
 */
public class PageUtils {

    /**
     * 开始分页
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return Page对象
     */
    public static <T> Page<T> startPage(int pageNum, int pageSize) {
        return new Page<>(pageNum, pageSize);
    }

    /**
     * 获取分页信息
     *
     * @param list 分页结果列表
     * @return 分页信息对象
     */
    public static PageInfo getPageInfo(List<?> list) {
        if (list instanceof com.baomidou.mybatisplus.extension.plugins.pagination.Page) {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<?> page = 
                (com.baomidou.mybatisplus.extension.plugins.pagination.Page<?>) list;
            
            PageInfo pageInfo = new PageInfo();
            pageInfo.setPageNum(page.getCurrent());
            pageInfo.setPageSize(page.getSize());
            pageInfo.setTotal(page.getTotal());
            pageInfo.setPages(page.getPages());
            pageInfo.setRecords(page.getRecords());
            
            return pageInfo;
        } else {
            PageInfo pageInfo = new PageInfo();
            pageInfo.setPageNum(1);
            pageInfo.setPageSize(list.size());
            pageInfo.setTotal(list.size());
            pageInfo.setPages(1);
            pageInfo.setRecords(list);
            
            return pageInfo;
        }
    }

    /**
     * 检查分页参数合法性
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 如果参数合法返回true，否则返回false
     */
    public static boolean checkPageParam(int pageNum, int pageSize) {
        // 页码和每页大小必须大于0
        return pageNum > 0 && pageSize > 0 && pageSize <= 1000; // 限制每页最大数量防止恶意请求
    }

    /**
     * 分页信息内部类
     */
    public static class PageInfo {
        private long pageNum;      // 当前页码
        private long pageSize;     // 每页大小
        private long total;        // 总记录数
        private long pages;        // 总页数
        private List<?> records;   // 当前页记录

        public long getPageNum() {
            return pageNum;
        }

        public void setPageNum(long pageNum) {
            this.pageNum = pageNum;
        }

        public long getPageSize() {
            return pageSize;
        }

        public void setPageSize(long pageSize) {
            this.pageSize = pageSize;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public long getPages() {
            return pages;
        }

        public void setPages(long pages) {
            this.pages = pages;
        }

        public List<?> getRecords() {
            return records;
        }

        public void setRecords(List<?> records) {
            this.records = records;
        }
    }
}