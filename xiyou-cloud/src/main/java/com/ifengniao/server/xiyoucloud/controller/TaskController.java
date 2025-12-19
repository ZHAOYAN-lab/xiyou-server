package com.ifengniao.server.xiyoucloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifengniao.server.xiyoucloud.entity.Task;
import com.ifengniao.server.xiyoucloud.service.TaskService;
import com.ifengniao.server.xiyoucloud.service.AuthorizationService;
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

    @Autowired
    private AuthorizationService authorizationService;

    /* ================= 获取当前登录用户名（统一 user_name） ================= */
    private String getCurrentUserName() {
        try {
            var loginUser = authorizationService.getCurrentLogin();
            return loginUser == null ? null : loginUser.getUserName();
        } catch (Exception e) {
            log.warn("获取当前登录用户失败", e);
            return null;
        }
    }

    /* ================= 管理端：任务列表 ================= */
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

        query.eq("is_deleted", 0);
        query.orderByDesc("create_time");

        Page<Task> resultPage = taskService.page(pageParam, query);

        List<Map<String, Object>> list = resultPage.getRecords().stream().map(task -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", task.getId());
            map.put("objectName", task.getObjectName());
            map.put("taskType", task.getTaskType());
            map.put("taskDesc", task.getTaskDesc());
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

    /* ================= H5：worker 我的任务 ================= */
    @GetMapping("/my")
    public Result<List<Map<String, Object>>> myTasks() {

        String currentUser = getCurrentUserName();
        if (currentUser == null) {
            return Result.error("未登录");
        }

        List<Task> tasks = taskService.listMyTasks(currentUser);

        List<Map<String, Object>> list = tasks.stream().map(task -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", task.getId());
            map.put("objectName", task.getObjectName());
            map.put("taskType", task.getTaskType());
            map.put("taskDesc", task.getTaskDesc());
            map.put("status", task.getStatus());
            map.put("areaId", task.getAreaId());
            map.put("areaName", task.getAreaName());
            return map;
        }).collect(Collectors.toList());

        return Result.success(list);
    }

    /* ================= 新增任务（admin） ================= */
    @PostMapping("/add")
    public Result<String> addTask(@RequestBody Task task) {

        task.setId(null);
        task.setStatus("待派发");
        task.setIsDeleted(0);
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());

        taskService.save(task);
        return Result.success("新增成功");
    }

    /* ================= 派发（admin） ================= */
    @PostMapping("/dispatch")
    public Result<String> dispatch(
            @RequestParam("taskId") Integer taskId,
            @RequestParam("employees") List<String> employees) {

        Task task = taskService.getById(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        task.setStatus("已派发");
        task.setAssignedTo(String.join(",", employees));
        task.setUpdateTime(new Date());

        taskService.updateById(task);
        return Result.success("派发成功");
    }

    /* ================= 取消派发（admin） ★★★ 新增 ★★★ ================= */
    @PostMapping("/cancel")
    public Result<String> cancel(@RequestParam("taskId") Integer taskId) {

        Task task = taskService.getById(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        // 只有「已派发」允许取消
        if (!"已派发".equals(task.getStatus())) {
            return Result.error("当前任务状态不可取消");
        }

        task.setStatus("待派发");
        task.setAssignedTo(null);
        task.setUpdateTime(new Date());

        taskService.updateById(task);
        return Result.success("已取消派发");
    }

    /* ================= H5：开始执行 ================= */
    @PostMapping("/start")
    public Result<String> start(@RequestParam("taskId") Integer taskId) {

        String currentUser = getCurrentUserName();
        Task task = taskService.getById(taskId);

        if (task == null) {
            return Result.error("任务不存在");
        }

        List<String> assignees = Arrays.asList(task.getAssignedTo().split(","));
        if (!assignees.contains(currentUser)) {
            return Result.error("无权执行该任务");
        }

        if (!"已派发".equals(task.getStatus())) {
            return Result.error("当前任务状态不可开始执行");
        }

        task.setStatus("执行中");
        task.setUpdateTime(new Date());
        taskService.updateById(task);

        return Result.success("任务已进入执行中");
    }

    /* ================= H5：已到达 ================= */
    @PostMapping("/arrived")
    public Result<String> arrived(@RequestParam("taskId") Integer taskId) {

        String currentUser = getCurrentUserName();
        Task task = taskService.getById(taskId);

        if (task == null) {
            return Result.error("任务不存在");
        }

        List<String> assignees = Arrays.asList(task.getAssignedTo().split(","));
        if (!assignees.contains(currentUser)) {
            return Result.error("无权操作该任务");
        }

        if (!"执行中".equals(task.getStatus())) {
            return Result.error("当前任务状态不可完成");
        }

        task.setStatus("已完成");
        task.setUpdateTime(new Date());
        taskService.updateById(task);

        return Result.success("任务已完成");
    }

    /* ================= 删除（admin） ================= */
    @PostMapping("/delete")
    public Result<String> delete(@RequestParam("id") Integer id) {
        try {
            taskService.deleteTaskWithStatusCheck(id);
            return Result.success("删除成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /* ================= 统一返回 ================= */
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
