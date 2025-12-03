package com.ifengniao.server.xiyoucloud.service.impl;

import com.ifengniao.server.xiyoucloud.mapper.NavigationMapper;
import com.ifengniao.server.xiyoucloud.service.NavigationService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NavigationServiceImpl implements NavigationService {

    private final NavigationMapper navigationMapper;

    @Override
    public List<Map<String, Object>> routeToProductArea(
            Long mapId,
            Double startX,
            Double startY,
            Long areaId
    ) {

        Map<String, Object> area = navigationMapper.getProductAreaGeom(areaId);
        if (area == null || area.get("wkt") == null) {
            throw new RuntimeException("未找到商品区域 areaId=" + areaId);
        }

        try {
            Polygon polygon = (Polygon) new WKTReader().read(area.get("wkt").toString());

            Coordinate start = new Coordinate(startX, startY);
            Coordinate nearest = getNearestPointOnPolygonEdge(polygon, start);

            // TODO: 使用真实 A*，现在用 mock
            List<Map<String, Object>> path = new ArrayList<>();
            path.add(Map.of("x", startX, "y", startY));
            path.add(Map.of("x", nearest.x, "y", nearest.y));
            return path;

        } catch (Exception e) {
            throw new RuntimeException("导航失败: " + e.getMessage(), e);
        }
    }

    private Coordinate getNearestPointOnPolygonEdge(Polygon polygon, Coordinate start) {
        Coordinate[] coords = polygon.getExteriorRing().getCoordinates();

        Coordinate result = null;
        double min = Double.MAX_VALUE;

        for (int i = 0; i < coords.length - 1; i++) {
            Coordinate a = coords[i];
            Coordinate b = coords[i + 1];

            Coordinate p = getClosestPointOnSegment(a, b, start);
            double d = p.distance(start);

            if (d < min) {
                min = d;
                result = p;
            }
        }
        return result;
    }

    private Coordinate getClosestPointOnSegment(Coordinate a, Coordinate b, Coordinate p) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;

        if (dx == 0 && dy == 0) return a;

        double t = ((p.x - a.x) * dx + (p.y - a.y) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));

        return new Coordinate(a.x + t * dx, a.y + t * dy);
    }
}
