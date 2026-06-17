package com.campus.medical.vo;

import lombok.Data;

import java.util.List;

@Data
public class MenuTreeVO {
    private Long id;
    private Long parentId;
    private String name;
    private String path;
    private String icon;
    private Integer menuType;
    private List<MenuTreeVO> children;
}
