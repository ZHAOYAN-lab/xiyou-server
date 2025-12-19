package com.ifengniao.server.xiyoucloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * 任务实体类
 * 对应数据库表：xy_task
 */
@TableName("xy_task")
public class Task {

    /**
     * 主键 ID
     * PostgreSQL serial / sequence
     * 必须使用 AUTO，让数据库生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** 对象名称 */
    @TableField("object_name")
    @JsonProperty("objectName")
    private String objectName;

    /** 任务类型（取货 / 送货） */
    @TableField("task_type")
    @JsonProperty("taskType")
    private String taskType;

    /** 任务内容 / 任务说明 */
    @TableField("task_desc")
    @JsonProperty("taskDesc")
    private String taskDesc;

    /** 当前状态：待派发 / 已派发 / 执行中 / 已完成 */
    @TableField("status")
    @JsonProperty("status")
    private String status;

    /** 派发员工（多个用逗号分隔） */
    @TableField("assigned_to")
    @JsonProperty("assignedTo")
    private String assignedTo;

    /** 创建时间 */
    @TableField("create_time")
    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 更新时间 */
    @TableField("update_time")
    @JsonProperty("updateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /** 是否删除：0=正常 1=删除 */
    @TableField("is_deleted")
    @JsonProperty("isDeleted")
    private Integer isDeleted;

    /** 商品区域 ID */
    @TableField("area_id")
    @JsonProperty("areaId")
    private Integer areaId;

    /** 商品区域名称 */
    @TableField("area_name")
    @JsonProperty("areaName")
    private String areaName;

    /* ========== Getter / Setter ========== */

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", objectName='" + objectName + '\'' +
                ", taskType='" + taskType + '\'' +
                ", taskDesc='" + taskDesc + '\'' +
                ", status='" + status + '\'' +
                ", assignedTo='" + assignedTo + '\'' +
                ", areaId=" + areaId +
                ", areaName='" + areaName + '\'' +
                ", isDeleted=" + isDeleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
