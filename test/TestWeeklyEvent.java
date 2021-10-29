import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.merendabot.university.events.Event;
import com.merendabot.university.events.EventClass;
import com.merendabot.university.events.EventInterval;
import com.merendabot.university.events.EventType;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class TestWeeklyEvent {

    Event now_event;
    Event now_by_weekday_event;
    Event not_now_by_time_event;
    Event not_now_by_weekday_event;
    LocalDate start_date;

    @Before
    public void setUp() {
        now_event = new EventClass(
                1,
                EventType.CLASS,
                EventInterval.WEEKLY,
                "Class Now",
                LocalDate.now(),
                Date.valueOf(LocalDate.now()),
                LocalTime.now(),
                Time.valueOf(LocalTime.now().plusHours(1)),
                "https://google.pt",
                1
        );
        now_by_weekday_event = new EventClass(
                3,
                EventType.CLASS,
                EventInterval.WEEKLY,
                "Class not now",
                LocalDate.now().minusDays(7),
                Date.valueOf(LocalDate.now().plusDays(5)),
                LocalTime.now(),
                Time.valueOf(LocalTime.now().plusHours(1)),
                "https://google.pt",
                3
        );
        not_now_by_time_event = new EventClass(
                2,
                EventType.CLASS,
                EventInterval.WEEKLY,
                "Class not now",
                LocalDate.now(),
                Date.valueOf(LocalDate.now()),
                LocalTime.now().plusMinutes(5),
                Time.valueOf(LocalTime.now().plusHours(1)),
                "https://google.pt",
                2
        );
        not_now_by_weekday_event = new EventClass(
                3,
                EventType.CLASS,
                EventInterval.WEEKLY,
                "Class not now",
                LocalDate.now().minusDays(8),
                Date.valueOf(LocalDate.now().plusDays(5)),
                LocalTime.now(),
                Time.valueOf(LocalTime.now().plusHours(1)),
                "https://google.pt",
                3
        );
    }

    @Test
    public void testIsNow() {
        Assert.assertTrue(now_event.isNow());
        Assert.assertTrue(now_by_weekday_event.isNow());
        Assert.assertFalse(not_now_by_time_event.isNow());
        Assert.assertFalse(not_now_by_weekday_event.isNow());
    }
}
