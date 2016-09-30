package com.github.vincentvangestel.osmdot;

import java.util.List;

import com.github.rinde.rinsim.geom.Connection;
import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.MultiAttributeData;
import com.github.rinde.rinsim.geom.TableGraph;
import com.google.common.base.Optional;

/**
 * This class is responsible for guiding the conversion process.
 * It will call the {@link OsmReader} to load the data,
 * it will convert the loaded data to the new format,
 * it will call the {@link DotWriter} to write the dot file.
 */
public class OsmConverter {

	private Optional<String> output_dir = Optional.absent();
	
	public Graph<MultiAttributeData> convert(String uri) {
		// Read file
		OsmMap map = OsmReader.read(uri);
		List<Connection<MultiAttributeData>> conns = map.getWays();
		
		// Convert to Graph
	    Graph<MultiAttributeData> graph = new TableGraph<>();
	    
	    for(Connection<MultiAttributeData> conn : conns) {
	    	graph.addConnection(conn);
	    }
		
		// Export file
		if(output_dir.isPresent()) {
			DotWriter.export(graph, output_dir.get());
		}
		
		return graph;
	}
	
	/**
	 * Sets the output folder of any newly converted osm file by this {@link OsmConverter}.
	 * @param folder The given folder.
	 */
	public void setOutputDir(String folder) {
		output_dir = Optional.of(folder);
	}

}
