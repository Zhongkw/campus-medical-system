package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.dto.MedicalRecordRequestDTO;
import com.campus.medical.dto.PrescriptionRequestDTO;
import com.campus.medical.entity.*;
import com.campus.medical.mapper.*;
import com.campus.medical.service.MedicalBusinessService;
import com.campus.medical.vo.PatientRecordVO;
import com.campus.medical.vo.PrescriptionDetailVO;
import com.campus.medical.vo.PrescriptionResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 诊疗业务服务实现类
 */
@Slf4j
@Service
public class MedicalBusinessServiceImpl implements MedicalBusinessService {

    private static final BigDecimal REGISTRATION_FEE = new BigDecimal("10.00");

    @Autowired
    private MedicalRecordMapper recordMapper;

    @Autowired
    private MedicalPrescriptionMapper prescriptionMapper;

    @Autowired
    private MedicalPrescriptionItemMapper prescriptionItemMapper;

    @Autowired
    private MedicalAppointmentMapper appointmentMapper;

    @Autowired
    private MedicalMedicineMapper medicineMapper;

    @Autowired
    private MedicalOrderMapper orderMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private MedicalDoctorMapper doctorMapper;

    @Autowired
    private MedicalDepartmentMapper departmentMapper;

    @Autowired
    private MedicalQueueMapper queueMapper;

