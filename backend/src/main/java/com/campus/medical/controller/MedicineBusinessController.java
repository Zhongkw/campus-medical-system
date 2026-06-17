package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.dto.InstockRequestDTO;
import com.campus.medical.dto.MedicineUpdateDTO;
import com.campus.medical.entity.MedicalMedicine;
import com.campus.medical.service.MedicineBusinessService;
import com.campus.medical.vo.InstockListVO;
import com.campus.medical.vo.InstockResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 药品管理业务控制器
 */
@RestController
@RequestMapping("/api/medical/medicine-business")
public class MedicineBusinessController {

    @Autowired
    private MedicineBusinessService medicineBusinessService;

    /**
     * 药品入库（核心接口）
     */
    @PostMapping("/instock")
    public Result<InstockResultVO> instockMedicine(@RequestBody InstockRequestDTO requestDTO) {
        InstockResultVO result = medicineBusinessService.instockMedicine(requestDTO);
        return ResultUtils.success("入库成功", result);
    }

    /**
     * 完成入库
     */
    @PostMapping("/instock/complete/{instockId}")
    public Result<Void> completeInstock(
            @PathVariable Long instockId,
            @RequestParam Long operatorId) {
        medicineBusinessService.completeInstock(instockId, operatorId);
        return ResultUtils.success("入库已完成", null);
    }

    /**
     * 取消入库（回退库存）
     */
    @PostMapping("/instock/cancel/{instockId}")
    public Result<Void> cancelInstock(
            @PathVariable Long instockId,
            @RequestParam Long operatorId) {
        medicineBusinessService.cancelInstock(instockId, operatorId);
        return ResultUtils.success("入库已取消，库存已回退", null);
    }

    /**
     * 更新药品库存
     */
    @PostMapping("/stock/update")
    public Result<Void> updateMedicineStock(
            @RequestParam Long medicineId,
            @RequestParam Integer quantity,
            @RequestParam Long operatorId) {
        medicineBusinessService.updateMedicineStock(medicineId, quantity, operatorId);
        return ResultUtils.success("库存更新成功", null);
    }

    /**
     * 更新药品库存信息
     */
    @PutMapping("/medicine/update")
    public Result<Void> updateMedicineInfo(@RequestBody MedicineUpdateDTO requestDTO) {
        medicineBusinessService.updateMedicineInfo(requestDTO);
        return ResultUtils.success("药品信息更新成功", null);
    }

    /**
     * 查询入库单列表
     */
    @GetMapping("/instock/list")
    public Result<List<InstockListVO>> getInstockList(
            @RequestParam(required = false) Long operatorId) {
        List<InstockListVO> list = medicineBusinessService.getInstockList(operatorId);
        return ResultUtils.success(list);
    }

    /**
     * 查询药品列表（支持分类筛选）
     */
    @GetMapping("/medicine/list")
    public Result<List<MedicalMedicine>> getMedicineList(
            @RequestParam(required = false) String category) {
        List<MedicalMedicine> list = medicineBusinessService.getMedicineList(category);
        return ResultUtils.success(list);
    }

    /**
     * 查询库存预警药品列表
     */
    @GetMapping("/medicine/low-stock")
    public Result<List<MedicalMedicine>> getLowStockMedicines() {
        List<MedicalMedicine> list = medicineBusinessService.getLowStockMedicines();
        return ResultUtils.success(list);
    }

    /**
     * 根据药品名称模糊查询
     */
    @GetMapping("/medicine/search")
    public Result<List<MedicalMedicine>> searchMedicines(
            @RequestParam String medicineName) {
        List<MedicalMedicine> list = medicineBusinessService.searchMedicines(medicineName);
        return ResultUtils.success(list);
    }
}
