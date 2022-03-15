package main.java.com.merendabot.university.subjects;

import main.java.com.merendabot.GuildManager;
import main.java.com.merendabot.Merenda;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "guild_subject")
public class SubjectClass implements Subject {

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

    public SubjectClass(GuildManager guild, String fullName, String shortName) {
        this.guild = guild;
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public SubjectClass() {}

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
    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
