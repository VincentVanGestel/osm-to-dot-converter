package com.github.vincentvangestel.osmdot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.rinde.rinsim.geom.MultiAttributeData;
import com.github.rinde.rinsim.geom.Point;

/**
 * This class is responsible for loading the osm file and breaking it down into two parts:
 * 1. Mapping of identifier to point data
 * 2. Annotated connections between points
 */
public class OsmReader {

	protected static OsmMap read(String uri) {
		OsmMap map = new OsmMap();
		
        // Parse XML to Dom
    	DocumentBuilder parser;
        Document dom;
		try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			dom = parser.parse("files/maps/Leuven.osm");
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			return map;
		}
		
		// Define node points
        List<Node> nodes = getNodes("node", dom.getChildNodes());
        Map<Integer, Point> pointMapping = new HashMap<>();
        
        for(Node node : nodes) {
        	pointMapping.put(Integer.parseInt(getNodeAttr("id", node )),
        			new Point(Double.parseDouble(getNodeAttr("lon", node)), 
        					Double.parseDouble(getNodeAttr("lat", node))));
        }
        
        // Construct ways
        List<Node> ways = getNodes("way", dom.getChildNodes());
        
        // Add connection to map
        Optional<MultiAttributeData> empty = Optional.empty();
		for(Node way: ways) {
			List<Node> points = getNodes("nd", way.getChildNodes());
			for(int i = 0; i < points.size() - 1; i++) {
				map.addConnection(pointMapping.get(getNodeAttr("ref",points.get(i))),
						pointMapping.get(getNodeAttr("ref",points.get(i + 1))),
						//TODO attribute reading
						empty);
			}
		}
        
		return map;
	}
	
	protected static List<Node> getNodes(String tagName, NodeList nodes) {
        List<Node> suitableNodes = new ArrayList<>();
    	for ( int x = 0; x < nodes.getLength(); x++ ) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                suitableNodes.add(node);
            }
        }
        return suitableNodes;
    }
     
    protected static String getNodeValue(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int x = 0; x < childNodes.getLength(); x++ ) {
            Node data = childNodes.item(x);
            if (data.getNodeType() == Node.TEXT_NODE)
                return data.getNodeValue();
        }
        return "";
    }
     
    protected static String getNodeValue(String tagName, NodeList nodes ) {
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++ ) {
                    Node data = childNodes.item(y);
                    if ( data.getNodeType() == Node.TEXT_NODE )
                        return data.getNodeValue();
                }
            }
        }
        return "";
    }
     
    protected static String getNodeAttr(String attrName, Node node ) {
        NamedNodeMap attrs = node.getAttributes();
        for (int y = 0; y < attrs.getLength(); y++ ) {
            Node attr = attrs.item(y);
            if (attr.getNodeName().equalsIgnoreCase(attrName)) {
                return attr.getNodeValue();
            }
        }
        return "";
    }
     
    protected static String getNodeAttr(String tagName, String attrName, NodeList nodes ) {
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++ ) {
                    Node data = childNodes.item(y);
                    if ( data.getNodeType() == Node.ATTRIBUTE_NODE ) {
                        if ( data.getNodeName().equalsIgnoreCase(attrName) )
                            return data.getNodeValue();
                    }
                }
            }
        }
        return "";
    }
}
