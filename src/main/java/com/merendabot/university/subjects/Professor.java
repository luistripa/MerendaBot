package main.java.com.merendabot.university.subjects;

import main.java.com.merendabot.GuildManager;
import main.java.com.merendabot.Merenda;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface Professor {

    /**
     * Gets the id of the professor.
     * This is equal to the primary key inside the database.
     *
     * @return The professor's id
     */
    int getId();

    GuildManager getGuild();

    /**
     * Gets the name of the professor.
     *
     * @return The name of the professor
     */
    String getName();

    /**
     * Gets the email of the professor.
     *
     * @return The email of the professor
     */
    String getEmail();

    Subject getSubject();

    /**
     * Gets a Professor by id.
     *
     * @param id The id of the professor
     * @return A Professor object if found, null otherwise
     */
    static Professor getProfessorById(int id) {
        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            Professor professor = session.get(ProfessorClass.class, id);
            tx.commit();
            return professor;

        } catch (Throwable throwable) {
            if (tx != null)
                tx.rollback();

        } finally {
            session.close();
        }
        return null;
    }

    /**
     * Gets all professors in the database.
     *
     * @return A List of Professors
     */
    static List<Professor> getProfessors(Session session) {
        List professors;
        professors = session.createQuery("from ProfessorClass order by subject.shortName").list();
        return professors;
    }
}
