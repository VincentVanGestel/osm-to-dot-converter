package com.github.vincentvangestel.osmdot;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Test 
{
    public static void main( String[] args ) throws ParserConfigurationException, SAXException, IOException {
    	OsmConverter converter = new OsmConverter();
        converter.setOutputDir("files/maps/");
        /**
         * TODO To Decide: does convert auto write? Does convert generate Graph, does convert generate dot file?
         */
        converter.convert("files/maps/Tervuursevest.osm");
    	
    }
}
