package com.github.vincentvangestel.osmdot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.rinde.rinsim.geom.ConnectionData;
import com.github.rinde.rinsim.geom.Point;
import com.google.common.collect.Lists;

public class OsmMap {

	private List<Point> nodes;
	
	private Map<Point,List<Point>> ways;
	
	private List<ConnectionData> way_data;
	
	/**
	 * Contruct a new empty osm map.
	 */
	protected OsmMap() {
		nodes = new ArrayList<>();
		ways = new HashMap<>();
		way_data = new ArrayList<>();
	}
	
	/**
	 * Construct a new osm map with predefined nodes, ways and connection data.
	 * @param nodes Predefined points of the map.
	 * @param ways Predefined connections of the map.
	 * @param way_data Predefinec meta-data of the connections on the map.
	 */
	protected OsmMap(List<Point> nodes, Map<Point,List<Point>> ways, List<ConnectionData> way_data) {
		this.nodes = nodes;
		this.ways = ways;
		this.way_data = way_data;
	}
	
	//TODO finalise methods
	//TODO make defensive
	
	protected void addNode(Point node) {
		nodes.add(node);
	}
	
	protected void addWay(Point node0, Point node1, Optional<ConnectionData> data) {
		addWay(node0, Lists.newArrayList(node1), data);
	}
	
	protected void addWay(Point node0, List<Point> nodexs, Optional<ConnectionData> data) {
		if(ways.containsKey(node0)) {
			List<Point> toPoints = ways.get(node0);
			toPoints.addAll(nodexs);
		} else {
			ways.put(node0, nodexs);
		}
		
		if(data.isPresent()) {
			way_data.add(data.get());
		}
	}
}