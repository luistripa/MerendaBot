package com.merendabot.university.events;

import com.merendabot.GuildManager;
import com.merendabot.university.subjects.Subject;
import com.merendabot.university.subjects.SubjectClass;
import net.dv8tion.jda.api.EmbedBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@MappedSuperclass
public abstract class BaseEventClass {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GuildManager guild;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "time", nullable = false)
    private Time time;

    @Column(name = "link", nullable = false)
    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SubjectClass subject;

    protected BaseEventClass() {}

    public BaseEventClass(GuildManager guild, String name, Date date, Time time, String link, SubjectClass subject) {
        this.guild = guild;
        this.name = name;
        this.date = date;
        this.time = time;
        this.link = link;
        this.subject = subject;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GuildManager getGuild() {
        return guild;
    }

    public void setGuild(GuildManager guild) {
        this.guild = guild;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public SubjectClass getSubject() {
        return subject;
    }

    public void setSubject(SubjectClass subject) {
        this.subject = subject;
    }

    public abstract boolean isNow();

    public abstract void addToEmbed(EmbedBuilder embedBuilder);
}
