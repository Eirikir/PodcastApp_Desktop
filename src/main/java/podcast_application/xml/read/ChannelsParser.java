package podcast_application.xml.read;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import podcast_application.singletons.WriteDefaultFiles;
import podcast_application.xml.model.Channel;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ChannelsParser {
    private final String ITEM = "item", TITLE = "title", LINK = "link", DESCRIPTION = "description", LANGUAGE = "language",
            DATE = "date", IMAGE = "image";

    public ChannelsParser() {
        // set up directories and files, if there are none
        File baseDir = new File("./Podcasts");
        if(!baseDir.exists()) {
            baseDir.mkdir();
            new WriteDefaultFiles(baseDir.getPath());
 //           writeDefaultChannels();
        }



    }

    private void writeDefaultChannels() {
        InputStream is = null;
        OutputStream out = null;
        try {
            is = getClass().getResourceAsStream("/defaultFiles/channels.xml");
            out = new FileOutputStream("./Allo/channels.xml");

            int read = 0;
            byte[] bytes = new byte[512];
            while((read = is.read(bytes)) != -1)
                out.write(bytes, 0, read);
            is.close();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Channel> readChannels() {
        List<Channel> channels = new ArrayList<>();

        try {

            // First, create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = new FileInputStream("./Podcasts/channels.xml");
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // read the xml document
            Channel channel = null;

            while(eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if(event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have an item element, we create a new channel
                    if(startElement.getName().getLocalPart().equals(ITEM)) {
                        channel = new Channel();
                    }

                    String type = event.asStartElement().getName().getLocalPart();
                    switch(type)
                    {
                        case TITLE: event = eventReader.nextEvent();
                            channel.setTitle(event.asCharacters().getData()); break;
                        case LINK: event = eventReader.nextEvent();
                            channel.setLink(event.asCharacters().getData()); break;
                        case DESCRIPTION: event = eventReader.nextEvent();
                            channel.setDescription(event.asCharacters().getData()); break;
                        case LANGUAGE: event = eventReader.nextEvent();
                            channel.setLanguage(event.asCharacters().getData()); break;
                        case DATE: event = eventReader.nextEvent();
                            channel.setDate(event.asCharacters().getData()); break;
                        case IMAGE: event = eventReader.nextEvent();
                            channel.setImage(event.asCharacters().getData()); break;
                    }
                }

                // If we reach the end of an item element, we add it to the list
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equals(ITEM)) {
                        channels.add(channel);
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return channels;
    }
}
