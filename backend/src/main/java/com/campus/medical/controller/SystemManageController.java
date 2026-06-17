package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.dto.BasicDataDTO;
import com.campus.medical.dto.MedicineManageDTO;
import com.campus.medical.dto.RoleMenuAssignDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.SysRole;
import com.campus.medical.mapper.SysRoleMapper;
import com.campus.medical.service.SystemManageService;
import com.campus.medical.utils.ThreadLocalUtils;
import com.campus.medical.vo.BasicDataVO;
import com.campus.medical.vo.DoctorBasicVO;
import com.campus.medical.vo.MedicineBasicVO;
import com.campus.medical.vo.MenuTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/manage")
public class SystemManageController {

    @Autowired
    private SystemManageService systemManageService;

    @Autowired
    private SysRoleMapper roleMapper;

    @GetMapping("/menu/tree")
    public Result<List<MenuTreeVO>> getMenuTree() {
        return ResultUtils.success(systemManageService.getMenuTree());
    }

    @GetMapping("/role-menu/{roleId}")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long roleId) {
        return ResultUtils.success(systemManageService.getRoleMenuIds(roleId));
    }

    @PostMapping("/role-menu/assign")
    public Result<Void> assignRoleMenus(@RequestBody RoleMenuAssignDTO dto) {
        systemManageService.assignRoleMenus(dto.getRoleId(), dto.getMenuIds());
        return ResultUtils.success("配置成功", null);
    }

    @GetMapping("/user-menus")
    public Result<List<MenuTreeVO>> getCurrentUserMenus() {
        Map<String, Object> currentUser = ThreadLocalUtils.get();
        String roleCode = String.valueOf(currentUser.get("role"));
        SysRole role = roleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, roleCode));
        if (role == null) {
            return ResultUtils.success(List.of());
        }
        return ResultUtils.success(systemManageService.getUserMenus(role.getId()));
    }

    @PostMapping("/role/add")
    public Result<Long> addRole(@RequestBody SysRole role) {
        return ResultUtils.success("新增成功", systemManageService.addRole(role));
    }

    @PostMapping("/role/edit")
    public Result<Boolean> editRole(@RequestBody SysRole role) {
        return ResultUtils.success(systemManageService.updateRole(role));
    }

    @DeleteMapping("/role/{roleId}")
    public Result<Boolean> deleteRole(@PathVariable Long roleId) {
        return ResultUtils.success(systemManageService.deleteRole(roleId));
    }

    @GetMapping("/basic-data/departments")
    public Result<List<BasicDataVO>> listDepartments() {
        return ResultUtils.success(systemManageService.listDepartments());
    }

    @PostMapping("/basic-data/department")
    public Result<Void> saveDepartment(@RequestBody BasicDataDTO dto) {
        systemManageService.saveDepartment(dto);
        return ResultUtils.success("保存成功", null);
    }

    @DeleteMapping("/basic-data/department/{id}")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        systemManageService.deleteDepartment(id);
        return ResultUtils.success("删除成功", null);
    }

    @GetMapping("/basic-data/department/{deptId}/doctors")
    public Result<List<DoctorBasicVO>> listDoctorsByDepartment(@PathVariable Long deptId) {
        return ResultUtils.success(systemManageService.listDoctorsByDepartment(deptId));
    }

    @GetMapping("/basic-data/drug-categories")
    public Result<List<BasicDataVO>> listDrugCategories() {
        return ResultUtils.success(systemManageService.listDrugCategories());
    }

    @PostMapping("/basic-data/drug-category")
    public Result<Void> saveDrugCategory(@RequestBody BasicDataDTO dto) {
        systemManageService.saveDrugCategory(dto);
        return ResultUtils.success("保存成功", null);
    }

    @DeleteMapping("/basic-data/drug-category/{id}")
    public Result<Void> deleteDrugCategory(@PathVariable Long id) {
        systemManageService.deleteDrugCategory(id);
        return ResultUtils.success("删除成功", null);
    }

    @GetMapping("/basic-data/diseases")
    public Result<List<BasicDataVO>> listDiseases() {
        return ResultUtils.success(systemManageService.listDiseases());
    }

    @PostMapping("/basic-data/disease")
    public Result<Void> saveDisease(@RequestBody BasicDataDTO dto) {
        systemManageService.saveDisease(dto);
        return ResultUtils.success("保存成功", null);
    }

    @DeleteMapping("/basic-data/disease/{id}")
    public Result<Void> deleteDisease(@PathVariable Long id) {
        systemManageService.deleteDisease(id);
        return ResultUtils.success("删除成功", null);
    }

    @GetMapping("/basic-data/medicines")
    public Result<List<MedicineBasicVO>> listMedicinesByCategory(@RequestParam String categoryCode) {
        return ResultUtils.success(systemManageService.listMedicinesByCategory(categoryCode));
    }

    @PostMapping("/basic-data/medicine")
    public Result<Void> saveMedicine(@RequestBody MedicineManageDTO dto) {
        systemManageService.saveMedicine(dto);
        return ResultUtils.success("保存成功", null);
    }

    @DeleteMapping("/basic-data/medicine/{id}")
    public Result<Void> deleteMedicine(@PathVariable Long id) {
        systemManageService.deleteMedicine(id);
        return ResultUtils.success("删除成功", null);
    }
}
