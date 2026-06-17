package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.dto.InstockRequestDTO;
import com.campus.medical.dto.MedicineUpdateDTO;
import com.campus.medical.entity.MedicalInstock;
import com.campus.medical.entity.MedicalInstockItem;
import com.campus.medical.entity.MedicalMedicine;
import com.campus.medical.entity.SysDictData;
import com.campus.medical.mapper.MedicalInstockItemMapper;
import com.campus.medical.mapper.MedicalInstockMapper;
import com.campus.medical.mapper.MedicalMedicineMapper;
import com.campus.medical.mapper.SysDictDataMapper;
import com.campus.medical.service.MedicineBusinessService;
import com.campus.medical.vo.InstockListVO;
import com.campus.medical.vo.InstockResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 药品管理业务服务实现类
 */
@Slf4j
@Service
public class MedicineBusinessServiceImpl implements MedicineBusinessService {

    @Autowired
    private MedicalInstockMapper instockMapper;

    @Autowired
    private MedicalInstockItemMapper instockItemMapper;

    @Autowired
    private MedicalMedicineMapper medicineMapper;

    @Autowired
    private SysDictDataMapper dictDataMapper;

    private static final String DICT_DRUG_CATEGORY = "drug_category";

    /**
     * 药品入库（核心业务方法 - 完整流程）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InstockResultVO instockMedicine(InstockRequestDTO requestDTO) {
        log.info("开始药品入库：operatorId={}", requestDTO.getOperatorId());

        // 1. 验证明细
        if (requestDTO.getItems() == null || requestDTO.getItems().isEmpty()) {
            throw new RuntimeException("入库明细不能为空");
        }

        // 2. 创建入库单
        MedicalInstock instock = new MedicalInstock();
        instock.setInstockNo(generateInstockNo());
        instock.setOperatorId(requestDTO.getOperatorId());
        instock.setRemark(requestDTO.getRemark());
        instock.setTotalAmount(BigDecimal.ZERO);
        instockMapper.insert(instock);

        // 3. 创建入库明细并计算总金额、增加库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<InstockResultVO.InstockItemVO> itemVOs = new ArrayList<>();

        for (InstockRequestDTO.InstockItemDTO itemDTO : requestDTO.getItems()) {
            // 3.1 验证药品是否存在
            MedicalMedicine medicine = medicineMapper.selectById(itemDTO.getMedicineId());
            if (medicine == null) {
                throw new RuntimeException("药品不存在：" + itemDTO.getMedicineName());
            }

            // 3.2 创建入库明细
            MedicalInstockItem item = new MedicalInstockItem();
            item.setInstockId(instock.getId());
            item.setMedicineId(itemDTO.getMedicineId());
            item.setMedicineName(itemDTO.getMedicineName() != null ? itemDTO.getMedicineName() : medicine.getMedicineName());
            item.setSpec(itemDTO.getSpec() != null ? itemDTO.getSpec() : (medicine.getSpec() != null ? medicine.getSpec() : "-"));
            item.setUnit(medicine.getUnit() != null ? medicine.getUnit() : "盒");
            item.setQuantity(itemDTO.getQuantity());
            item.setPrice(itemDTO.getPrice());
            item.setBatchNo(itemDTO.getBatchNo());
            item.setExpireDate(itemDTO.getExpireDate());
            item.setAmount(itemDTO.getPrice().multiply(new BigDecimal(itemDTO.getQuantity())));
            instockItemMapper.insert(item);

            // 3.3 计算总金额
            totalAmount = totalAmount.add(item.getAmount());

            // 3.4 增加药品库存
            medicine.setStock(medicine.getStock() + itemDTO.getQuantity());
            
            // 3.5 更新药品信息（如果有新数据）
            if (itemDTO.getBatchNo() != null) {
                medicine.setBatchNo(itemDTO.getBatchNo());
            }
            if (itemDTO.getExpireDate() != null) {
                medicine.setExpireDate(itemDTO.getExpireDate());
            }
            if (itemDTO.getSpec() != null) {
                medicine.setSpec(itemDTO.getSpec());
            }
            medicineMapper.updateById(medicine);

            // 3.6 构建返回VO
            InstockResultVO.InstockItemVO itemVO = new InstockResultVO.InstockItemVO();
            itemVO.setId(item.getId());
            itemVO.setMedicineName(itemDTO.getMedicineName());
            itemVO.setSpec(itemDTO.getSpec());
            itemVO.setQuantity(itemDTO.getQuantity());
            itemVO.setPrice(itemDTO.getPrice());
            itemVO.setSubtotal(item.getAmount());
            itemVOs.add(itemVO);

            log.info("药品入库成功：{}，数量：{}，当前库存：{}", 
                    itemDTO.getMedicineName(), itemDTO.getQuantity(), medicine.getStock());
        }

        // 4. 更新入库单总金额
        instock.setTotalAmount(totalAmount);
        instockMapper.updateById(instock);

        // 5. 构建返回结果
        InstockResultVO resultVO = new InstockResultVO();
        resultVO.setInstockId(instock.getId());
        resultVO.setInstockNo(instock.getInstockNo());
        resultVO.setTotalAmount(totalAmount);
        resultVO.setItems(itemVOs);
        resultVO.setMessage("入库成功");

        log.info("药品入库完成：{}", resultVO);
        return resultVO;
    }

    /**
     * 完成入库（状态变更）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeInstock(Long instockId, Long operatorId) {
        log.info("完成入库：instockId={}, operatorId={}", instockId, operatorId);

        MedicalInstock instock = instockMapper.selectById(instockId);
        if (instock == null) {
            throw new RuntimeException("入库单不存在");
        }

        // 验证操作人权限
        if (!instock.getOperatorId().equals(operatorId)) {
            throw new RuntimeException("无权操作该入库单");
        }

        // 更新状态（如果需要）
        // 注：MedicalInstock实体没有status字段，这里仅记录日志
        log.info("入库单已完成：{}", instockId);
    }

    /**
     * 取消入库（回退库存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelInstock(Long instockId, Long operatorId) {
        log.info("取消入库：instockId={}, operatorId={}", instockId, operatorId);

        MedicalInstock instock = instockMapper.selectById(instockId);
        if (instock == null) {
            throw new RuntimeException("入库单不存在");
        }

        // 验证操作人权限
        if (!instock.getOperatorId().equals(operatorId)) {
            throw new RuntimeException("无权操作该入库单");
        }

        // 查询入库明细并回退库存
        LambdaQueryWrapper<MedicalInstockItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalInstockItem::getInstockId, instockId);
        List<MedicalInstockItem> items = instockItemMapper.selectList(wrapper);

        for (MedicalInstockItem item : items) {
            MedicalMedicine medicine = medicineMapper.selectById(item.getMedicineId());
            if (medicine != null) {
                // 减少库存
                medicine.setStock(medicine.getStock() - item.getQuantity());
                medicineMapper.updateById(medicine);
                log.info("回退库存：{}，数量：{}，当前库存：{}", 
                        item.getMedicineName(), item.getQuantity(), medicine.getStock());
            }
        }

        log.info("入库单已取消，库存已回退：{}", instockId);
    }

    /**
     * 更新药品库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMedicineStock(Long medicineId, Integer quantity, Long operatorId) {
        log.info("更新药品库存：medicineId={}, quantity={}, operatorId={}", 
                medicineId, quantity, operatorId);

        MedicalMedicine medicine = medicineMapper.selectById(medicineId);
        if (medicine == null) {
            throw new RuntimeException("药品不存在");
        }

        // 如果是减少库存，验证库存是否充足
        if (quantity < 0 && medicine.getStock() < Math.abs(quantity)) {
            throw new RuntimeException("库存不足，当前库存：" + medicine.getStock());
        }

        // 更新库存
        medicine.setStock(medicine.getStock() + quantity);
        medicineMapper.updateById(medicine);

        log.info("库存更新成功：{}，新库存：{}", medicine.getMedicineName(), medicine.getStock());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMedicineInfo(MedicineUpdateDTO requestDTO) {
        if (requestDTO == null || requestDTO.getMedicineId() == null) {
            throw new RuntimeException("药品ID不能为空");
        }
        MedicalMedicine medicine = medicineMapper.selectById(requestDTO.getMedicineId());
        if (medicine == null) {
            throw new RuntimeException("药品不存在");
        }
        if (requestDTO.getStock() != null) {
            if (requestDTO.getStock() < 0) {
                throw new RuntimeException("库存不能为负数");
            }
            medicine.setStock(requestDTO.getStock());
        }
        if (requestDTO.getMinStock() != null) {
            if (requestDTO.getMinStock() < 0) {
                throw new RuntimeException("最低库存不能为负数");
            }
            medicine.setMinStock(requestDTO.getMinStock());
        }
        if (requestDTO.getExpireDate() != null && !requestDTO.getExpireDate().isEmpty()) {
            medicine.setExpireDate(java.time.LocalDate.parse(requestDTO.getExpireDate().substring(0, 10)));
        }
        medicine.setUpdateTime(java.time.LocalDateTime.now());
        medicineMapper.updateById(medicine);
    }

    /**
     * 查询入库单列表
     */
    @Override
    public List<InstockListVO> getInstockList(Long operatorId) {
        LambdaQueryWrapper<MedicalInstock> wrapper = new LambdaQueryWrapper<>();
        if (operatorId != null) {
            wrapper.eq(MedicalInstock::getOperatorId, operatorId);
        }
        wrapper.orderByDesc(MedicalInstock::getCreateTime);
        List<MedicalInstock> instocks = instockMapper.selectList(wrapper);

        List<InstockListVO> result = new ArrayList<>();
        for (MedicalInstock instock : instocks) {
            InstockListVO vo = new InstockListVO();
            vo.setId(instock.getId());
            vo.setInstockNo(instock.getInstockNo());
            vo.setOperatorId(instock.getOperatorId());
            vo.setTotalAmount(instock.getTotalAmount());
            vo.setRemark(instock.getRemark());
            vo.setCreateTime(instock.getCreateTime());

            LambdaQueryWrapper<MedicalInstockItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(MedicalInstockItem::getInstockId, instock.getId());
            itemWrapper.orderByAsc(MedicalInstockItem::getId);
            itemWrapper.last("LIMIT 1");
            MedicalInstockItem item = instockItemMapper.selectOne(itemWrapper);
            if (item != null) {
                vo.setMedicineName(item.getMedicineName());
                vo.setSpec(item.getSpec());
                vo.setQuantity(item.getQuantity());
                vo.setPrice(item.getPrice());
                vo.setBatchNo(item.getBatchNo());
                vo.setExpireDate(item.getExpireDate());
                if (vo.getBatchNo() == null && item.getMedicineId() != null) {
                    MedicalMedicine medicine = medicineMapper.selectById(item.getMedicineId());
                    if (medicine != null && medicine.getBatchNo() != null) {
                        vo.setBatchNo(medicine.getBatchNo());
                    }
                }
            } else {
                vo.setMedicineName(instock.getRemark());
            }
            result.add(vo);
        }
        return result;
    }

