package podcast_application.management.data.write;

import podcast_application.database.SubscriptionsDB;

import javax.xml.stream.*;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

/**
 * StAX has two API's
 *  - The 'Cursor API' using XMLStreamWriter
 *  - The 'Iterator API' using XMLEventWriter
 *
 *  In order to follow clean OPML structure, we need to be able to create empty elements.
 *  As this is not posible using *Iterator API' the 'Cursor API' will be used.
 */
public class OPMLBuilder {

    /**
     * Cursor API
     * @param file
     * @param subscriptionsDB
     */
    public void writeSubscriptions(File file, SubscriptionsDB subscriptionsDB) {
        final String OPML = "opml", BREAK = "\n", TAB = "\t";

        try {
            // create an XMLOutputFactory
            XMLOutputFactory factory = XMLOutputFactory.newInstance();

            // create stream writer
            XMLStreamWriter writer = factory.createXMLStreamWriter(new FileWriter(file));

            writer.writeStartDocument("UTF-8", "1.0");

            writer.writeDTD(BREAK);
            writer.writeStartElement(OPML);
            writer.writeAttribute("version", "2.0");


            // write head
            writer.writeDTD(BREAK+TAB);
            writer.writeStartElement("head");

            writer.writeDTD(BREAK+TAB+TAB);
            writer.writeStartElement("title");
            writer.writeCharacters("Podcast Subscriptions");
            writer.writeEndElement();

            writer.writeDTD(BREAK+TAB);
            writer.writeEndElement(); // end head


            // write body
            writer.writeDTD(BREAK+TAB);
            writer.writeStartElement("body");

            // write outlines
            Map<String, String> subs = subscriptionsDB.getSubscriptions();
            String title = null, url = null;
            for (Map.Entry<String, String> entry : subs.entrySet()) {
                title = entry.getKey();
                url = entry.getValue();

                writer.writeDTD(BREAK+TAB+TAB);
                writer.writeEmptyElement("outline");
                writer.writeAttribute("title", title);
                writer.writeAttribute("text", title);
                writer.writeAttribute("type", "rss");
                writer.writeAttribute("xmlUrl", url);
            }

            writer.writeDTD(BREAK+TAB);
            writer.writeEndElement(); // end body


            // end document
            writer.writeDTD(BREAK);
            writer.writeEndElement(); // end OPML
            writer.writeEndDocument();

            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
