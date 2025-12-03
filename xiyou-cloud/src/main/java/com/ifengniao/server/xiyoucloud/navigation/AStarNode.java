package com.ifengniao.server.xiyoucloud.navigation;

/**
 * A*算法中的一个节点
 */
public class AStarNode {
    private int x;
    private int y;
    private double g; // 起点到当前节点的代价
    private double h; // 预估当前节点到终点的代价
    private double f; // g + h
    private AStarNode parent;

    public AStarNode(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
        this.f = this.g + this.h;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
        this.f = this.g + this.h;
    }

    public double getF() {
        return f;
    }

    public AStarNode getParent() {
        return parent;
    }

    public void setParent(AStarNode parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AStarNode)) return false;
        AStarNode other = (AStarNode) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return (x * 31) ^ y;
    }
}
