package com.campus.medical.dto;

import lombok.Data;

/**
 * 订单查询条件
 */
@Data
public class OrderQueryDTO {
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 学生姓名/学号关键字
     */
    private String studentKey;
    
    /**
     * 订单状态（0-待支付 1-已支付 2-已取消 3-已退款）
     */
    private Integer orderStatus;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 最小超期天数（未缴费催缴）
     */
    private Integer minOverdueDays;
    
    /**
     * 页码
     */
    private Long pageNum = 1L;
    
    /**
     * 每页记录数
     */
    private Long pageSize = 10L;
}
