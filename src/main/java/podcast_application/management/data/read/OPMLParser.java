package podcast_application.management.data.read;

import podcast_application.database.PlaylistDB;
import podcast_application.database.SubscriptionsDB;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

public class OPMLParser {
    private final String OUTLINE = "outline", TITLE = "title", URL = "xmlUrl";

    public OPMLParser() {}

    public SubscriptionsDB readSubscriptions(File file) {
        SubscriptionsDB subscriptionsDB = new SubscriptionsDB();

        try {
            // First, create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = new FileInputStream(file);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // read the xml document
//            Channel channel = null;
            String title = null, source_rss = null;

            while(eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if(event.isStartElement()) {
                    StartElement startElement = event.asStartElement();

                    // If we have an outline element, we create a new channel
                    if(startElement.getName().getLocalPart().equals(OUTLINE)) {
                        title = event.asStartElement().getAttributeByName(new QName(TITLE)).getValue();
                        source_rss = event.asStartElement().getAttributeByName(new QName(URL)).getValue();
                    }

                }

                // If we reach the end of an outline element, we add it to the list
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equals(OUTLINE)) {
                        subscriptionsDB.addSubscription(title, source_rss);
                    }
                }
            }
            eventReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return subscriptionsDB;
    }


    public PlaylistDB readPlaylist(File file) {
        final String GUID = "text", LINK = "URL", CHANNEL = "channel";
        PlaylistDB playlistDB = new PlaylistDB();

        try {
            // First, create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = new FileInputStream(file);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // read the xml document
//            Channel channel = null;
            String guid = null, link = null, channel = null;

            while(eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if(event.isStartElement()) {
                    StartElement startElement = event.asStartElement();

                    // If we have an outline element, we create a new channel
                    if(startElement.getName().getLocalPart().equals(OUTLINE)) {
                        guid = event.asStartElement().getAttributeByName(new QName(GUID)).getValue();
                        link = event.asStartElement().getAttributeByName(new QName(LINK)).getValue();
//                        channel = event.asStartElement().getAttributeByName(new QName(CHANNEL)).getValue();
                    }

                }

                // If we reach the end of an outline element, we add it to the list
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equals(OUTLINE)) {
                        playlistDB.addToPlaylist(guid, link);
                    }
                }
            }
            eventReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return playlistDB;
    }

}
