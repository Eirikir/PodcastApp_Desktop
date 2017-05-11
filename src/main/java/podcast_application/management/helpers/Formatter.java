package podcast_application.management.helpers;

import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Formatter {
    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static SimpleDateFormat DATE_FORMATTER_ROME = new SimpleDateFormat("yyyy/MM/dd");
//    private static SimpleDateFormat DATE_FORMATTER_ROME = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");

    public static String FORMAT_DATE(String date) {
        LocalDateTime tmp = LocalDateTime.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(date));
//        LocalDateTime tmp = LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(date));
        return tmp.format(DATE_FORMATTER);
    }

    // used in parsing RSS ROME files
    public static String FORMAT_DATE_ROME(Date date) {
        return DATE_FORMATTER_ROME.format(date);
    }

    /**
     * used mainly for formatting strings in data
     * @param duration
     * @return
     */
    public static String DURATION_TO_STRING(Duration duration) {
        long dur = (long) duration.toSeconds();
        long hours = dur / 3600;
        long minutes = (dur % 3600) / 60;
        long seconds = dur % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static Duration STRING_TO_DURATION(String time) {
        String[] tmp = time.split(":");
        long dur = Long.parseLong(tmp[0]) * 3600;
        dur += Long.parseLong(tmp[1]) * 60;
        dur += Long.parseLong(tmp[2]);

        return Duration.seconds(dur);
    }

    /*
        used in GUI
     */
    public static String FORMAT_TIME(Duration duration) {
        long totalSeconds = (long)Math.floor(duration.toSeconds());
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if(hours > 0)
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

    /*
        used in GUI (NOT IN USE)
     */
    public static String FORMAT_TIME_VALUES(Duration elapsed, Duration duration) {
        long totalElapsedSeconds = (long)Math.floor(elapsed.toSeconds());
        long elapsedHours = totalElapsedSeconds / 3600;
        long elapsedMinutes = (totalElapsedSeconds % 3600) / 60;
        long elapsedSeconds = totalElapsedSeconds % 60;

        if(duration.greaterThan(Duration.ZERO)) {
            long totalDurationSeconds = (long)Math.floor(duration.toSeconds());
            long durationHours = totalDurationSeconds / 3600;
            long durationMinutes = (totalDurationSeconds % 3600) / 60;
            long durationSeconds = totalDurationSeconds % 60;

            if(durationHours > 0)
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            else
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds,durationMinutes,
                        durationSeconds);
        }

        else {
            if (elapsedHours > 0)
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);

            else
                return String.format("%02d:%02d",elapsedMinutes,
                        elapsedSeconds);
        }
    }
}
