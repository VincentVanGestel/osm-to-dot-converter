package com.github.vincentvangestel.osmdot;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Test 
{
    public static void main( String[] args ) throws ParserConfigurationException, SAXException, IOException {
        OsmConverter converter = new OsmConverter();
        converter.setOutputDir("files/maps/");
        /**
         * TODO To Decide: does convert auto write? Does convert generate Graph, does convert generate dot file?
         */
        converter.convert("files/maps/Leuven.osm");
    	
        // Testing
    	DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document dom = parser.parse("files/maps/Leuven.osm");
        OsmReader reader = new OsmReader();
        
        NodeList ways = dom.getElementsByTagName("way");
        NodeList nodes = dom.getElementsByTagName("node");

        
        Node way0 = ways.item(0);
        System.out.println(reader.getNodeAttr("id",way0) + ", size: " + reader.getNodes("nd",way0.getChildNodes()).size());
        
        Node node0 = nodes.item(0);
        System.out.println("Lat: " + reader.getNodeAttr("lat",node0) + ", Lon: " + reader.getNodeAttr("lon",node0));
    }
}
