package kafkastreams_dsp.windows;

import org.apache.kafka.streams.kstream.internals.TimeWindow;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of a daily custom window with a given timezone
 */
public class DailyTimeWindows extends CustomTimeWindows {

    private final static long SIZE_IN_MILLIS = Duration.ofDays(1L).toMillis();
    private final int startHour;

    /**
     * Default constructor
     * @param zoneId to be setted
     * @param grace Duration representing the grace period
     */
    @SuppressWarnings("deprecation")
    public DailyTimeWindows(final ZoneId zoneId, final Duration grace) {
        super(zoneId, grace);
        this.startHour = 0;

        // set up retention time
        this.until(SIZE_IN_MILLIS + grace.toMillis());
    }

    /**
     * Function that assign an event to the correct daily window based on the timestamp
     * @param timestamp of the event
     * @return the map of the windows
     */
    @Override
    public Map<Long, TimeWindow> windowsFor(final long timestamp) {
        final Instant instant = Instant.ofEpochMilli(timestamp);
        final ZonedDateTime zonedDateTime = instant.atZone(zoneId);

        //evaluate start time and end time
        final ZonedDateTime startTime = zonedDateTime.getHour() >= startHour ?
                zonedDateTime.truncatedTo(ChronoUnit.DAYS).withHour(startHour) :
                zonedDateTime.truncatedTo(ChronoUnit.DAYS).minusDays(1).withHour(startHour);
        final ZonedDateTime endTime = startTime.plusDays(1);

        final Map<Long, TimeWindow> windows = new LinkedHashMap<>();
        windows.put(toEpochMilli(startTime), new TimeWindow(toEpochMilli(startTime), toEpochMilli(endTime)));
        return windows;
    }

    /**
     * Getter for the size
     * @return the size of the window (1 day)
     */
    @Override
    public long size() {
        return Duration.ofDays(1).toMillis();
    }
}