package com.campus.medical.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 入库单列表VO（含首条明细）
 */
@Data
public class InstockListVO {

    private Long id;
    private String instockNo;
    private Long operatorId;
    private BigDecimal totalAmount;
    private String remark;
    private LocalDateTime createTime;

    private String medicineName;
    private String spec;
    private Integer quantity;
    private BigDecimal price;
    private String batchNo;
    private LocalDate expireDate;
}
