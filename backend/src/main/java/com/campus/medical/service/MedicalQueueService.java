package com.campus.medical.service;

import com.campus.medical.entity.MedicalQueue;
import com.campus.medical.vo.QueueDetailVO;

import java.util.List;

/**
 * 队列服务接口
 */
public interface MedicalQueueService extends IBaseService<MedicalQueue> {

    /**
     * 根据科室ID查询队列列表
     */
    List<MedicalQueue> getByDeptId(Long deptId);

    /**
     * 根据医生ID查询队列列表
     */
    List<MedicalQueue> getByDoctorId(Long doctorId);

    /**
     * 查询医生候诊队列详情（含学生信息）
     */
    List<QueueDetailVO> getQueueDetailByDoctor(Long doctorId, String queueDate, String status);

    /**
     * 删除候诊队列记录
     */
    void deleteQueueRecord(Long queueId, Long doctorId);
}
