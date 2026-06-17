package com.campus.medical.service.impl;

import com.campus.medical.entity.MedicalDoctor;
import com.campus.medical.mapper.MedicalDoctorMapper;
import com.campus.medical.service.MedicalDoctorService;
import org.springframework.stereotype.Service;

/**
 * 医生服务实现类
 */
@Service
public class MedicalDoctorServiceImpl extends BaseServiceImpl<MedicalDoctorMapper, MedicalDoctor> implements MedicalDoctorService {
}
