package com.quitarts.cellfense;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LevelXmlHandler extends DefaultHandler {
	private boolean in_resources = false;
	private boolean in_stringArray = false;
	private boolean in_item= false;
	private int wave = 0;
	//private LevelDataSet levelDataSet;

	/*public LevelDataSet getParsedData() {
		return levelDataSet;
	}*/

	@Override
	public void startDocument() throws SAXException {
		//levelDataSet = new LevelDataSet();
	}

	@Override
	public void endDocument() throws SAXException {
		//nothing to do
	}	
	
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		super.startElement(namespaceURI, localName, qName, atts);
		if (localName.equals("resources")) {			
			this.in_resources = true;			
		} else if (localName.equals("string-array")) {			
			this.in_stringArray = true;
			String attrResource = atts.getValue("resource");
			String attrTowers = atts.getValue("towers");
			if(attrResource != null) {
				int i = Integer.parseInt(attrResource);			
				LevelDataSet.setResources(i);				
			}			
			if(attrTowers != null) {
				LevelDataSet.setTowers(attrTowers);
			}
			LevelDataSet.setWave(wave++);			
		} else if (localName.equals("item")) {			
			this.in_item = true;
		}		
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (localName.equals("resources")) {			
			this.in_resources = false;
		} else if (localName.equals("string-array")) {			
			this.in_stringArray = false;
			LevelDataSet.setLevels();
		} else if (localName.equals("item")) {			
			this.in_item = false;
		} 
	}

	@Override
	public void characters(char ch[], int start, int length) {
		if (this.in_item) {
			LevelDataSet.addDataToLevel(new String(ch, start, length));
		}
	}
}