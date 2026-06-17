package com.campus.medical.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.medical.dto.SysUserDTO;
import com.campus.medical.dto.UserQueryDTO;
import com.campus.medical.entity.SysUser;
import com.campus.medical.vo.PageResultVO;
import com.campus.medical.vo.SysUserVO;

/**
 * 系统用户服务接口
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);
    
    /**
     * 分页查询系统用户
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResultVO<SysUserVO> pageQuerySysUser(UserQueryDTO queryDTO);
    
    /**
     * 新增系统用户
     * @param userDTO 用户信息
     * @return 是否成功
     */
    Boolean addSystemUser(SysUserDTO userDTO);
    
    /**
     * 编辑系统用户
     * @param userDTO 用户信息
     * @return 是否成功
     */
    Boolean editSystemUser(SysUserDTO userDTO);
    
    /**
     * 切换用户状态
     * @param userId 用户ID
     * @param status 状态
     * @return 是否成功
     */
    Boolean switchUserStatus(Long userId, Integer status);
    
    /**
     * 重置用户密码
     * @param userId 用户ID
     * @return 新密码
     */
    String resetUserPwd(Long userId);

    Boolean deleteSystemUser(Long userId);
}
