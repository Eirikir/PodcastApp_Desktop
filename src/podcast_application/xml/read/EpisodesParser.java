package podcast_application.xml.read;

import podcast_application.xml.model.Item;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class EpisodesParser {
    private final String ITEM = "item", TITLE = "title", DESCRIPTION = "description",
            DATE = "date", LINK = "link", DURATION = "duration", PROGRESS = "progress";

    public List<Item> readEpisodes(String file) {
        List<Item> items = new ArrayList<>();
        try {
            // First, create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = new FileInputStream(file);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // read the XML document
            Item item = null;

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have an item element, we create a new item
                    if (startElement.getName().getLocalPart().equals(ITEM)) {
                        item = new Item();
                    }

                    String type = event.asStartElement().getName().getLocalPart();
                    switch (type)
                    {
                        case TITLE: event = eventReader.nextEvent();
                            item.setTitle(event.asCharacters().getData()); break;

                        case DESCRIPTION: event = eventReader.nextEvent();
                            item.setDescription(event.asCharacters().getData()); break;

                        case DATE: event = eventReader.nextEvent();
                            item.setDate(event.asCharacters().getData()); break;

                        case LINK: event = eventReader.nextEvent();
                            item.setLink(event.asCharacters().getData()); break;

                        case DURATION: event = eventReader.nextEvent();
                            item.setDuration(event.asCharacters().getData()); break;

                        case PROGRESS: event = eventReader.nextEvent();
                            item.setProgress(event.asCharacters().getData()); break;
                    }

                }
                // If we reach the end of an item element, we add it to the list
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equals(ITEM)) {
                        items.add(item);
                    }
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        return items;
    }


}
