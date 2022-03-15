package com.merendabot.university.subjects;

import com.merendabot.GuildManager;
import com.merendabot.Merenda;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "guild_professors")
public class ProfessorClass implements Professor {

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

    @Column(name = "email", nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SubjectClass subject;

    public ProfessorClass(GuildManager guild, String name, String email, SubjectClass subject) {
        this.guild = guild;
        this.name = name;
        this.email = email;
        this.subject = subject;
    }

    public ProfessorClass() {}

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
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

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(SubjectClass subject) {
        this.subject = subject;
    }
}
