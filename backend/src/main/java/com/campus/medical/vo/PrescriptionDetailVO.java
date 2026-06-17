package com.campus.medical.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 处方详情VO（含患者、医生、药品明细）
 */
@Data
public class PrescriptionDetailVO {

    private Long id;
    private String prescriptionNo;
    private Long recordId;
    private Long userId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String diagnosis;
    private Integer medicineCount;
    private BigDecimal totalAmount;
    private String createTime;
    private String status;
    private List<MedicineItemVO> medicines;

    @Data
    public static class MedicineItemVO {
        private Long id;
        private String name;
        private String spec;
        private Integer quantity;
        private BigDecimal price;
        private String usage;
        private Boolean dispensed;
    }
}