    /**
     * 查询药品列表（支持分类筛选）
     */
    @Override
    public List<MedicalMedicine> getMedicineList(String category) {
        LambdaQueryWrapper<MedicalMedicine> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            List<String> aliases = resolveCategoryAliases(category);
            wrapper.in(MedicalMedicine::getCategory, aliases);
        }
        wrapper.eq(MedicalMedicine::getStatus, 1);
        wrapper.orderByAsc(MedicalMedicine::getCategory, MedicalMedicine::getMedicineName);
        return medicineMapper.selectList(wrapper);
    }

    private List<String> resolveCategoryAliases(String categoryCode) {
        String categoryName = resolveCategoryName(categoryCode);
        List<String> aliases = new ArrayList<>();
        aliases.add(categoryCode);
        if (categoryName != null && !categoryName.isBlank()) {
            aliases.add(categoryName);
        }
        switch (categoryCode) {
            case "KSS" -> aliases.addAll(List.of("抗生素", "抗生素类"));
            case "GM" -> aliases.addAll(List.of("中成药", "感冒用药", "感冒类"));
            case "ZT" -> aliases.addAll(List.of("解热镇痛", "止痛药", "止痛类"));
            case "YW" -> aliases.addAll(List.of("外用药", "外用类"));
            case "AA" -> aliases.addAll(List.of("中药", "中药类"));
            default -> { }
        }
        return aliases.stream().distinct().collect(Collectors.toList());
    }

    private String resolveCategoryName(String categoryCode) {
        SysDictData dict = dictDataMapper.selectOne(new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, DICT_DRUG_CATEGORY)
                .eq(SysDictData::getDictValue, categoryCode)
                .last("LIMIT 1"));
        return dict != null ? dict.getDictLabel() : categoryCode;
    }

    /**
     * 查询库存预警药品列表
     */
    @Override
    public List<MedicalMedicine> getLowStockMedicines() {
        LambdaQueryWrapper<MedicalMedicine> wrapper = new LambdaQueryWrapper<>();
        // 查询库存小于等于最小库存阈值的药品
        wrapper.apply("stock <= min_stock");
        wrapper.eq(MedicalMedicine::getStatus, 1);
        wrapper.orderByAsc(MedicalMedicine::getStock);
        return medicineMapper.selectList(wrapper);
    }

    /**
     * 根据药品名称模糊查询
     */
    @Override
    public List<MedicalMedicine> searchMedicines(String medicineName) {
        LambdaQueryWrapper<MedicalMedicine> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(MedicalMedicine::getMedicineName, medicineName);
        wrapper.eq(MedicalMedicine::getStatus, 1);
        wrapper.orderByAsc(MedicalMedicine::getMedicineName);
        return medicineMapper.selectList(wrapper);
    }

    /**
     * 生成入库单号
     * 格式：RK + yyyyMMdd + 4位序号
     */
    private String generateInstockNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "RK" + dateStr;
        
        LambdaQueryWrapper<MedicalInstock> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(MedicalInstock::getInstockNo, prefix);
        wrapper.orderByDesc(MedicalInstock::getInstockNo);
        wrapper.last("LIMIT 1");
        MedicalInstock lastInstock = instockMapper.selectOne(wrapper);
        
        int sequence = 1;
        if (lastInstock != null) {
            String lastNo = lastInstock.getInstockNo();
            sequence = Integer.parseInt(lastNo.substring(lastNo.length() - 4)) + 1;
        }
        
        return prefix + String.format("%04d", sequence);
    }
}
