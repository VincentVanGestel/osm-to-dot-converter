package com.github.vincentvangestel.osmdot.pruner;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.math3.random.MersenneTwister;

import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.Graphs;
import com.github.rinde.rinsim.geom.MultiAttributeData;
import com.github.rinde.rinsim.geom.PathNotFoundException;
import com.github.rinde.rinsim.geom.Point;
import com.google.common.collect.ImmutableList;

public class CenterPruner implements Pruner {

	/**
	 * Prunes all nodes, unreachable from the center most point from the graph. The supplied graph is modified (no copy is taken)
	 * @param g The graph to be pruned
	 * @return The modified graph
	 */
	@Override
	public Graph<MultiAttributeData> prune(Graph<MultiAttributeData> g) {
		Point center = getCenterMostPoint(g);
		
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

	
	  /**
	   * Returns the point closest to the exact center of the area spanned by the
	   * graph.
	   * @param graph The graph.
	   * @return The point of the graph closest to the exact center of the area
	   *         spanned by the graph.
	   */
	  private Point getCenterMostPoint(Graph<?> graph) {
	    final ImmutableList<Point> extremes = Graphs.getExtremes(graph);
	    final Point exactCenter =
	      Point.divide(Point.add(extremes.get(0), extremes.get(1)), 2d);
	    Point center = graph.getRandomNode(new MersenneTwister());
	    double distance = Point.distance(center, exactCenter);

	    for (final Point p : graph.getNodes()) {
	      final double pDistance = Point.distance(p, exactCenter);
	      if (pDistance < distance) {
	        center = p;
	        distance = pDistance;
	      }

	      if (center.equals(exactCenter)) {
	        return center;
	      }
	    }

	    return center;
	  }

}
