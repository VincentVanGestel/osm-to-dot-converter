package com.github.vincentvangestel.osmdot;

import java.io.File;
import java.io.IOException;

import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.MultiAttributeData;
import com.github.rinde.rinsim.geom.io.DotGraphIO;
import com.google.common.base.Optional;

/**
 * This class is responsible for writing the final product. It uses the build-in write functionality from {@link DotGraphIO}. 
 */
public class DotWriter {

	protected static void export(Graph<MultiAttributeData> graph, String folder, Optional<String> op_name) {
		String name = "out" + System.currentTimeMillis() + ".dot";
		if(op_name.isPresent()) { name = op_name.get(); }
		try {
			DotGraphIO.getMultiAttributeGraphIO().write(graph, folder + File.separator + name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
