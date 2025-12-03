package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.server.xiyoucloud.service.NavigationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/navigation")
@RequiredArgsConstructor
public class NavigationController {

    private final NavigationService navigationService;

    @PostMapping("/routeToProductArea")
    public Object route(@RequestBody RouteReq req) {

        List<Map<String, Object>> path = navigationService.routeToProductArea(
                req.getMapId(),
                req.getStartX(),
                req.getStartY(),
                req.getAreaId()
        );

        return Map.of("code", "0000", "msg", "success", "data", path);
    }

    @Data
    public static class RouteReq {
        private Long mapId;
        private Double startX;
        private Double startY;
        private Long areaId;
    }
}
