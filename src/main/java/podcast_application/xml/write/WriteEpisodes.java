package podcast_application.xml.write;


import podcast_application.singletons.Formatter;
import podcast_application.media.gui.PodcastEpisode;

import java.io.FileOutputStream;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class WriteEpisodes {
    private String configFile;
    final String EPISODES = "episodes", ITEM = "item", TITLE = "title", DESCRIPTION = "description", DATE = "date",
            LINK = "link", DURATION = "duration", PROGRESS = "progress";

    public void setFile(String configFile) {
        this.configFile = configFile;
    }

    public void saveEpisodes(List<PodcastEpisode> list) throws Exception {
        // create an XMLOutputFactory
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        // create XMLEventWriter
        XMLEventWriter eventWriter = outputFactory
                .createXMLEventWriter(new FileOutputStream(configFile));
        // create an EventFactory
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        // create and write Start Tag
        StartDocument startDocument = eventFactory.createStartDocument();
        eventWriter.add(startDocument);

        eventWriter.add(end);
        // create config open tag
        StartElement episodesStartElement = eventFactory.createStartElement("",
                "", EPISODES);
        eventWriter.add(episodesStartElement);
        eventWriter.add(end);


        for (PodcastEpisode episode : list)
            createItem(eventWriter, episode);


        eventWriter.add(eventFactory.createEndElement("", "", EPISODES));
        eventWriter.add(end);
        eventWriter.add(eventFactory.createEndDocument());
        eventWriter.close();
    }

    private void createItem(XMLEventWriter eventWriter, PodcastEpisode episode) throws XMLStreamException {
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");

        StartElement itemStartElement = eventFactory.createStartElement("","",ITEM);
        eventWriter.add(tab);
        eventWriter.add(itemStartElement);
        eventWriter.add(end);

        // Write the different nodes
        createNode(eventWriter, TITLE, episode.getTitle());
        createNode(eventWriter, DESCRIPTION, episode.getDescription());
        createNode(eventWriter, DATE, episode.getPubDate());
        createNode(eventWriter, LINK, episode.getLink());

        String dur = Formatter.DURATION_TO_STRING(episode.getDuration());
        createNode(eventWriter, DURATION, dur);
        dur = Formatter.DURATION_TO_STRING(episode.getProgress());
        createNode(eventWriter, PROGRESS, dur);

        eventWriter.add(tab);
        eventWriter.add(eventFactory.createEndElement("","",ITEM));
        eventWriter.add(end);
    }

    private void createNode(XMLEventWriter eventWriter, String name,
                            String value) throws XMLStreamException {

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t\t");
        // create Start node
        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        // create Content
        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);

    }
}

