package com.campus.medical.vo;

import lombok.Data;

/**
 * 学院病假统计VO
 */
@Data
public class CollegeSickStatVO {

    /**
     * 学院名称
     */
    private String college;

    /**
     * 病假申请数量
     */
    private Long count;

    /**
     * 病假申请数量（与count字段同义）
     */
    private Long applyCount;

    /**
     * 占比（百分比）
     */
    private Double percentage;

    /**
     * 已通过数量
     */
    private Long approvedCount;

    /**
     * 已驳回数量
     */
    private Long rejectedCount;

    /**
     * 待审核数量
     */
    private Long pendingCount;

    public void setApplyCount(Long applyCount) {
        this.applyCount = applyCount;
    }

    public Long getApplyCount() {
        return this.applyCount != null ? this.applyCount : this.count;
    }

    public void setApplyCount(int applyCount) {
        this.applyCount = (long) applyCount;
    }

    public int getApplyCountAsInt() {
        return this.applyCount != null ? this.applyCount.intValue() : (this.count != null ? this.count.intValue() : 0);
    }
}
