package com.ifengniao.server.xiyoucloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifengniao.server.xiyoucloud.entity.Task;
import com.ifengniao.server.xiyoucloud.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/task")
public class TaskController {

    @Autowired
    private TaskService taskService;


    /*-----------------------------------------
     * 任务列表（已改：移除软删除过滤 is_deleted=0）
     -----------------------------------------*/
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

        // ★ 删除软删除过滤逻辑，物理删除后也能正常分页
        query.orderByDesc("create_time");

        Page<Task> resultPage = taskService.page(pageParam, query);

        List<Map<String, Object>> list = resultPage.getRecords().stream().map(task -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", task.getId());
            map.put("objectName", task.getObjectName());
            map.put("taskType", task.getTaskType());
            map.put("status", task.getStatus());
            map.put("createTime", task.getCreateTime());

            // 商品区域
            map.put("areaId", task.getAreaId());
            map.put("areaName", task.getAreaName());

            // 派发员工
            String assignedStr = task.getAssignedTo();
            List<String> assignedList = new ArrayList<>();
            if (assignedStr != null && !assignedStr.isEmpty()) {
                assignedList = Arrays.asList(assignedStr.split(","));
            }
            map.put("assignedTo", assignedList);

            return map;
        }).collect(Collectors.toList());

        Map<String, Object> detail = new HashMap<>();
        detail.put("total", resultPage.getTotal());
        detail.put("list", list);

        return Result.success(detail);
    }


    /*-----------------------------------------
     * 新增任务
     -----------------------------------------*/
    @PostMapping("/add")
    public Result<String> addTask(@RequestBody Task task) {

        task.setStatus("待派发");
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        task.setIsDeleted(0);

        taskService.save(task);

        return Result.success("新增成功");
    }


    /*-----------------------------------------
     * 修改任务
     -----------------------------------------*/
    @PostMapping("/update")
    public Result<String> update(@RequestBody Task task) {

        task.setUpdateTime(new Date());
        taskService.updateById(task);

        return Result.success("修改成功");
    }


    /*-----------------------------------------
     * 派发任务
     -----------------------------------------*/
    @PostMapping("/dispatch")
    public Result<String> dispatchTask(
            @RequestParam("taskId") Long taskId,
            @RequestParam(value = "employees", required = false) List<String> employees) {

        Task task = taskService.getById(taskId);
        if (task == null) return Result.error("任务不存在");

        task.setStatus("已派发");
        task.setUpdateTime(new Date());

        if (employees != null && !employees.isEmpty()) {
            task.setAssignedTo(String.join(",", employees));
        } else {
            task.setAssignedTo("");
        }

        taskService.updateById(task);
        return Result.success("派发成功");
    }


    /*-----------------------------------------
     * 取消任务
     -----------------------------------------*/
    @PostMapping("/cancel")
    public Result<String> cancelTask(@RequestParam("taskId") Long taskId) {

        Task task = taskService.getById(taskId);
        if (task == null) return Result.error("任务不存在");

        task.setStatus("待派发");
        task.setAssignedTo("");
        task.setUpdateTime(new Date());
        taskService.updateById(task);

        return Result.success("取消成功");
    }


    /*-----------------------------------------
     * 删除任务（物理删除）
     -----------------------------------------*/
    @PostMapping("/delete")
    public Result<String> deleteTask(@RequestParam("id") Long id) {

        Task task = taskService.getById(id);
        if (task == null) return Result.error("任务不存在");

        // ★ 改为物理删除：直接 removeById
        taskService.removeById(id);

        return Result.success("删除成功");
    }



    /*-----------------------------------------
     * 统一返回结构
     -----------------------------------------*/
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
            return r;
        }

        public static <T> Result<T> error(String msg) {
            Result<T> r = new Result<>();
            r.code = "5000";
            r.msg = msg;
            return r;
        }

        public T getDetail() { return detail; }
        public String getMsg() { return msg; }
        public String getCode() { return code; }
    }
}
