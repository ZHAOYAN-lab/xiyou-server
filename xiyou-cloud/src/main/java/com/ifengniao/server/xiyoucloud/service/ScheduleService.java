package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.server.xiyoucloud.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ScheduleService {

    @Autowired
    private LocalServerService localServerService;
    @Autowired
    private BaseStationService baseStationService;
    @Autowired
    private BeaconService beaconService;

    //@Scheduled(cron = "0 * * * * ?") // 每定点执行一次
    @Scheduled(fixedRate = 5 * 1000) // 每T执行一次
    public void schedule() {
        try {
            localServerService.updateOnlineStatus(Constants.OFFLINE_TIME_MILLIS);
        } catch (Throwable e) {
            log.error("本地服务离线检测失败：", e);
        }
        try {
            baseStationService.updateOnlineStatus(Constants.OFFLINE_TIME_MILLIS);
        } catch (Throwable e) {
            log.error("基站离线检测失败：", e);
        }
        try {
            beaconService.updateOnlineStatus(Constants.OFFLINE_TIME_MILLIS);
        } catch (Throwable e) {
            log.error("信标离线检测失败：", e);
        }
    }

    /**
     * 每周二00:00定时生成上一周周报任务
     */
    @Scheduled(cron = "0 0 0 ? * TUE") // 每周二00:00执行一次
    public void WeeklySchedule() {

    }

    /**
     * 每天日报提醒前15秒做提前缓存
     * 缓存校验三次
     * 2023-04-27 17:59:45
     * 2023-04-27 17:59:50
     * 2023-04-27 17:59:55
     * 2023-04-27 19:59:45
     * 2023-04-27 19:59:50
     * 2023-04-27 19:59:55
     * 2023-04-28 08:59:45
     * 2023-04-28 08:59:50
     * 2023-04-28 08:59:55
     */
    @Scheduled(cron = "45,50,55 59 8,17,19 * * ?")
    public void userListDataCache() {

    }

}
