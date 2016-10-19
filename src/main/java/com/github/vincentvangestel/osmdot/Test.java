package com.github.vincentvangestel.osmdot;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Test 
{
    public static void main( String[] args ) throws ParserConfigurationException, SAXException, IOException {
    	OsmConverter converter = new OsmConverter();
        converter.setOutputDir("files/maps/")
        	.withOutputName("leuven-large.dot")
        	.convert("files/maps/leuven-large.osm");
    	
    }
}