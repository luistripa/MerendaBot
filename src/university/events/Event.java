package university.events;

import org.jetbrains.annotations.Nullable;
import university.subjects.Subject;

import java.time.LocalDate;
import java.time.LocalTime;

public interface Event {

    /**
     * Gets the id of the event.
     * This id is the primary key of the database.
     *
     * @return The id of the event
     */
    int getId();

    /**
     * Gets the event type of this event.
     *
     * @return An EventType Object
     */
    EventType getEventType();

    /**
     * Gets the interval type of this event.
     *
     * @return An EventInterval Object
     */
    EventInterval getEventInterval();

    /**
     * Gets the name of the event.
     *
     * @return The name of the event
     */
    String getName();

    /**
     * Gets the start date of the event.
     * {@link EventInterval#SINGLE Single Events} have the same start date and end date.
     *
     * @return A LocalTime object
     */
    LocalDate getStartDate();

    /**
     * Gets the end date of the event.
     * {@link EventInterval#SINGLE Single Events} have the same start date and end date.
     *
     * @return A LocalTime object
     */
    LocalDate getEndDate();

    /**
     * Gets the start time of the event.
     *
     * @return A LocalTime Object
     */
    LocalTime getStartTime();

    /**
     * Gets the end time of the event.
     * {@link Assignment Assignments} do not have an end time
     *
     * @return A LocalTime Object
     */
    LocalTime getEndTime();

    /**
     * Gets the link of the event.
     *
     * @return The link of the event
     */
    String getLink();

    /**
     * Gets the id of the subject linked to this event.
     * This id is the primary key of the subject in the database.
     *
     * @return 0 if no subject linked, n > 0 otherwise.
     */
    int getSubjectId();

    /**
     * Gets the subject associated with this event.
     *
     * @return A Subject Object
     */
    @Nullable Subject getSubject();

    /**
     * Checks if the event is happening now.
     *
     * @return True if now, False otherwise.
     */
    boolean isNow();

    /**
     * Sets the subject associated with the event
     * @param subject A Subject Object
     */
    void setSubject(Subject subject);
}
