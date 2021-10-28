import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import university.events.Event;
import university.events.EventClass;
import university.events.EventInterval;
import university.events.EventType;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class TestSingleEvent {

    Event now_by_time_event;
    Event not_now_by_time_event;
    Event not_now_by_date_event;

    @Before
    public void setUp() {
        now_by_time_event = new EventClass(
                1,
                EventType.CLASS,
                EventInterval.SINGLE,
                "Class now",
                LocalDate.now(),
                Date.valueOf(LocalDate.now().plusDays(5)),
                LocalTime.now(),
                Time.valueOf(LocalTime.now().plusHours(1)),
                "https://google.pt",
                1
        );
        not_now_by_time_event = new EventClass(
                2,
                EventType.CLASS,
                EventInterval.SINGLE,
                "Class not now",
                LocalDate.now(),
                Date.valueOf(LocalDate.now().plusDays(5)),
                LocalTime.now().plusMinutes(5),
                Time.valueOf(LocalTime.now().plusHours(1)),
                "https://google.pt",
                2
        );
        not_now_by_date_event = new EventClass(
                3,
                EventType.CLASS,
                EventInterval.SINGLE,
                "Class not now",
                LocalDate.now().plusDays(1),
                Date.valueOf(LocalDate.now().plusDays(5)),
                LocalTime.now(),
                Time.valueOf(LocalTime.now().plusHours(1)),
                "https://google.pt",
                3
        );
    }

    @Test
    public void testIsNow() {
        Assert.assertTrue(now_by_time_event.isNow());
        Assert.assertFalse(not_now_by_time_event.isNow());
        Assert.assertFalse(not_now_by_date_event.isNow());
    }
}
