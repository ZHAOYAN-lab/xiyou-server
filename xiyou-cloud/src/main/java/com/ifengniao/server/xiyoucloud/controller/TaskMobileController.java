package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.server.xiyoucloud.dto.MobileTaskDto;
import com.ifengniao.server.xiyoucloud.service.TaskMobileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/task/mobile")
public class TaskMobileController {

    @Autowired
    private TaskMobileService taskMobileService;

    /* 使用 TaskController.Result 作为返回值类型 */
    @GetMapping("/current")
    public TaskController.Result<MobileTaskDto> getMyTask(@RequestParam String objectName) {

        MobileTaskDto dto = taskMobileService.getMyTask(objectName);

        if (dto == null) {
            return TaskController.Result.error("当前没有派发任务");
        }

        return TaskController.Result.success(dto);
    }
}
