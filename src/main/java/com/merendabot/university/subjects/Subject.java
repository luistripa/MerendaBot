package main.java.com.merendabot.university.subjects;

import main.java.com.merendabot.GuildManager;
import org.hibernate.Session;

import java.util.List;

public interface Subject {

    /**
     * Gets the id of the subject.
     *
     * @return The id of the subject
     */
    int getId();

    GuildManager getGuild();

    /**
     * Gets the name of the subject.
     *
     * @return The name of the subject
     */
    String getFullName();

    /**
     * Gets a short version of the subject's name.
     *
     * Example: Software Engineering becomes SE
     *
     * @return The subject's short name
     */
    String getShortName();

    static List<Subject> getSubjects(Session session) {
        List subjects;
        subjects = session.createQuery("from SubjectClass").list();
        return subjects;
    }
}
