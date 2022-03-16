package com.merendabot.university.subjects;

import com.merendabot.GuildManager;
import com.merendabot.Merenda;
import com.merendabot.university.subjects.exceptions.SubjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "guild_subject")
public class Subject {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GuildManager guild;

    private String fullName;

    private String shortName;

    public Subject(GuildManager guild, String fullName, String shortName) {
        this.guild = guild;
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public Subject() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GuildManager getGuild() {
        GuildManager guildManager = Merenda.getInstance().getGuild(guild.getGuildId());
        if (guildManager == null)
            return guild;
        guild = guildManager;
        return guildManager;
    }

    public void setGuild(GuildManager guild) {
        this.guild = guild;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public static Subject getSubjectById(Session session, int id) throws SubjectNotFoundException {
        Subject subject = session.get(Subject.class, id);
        if (subject == null)
            throw new SubjectNotFoundException(id);
        return subject;
    }

    public static Subject getSubjectByShortName(Session session, String shortName) throws SubjectNotFoundException {
        Subject subject = (Subject) session.createQuery("from Subject where shortName = :short")
                .setParameter("short", shortName).uniqueResult();
        if (subject == null)
            throw new SubjectNotFoundException(shortName);
        return subject;
    }

    public static List<Subject> getSubjects(Session session) {
        List subjects;
        subjects = session.createQuery("from Subject ").list();
        return subjects;
    }
}
