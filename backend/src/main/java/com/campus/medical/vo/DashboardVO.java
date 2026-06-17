package com.campus.medical.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 工作台数据VO
 */
@Data
public class DashboardVO {

    /**
     * 欢迎语
     */
    private String welcome;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 统计卡片数据
     */
    private List<StatisticCardVO> statistics;

    /**
     * 待办事项数量
     */
    private Integer todoCount;

    /**
     * 已办事项数量
     */
    private Integer doneCount;

    /**
     * 待办事项列表
     */
    private List<TodoItemVO> todoList;

    /**
     * 已办事项列表
     */
    private List<DoneItemVO> doneList;

    /**
     * 快捷操作列表
     */
    private List<QuickActionVO> quickActions;

    @Data
    public static class StatisticCardVO {
        /**
         * 卡片名称
         */
        private String name;

        /**
         * 卡片数值
         */
        private Integer value;
    }

    @Data
    public static class TodoItemVO {
        /**
         * 事项ID
         */
        private Long id;

        /**
         * 事项标题
         */
        private String title;

        /**
         * 事项内容
         */
        private String content;

        /**
         * 创建时间
         */
        private String createTime;

        /**
         * 跳转链接
         */
        private String url;
    }

    @Data
    public static class DoneItemVO {
        /**
         * 事项ID
         */
        private Long id;

        /**
         * 事项标题
         */
        private String title;

        /**
         * 事项内容
         */
        private String content;

        /**
         * 完成时间
         */
        private String doneTime;

        /**
         * 跳转链接
         */
        private String url;
    }

    @Data
    public static class QuickActionVO {
        /**
         * 操作名称
         */
        private String name;

        /**
         * 操作图标
         */
        private String icon;

        /**
         * 跳转链接
         */
        private String url;
    }
}
