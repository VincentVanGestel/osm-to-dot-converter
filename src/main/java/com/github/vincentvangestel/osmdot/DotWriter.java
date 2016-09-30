package com.github.vincentvangestel.osmdot;

import java.io.File;
import java.io.IOException;

import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.MultiAttributeData;
import com.github.rinde.rinsim.geom.io.DotGraphIO;

/**
 * This class is responsible for writing the final product. It uses the build-in write functionality from {@link DotGraphIO}. 
 */
public class DotWriter {

	protected static void export(Graph<MultiAttributeData> graph, String folder) {
		try {
			DotGraphIO.getMultiAttributeGraphIO().write(graph, folder + File.pathSeparator + "out" + System.currentTimeMillis() + ".dot");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
