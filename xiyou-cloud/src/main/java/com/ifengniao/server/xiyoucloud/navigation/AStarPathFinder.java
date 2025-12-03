package com.ifengniao.server.xiyoucloud.navigation;

import java.util.*;

/**
 * 简单的 A* 寻路实现，支持 8 方向移动
 */
public class AStarPathFinder {

    private static final int[][] DIRECTIONS = new int[][]{
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    public List<AStarNode> findPath(GridMap map, int startX, int startY, int endX, int endY) {
        if (!map.isWalkable(startX, startY) || !map.isWalkable(endX, endY)) {
            return Collections.emptyList();
        }

        AStarNode start = new AStarNode(startX, startY);
        AStarNode end = new AStarNode(endX, endY);

        PriorityQueue<AStarNode> openList = new PriorityQueue<>(Comparator.comparingDouble(AStarNode::getF));
        Map<String, AStarNode> openMap = new HashMap<>();
        Map<String, AStarNode> closedMap = new HashMap<>();

        start.setG(0);
        start.setH(heuristic(start, end));
        openList.add(start);
        openMap.put(key(startX, startY), start);

        while (!openList.isEmpty()) {
            AStarNode current = openList.poll();
            openMap.remove(key(current.getX(), current.getY()));
            closedMap.put(key(current.getX(), current.getY()), current);

            if (current.getX() == end.getX() && current.getY() == end.getY()) {
                return buildPath(current);
            }

            for (int[] dir : DIRECTIONS) {
                int nx = current.getX() + dir[0];
                int ny = current.getY() + dir[1];

                if (!map.isWalkable(nx, ny)) {
                    continue;
                }

                String nKey = key(nx, ny);
                if (closedMap.containsKey(nKey)) {
                    continue;
                }

                double stepCost = (dir[0] == 0 || dir[1] == 0) ? 1.0 : Math.sqrt(2.0);
                double tentativeG = current.getG() + stepCost;

                AStarNode neighbor = openMap.get(nKey);
                if (neighbor == null) {
                    neighbor = new AStarNode(nx, ny);
                    neighbor.setParent(current);
                    neighbor.setG(tentativeG);
                    neighbor.setH(heuristic(neighbor, end));
                    openList.add(neighbor);
                    openMap.put(nKey, neighbor);
                } else {
                    if (tentativeG < neighbor.getG()) {
                        neighbor.setParent(current);
                        neighbor.setG(tentativeG);
                        neighbor.setH(heuristic(neighbor, end));
                        // 优先队列中元素的优先级改变，简单做法是重新加入
                        openList.remove(neighbor);
                        openList.add(neighbor);
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    private double heuristic(AStarNode a, AStarNode b) {
        int dx = Math.abs(a.getX() - b.getX());
        int dy = Math.abs(a.getY() - b.getY());
        return Math.sqrt(dx * dx + dy * dy);
    }

    private List<AStarNode> buildPath(AStarNode end) {
        List<AStarNode> path = new ArrayList<>();
        AStarNode current = end;
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    private String key(int x, int y) {
        return x + "_" + y;
    }
}
