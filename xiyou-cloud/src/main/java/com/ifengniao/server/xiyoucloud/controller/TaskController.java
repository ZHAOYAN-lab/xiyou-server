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

    /* 任务列表 */
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

        Page<Task> resultPage = taskService.page(pageParam, query);

        List<Map<String, Object>> list = resultPage.getRecords().stream().map(task -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", task.getId());
            map.put("objectName", task.getObjectName());
            map.put("taskType", task.getTaskType());
            map.put("taskDesc", task.getTaskDesc()); // ★ 关键
            map.put("status", task.getStatus());
            map.put("createTime", task.getCreateTime());
            map.put("areaId", task.getAreaId());
            map.put("areaName", task.getAreaName());

            List<String> assignedList = new ArrayList<>();
            if (task.getAssignedTo() != null && !task.getAssignedTo().isEmpty()) {
                assignedList = Arrays.asList(task.getAssignedTo().split(","));
            }
            map.put("assignedTo", assignedList);

            return map;
        }).collect(Collectors.toList());

        Map<String, Object> detail = new HashMap<>();
        detail.put("total", resultPage.getTotal());
        detail.put("list", list);

        return Result.success(detail);
    }

    /* 新增任务 */
    @PostMapping("/add")
    public Result<String> addTask(@RequestBody Task task) {

        task.setStatus("待派发");
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        task.setIsDeleted(0);

        taskService.save(task);

        return Result.success("新增成功");
    }

    /* 派发 */
    @PostMapping("/dispatch")
    public Result<String> dispatchTask(
            @RequestParam("taskId") Long taskId,
            @RequestParam(value = "employees", required = false) List<String> employees) {

        Task task = taskService.getById(taskId);
        if (task == null) return Result.error("任务不存在");

        task.setStatus("已派发");
        task.setAssignedTo(
                employees != null && !employees.isEmpty()
                        ? String.join(",", employees)
                        : ""
        );
        task.setUpdateTime(new Date());

        taskService.updateById(task);
        return Result.success("派发成功");
    }

    /* 取消 */
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

    /* 删除（强校验） */
    @PostMapping("/delete")
    public Result<String> deleteTask(@RequestParam("id") Long id) {
        try {
            taskService.deleteTaskWithStatusCheck(id);
            return Result.success("删除成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /* 统一返回 */
    public static class Result<T> {
        private String code;
        private String msg;
        private T detail;

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

        public String getCode() { return code; }
        public String getMsg() { return msg; }
        public T getDetail() { return detail; }
    }
}
