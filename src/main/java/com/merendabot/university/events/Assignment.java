package com.merendabot.university.events;

import com.merendabot.GuildManager;
import com.merendabot.university.subjects.SubjectClass;
import net.dv8tion.jda.api.EmbedBuilder;
import org.hibernate.Session;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Entity
@Table(name = "guild_events_assignment")
public class Assignment extends BaseEventClass {

    public Assignment(GuildManager guild, String name, Date dueDate, Time dueTime, String link, SubjectClass subject) {
        super(guild, name, dueDate, dueTime, link, subject);
    }

    public Assignment() {

    }

    @Override
    public boolean isNow() {
        return false; // TODO
    }

    @Override
    public void addToEmbed(EmbedBuilder embedBuilder) {
        // TODO
    }

    public static List<Assignment> getAssignments(Session session) {
        List assignments;
        assignments = session.createQuery("from Assignment").list();
        return assignments;
    }


}
