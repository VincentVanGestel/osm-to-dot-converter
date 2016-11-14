package com.github.vincentvangestel.osmdot.pruner;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.Graphs;
import com.github.rinde.rinsim.geom.MultiAttributeData;
import com.github.rinde.rinsim.geom.PathNotFoundException;
import com.github.rinde.rinsim.geom.Point;

public class CenterPruner implements Pruner {

	/**
	 * Prunes all nodes, unreachable from the center most point from the graph. The supplied graph is modified (no copy is taken)
	 * @param g The graph to be pruned
	 * @return The modified graph
	 */
	@Override
	public Graph<MultiAttributeData> prune(Graph<MultiAttributeData> g) {
		Point center = Graphs.getCenterMostPoint(g);
		
		List<Point> toRemove = new ArrayList<>();
		
		for(Point node : g.getNodes()) {
			try {
				Graphs.shortestPathEuclideanDistance(g, center, node);
				Graphs.shortestPathEuclideanDistance(g, node, center);
			} catch(PathNotFoundException e) {
				// Unreachable
				toRemove.add(node);
			}
		}
		
		for(Point node : toRemove) {
			g.removeNode(node);
		}
		
		Logger.getGlobal().info("CenterPruner pruned " + toRemove.size() + " nodes from the graph");
		return g;
	}


}