    /**
     * 医生接诊（创建或更新诊疗记录）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MedicalRecord receivePatient(MedicalRecordRequestDTO requestDTO) {
        log.info("医生接诊：appointmentId={}, doctorId={}", requestDTO.getAppointmentId(), requestDTO.getDoctorId());

        MedicalAppointment appointment = appointmentMapper.selectById(requestDTO.getAppointmentId());
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        if (!appointment.getDoctorId().equals(requestDTO.getDoctorId())) {
            throw new RuntimeException("无权接诊该患者");
        }

        // 已完成预约：若已有病历则更新，支持重复保存/补开处方
        if ("已完成".equals(appointment.getStatus())) {
            MedicalRecord existing = getRecordByAppointmentId(requestDTO.getAppointmentId());
            if (existing != null) {
                requestDTO.setId(existing.getId());
                return updateRecord(requestDTO);
            }
            throw new RuntimeException("该预约已完成，未找到对应病历");
        }

        if (!"已确认".equals(appointment.getStatus()) && !"待确认".equals(appointment.getStatus())) {
            throw new RuntimeException("当前预约状态不可接诊");
        }
        if ("待确认".equals(appointment.getStatus())) {
            appointment.setStatus("已确认");
            appointmentMapper.updateById(appointment);
        }

        MedicalRecord record = new MedicalRecord();
        record.setRecordNo(generateRecordNo());
        record.setUserId(requestDTO.getUserId());
        record.setDoctorId(requestDTO.getDoctorId());
        record.setDeptId(requestDTO.getDeptId());
        fillDeptName(record);
        record.setAppointmentId(requestDTO.getAppointmentId());
        record.setChiefComplaint(requestDTO.getChiefComplaint());
        record.setPresentIllness(requestDTO.getPresentIllness());
        record.setPastHistory(requestDTO.getPastHistory());
        record.setPhysicalExamination(requestDTO.getPhysicalExamination());
        record.setDiagnosis(requestDTO.getDiagnosis());
        record.setDiseaseCode(requestDTO.getDiseaseCode());
        record.setDiseaseName(requestDTO.getDiseaseName());
        record.setAdvice(requestDTO.getAdvice());
        recordMapper.insert(record);

        appointment.setStatus("已完成");
        appointmentMapper.updateById(appointment);
        updateQueueStatus(requestDTO.getAppointmentId(), "已完成");

        log.info("接诊成功，病历号：{}", record.getRecordNo());
        return record;
    }

    private void fillDeptName(MedicalRecord record) {
        if (record.getDeptId() == null) {
            return;
        }
        MedicalDepartment department = departmentMapper.selectById(record.getDeptId());
        if (department != null) {
            record.setDeptName(department.getDeptName());
        }
    }

    private void updateQueueStatus(Long appointmentId, String status) {
        LambdaQueryWrapper<MedicalQueue> queueWrapper = new LambdaQueryWrapper<>();
        queueWrapper.eq(MedicalQueue::getAppointmentId, appointmentId);
        MedicalQueue queue = queueMapper.selectOne(queueWrapper);
        if (queue != null) {
            queue.setStatus(status);
            queueMapper.updateById(queue);
        }
    }

    /**
     * 更新诊疗记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MedicalRecord updateRecord(MedicalRecordRequestDTO requestDTO) {
        log.info("更新诊疗记录：recordId={}", requestDTO.getId());

        MedicalRecord record = recordMapper.selectById(requestDTO.getId());
        if (record == null) {
            throw new RuntimeException("病历不存在");
        }

        // 验证医生权限
        if (!record.getDoctorId().equals(requestDTO.getDoctorId())) {
            throw new RuntimeException("无权修改该病历");
        }

        // 更新病历信息
        record.setChiefComplaint(requestDTO.getChiefComplaint());
        record.setPresentIllness(requestDTO.getPresentIllness());
        record.setPastHistory(requestDTO.getPastHistory());
        record.setPhysicalExamination(requestDTO.getPhysicalExamination());
        record.setDiagnosis(requestDTO.getDiagnosis());
        record.setDiseaseCode(requestDTO.getDiseaseCode());
        record.setDiseaseName(requestDTO.getDiseaseName());
        record.setAdvice(requestDTO.getAdvice());
        recordMapper.updateById(record);
        if (record.getAppointmentId() != null) {
            updateQueueStatus(record.getAppointmentId(), "已完成");
        }

        log.info("病历更新成功：{}", record.getRecordNo());
        return record;
    }

    @Override
    public MedicalRecord getRecordByAppointmentId(Long appointmentId) {
        if (appointmentId == null) {
            return null;
        }
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalRecord::getAppointmentId, appointmentId);
        wrapper.orderByDesc(MedicalRecord::getCreateTime);
        wrapper.last("LIMIT 1");
        return recordMapper.selectOne(wrapper);
    }

    /**
     * 开具处方（核心业务方法 - 完整流程）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrescriptionResultVO createPrescription(PrescriptionRequestDTO requestDTO) {
        log.info("开始开具处方：recordId={}, doctorId={}", requestDTO.getRecordId(), requestDTO.getDoctorId());

        // 1. 验证病历是否存在
        MedicalRecord record = recordMapper.selectById(requestDTO.getRecordId());
        if (record == null) {
            throw new RuntimeException("病历不存在");
        }

        // 2. 验证医生权限
        if (!record.getDoctorId().equals(requestDTO.getDoctorId())) {
            throw new RuntimeException("无权为该病历开具处方");
        }

        // 3. 验证处方明细
        if (requestDTO.getItems() == null || requestDTO.getItems().isEmpty()) {
            throw new RuntimeException("处方明细不能为空");
        }

        // 4. 预校验明细并计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PrescriptionRequestDTO.PrescriptionItemDTO> validatedItems = new ArrayList<>();

        for (PrescriptionRequestDTO.PrescriptionItemDTO itemDTO : requestDTO.getItems()) {
            MedicalMedicine medicine = medicineMapper.selectById(itemDTO.getMedicineId());
            if (medicine == null) {
                throw new RuntimeException("药品不存在：" + itemDTO.getMedicineName());
            }
            if (medicine.getStock() < itemDTO.getQuantity()) {
                throw new RuntimeException("药品库存不足：" + medicine.getMedicineName()
                        + "，当前库存：" + medicine.getStock());
            }
            BigDecimal unitPrice = itemDTO.getPrice();
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                unitPrice = medicine.getPrice() != null ? medicine.getPrice() : BigDecimal.ZERO;
            }
            itemDTO.setPrice(unitPrice);
            if (itemDTO.getMedicineName() == null || itemDTO.getMedicineName().isEmpty()) {
                itemDTO.setMedicineName(medicine.getMedicineName());
            }
            totalAmount = totalAmount.add(unitPrice.multiply(new BigDecimal(itemDTO.getQuantity())));
            validatedItems.add(itemDTO);
        }

        // 5. 创建或复用草稿处方
        MedicalPrescription prescription = findDraftPrescription(requestDTO.getRecordId());
        if (prescription != null) {
            LambdaQueryWrapper<MedicalPrescriptionItem> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(MedicalPrescriptionItem::getPrescriptionId, prescription.getId());
            prescriptionItemMapper.delete(deleteWrapper);
            prescription.setTotalAmount(totalAmount);
            prescriptionMapper.updateById(prescription);
        } else {
            prescription = new MedicalPrescription();
            prescription.setPrescriptionNo(generatePrescriptionNo());
            prescription.setRecordId(requestDTO.getRecordId());
            prescription.setUserId(requestDTO.getUserId());
            prescription.setDoctorId(requestDTO.getDoctorId());
            prescription.setTotalAmount(totalAmount);
            prescription.setStatus("草稿");
            prescriptionMapper.insert(prescription);
        }

        // 6. 创建处方明细
        List<PrescriptionResultVO.PrescriptionItemVO> itemVOs = new ArrayList<>();
        for (PrescriptionRequestDTO.PrescriptionItemDTO itemDTO : validatedItems) {
            MedicalMedicine medicine = medicineMapper.selectById(itemDTO.getMedicineId());
            BigDecimal subtotal = itemDTO.getPrice().multiply(new BigDecimal(itemDTO.getQuantity()));

            MedicalPrescriptionItem item = new MedicalPrescriptionItem();
            item.setPrescriptionId(prescription.getId());
            item.setMedicineId(itemDTO.getMedicineId());
            item.setMedicineName(itemDTO.getMedicineName());
            item.setSpec(medicine.getSpec() != null ? medicine.getSpec() : "-");
            item.setUnit(medicine.getUnit() != null ? medicine.getUnit() : "盒");
            item.setUsageMethod(itemDTO.getUsage() + "，" + itemDTO.getFrequency());
            item.setQuantity(itemDTO.getQuantity());
            item.setPrice(itemDTO.getPrice());
            item.setAmount(subtotal);
            item.setTotalPrice(subtotal);
            prescriptionItemMapper.insert(item);

            PrescriptionResultVO.PrescriptionItemVO itemVO = new PrescriptionResultVO.PrescriptionItemVO();
            itemVO.setId(item.getId());
            itemVO.setMedicineName(itemDTO.getMedicineName());
            itemVO.setUsage(itemDTO.getUsage());
            itemVO.setFrequency(itemDTO.getFrequency());
            itemVO.setQuantity(itemDTO.getQuantity());
            itemVO.setPrice(itemDTO.getPrice());
            itemVO.setSubtotal(subtotal);
            itemVOs.add(itemVO);
        }

        // 7. 构建返回结果
        PrescriptionResultVO resultVO = new PrescriptionResultVO();
        resultVO.setPrescriptionId(prescription.getId());
        resultVO.setPrescriptionNo(prescription.getPrescriptionNo());
        resultVO.setTotalAmount(totalAmount);
        resultVO.setStatus(prescription.getStatus());
        resultVO.setItems(itemVOs);
        resultVO.setMessage("处方创建成功，请提交后生效");

        log.info("处方创建成功：{}", resultVO);
        return resultVO;
    }

    /**
     * 提交处方（状态变更 + 扣减库存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitPrescription(Long prescriptionId, Long doctorId) {
        log.info("提交处方：prescriptionId={}, doctorId={}", prescriptionId, doctorId);

        // 1. 查询处方
        MedicalPrescription prescription = prescriptionMapper.selectById(prescriptionId);
        if (prescription == null) {
            throw new RuntimeException("处方不存在");
        }

        // 2. 验证医生权限
        if (!prescription.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("无权操作该处方");
        }

        // 3. 检查处方状态
        if (!"草稿".equals(prescription.getStatus())) {
            throw new RuntimeException("只有草稿状态的处方才能提交");
        }

        // 4. 查询处方明细
        LambdaQueryWrapper<MedicalPrescriptionItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalPrescriptionItem::getPrescriptionId, prescriptionId);
        List<MedicalPrescriptionItem> items = prescriptionItemMapper.selectList(wrapper);

        // 5. 扣减药品库存
        for (MedicalPrescriptionItem item : items) {
            MedicalMedicine medicine = medicineMapper.selectById(item.getMedicineId());
            if (medicine == null) {
                throw new RuntimeException("药品不存在：" + item.getMedicineName());
            }

            if (medicine.getStock() < item.getQuantity()) {
                throw new RuntimeException("药品库存不足：" + medicine.getMedicineName());
            }

            // 扣减库存
            medicine.setStock(medicine.getStock() - item.getQuantity());
            medicineMapper.updateById(medicine);
        }

        // 6. 更新处方状态
        prescription.setStatus("已提交");
        prescriptionMapper.updateById(prescription);

        // 7. 自动创建待支付订单（挂号费 + 药品费）
        BigDecimal drugTotal = prescription.getTotalAmount() != null ? prescription.getTotalAmount() : BigDecimal.ZERO;
        MedicalOrder order = new MedicalOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(prescription.getUserId());
        order.setPrescriptionId(prescriptionId);
        order.setRecordId(prescription.getRecordId());
        order.setTotalAmount(REGISTRATION_FEE.add(drugTotal));
        order.setStatus("待支付");
        orderMapper.insert(order);

        log.info("处方提交成功，已创建待支付订单：{}", order.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRegistrationOrder(Long recordId, Long doctorId) {
        MedicalRecord record = recordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("病历不存在");
        }
        if (!record.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("无权操作该病历");
        }

        Long prescriptionCount = prescriptionMapper.selectCount(new LambdaQueryWrapper<MedicalPrescription>()
                .eq(MedicalPrescription::getRecordId, recordId));
        if (prescriptionCount > 0) {
            return;
        }

        Long orderCount = orderMapper.selectCount(new LambdaQueryWrapper<MedicalOrder>()
                .eq(MedicalOrder::getRecordId, recordId));
        if (orderCount > 0) {
            return;
        }

        MedicalOrder order = new MedicalOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(record.getUserId());
        order.setRecordId(recordId);
        order.setTotalAmount(REGISTRATION_FEE);
        order.setStatus("待支付");
        orderMapper.insert(order);
        log.info("已创建挂号费订单：recordId={}, orderNo={}", recordId, order.getOrderNo());
    }

    /**
     * 取消处方
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelPrescription(Long prescriptionId, Long doctorId) {
        log.info("取消处方：prescriptionId={}, doctorId={}", prescriptionId, doctorId);

        // 1. 查询处方
        MedicalPrescription prescription = prescriptionMapper.selectById(prescriptionId);
        if (prescription == null) {
            throw new RuntimeException("处方不存在");
        }

        // 2. 验证医生权限
        if (!prescription.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("无权操作该处方");
        }

        // 3. 检查处方状态
        if (!"草稿".equals(prescription.getStatus())) {
            throw new RuntimeException("只有草稿状态的处方才能取消");
        }

        // 4. 更新处方状态
        prescription.setStatus("已取消");
        prescriptionMapper.updateById(prescription);

        log.info("处方取消成功：{}", prescriptionId);
    }

    /**
     * 查询患者诊疗记录列表
     */
    @Override
    public List<MedicalRecord> getPatientRecords(Long userId) {
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalRecord::getUserId, userId);
        wrapper.orderByDesc(MedicalRecord::getCreateTime);
        return recordMapper.selectList(wrapper);
    }

    @Override
    public List<PatientRecordVO> getPatientRecordDetails(Long userId) {
        List<MedicalRecord> records = getPatientRecords(userId);
        List<PatientRecordVO> result = new ArrayList<>();
        for (MedicalRecord record : records) {
            PatientRecordVO vo = new PatientRecordVO();
            vo.setId(record.getId());
            vo.setVisitDate(record.getCreateTime() != null
                    ? record.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    : null);
            if (record.getDeptName() != null && !record.getDeptName().isEmpty()) {
                vo.setDepartment(record.getDeptName());
            } else if (record.getDeptId() != null) {
                MedicalDepartment dept = departmentMapper.selectById(record.getDeptId());
                if (dept != null) {
                    vo.setDepartment(dept.getDeptName());
                }
            }
            MedicalDoctor doctor = doctorMapper.selectById(record.getDoctorId());
            if (doctor != null) {
                SysUser doctorUser = userMapper.selectById(doctor.getUserId());
                if (doctorUser != null) {
                    vo.setDoctorName(doctorUser.getRealName());
                }
            }
            vo.setSymptom(record.getChiefComplaint());
            vo.setDiagnosis(record.getDiagnosis());
            vo.setAdvice(record.getAdvice());
            vo.setStatus("已完成");

            LambdaQueryWrapper<MedicalPrescription> prescriptionWrapper = new LambdaQueryWrapper<>();
            prescriptionWrapper.eq(MedicalPrescription::getRecordId, record.getId());
            prescriptionWrapper.orderByDesc(MedicalPrescription::getCreateTime);
            prescriptionWrapper.last("LIMIT 1");
            MedicalPrescription prescription = prescriptionMapper.selectOne(prescriptionWrapper);
            if (prescription != null) {
                PrescriptionDetailVO prescriptionDetail = buildPrescriptionDetailVO(prescription);
                vo.setPrescriptionId(prescription.getId());
                vo.setPrescriptionNo(prescriptionDetail.getPrescriptionNo());
                vo.setPrescribeDoctorName(prescriptionDetail.getDoctorName());
                vo.setPrescribeDate(prescriptionDetail.getCreateTime());
                vo.setMedicines(prescriptionDetail.getMedicines());
            }
            result.add(vo);
        }
        return result;
    }

    /**
     * 查询医生诊疗记录列表
     */
    @Override
    public List<MedicalRecord> getDoctorRecords(Long doctorId) {
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalRecord::getDoctorId, doctorId);
        wrapper.orderByDesc(MedicalRecord::getCreateTime);
        return recordMapper.selectList(wrapper);
    }

    /**
     * 查询患者处方列表
     */
    @Override
    public List<MedicalPrescription> getPatientPrescriptions(Long userId) {
        LambdaQueryWrapper<MedicalPrescription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalPrescription::getUserId, userId);
        wrapper.orderByDesc(MedicalPrescription::getCreateTime);
        return prescriptionMapper.selectList(wrapper);
    }

    /**
     * 查询医生处方列表
     */
    @Override
    public List<MedicalPrescription> getDoctorPrescriptions(Long doctorId) {
        LambdaQueryWrapper<MedicalPrescription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalPrescription::getDoctorId, doctorId);
        wrapper.orderByDesc(MedicalPrescription::getCreateTime);
        return prescriptionMapper.selectList(wrapper);
    }

    /**
     * 生成病历号
     * 格式：MR + yyyyMMdd + 4位序号
     */
    private String generateRecordNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "MR" + dateStr;
        
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(MedicalRecord::getRecordNo, prefix);
        wrapper.orderByDesc(MedicalRecord::getRecordNo);
        wrapper.last("LIMIT 1");
        MedicalRecord lastRecord = recordMapper.selectOne(wrapper);
        
        int sequence = 1;
        if (lastRecord != null) {
            String lastNo = lastRecord.getRecordNo();
            sequence = Integer.parseInt(lastNo.substring(lastNo.length() - 4)) + 1;
        }
        
        return prefix + String.format("%04d", sequence);
    }

    /**
     * 生成处方编号
     * 格式：RX + yyyyMMdd + 4位序号
     */
    @Override
    public List<PrescriptionDetailVO> getPrescriptionDetailList(String status, Long doctorId, String prescriptionNo) {
        LambdaQueryWrapper<MedicalPrescription> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(MedicalPrescription::getStatus, status);
        }
        if (doctorId != null) {
            wrapper.eq(MedicalPrescription::getDoctorId, doctorId);
        }
        if (prescriptionNo != null && !prescriptionNo.isEmpty()) {
            wrapper.like(MedicalPrescription::getPrescriptionNo, prescriptionNo);
        }
        wrapper.orderByDesc(MedicalPrescription::getCreateTime);
        List<MedicalPrescription> prescriptions = prescriptionMapper.selectList(wrapper);
        List<PrescriptionDetailVO> result = new ArrayList<>();
        for (MedicalPrescription prescription : prescriptions) {
            result.add(buildPrescriptionDetailVO(prescription));
        }
        return result;
    }

    @Override
    public PrescriptionDetailVO getPrescriptionDetail(Long prescriptionId) {
        MedicalPrescription prescription = prescriptionMapper.selectById(prescriptionId);
        if (prescription == null) {
            throw new RuntimeException("处方不存在");
        }
        return buildPrescriptionDetailVO(prescription);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startDispense(Long prescriptionId) {
        MedicalPrescription prescription = prescriptionMapper.selectById(prescriptionId);
        if (prescription == null) {
            throw new RuntimeException("处方不存在");
        }
        if (!"待配药".equals(prescription.getStatus())) {
            throw new RuntimeException("学生缴费后处方才会进入待配药状态");
        }
        prescription.setStatus("配药中");
        prescriptionMapper.updateById(prescription);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeDispense(Long prescriptionId) {
        MedicalPrescription prescription = prescriptionMapper.selectById(prescriptionId);
        if (prescription == null) {
            throw new RuntimeException("处方不存在");
        }
        if (!"配药中".equals(prescription.getStatus())) {
            throw new RuntimeException("只有配药中状态的处方才能完成发药");
        }
        prescription.setStatus("已完成");
        prescriptionMapper.updateById(prescription);
    }

    private PrescriptionDetailVO buildPrescriptionDetailVO(MedicalPrescription prescription) {
        PrescriptionDetailVO vo = new PrescriptionDetailVO();
        vo.setId(prescription.getId());
        vo.setPrescriptionNo(prescription.getPrescriptionNo());
        vo.setRecordId(prescription.getRecordId());
        vo.setUserId(prescription.getUserId());
        vo.setDoctorId(prescription.getDoctorId());
        vo.setTotalAmount(prescription.getTotalAmount());
        vo.setStatus(prescription.getStatus());
        vo.setCreateTime(prescription.getCreateTime() != null
                ? prescription.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : null);

        SysUser patient = userMapper.selectById(prescription.getUserId());
        if (patient != null) {
            vo.setPatientName(patient.getRealName());
        }

        MedicalDoctor doctor = doctorMapper.selectById(prescription.getDoctorId());
        if (doctor != null) {
            SysUser doctorUser = userMapper.selectById(doctor.getUserId());
            if (doctorUser != null) {
                vo.setDoctorName(doctorUser.getRealName());
            }
        }

        if (prescription.getRecordId() != null) {
            MedicalRecord record = recordMapper.selectById(prescription.getRecordId());
            if (record != null) {
                vo.setDiagnosis(record.getDiagnosis());
            }
        }

        LambdaQueryWrapper<MedicalPrescriptionItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(MedicalPrescriptionItem::getPrescriptionId, prescription.getId());
        List<MedicalPrescriptionItem> items = prescriptionItemMapper.selectList(itemWrapper);
        vo.setMedicineCount(items.size());

        List<PrescriptionDetailVO.MedicineItemVO> medicineVOs = new ArrayList<>();
        boolean dispensed = "已完成".equals(prescription.getStatus()) || "配药中".equals(prescription.getStatus());
        for (MedicalPrescriptionItem item : items) {
            PrescriptionDetailVO.MedicineItemVO itemVO = new PrescriptionDetailVO.MedicineItemVO();
            itemVO.setId(item.getId());
            itemVO.setName(item.getMedicineName());
            itemVO.setQuantity(item.getQuantity());
            itemVO.setPrice(item.getPrice());
            itemVO.setUsage(item.getUsageMethod());
            itemVO.setDispensed(dispensed);
            MedicalMedicine medicine = medicineMapper.selectById(item.getMedicineId());
            if (medicine != null) {
                itemVO.setSpec(medicine.getSpec());
            }
            medicineVOs.add(itemVO);
        }
        vo.setMedicines(medicineVOs);
        return vo;
    }

    private MedicalPrescription findDraftPrescription(Long recordId) {
        LambdaQueryWrapper<MedicalPrescription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalPrescription::getRecordId, recordId);
        wrapper.eq(MedicalPrescription::getStatus, "草稿");
        wrapper.orderByDesc(MedicalPrescription::getCreateTime);
        wrapper.last("LIMIT 1");
        return prescriptionMapper.selectOne(wrapper);
    }

    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "ORD" + dateStr;
        LambdaQueryWrapper<MedicalOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(MedicalOrder::getOrderNo, prefix);
        wrapper.orderByDesc(MedicalOrder::getOrderNo);
        wrapper.last("LIMIT 1");
        MedicalOrder lastOrder = orderMapper.selectOne(wrapper);
        int sequence = 1;
        if (lastOrder != null) {
            String lastNo = lastOrder.getOrderNo();
            sequence = Integer.parseInt(lastNo.substring(lastNo.length() - 4)) + 1;
        }
        return prefix + String.format("%04d", sequence);
    }

    private String generatePrescriptionNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "RX" + dateStr;
        
        LambdaQueryWrapper<MedicalPrescription> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(MedicalPrescription::getPrescriptionNo, prefix);
        wrapper.orderByDesc(MedicalPrescription::getPrescriptionNo);
        wrapper.last("LIMIT 1");
        MedicalPrescription lastPrescription = prescriptionMapper.selectOne(wrapper);
        
        int sequence = 1;
        if (lastPrescription != null) {
            String lastNo = lastPrescription.getPrescriptionNo();
            sequence = Integer.parseInt(lastNo.substring(lastNo.length() - 4)) + 1;
        }
        
        return prefix + String.format("%04d", sequence);
    }
}
