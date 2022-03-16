package com.merendabot.university.subjects;

import com.merendabot.GuildManager;
import com.merendabot.Merenda;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "guild_professors")
public class Professor {

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
    private Subject subject;

    public Professor(GuildManager guild, String name, String email, Subject subject) {
        this.guild = guild;
        this.name = name;
        this.email = email;
        this.subject = subject;
    }

    public Professor() {}

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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    /**
     * Gets a Professor by id.
     *
     * @param id The id of the professor
     * @return A Professor object if found, null otherwise
     */
    public static Professor getProfessorById(int id) {
        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = null;

        try (session) {
            tx = session.beginTransaction();
            Professor professor = session.get(Professor.class, id);
            tx.commit();
            return professor;

        } catch (Throwable throwable) {
            if (tx != null)
                tx.rollback();
        }
        return null;
    }

    /**
     * Gets all professors in the database.
     *
     * @return A List of Professors
     */
    public static List<Professor> getProfessors(Session session) {
        List professors;
        professors = session.createQuery("from Professor order by subject.shortName").list();
        return professors;
    }
}
