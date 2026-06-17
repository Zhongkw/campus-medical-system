package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.dto.SysUserDTO;
import com.campus.medical.dto.UserQueryDTO;
import com.campus.medical.entity.SysUser;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.SysUserService;
import com.campus.medical.vo.PageResultVO;
import com.campus.medical.vo.SysUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统用户控制器
 */
@RestController
@RequestMapping("/api/system/user")
public class SysUserController extends BaseController<SysUser> {

    @Autowired
    private SysUserService sysUserService;

    @Override
    protected IBaseService<SysUser> getService() {
        return (IBaseService<SysUser>) sysUserService;
    }

    /**
     * 根据用户名查询用户
     */
    @GetMapping("/username/{username}")
    public Result<SysUser> getByUsername(@PathVariable String username) {
        return ResultUtils.success(sysUserService.getByUsername(username));
    }
    
    /**
     * 分页查询系统用户
     */
    @GetMapping("/pageList")
    public Result<PageResultVO<SysUserVO>> pageList(UserQueryDTO queryDTO) {
        PageResultVO<SysUserVO> result = sysUserService.pageQuerySysUser(queryDTO);
        return ResultUtils.success(result);
    }
    
    /**
     * 新增系统用户
     */
    @PostMapping("/add")
    public Result<Boolean> addUser(@RequestBody SysUserDTO userDTO) {
        Boolean result = sysUserService.addSystemUser(userDTO);
        return ResultUtils.success(result);
    }
    
    /**
     * 编辑系统用户
     */
    @PostMapping("/edit")
    public Result<Boolean> editUser(@RequestBody SysUserDTO userDTO) {
        Boolean result = sysUserService.editSystemUser(userDTO);
        return ResultUtils.success(result);
    }
    
    /**
     * 切换用户状态
     */
    @PostMapping("/switchStatus")
    public Result<Boolean> switchStatus(
            @RequestParam Long userId,
            @RequestParam (required = false) Integer status) {
        Boolean result = sysUserService.switchUserStatus(userId, status);
        return ResultUtils.success(result);
    }
    
    /**
     * 重置用户密码
     */
    @PostMapping("/resetPwd")
    public Result<String> resetPwd(@RequestParam Long userId) {
        String newPwd = sysUserService.resetUserPwd(userId);
        return ResultUtils.success("重置成功，新密码为：" + newPwd);
    }

    @PostMapping("/delete")
    public Result<Boolean> deleteUser(@RequestParam Long userId) {
        return ResultUtils.success("删除成功", sysUserService.deleteSystemUser(userId));
    }
}
