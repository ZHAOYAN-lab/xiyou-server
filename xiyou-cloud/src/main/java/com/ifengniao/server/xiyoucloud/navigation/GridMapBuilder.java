package com.ifengniao.server.xiyoucloud.navigation;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.List;

/**
 * 根据起点、终点、障碍物 polygon 生成网格地图
 */
public class GridMapBuilder {

    private final double cellSize; // cm
    private final double margin = 200.0; // 在起终点周围各扩展 200cm

    private final GeometryFactory geometryFactory = new GeometryFactory();

    public GridMapBuilder(double cellSize) {
        this.cellSize = cellSize;
    }

    public GridMap buildGrid(double startX, double startY,
                             double endX, double endY,
                             List<Polygon> obstacles) {

        double minX = Math.min(startX, endX) - margin;
        double maxX = Math.max(startX, endX) + margin;
        double minY = Math.min(startY, endY) - margin;
        double maxY = Math.max(startY, endY) + margin;

        int width = (int) Math.ceil((maxX - minX) / cellSize);
        int height = (int) Math.ceil((maxY - minY) / cellSize);

        if (width <= 0) width = 1;
        if (height <= 0) height = 1;

        boolean[][] walkable = new boolean[width][height];

        // 初始化为可走，再用障碍物覆盖
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double wx = minX + (x + 0.5) * cellSize;
                double wy = minY + (y + 0.5) * cellSize;

                Point p = geometryFactory.createPoint(new Coordinate(wx, wy));
                boolean blocked = false;
                if (obstacles != null) {
                    for (Polygon poly : obstacles) {
                        if (poly.contains(p)) {
                            blocked = true;
                            break;
                        }
                    }
                }
                walkable[x][y] = !blocked;
            }
        }

        // 左下角作为原点
        return new GridMap(walkable, minX, minY, cellSize);
    }
}
