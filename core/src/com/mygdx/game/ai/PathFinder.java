package com.mygdx.game.ai;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Evgheniy on 5/15/2017.
 */

public class PathFinder {

    private final Heuristic<MyNode> heuristic;
    private final DefaultIndexGraphPath graph;
    private IndexedAStarPathFinder<MyNode> indexedAStarPathFinder;
    private final AStarMap map;

    private static final int[][] NEIGHBORHOOD = new int[][]{
            new int[]{-1, 0},
            new int[]{0, -1},
            new int[]{0, 1},
            new int[]{1, 0},
    };


    public PathFinder(AStarMap map) {
        this.map = map;
        heuristic = new ManhattanDistanceHeuristic();
        graph = new DefaultIndexGraphPath();
        indexedAStarPathFinder = new IndexedAStarPathFinder<MyNode>(graph);
        findAllowedNodes(map);
    }


    public MyNode findNextNode(Vector2 source, Vector2 target) {
        int sourceX = MathUtils.floor(source.x);
        int sourceY = MathUtils.floor(source.y);

        int targetX = MathUtils.floor(target.x);
        int targetY = MathUtils.floor(target.y);

        if (map == null
                || crossedMapXBounds(sourceX)
                || crossedMapYBounds(sourceY)
                || crossedMapXBounds(targetX)
                || crossedMapYBounds(targetY))
            return null;

        MyNode sourceNode = map.getNodeAt(sourceX, sourceY);
        MyNode targetNode = map.getNodeAt(targetX, targetY);
        graph.clear();
        indexedAStarPathFinder.searchNodePath(sourceNode, targetNode, heuristic, graph);
        return graph.getNodeCount() == 0 ? null : graph.get(0);
    }

    private boolean crossedMapXBounds(int xAxis) {
        return xAxis < 0 || xAxis >= map.getWidth();
    }

    private boolean crossedMapYBounds(int yAxis) {
        return yAxis < 0 || yAxis >= map.getHeight();
    }

    private void findAllowedNodes(AStarMap map) {
        final int mapHeight = map.getHeight();
        final int mapWidth = map.getWidth();


        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                MyNode node = map.getNodeAt(x, y);
                if (node.isWall()) continue;
                addNodeNeighborHood(node);
            }
        }
    }

    private void addNodeNeighborHood(MyNode node) {
        for (int offset = 0; offset < NEIGHBORHOOD.length; offset++) {
            int neighborX = node.x + NEIGHBORHOOD[offset][0];
            int neighborY = node.y + NEIGHBORHOOD[offset][1];
            boolean notCrossedXBounds = neighborX >= 0 && neighborX < map.getWidth();
            boolean notCrossedYBounds = neighborY >= 0 && neighborY < map.getHeight();

            if (notCrossedXBounds && notCrossedYBounds)
                node.addNeighbour(new MyNode(neighborX, neighborY));
        }
    }


}
