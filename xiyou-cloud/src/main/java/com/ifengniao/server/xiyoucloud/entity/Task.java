package com.ifengniao.server.xiyoucloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * 任务实体类 (手动 Getter/Setter + JsonProperty 强绑定版)
 */
@TableName("xy_task")
public class Task {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 强制绑定前端传来的 "objectName"
    @TableField("object_name")
    @JsonProperty("objectName")
    private String objectName;

    // 强制绑定前端传来的 "taskType"
    @TableField("task_type")
    @JsonProperty("taskType")
    private String taskType;

    @TableField("status")
    @JsonProperty("status")
    private String status;

    @TableField("assigned_to")
    @JsonProperty("assignedTo")
    private String assignedTo; 

    @TableField("create_time")
    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField("update_time")
    @JsonProperty("updateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    
    @TableField("is_deleted")
    @JsonProperty("isDeleted")
    private Integer isDeleted;

    // ==========================================
    // 手动 Getter / Setter (必须要有这些，否则后端接收全是 null)
    // ==========================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getObjectName() { return objectName; }
    public void setObjectName(String objectName) { this.objectName = objectName; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

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

    // 打印日志用，方便在控制台看接收到的数据
    @Override
    public String toString() {
        return "Task{id=" + id + ", objectName='" + objectName + "', taskType='" + taskType + "', status='" + status + "'}";
    }
}