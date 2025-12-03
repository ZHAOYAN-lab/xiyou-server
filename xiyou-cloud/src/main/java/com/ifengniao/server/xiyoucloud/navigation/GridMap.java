package com.ifengniao.server.xiyoucloud.navigation;

/**
 * 网格地图：记录每个格子是否可通行，以及世界坐标与网格坐标的转换
 */
public class GridMap {

    private final boolean[][] walkable;
    private final double originX;    // 左下角世界坐标X（cm）
    private final double originY;    // 左下角世界坐标Y（cm）
    private final double cellSize;   // 网格大小（cm）

    public GridMap(boolean[][] walkable, double originX, double originY, double cellSize) {
        this.walkable = walkable;
        this.originX = originX;
        this.originY = originY;
        this.cellSize = cellSize;
    }

    public int getWidth() {
        return walkable.length;
    }

    public int getHeight() {
        return walkable[0].length;
    }

    public boolean isWalkable(int x, int y) {
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
            return false;
        }
        return walkable[x][y];
    }

    public double gridToWorldX(int gx) {
        return originX + (gx + 0.5) * cellSize;
    }

    public double gridToWorldY(int gy) {
        return originY + (gy + 0.5) * cellSize;
    }

    public int worldToGridX(double wx) {
        return (int) Math.floor((wx - originX) / cellSize);
    }

    public int worldToGridY(double wy) {
        return (int) Math.floor((wy - originY) / cellSize);
    }
}
