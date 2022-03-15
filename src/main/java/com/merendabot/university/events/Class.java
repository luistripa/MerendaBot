package com.merendabot.university.events;

import com.merendabot.GuildManager;
import com.merendabot.Merenda;
import com.merendabot.university.subjects.Subject;
import com.merendabot.university.subjects.SubjectClass;
import net.dv8tion.jda.api.EmbedBuilder;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.List;

@Entity
@Table(name = "guild_events_class")
public class Class extends BaseEventClass {

    @Column(name = "endDate", nullable = false)
    private Date endDate;

    @Column(name = "endTime", nullable = false)
    private Time endTime;

    public Class(int id, GuildManager guild, String name, Date startDate, Date endDate, Time startTime, Time endTime, String link, SubjectClass subject) {
        super(guild, name, startDate, startTime, link, subject);
        this.endDate = endDate;
        this.endTime = endTime;
    }

    public Class() {}

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean isNow() {
        return false; // TODO
    }

    @Override
    public void addToEmbed(EmbedBuilder embedBuilder) {
        // TODO
    }

    public static List<Class> getClasses(Session session) {
        List classes;
        classes = session.createQuery("from Class").list();
        return classes;
    }

    public static List<Class> getClassesByWeekday(Session session, DayOfWeek dayOfWeek) {
        List classes;
        classes = session.createQuery("from Class where function('date_part', 'dow', date) = :dows ").setParameter("dows", dayOfWeek.getValue()%7).list();
        return classes;
    }
}
