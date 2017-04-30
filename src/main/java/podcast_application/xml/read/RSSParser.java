package podcast_application.xml.read;



import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.itunes.AbstractITunesObject;
import com.sun.syndication.feed.module.itunes.EntryInformation;
import com.sun.syndication.feed.module.itunes.FeedInformation;
import com.sun.syndication.feed.module.itunes.types.Duration;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import podcast_application.xml.model.Channel;
import podcast_application.xml.model.Item;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class RSSParser {
    private final String BASE_PATH = "./Podcasts/",
            CHANNEL_FILE = "/channel.rss";
    private List<Channel> channels = new ArrayList<>();

    public RSSParser() {
        if(Files.notExists(Paths.get(BASE_PATH))) // create 'Podcasts' dir if not present
            writeDefaultFiles();

        // load base channel info
        for (Subscription sub : getSubscriptions()) {

            if(Files.notExists(Paths.get(BASE_PATH + sub.getTitle()))) // create channel dir if not present
                new File(BASE_PATH + sub.getTitle()).mkdir();

            // check whether the channel feed needs to be updated (or created if not present)
            File channelFile = new File(BASE_PATH + sub.getTitle() + CHANNEL_FILE);
            updateLocalFile(sub.getSourceRSS(), channelFile);

            // ... get channel & episode information
            parseFEED(channelFile);
        }

    }

    private void writeDefaultFiles() {
        new File(BASE_PATH).mkdir(); // create 'Podcasts' dir

        final String SUB_FILE = "/subscriptions.xml";

        // Now copy default files
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream("/defaultFiles"+SUB_FILE);
            Files.copy(is, Paths.get(BASE_PATH + SUB_FILE));

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if(is != null)
                try {
                    is.close();
                    System.out.println("Closing stream");
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
        }
    }

    private void updateLocalFile(String source, File localFile) {

        try {
            URL url = new URL(source);

            // determine age of files (if source is newer than local; replace it)
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            if(conn.getLastModified() > localFile.lastModified() || !localFile.exists()) {
                ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());

//            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(localFile);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();


//                long tmp = conn.getLastModified();
//                String modified = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(tmp));
//                System.out.println("File modified: " + modified);

                fos.close();
                rbc.close();
            }
            conn.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private List<Subscription> getSubscriptions() {
        final String CHANNEL = "channel", TITLE = "title", SOURCE_RSS = "source_rss";
        List<Subscription> subscriptions = new ArrayList<>();

        InputStream in = null;
        XMLEventReader eventReader = null;
        try {
            // First, create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            in = new FileInputStream("./Podcasts/subscriptions.xml");
            eventReader = inputFactory.createXMLEventReader(in);
            // read the xml document
            Subscription subscription = null;


            while(eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if(event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have an item element, we create a new channel
                    if(startElement.getName().getLocalPart().equals(CHANNEL)) {
                        subscription = new Subscription();
                    }

                    String type = event.asStartElement().getName().getLocalPart();
                    switch(type)
                    {
                        case TITLE: event = eventReader.nextEvent();
                            subscription.setTitle(event.asCharacters().getData()); break;
                        case SOURCE_RSS: event = eventReader.nextEvent();
                            subscription.setSourceRSS(event.asCharacters().getData()); break;
                    }
                }

                // If we reach the end of an item element, we add it to the list
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equals(CHANNEL)) {
                        subscriptions.add(subscription);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(in != null)
                    in.close();
                if(eventReader != null)
                    eventReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return subscriptions;
    }

    private void parseFEED(File channelFile) {
        try {
//            String url = "http://feeds.soundcloud.com/users/soundcloud:users:38128127/sounds.rss";
            URL feedSource = channelFile.toURI().toURL();

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));
            Module module = feed.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd");
            FeedInformation feedInfo = (FeedInformation) module;

            Channel channel = new Channel();
            channel.setTitle(feed.getTitle());
            channel.setDescription(feed.getDescription());
            channel.setDate(feed.getPublishedDate().toString());
            channel.setImage(feedInfo.getImage().toString());
            channel.setLanguage(feed.getLanguage());
            channel.setLink(feed.getLink());


            List<Item> items = new ArrayList<>();
            for (Object entry : feed.getEntries())
                items.add(parseItem((SyndEntry)entry));

            channel.setItems(items);
            channels.add(channel);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Item parseItem(SyndEntry entry) {
        Item tmp = new Item();

        tmp.setTitle(entry.getTitle());
        tmp.setDescription(entry.getDescription().getValue());

        tmp.setDate(entry.getPublishedDate());
        tmp.setLink(((SyndEnclosure) entry.getEnclosures().get(0)).getUrl());
        Duration duration = ((EntryInformation)entry.getModule(AbstractITunesObject.URI)).getDuration();
        String durValue = (duration == null) ? "00:00:00" : duration.toString();
        tmp.setDuration(durValue);
//        tmp.setDuration(((EntryInformation) entry.getModule(AbstractITunesObject.URI)).getDuration().toString());
        tmp.setProgress("00:00:00");

        return tmp;
    }

    public List<Channel> getChannels() { return channels; }
}

class Subscription {
    private String title, sourceRSS;
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSourceRSS() { return sourceRSS; }
    public void setSourceRSS(String sourceRSS) { this.sourceRSS = sourceRSS; }
}