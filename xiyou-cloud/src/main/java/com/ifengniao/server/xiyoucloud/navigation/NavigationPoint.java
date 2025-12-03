package com.ifengniao.server.xiyoucloud.navigation;

/**
 * 导航路径上的一个点（单位：cm）
 */
public class NavigationPoint {
    private double x;
    private double y;

    public NavigationPoint() {
    }

    public NavigationPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
