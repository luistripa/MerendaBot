package main.java.com.merendabot.university.events;

import main.java.com.merendabot.GuildManager;
import main.java.com.merendabot.university.subjects.SubjectClass;
import net.dv8tion.jda.api.EmbedBuilder;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Time;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "guild_events_test")
public class Test extends BaseEventClass {

    @Column(name = "endTime", nullable = false)
    private Time endTime;

    public Test(GuildManager guild, String name, Date dueDate, Time startTime, Time endTime, String link, SubjectClass subject) {
        super(guild, name, dueDate, startTime, link, subject);
        this.endTime = endTime;
    }

    public Test() {

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

    public static List<Test> getTests(Session session) {
        List tests;
        tests = session.createQuery("from Test").list();
        return tests;
    }
}
