package com.github.vincentvangestel.osmdot;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.Graphs;
import com.github.rinde.rinsim.geom.MultiAttributeData;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.geom.io.DotGraphIO;
import com.github.vincentvangestel.osmdot.pruner.CenterPruner;
import com.github.vincentvangestel.osmdot.pruner.RoundAboutPruner;

public class Test 
{
    public static void main( String[] args ) throws ParserConfigurationException, SAXException, IOException {
    	Graph<MultiAttributeData> graph;
//		Graph<MultiAttributeData> graph = DotGraphIO.getMultiAttributeGraphIO().read("files/maps/brussels-simple.dot");
//		System.out.println("Brussels simple has " + graph.getNumberOfNodes() + " nodes and " + graph.getNumberOfConnections() + " connections");
//		List<Point> extremes = Graphs.getExtremes(graph);
//		System.out.println("Brussels has diagonal " + Point.distance(extremes.get(0), extremes.get(1)));
//		
//		graph = DotGraphIO.getMultiAttributeGraphIO().read("files/maps/leuven-simple.dot");
//		System.out.println("Leuven simple has " + graph.getNumberOfNodes() + " nodes and " + graph.getNumberOfConnections() + " connections");
//		extremes = Graphs.getExtremes(graph);
//		System.out.println("Leuven simple has diagonal " + Point.distance(extremes.get(0), extremes.get(1)));
//		
		graph = DotGraphIO.getMultiAttributeGraphIO().read("files/maps/leuven-large-pruned.dot");
		System.out.println("Leuven large has " + graph.getNumberOfNodes() + " nodes and " + graph.getNumberOfConnections() + " connections");
//		extremes = Graphs.getExtremes(graph);
//		System.out.println("Leuven large has diagonal " + Point.distance(extremes.get(0), extremes.get(1)));
		
//        OsmConverter converter = new OsmConverter();
//        converter.setOutputDir("files/maps/")
//        	.withOutputName("leuven-large-simplified-50-2.dot")
//        	.inputIsDot(true)
//        	.withPruner(new CenterPruner())
//        	.convert("files/maps/leuven-large-simplified-50-1.dot");
        
		graph = DotGraphIO.getMultiAttributeGraphIO().read("files/maps/leuven-large-simplified-50-1.dot");
		System.out.println("Leuven simplified 50-1 has " + graph.getNumberOfNodes() + " nodes and " + graph.getNumberOfConnections() + " connections");

    }
}