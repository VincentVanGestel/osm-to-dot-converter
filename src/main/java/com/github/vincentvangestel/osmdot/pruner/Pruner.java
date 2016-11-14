package com.github.vincentvangestel.osmdot.pruner;

import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.MultiAttributeData;

public interface Pruner {

	public Graph<MultiAttributeData> prune(Graph<MultiAttributeData> g); 

}
