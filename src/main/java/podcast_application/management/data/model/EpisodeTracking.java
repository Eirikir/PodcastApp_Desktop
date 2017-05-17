package podcast_application.management.data.model;

import java.io.Serializable;

public class EpisodeTracking implements Serializable {
    private String progress;
    private boolean isDone;

    public void setProgress(String progress) { this.progress = progress; }
    public String getProgress() { return progress; }

    public EpisodeTracking(String progress) {
        this.progress = progress;
        isDone = false;
    }

    public EpisodeTracking(String progress, boolean isDone) {
        this.progress = progress;
        this.isDone = isDone;
    }

    public void setIsDone(boolean value) { this.isDone = value; }
    public boolean getIsDone() { return isDone; }
}
