package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.dto.BasicDataDTO;
import com.campus.medical.dto.MedicineManageDTO;
import com.campus.medical.entity.MedicalDepartment;
import com.campus.medical.entity.MedicalDoctor;
import com.campus.medical.entity.MedicalMedicine;
import com.campus.medical.entity.SysDictData;
import com.campus.medical.entity.SysDictType;
import com.campus.medical.entity.SysMenu;
import com.campus.medical.entity.SysRole;
import com.campus.medical.entity.SysRoleMenu;
import com.campus.medical.entity.SysUser;
import com.campus.medical.mapper.MedicalDepartmentMapper;
import com.campus.medical.mapper.MedicalDoctorMapper;
import com.campus.medical.mapper.MedicalMedicineMapper;
import com.campus.medical.mapper.SysDictDataMapper;
import com.campus.medical.mapper.SysDictTypeMapper;
import com.campus.medical.mapper.SysMenuMapper;
import com.campus.medical.mapper.SysRoleMapper;
import com.campus.medical.mapper.SysRoleMenuMapper;
import com.campus.medical.mapper.SysUserMapper;
import com.campus.medical.service.SystemManageService;
import com.campus.medical.vo.BasicDataVO;
import com.campus.medical.vo.DoctorBasicVO;
import com.campus.medical.vo.MedicineBasicVO;
import com.campus.medical.vo.MenuTreeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SystemManageServiceImpl implements SystemManageService {

    private static final String DICT_DRUG_CATEGORY = "drug_category";
    private static final String DICT_DISEASE = "disease";

    @Autowired
    private SysMenuMapper menuMapper;
    @Autowired
    private SysRoleMenuMapper roleMenuMapper;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private MedicalDepartmentMapper departmentMapper;
    @Autowired
    private SysDictDataMapper dictDataMapper;
    @Autowired
    private SysDictTypeMapper dictTypeMapper;
    @Autowired
    private MedicalMedicineMapper medicineMapper;
    @Autowired
    private MedicalDoctorMapper doctorMapper;

    @Override
    public List<MenuTreeVO> getMenuTree() {
        List<SysMenu> menus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getStatus, 1)
                .orderByAsc(SysMenu::getSort));
        return buildMenuTree(menus, 0L);
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, roleId))
                .stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoleMenus(Long roleId, List<Long> menuIds) {
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        for (Long menuId : menuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    @Override
    public List<MenuTreeVO> getUserMenus(Long roleId) {
        List<Long> menuIds = getRoleMenuIds(roleId);
        if (menuIds.isEmpty()) {
            return List.of();
        }
        List<SysMenu> menus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .in(SysMenu::getId, menuIds)
                .eq(SysMenu::getStatus, 1)
                .eq(SysMenu::getMenuType, 1)
                .isNotNull(SysMenu::getMenuPath)
                .ne(SysMenu::getMenuPath, "")
                .orderByAsc(SysMenu::getSort));
        return menus.stream().map(this::toMenuVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addRole(SysRole role) {
        if (role.getRoleCode() == null || role.getRoleCode().isBlank()) {
            throw new RuntimeException("角色编码不能为空");
        }
        Long count = roleMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, role.getRoleCode()));
        if (count > 0) {
            throw new RuntimeException("角色编码已存在");
        }
        role.setStatus(1);
        role.setCreateTime(LocalDateTime.now());
        roleMapper.insert(role);
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateRole(SysRole role) {
        SysRole existing = roleMapper.selectById(role.getId());
        if (existing == null) {
            throw new RuntimeException("角色不存在");
        }
        existing.setRoleName(role.getRoleName());
        existing.setDescription(role.getDescription());
        existing.setUpdateTime(LocalDateTime.now());
        return roleMapper.updateById(existing) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRole(Long roleId) {
        Long userCount = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRoleId, roleId));
        if (userCount > 0) {
            throw new RuntimeException("该角色下仍有用户，无法删除");
        }
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        return roleMapper.deleteById(roleId) > 0;
    }

    @Override
    public List<BasicDataVO> listDepartments() {
        return departmentMapper.selectList(new LambdaQueryWrapper<MedicalDepartment>()
                        .orderByAsc(MedicalDepartment::getSort))
                .stream().map(dept -> {
                    BasicDataVO vo = new BasicDataVO();
                    vo.setId(dept.getId());
                    vo.setCode(dept.getDeptCode());
                    vo.setName(dept.getDeptName());
                    vo.setDescription(dept.getDescription());
                    vo.setStatus(dept.getStatus() != null && dept.getStatus() == 1 ? "启用" : "禁用");
                    return vo;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDepartment(BasicDataDTO dto) {
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new RuntimeException("科室编码不能为空");
        }
        if (dto.getId() != null) {
            MedicalDepartment dept = departmentMapper.selectById(dto.getId());
            if (dept == null) {
                throw new RuntimeException("科室不存在");
            }
            dept.setDeptCode(dto.getCode());
            dept.setDeptName(dto.getName());
            dept.setDescription(dto.getDescription());
            dept.setUpdateTime(LocalDateTime.now());
            departmentMapper.updateById(dept);
            return;
        }
        Long count = departmentMapper.selectCount(new LambdaQueryWrapper<MedicalDepartment>()
                .eq(MedicalDepartment::getDeptCode, dto.getCode()));
        if (count > 0) {
            throw new RuntimeException("科室编码已存在");
        }
        MedicalDepartment dept = new MedicalDepartment();
        dept.setDeptCode(dto.getCode());
        dept.setDeptName(dto.getName());
        dept.setDescription(dto.getDescription());
        dept.setSort(99);
        dept.setStatus(1);
        dept.setCreateTime(LocalDateTime.now());
        departmentMapper.insert(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Long id) {
        departmentMapper.deleteById(id);
    }

    @Override
    public List<DoctorBasicVO> listDoctorsByDepartment(Long deptId) {
        List<MedicalDoctor> doctors = doctorMapper.selectList(new LambdaQueryWrapper<MedicalDoctor>()
                .eq(MedicalDoctor::getDeptId, deptId)
                .orderByAsc(MedicalDoctor::getId));
        List<DoctorBasicVO> result = new ArrayList<>();
        for (MedicalDoctor doctor : doctors) {
            DoctorBasicVO vo = new DoctorBasicVO();
            vo.setId(doctor.getId());
            vo.setUserId(doctor.getUserId());
            vo.setTitle(doctor.getTitle());
            vo.setSpecialty(doctor.getSpecialty());
            SysUser user = userMapper.selectById(doctor.getUserId());
            if (user != null) {
                vo.setDoctorName(user.getRealName());
                vo.setPhone(user.getPhone());
                vo.setStatus(user.getStatus() != null && user.getStatus() == 1 ? "启用" : "禁用");
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<BasicDataVO> listDrugCategories() {
        return listDictBasicData(DICT_DRUG_CATEGORY);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDrugCategory(BasicDataDTO dto) {
        saveDictBasicData(DICT_DRUG_CATEGORY, "药品分类", dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDrugCategory(Long id) {
        dictDataMapper.deleteById(id);
    }

    @Override
    public List<BasicDataVO> listDiseases() {
        return listDictBasicData(DICT_DISEASE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDisease(BasicDataDTO dto) {
        saveDictBasicData(DICT_DISEASE, "病种管理", dto, dto.getCategory());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDisease(Long id) {
        dictDataMapper.deleteById(id);
    }

    private List<BasicDataVO> listDictBasicData(String dictType) {
        ensureDictType(dictType, dictType);
        return dictDataMapper.selectList(new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getDictType, dictType)
                        .orderByAsc(SysDictData::getSort))
                .stream().map(item -> {
                    BasicDataVO vo = new BasicDataVO();
                    vo.setId(item.getId());
                    vo.setCode(item.getDictValue());
                    vo.setName(item.getDictLabel());
                    vo.setDescription(item.getRemark());
                    vo.setCategory(item.getCategory());
                    vo.setStatus(item.getStatus() != null && item.getStatus() == 1 ? "启用" : "禁用");
                    return vo;
                }).collect(Collectors.toList());
    }

    private void saveDictBasicData(String dictType, String dictName, BasicDataDTO dto) {
        saveDictBasicData(dictType, dictName, dto, null);
    }

    private void saveDictBasicData(String dictType, String dictName, BasicDataDTO dto, String category) {
        ensureDictType(dictType, dictName);
        if (dto.getId() != null) {
            SysDictData data = dictDataMapper.selectById(dto.getId());
            if (data == null) {
                throw new RuntimeException("数据不存在");
            }
            data.setDictValue(dto.getCode());
            data.setDictLabel(dto.getName());
            data.setRemark(dto.getDescription());
            data.setCategory(category);
            data.setUpdateTime(LocalDateTime.now());
            dictDataMapper.updateById(data);
            return;
        }
        SysDictData data = new SysDictData();
        data.setDictType(dictType);
        data.setDictValue(dto.getCode());
        data.setDictLabel(dto.getName());
        data.setRemark(dto.getDescription());
        data.setCategory(category);
        data.setSort(99);
        data.setStatus(1);
        data.setCreateTime(LocalDateTime.now());
        dictDataMapper.insert(data);
    }

    private void ensureDictType(String dictCode, String dictName) {
        Long count = dictTypeMapper.selectCount(new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, dictCode));
        if (count == 0) {
            SysDictType dictType = new SysDictType();
            dictType.setDictType(dictCode);
            dictType.setDictName(dictName);
            dictType.setStatus(1);
            dictType.setCreateTime(LocalDateTime.now());
            dictTypeMapper.insert(dictType);
        }
    }

    private List<MenuTreeVO> buildMenuTree(List<SysMenu> menus, Long parentId) {
        Map<Long, List<SysMenu>> grouped = menus.stream()
                .collect(Collectors.groupingBy(m -> m.getParentId() == null ? 0L : m.getParentId()));
        return buildChildren(grouped, parentId);
    }

    private List<MenuTreeVO> buildChildren(Map<Long, List<SysMenu>> grouped, Long parentId) {
        List<SysMenu> children = grouped.getOrDefault(parentId, List.of());
        return children.stream()
                .sorted(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Integer::compareTo)))
                .map(menu -> {
                    MenuTreeVO vo = toMenuVO(menu);
                    vo.setChildren(buildChildren(grouped, menu.getId()));
                    return vo;
                }).collect(Collectors.toList());
    }

    @Override
    public List<MedicineBasicVO> listMedicinesByCategory(String categoryCode) {
        if (categoryCode == null || categoryCode.isBlank()) {
            return List.of();
        }
        List<String> aliases = resolveCategoryAliases(categoryCode);
        LambdaQueryWrapper<MedicalMedicine> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MedicalMedicine::getCategory, aliases);
        wrapper.orderByAsc(MedicalMedicine::getMedicineName);
        return medicineMapper.selectList(wrapper).stream().map(this::toMedicineVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMedicine(MedicineManageDTO dto) {
        if (dto.getCategoryCode() == null || dto.getCategoryCode().isBlank()) {
            throw new RuntimeException("药品分类不能为空");
        }
        if (dto.getMedicineName() == null || dto.getMedicineName().isBlank()) {
            throw new RuntimeException("药品名称不能为空");
        }
        if (dto.getSpec() == null || dto.getSpec().isBlank()) {
            throw new RuntimeException("药品规格不能为空");
        }
        if (dto.getId() != null) {
            MedicalMedicine medicine = medicineMapper.selectById(dto.getId());
            if (medicine == null) {
                throw new RuntimeException("药品不存在");
            }
            medicine.setMedicineName(dto.getMedicineName());
            medicine.setSpec(dto.getSpec());
            medicine.setDescription(dto.getDescription());
            medicine.setCategory(dto.getCategoryCode());
            medicine.setUnit(dto.getUnit() != null ? dto.getUnit() : medicine.getUnit());
            if (dto.getPrice() != null) {
                medicine.setPrice(dto.getPrice());
            }
            medicine.setUpdateTime(LocalDateTime.now());
            medicineMapper.updateById(medicine);
            return;
        }
        MedicalMedicine medicine = new MedicalMedicine();
        medicine.setMedicineName(dto.getMedicineName());
        medicine.setSpec(dto.getSpec());
        medicine.setDescription(dto.getDescription());
        medicine.setCategory(dto.getCategoryCode());
        medicine.setUnit(dto.getUnit() != null ? dto.getUnit() : "盒");
        medicine.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);
        medicine.setStock(0);
        medicine.setMinStock(10);
        medicine.setStatus(1);
        medicine.setCreateTime(LocalDateTime.now());
        medicineMapper.insert(medicine);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMedicine(Long id) {
        medicineMapper.deleteById(id);
    }

    private String resolveCategoryName(String categoryCode) {
        SysDictData dict = dictDataMapper.selectOne(new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, DICT_DRUG_CATEGORY)
                .eq(SysDictData::getDictValue, categoryCode)
                .last("LIMIT 1"));
        return dict != null ? dict.getDictLabel() : categoryCode;
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

    private MedicineBasicVO toMedicineVO(MedicalMedicine medicine) {
        MedicineBasicVO vo = new MedicineBasicVO();
        vo.setId(medicine.getId());
        vo.setCategoryCode(medicine.getCategory());
        vo.setMedicineName(medicine.getMedicineName());
        vo.setSpec(medicine.getSpec());
        vo.setDescription(medicine.getDescription());
        vo.setUnit(medicine.getUnit());
        vo.setPrice(medicine.getPrice());
        vo.setStock(medicine.getStock());
        vo.setStatus(medicine.getStatus() != null && medicine.getStatus() == 1 ? "正常" : "禁用");
        return vo;
    }

    private MenuTreeVO toMenuVO(SysMenu menu) {
        MenuTreeVO vo = new MenuTreeVO();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setName(menu.getMenuName());
        vo.setPath(menu.getMenuPath());
        vo.setIcon(menu.getMenuIcon());
        vo.setMenuType(menu.getMenuType());
        return vo;
    }
}
