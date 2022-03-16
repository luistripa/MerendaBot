package com.merendabot.university.important_links;

import com.merendabot.GuildManager;
import com.merendabot.university.subjects.Subject;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "guild_importantlinks")
public class ImportantLink {

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
    private Subject subject;

    public ImportantLink(GuildManager guild, String name, String url, Subject subject) {
        this.guild = guild;
        this.name = name;
        this.url = url;
        this.subject = subject;
    }

    public ImportantLink() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public GuildManager getGuild() {
        return guild;
    }

    public void setGuild(GuildManager guild) {
        this.guild = guild;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public static List<ImportantLink> getLinks(Session session) {
        List links;
        links = session.createQuery("from ImportantLink").list();
        return links;
    }
}
