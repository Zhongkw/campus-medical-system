package com.campus.medical.vo;

import lombok.Data;

/**
 * 病假统计汇总
 */
@Data
public class SickLeaveSummaryVO {

    private Long total;

    private Long approved;

    private Long rejected;

    private Long pending;
}
