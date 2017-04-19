package com.github.vincentvangestel.osmdot.pruner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.github.rinde.rinsim.geom.Connection;
import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.MultiAttributeData;
import com.github.rinde.rinsim.geom.Point;
import com.google.common.base.Optional;

public class RoundAboutPruner implements Pruner {

	private final double size;
	
	public RoundAboutPruner(double size) {
		this.size = size;
	}
	
	/**
	 * Prunes all nodes forming roundabouts. These roundabouts are replaced by a single node, simulating a crossroad. The supplied graph is modified (no copy is taken)
	 * @param g The graph to be pruned
	 * @return The modified graph
	 */
	@Override
	public Graph<MultiAttributeData> prune(Graph<MultiAttributeData> g) {
		Set<Connection<MultiAttributeData>> toRemove = new HashSet<>();
		Map<Point, List<Connection<MultiAttributeData>>> centers = new HashMap<>();
		Map<Point, Set<Point>> fromCenter = new HashMap<>();
		Map<Point, Set<Point>> toCenter = new HashMap<>();
		
		Map<Point,Double> averageSpeed = new HashMap<>();
		
		// For each connection branch forwards
		int iteration = 1;
		for(Connection<MultiAttributeData> conn : g.getConnections()) {
			Logger.getGlobal().info("RoundAboutPruner handling connection " + iteration + " out of " + g.getNumberOfConnections()); iteration++;
			
			Optional<List<Connection<MultiAttributeData>>> path = branchConn(conn, new LinkedList<Connection<MultiAttributeData>>(), 0, g);
			
			// If after not a lot of distance, branch PATH comes back to original connection
			if(!path.isPresent()) { continue; }
			else {
				// Then PATH is a cycle (roundabout)
				
				Set<Point> pathPointsSet = new HashSet<>();
				Set<Point> fromPathCenter = new HashSet<>();
				Set<Point> toPathCenter = new HashSet<>();
				double averagePointSpeed = 0;
				
				// Create a list of all points present in the cycle
				boolean isContainedInToRemove = false;
				for(Connection<MultiAttributeData> pathConn : path.get()) {
					pathPointsSet.add(pathConn.from()); // Path forms a cycle, so only from is sufficient
					averagePointSpeed += pathConn.data().get().getMaxSpeed().get();
					if(toRemove.contains(pathConn)) { isContainedInToRemove = true; }
				}
				// Average speed, this shouldn't be necessary as maximum speed
				// shouldn't change within the cycle, but we do it anyway
				averagePointSpeed = averagePointSpeed / path.get().size();
				
				// If Cycle is already handled:
				//  - OVERWRITE IF LARGER
				//  - DISCARD IF SMALLER/EQUAL
				if(isContainedInToRemove) {
					if(toRemove.containsAll(path.get())) {
						continue; // Discard smaller/equal
					} else {
						// Overwrite
						Point toRemoveCenter = null;
						for(Point center : centers.keySet()) {
							if(path.get().containsAll(centers.get(center))) {
								toRemoveCenter = center;
								break;
							}
						}
						centers.remove(toRemoveCenter);
						fromCenter.remove(toRemoveCenter);
						toCenter.remove(toRemoveCenter);
					}
				}
				
				for(Connection<MultiAttributeData> pathConn : path.get()) {
					// For each connection leaving/entering PATH (not part of PATH), make a new connection to new POINT
					fromPathCenter.addAll(g.getOutgoingConnections(pathConn.from()));
					fromPathCenter.removeAll(pathPointsSet); // Filter
					
					toPathCenter.addAll(g.getIncomingConnections(pathConn.from()));
					toPathCenter.removeAll(pathPointsSet); // Filter
				}
				
				// Create a new point at the center of the cycle
				Point center = Point.centroid(pathPointsSet);
				
				/**************************************************************************************************/
				
				// EDGE CASE - check for touching cycles, fromCenter and toCenter should be updated with new center
				// AND current fromPathCenter and toPathCenter should be updated with old center
				Map<Point,Point> toBeUpdated = new HashMap<>();
				for(Point outgoing : fromPathCenter) {
					for(Point oldCenter : centers.keySet()) {
						for(Connection<MultiAttributeData> centerConn : centers.get(oldCenter)) {
							if(centerConn.to().equals(outgoing)) {
								toBeUpdated.put(outgoing, oldCenter);
								break;
							}
						}
					}
				}
				for(Point outgoing : toBeUpdated.keySet()) {
					fromPathCenter.remove(outgoing);
					fromPathCenter.add(toBeUpdated.get(outgoing));
				}
				toBeUpdated = new HashMap<>();
				for(Point incoming : toPathCenter) {
					for(Point oldCenter : centers.keySet()) {
						for(Connection<MultiAttributeData> centerConn : centers.get(oldCenter)) {
							if(centerConn.from().equals(incoming)) {
								toBeUpdated.put(incoming, oldCenter);
								break;
							}
						}
					}
				}
				for(Point incoming : toBeUpdated.keySet()) {
					toPathCenter.remove(incoming);
					toPathCenter.add(toBeUpdated.get(incoming));
				}
				
				toBeUpdated = new HashMap<>();
				for(Point oldCenter : centers.keySet()) {
					for(Point outgoing : fromCenter.get(oldCenter)) {
						if(pathPointsSet.contains(outgoing)) {
							toBeUpdated.put(oldCenter, outgoing);
						}
					}
				}
				for(Point oldCenter : toBeUpdated.keySet()) {
					fromCenter.get(oldCenter).remove(toBeUpdated.get(oldCenter));
					fromCenter.get(oldCenter).add(center);
				}
				toBeUpdated = new HashMap<>();
				for(Point oldCenter : centers.keySet()) {
					for(Point incoming : toCenter.get(oldCenter)) {
						if(pathPointsSet.contains(incoming)) {
							toBeUpdated.put(oldCenter, incoming);
						}
					}
				}
				for(Point oldCenter : toBeUpdated.keySet()) {
					toCenter.get(oldCenter).remove(toBeUpdated.get(oldCenter));
					toCenter.get(oldCenter).add(center);
				}
				
				
				/**************************************************************************************************/
			
				// REMOVE PATH
				averageSpeed.put(center, averagePointSpeed);
				toRemove.addAll(path.get());
				centers.put(center, path.get());
				fromCenter.put(center, fromPathCenter);
				toCenter.put(center, toPathCenter);
			}
		}
		
		// MAKE CHANGES for each roundabout
		for(Connection<MultiAttributeData> conn : toRemove) {
			g.removeNode(conn.from());
			//g.removeConnection(conn.from(), conn.to());
		}
		
		for(Point center : centers.keySet()) {
			for(Point to : fromCenter.get(center)) {
				try {
					g.addConnection(center, to, MultiAttributeData.builder()
							.setMaxSpeed(averageSpeed.get(center))
							.addAttribute("ts", averageSpeed.get(center))
							.setLength(Point.distance(center, to))
							.build());
				} catch(IllegalArgumentException e) {}
			}
			for(Point from : toCenter.get(center)) {
				try {
					g.addConnection(from, center, MultiAttributeData.builder()
							.setMaxSpeed(averageSpeed.get(center))
							.addAttribute("ts", averageSpeed.get(center))
							.setLength(Point.distance(from, center))
							.build());
				} catch(IllegalArgumentException e) {}
			}
		}
		
		Logger.getGlobal().info("RoundAboutPruner pruned " + centers.size() + " roundabouts from the graph");
		return g;
	}
	
	private Optional<List<Connection<MultiAttributeData>>> branchConn(Connection<MultiAttributeData> conn, List<Connection<MultiAttributeData>> path, double pathDistance, Graph<MultiAttributeData> g) {
		// A to be pruned roundabout is shorter than size
		if(pathDistance + conn.getLength() > size) { return Optional.absent(); }
		
		// Add conn to path
		path.add(conn);
		
		// Cycle detected
		if(conn.to().equals(path.get(0).from())) { return Optional.of(path); }
		
		// Branch
		for(Point branch : g.getOutgoingConnections(conn.to())) {
			if(branch.equals(conn.from())) { continue; } // A bi-directional road is not a cycle!
			Optional<List<Connection<MultiAttributeData>>> branched = branchConn(g.getConnection(conn.to(), branch), path, pathDistance + conn.getLength(), g);
			if(branched.isPresent()) { return branched; }
		}
		
		return Optional.absent();
	}

}
