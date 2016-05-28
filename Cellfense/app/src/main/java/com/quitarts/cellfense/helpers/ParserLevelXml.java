package com.quitarts.cellfense.helpers;

import com.quitarts.cellfense.game.LevelDataSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ParserLevelXml extends DefaultHandler {
    private boolean in_resources = false;
    private boolean in_stringArray = false;
    private boolean in_item = false;
    private int wave = 0;

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if (localName.equals("resources"))
            this.in_resources = true;
        else if (localName.equals("string-array")) {
            this.in_stringArray = true;
            String attributeResource = attributes.getValue("resource");
            String attributeTowers = attributes.getValue("towers");
            if (attributeResource != null) {
                int resource = Integer.parseInt(attributeResource);
                LevelDataSet.setLevelResource(resource);
            }

            if (attributeTowers != null)
                LevelDataSet.setLevelTowers(attributeTowers);

            LevelDataSet.setWave(wave++);
        } else if (localName.equals("item"))
            this.in_item = true;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("resources"))
            this.in_resources = false;
        else if (localName.equals("string-array")) {
            this.in_stringArray = false;
            LevelDataSet.setLevels();
        } else if (localName.equals("item"))
            this.in_item = false;
    }

    @Override
    public void characters(char ch[], int start, int length) {
        if (this.in_item)
            LevelDataSet.setLevel(new String(ch, start, length));
    }
}