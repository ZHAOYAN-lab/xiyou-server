package com.ifengniao.server.xiyoucloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifengniao.server.xiyoucloud.entity.Task;
import com.ifengniao.server.xiyoucloud.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务管理接口 (V4: 修复删除ID转换错误)
 */
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    public TaskController() {
        System.err.println(">>> TaskController [V4-修复版] 已启动！");
    }

    // 1. 列表
    @GetMapping("/list")
    public Result<Map<String, Object>> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String objectName) {

        Page<Task> pageParam = new Page<>(page, size);
        QueryWrapper<Task> query = new QueryWrapper<>();
        if (objectName != null && !objectName.trim().isEmpty()) {
            query.like("object_name", objectName);
        }
        query.orderByDesc("create_time");
        query.eq("is_deleted", 0); 

        Page<Task> resultPage = taskService.page(pageParam, query);

        List<Map<String, Object>> list = resultPage.getRecords().stream().map(task -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", task.getId());
            map.put("objectName", task.getObjectName());
            map.put("taskType", task.getTaskType());
            map.put("status", task.getStatus());
            map.put("createTime", task.getCreateTime());
            
            String assignedStr = task.getAssignedTo();
            List<String> assignedList = new ArrayList<>();
            if (assignedStr != null && !assignedStr.isEmpty()) {
                assignedStr = assignedStr.replace("[", "").replace("]", "").replace("\"", "");
                if (assignedStr.trim().length() > 0) {
                    assignedList = Arrays.asList(assignedStr.split(","));
                }
            }
            map.put("assignedTo", assignedList);
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> detail = new HashMap<>();
        detail.put("total", resultPage.getTotal());
        detail.put("list", list);

        return Result.success(detail);
    }

    // 2. 新增
    @PostMapping("/add")
    public Result<String> addTask(Task task) {
        System.out.println(">>> [新增请求] " + task.toString());
        
        if (task.getObjectName() == null) {
            System.err.println(">>> 警告：接收到的 objectName 为空！");
        }

        task.setStatus("待派发");
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        task.setIsDeleted(0);
        taskService.save(task);
        return Result.success("新增成功");
    }

    // 3. 派发 (使用 @RequestParam 接收基本类型)
    @PostMapping("/dispatch")
    public Result<String> dispatchTask(
            @RequestParam("taskId") Long taskId, 
            @RequestParam(value = "employees", required = false) List<String> employees) {
        
        System.out.println(">>> [派发请求] ID: " + taskId + ", 员工: " + employees);
        
        if (taskId == null) return Result.error("ID不能为空");

        Task task = taskService.getById(taskId);
        if (task == null) return Result.error("任务不存在");

        task.setStatus("已派发");
        task.setUpdateTime(new Date());
        
        if (employees != null && !employees.isEmpty()) {
            String joined = employees.stream().collect(Collectors.joining(","));
            task.setAssignedTo(joined);
        } else {
            task.setAssignedTo("");
        }
        
        taskService.updateById(task);
        return Result.success("派发成功");
    }

    // 4. 取消 (使用 @RequestParam)
    @PostMapping("/cancel")
    public Result<String> cancelTask(@RequestParam("taskId") Long taskId) {
        if (taskId == null) return Result.error("ID不能为空");

        Task task = taskService.getById(taskId);
        if (task == null) return Result.error("任务不存在");

        task.setStatus("待派发");
        task.setAssignedTo("");
        task.setUpdateTime(new Date());
        taskService.updateById(task);
        return Result.success("取消成功");
    }

    // 5. 删除 (使用 @RequestParam)
    @PostMapping("/delete")
    public Result<String> deleteTask(@RequestParam("id") Long id) {
        System.out.println(">>> [删除请求] ID: " + id);
        
        if (id == null) return Result.error("ID不能为空");

        Task task = new Task();
        task.setId(id);
        task.setIsDeleted(1);
        task.setUpdateTime(new Date());
        taskService.removeById(id);
        return Result.success("删除成功");
    }

    // 结果类
    public static class Result<T> {
        private String code;
        private String msg;
        private T detail;
        private String errorDetail;

        public static <T> Result<T> success(T detail) {
            Result<T> r = new Result<>();
            r.code = "0000";
            r.msg = "成功";
            r.detail = detail;
            r.errorDetail = null;
            return r;
        }
        
        public static <T> Result<T> error(String msg) {
            Result<T> r = new Result<>();
            r.code = "5000";
            r.msg = msg;
            return r;
        }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getMsg() { return msg; }
        public void setMsg(String msg) { this.msg = msg; }
        public T getDetail() { return detail; }
        public void setDetail(T detail) { this.detail = detail; }
        public String getErrorDetail() { return errorDetail; }
        public void setErrorDetail(String errorDetail) { this.errorDetail = errorDetail; }
    }
}