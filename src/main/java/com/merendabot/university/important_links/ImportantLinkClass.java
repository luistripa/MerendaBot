package com.merendabot.university.important_links;

import com.merendabot.GuildManager;
import com.merendabot.university.subjects.Subject;
import com.merendabot.university.subjects.SubjectClass;

import javax.persistence.*;

@Entity
@Table(name = "guild_importantlinks")
public class ImportantLinkClass implements ImportantLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id", nullable = false)
    private GuildManager guild;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectClass subject;

    public ImportantLinkClass(GuildManager guild, String name, String url, SubjectClass subject) {
        this.guild = guild;
        this.name = name;
        this.url = url;
        this.subject = subject;
    }

    public ImportantLinkClass() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public GuildManager getGuild() {
        return guild;
    }

    public void setGuild(GuildManager guild) {
        this.guild = guild;
    }

    @Override
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(SubjectClass subject) {
        this.subject = subject;
    }
}
