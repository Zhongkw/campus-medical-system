package com.campus.medical.service;

import com.campus.medical.dto.BasicDataDTO;
import com.campus.medical.dto.MedicineManageDTO;
import com.campus.medical.entity.SysRole;
import com.campus.medical.vo.BasicDataVO;
import com.campus.medical.vo.DoctorBasicVO;
import com.campus.medical.vo.MedicineBasicVO;
import com.campus.medical.vo.MenuTreeVO;

import java.util.List;

public interface SystemManageService {

    List<MenuTreeVO> getMenuTree();

    List<Long> getRoleMenuIds(Long roleId);

    void assignRoleMenus(Long roleId, List<Long> menuIds);

    List<MenuTreeVO> getUserMenus(Long roleId);

    Long addRole(SysRole role);

    Boolean updateRole(SysRole role);

    Boolean deleteRole(Long roleId);

    List<BasicDataVO> listDepartments();

    void saveDepartment(BasicDataDTO dto);

    void deleteDepartment(Long id);

    List<DoctorBasicVO> listDoctorsByDepartment(Long deptId);

    List<BasicDataVO> listDrugCategories();

    void saveDrugCategory(BasicDataDTO dto);

    void deleteDrugCategory(Long id);

    List<BasicDataVO> listDiseases();

    void saveDisease(BasicDataDTO dto);

    void deleteDisease(Long id);

    List<MedicineBasicVO> listMedicinesByCategory(String categoryCode);

    void saveMedicine(MedicineManageDTO dto);

    void deleteMedicine(Long id);
}
