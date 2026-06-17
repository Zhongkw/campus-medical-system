package com.campus.medical.service;

import com.campus.medical.dto.InstockRequestDTO;
import com.campus.medical.dto.MedicineUpdateDTO;
import com.campus.medical.entity.MedicalMedicine;
import com.campus.medical.vo.InstockListVO;
import com.campus.medical.vo.InstockResultVO;

import java.util.List;

/**
 * 药品管理业务服务接口
 */
public interface MedicineBusinessService {

    /**
     * 药品入库（核心业务方法）
     * @param requestDTO 入库请求
     * @return 入库结果
     */
    InstockResultVO instockMedicine(InstockRequestDTO requestDTO);

    /**
     * 完成入库（状态变更）
     * @param instockId 入库单ID
     * @param operatorId 操作人ID
     */
    void completeInstock(Long instockId, Long operatorId);

    /**
     * 取消入库
     * @param instockId 入库单ID
     * @param operatorId 操作人ID
     */
    void cancelInstock(Long instockId, Long operatorId);

    /**
     * 更新药品库存
     * @param medicineId 药品ID
     * @param quantity 数量（正数增加，负数减少）
     * @param operatorId 操作人ID
     */
    void updateMedicineStock(Long medicineId, Integer quantity, Long operatorId);

    /**
     * 更新药品库存信息（当前库存、最低库存、有效期）
     */
    void updateMedicineInfo(MedicineUpdateDTO requestDTO);

    /**
     * 查询入库单列表
     * @param operatorId 操作人ID（可选）
     * @return 入库单列表
     */
    List<InstockListVO> getInstockList(Long operatorId);

    /**
     * 查询药品列表（支持分类筛选）
     * @param category 药品分类（可选）
     * @return 药品列表
     */
    List<MedicalMedicine> getMedicineList(String category);

    /**
     * 查询库存预警药品列表
     * @return 库存低于最小库存的药品
     */
    List<MedicalMedicine> getLowStockMedicines();

    /**
     * 根据药品名称模糊查询
     * @param medicineName 药品名称
     * @return 药品列表
     */
    List<MedicalMedicine> searchMedicines(String medicineName);
}
