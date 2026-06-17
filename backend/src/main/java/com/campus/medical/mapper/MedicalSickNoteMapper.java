package com.campus.medical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.medical.entity.MedicalSickNote;
import org.apache.ibatis.annotations.Mapper;

/**
 * 电子病假条表 Mapper 接口
 */
@Mapper
public interface MedicalSickNoteMapper extends BaseMapper<MedicalSickNote> {
}