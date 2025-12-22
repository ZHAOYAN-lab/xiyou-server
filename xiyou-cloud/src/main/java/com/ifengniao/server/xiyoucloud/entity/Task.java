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

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** 对象名称 */
    @TableField("object_name")
    @JsonProperty("objectName")
    private String objectName;

    /**
     * 所属类型（导航 / 取货 / 送货）
     * 你现在叫 taskType，这里沿用不改字段名，减少牵连
     */
    @TableField("task_type")
    @JsonProperty("taskType")
    private String taskType;

    /**
     * 旧字段：任务内容 / 任务说明（历史兼容保留）
     * 新前端改用 remark
     */
    @TableField("task_desc")
    @JsonProperty("taskDesc")
    private String taskDesc;

    /** ✅ 新字段：备注（替代任务内容） */
    @TableField("remark")
    @JsonProperty("remark")
    private String remark;

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

    /**
     * 旧字段：商品区域（历史兼容保留）
     * 你的移动端/地图现在用的是 areaId/areaName
     * 后续你要改成 endAreaId/endAreaName，但先不删
     */
    @TableField("area_id")
    @JsonProperty("areaId")
    private Integer areaId;

    @TableField("area_name")
    @JsonProperty("areaName")
    private String areaName;

    /* ================= 新增：开始/结束区域（导航/取货/送货） ================= */

    /** 是否从当前位置开始：1=是 0=否 */
    @TableField("start_from_current")
    @JsonProperty("startFromCurrent")
    private Integer startFromCurrent;

    /** 开始区域（当 startFromCurrent=0 时使用） */
    @TableField("start_area_id")
    @JsonProperty("startAreaId")
    private Integer startAreaId;

    @TableField("start_area_name")
    @JsonProperty("startAreaName")
    private String startAreaName;

    /** 结束区域（必须） */
    @TableField("end_area_id")
    @JsonProperty("endAreaId")
    private Integer endAreaId;

    @TableField("end_area_name")
    @JsonProperty("endAreaName")
    private String endAreaName;

    /* ================= Getter / Setter ================= */

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getObjectName() { return objectName; }
    public void setObjectName(String objectName) { this.objectName = objectName; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getTaskDesc() { return taskDesc; }
    public void setTaskDesc(String taskDesc) { this.taskDesc = taskDesc; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    public Integer getAreaId() { return areaId; }
    public void setAreaId(Integer areaId) { this.areaId = areaId; }

    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }

    public Integer getStartFromCurrent() { return startFromCurrent; }
    public void setStartFromCurrent(Integer startFromCurrent) { this.startFromCurrent = startFromCurrent; }

    public Integer getStartAreaId() { return startAreaId; }
    public void setStartAreaId(Integer startAreaId) { this.startAreaId = startAreaId; }

    public String getStartAreaName() { return startAreaName; }
    public void setStartAreaName(String startAreaName) { this.startAreaName = startAreaName; }

    public Integer getEndAreaId() { return endAreaId; }
    public void setEndAreaId(Integer endAreaId) { this.endAreaId = endAreaId; }

    public String getEndAreaName() { return endAreaName; }
    public void setEndAreaName(String endAreaName) { this.endAreaName = endAreaName; }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", objectName='" + objectName + '\'' +
                ", taskType='" + taskType + '\'' +
                ", taskDesc='" + taskDesc + '\'' +
                ", remark='" + remark + '\'' +
                ", status='" + status + '\'' +
                ", assignedTo='" + assignedTo + '\'' +
                ", areaId=" + areaId +
                ", areaName='" + areaName + '\'' +
                ", startFromCurrent=" + startFromCurrent +
                ", startAreaId=" + startAreaId +
                ", startAreaName='" + startAreaName + '\'' +
                ", endAreaId=" + endAreaId +
                ", endAreaName='" + endAreaName + '\'' +
                ", isDeleted=" + isDeleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
