package podcast_application.management.data.write;

import podcast_application.database.SubscriptionsDB;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class SubscriptionsBuilder {

    public void write(File file, SubscriptionsDB subscriptionsDB) {

        String OPML = "opml";
//        String OPML_START = "<opml version=\"2.0\">";
//        String START = "<opml version='1.1'>\n<head>\n<title>technoXist export</title>\n<dateCreated>";

        try {
            // create an XMLOutputFactory
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            // create XMLEventWriter
            XMLEventWriter eventWriter = outputFactory
                    .createXMLEventWriter(new FileOutputStream(file), "UTF-8");

            // create an EventFactory
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent end = eventFactory.createDTD("\n");
            XMLEvent tab = eventFactory.createDTD("\t");
            XMLEvent doubleTab = eventFactory.createDTD("\t\t");

            // create and write Start Tag
            StartDocument startDocument = eventFactory.createStartDocument();
            eventWriter.add(startDocument);

            eventWriter.add(end);

            // create opml element
            eventWriter.add(eventFactory.createStartElement("","",OPML));
            eventWriter.add(eventFactory.createAttribute("version", "2.0"));
            eventWriter.add(end);

            // write head section
            writeHead(eventWriter);




            // write body
            writeBody(eventWriter, subscriptionsDB);



            eventWriter.add(eventFactory.createEndElement("","",OPML));
            eventWriter.add(eventFactory.createEndDocument());
            eventWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeHead(XMLEventWriter eventWriter) throws XMLStreamException {
        String HEAD = "head", SUB_TITLE = "Podcast Subscriptions";

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");
        XMLEvent doubleTab = eventFactory.createDTD("\t\t");


        // create head element
        eventWriter.add(tab);
        eventWriter.add(eventFactory.createStartElement("","",HEAD));
        eventWriter.add(end);

        // sub title
        eventWriter.add(doubleTab);
        eventWriter.add(eventFactory.createStartElement("","","title"));
        eventWriter.add(eventFactory.createCharacters(SUB_TITLE));
        eventWriter.add(eventFactory.createEndElement("","","title"));
        eventWriter.add(end);

        eventWriter.add(tab);
        eventWriter.add(eventFactory.createEndElement("", "", HEAD));
        eventWriter.add(end);
    }

    private void writeBody(XMLEventWriter eventWriter, SubscriptionsDB db) throws XMLStreamException {
        String BODY = "body";
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");
        XMLEvent doubleTab = eventFactory.createDTD("\t\t");

        // create body element
        eventWriter.add(tab);
        eventWriter.add(eventFactory.createStartElement("","",BODY));

        // write outlines
        Map<String, String> subs = db.getSubscriptions();
        String title = null, url = null;
        for (Map.Entry<String, String> entry : subs.entrySet()) {
            title = entry.getKey();
            url = entry.getValue();

            eventWriter.add(end);
            eventWriter.add(doubleTab);


            eventWriter.add(eventFactory.createStartElement("","","outline"));
            eventWriter.add(eventFactory.createAttribute("title", title));
            eventWriter.add(eventFactory.createAttribute("text", title));
            eventWriter.add(eventFactory.createAttribute("type", "rss"));
            eventWriter.add(eventFactory.createAttribute("xmlUrl", url));

//            eventWriter.add(end);
//            eventWriter.add(doubleTab);
            eventWriter.add(eventFactory.createEndElement("","","outline"));
        }

        eventWriter.add(end);
        eventWriter.add(tab);
        eventWriter.add(eventFactory.createEndElement("", "", BODY));
        eventWriter.add(end);
    }
}
