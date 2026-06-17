package com.campus.medical.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 预警历史
 */
@Data
public class WarnHistoryVO {
    /**
     * ID
     */
    private Long id;
    
    /**
     * 疾病名称
     */
    private String diseaseName;
    
    /**
     * 预警时间
     */
    private LocalDateTime warnTime;
    
    /**
     * 状态（0-未处理 1-已处理）
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 处理备注
     */
    private String handleRemark;
}
