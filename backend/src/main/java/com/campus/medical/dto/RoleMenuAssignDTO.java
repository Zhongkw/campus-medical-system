package com.campus.medical.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleMenuAssignDTO {
    private Long roleId;
    private List<Long> menuIds;
}
